package cz.muni.fi.MIS;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by V.Mecko on 8.3.2017.
 */
public class GuestManagerImpl implements GuestManager {

    private JdbcTemplate jdbc;
    final static Logger log = LoggerFactory.getLogger(GuestManagerImpl.class);

    public GuestManagerImpl(DataSource dataSource){
        this.jdbc = new JdbcTemplate(dataSource);
    }

    @Override
    public void createGuest(Guest guest){
        SimpleJdbcInsert insertGuest = new SimpleJdbcInsert(jdbc)
                .withTableName("guests").usingGeneratedKeyColumns("guestid");

        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("phonenumber",guest.getPhoneNumber())
                .addValue("address",guest.getAddress())
                .addValue("fullname",guest.getFullName());

        Number id = insertGuest.executeAndReturnKey(parameters);
        guest.setGuestID(id.longValue());
        log.info("Guest {} added to DB.",guest);
    }

    @Override
    public void updateGuest(Guest guest) {
        jdbc.update("UPDATE guests SET phonenumber=?,address=?,fullname=? WHERE guestID=?",guest.getPhoneNumber(),guest.getAddress(),guest.getFullName(),guest.getGuestID());
        log.info("Guest {} updated.",guest);
    }

    @Override
    public void deleteGuest(Guest guest){
        jdbc.update("DELETE FROM guests WHERE guestID=?",guest.getGuestID());
        log.info("Guest deleted.");
    }

    private RowMapper<Guest> guestMapper = (rs,rowNum) ->
            new Guest(rs.getLong("guestID"),rs.getString("phonenumber"),rs.getString("address"),rs.getString("fullname"));

    @Transactional
    @Override
    public List<Guest> listAllGuests() {
        log.info("Listing all guests.");
        return jdbc.query("SELECT * FROM guests",guestMapper);
    }

    @Override
    public Guest getGuestByID(Long guestID) {
        List<Guest> guests = jdbc.query("SELECT * FROM guests WHERE guestID=?", guestMapper, guestID);
        if(guests.isEmpty()) return null;
        return guests.get(0);
    }


    @Override
    public List<Guest> findGuestByName(String fullName) {
        log.info("Looking for Guest " + fullName + ".");
        return jdbc.query("SELECT * FROM guests WHERE fullname=?",guestMapper,fullName);
    }
}