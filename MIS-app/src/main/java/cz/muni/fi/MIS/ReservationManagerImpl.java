package cz.muni.fi.MIS;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import javax.xml.bind.ValidationException;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by V.Mecko on 8.3.2017.
 */
public class ReservationManagerImpl implements ReservationManager {

    private JdbcTemplate jdbc;
    private RoomManager roomManager;
    private GuestManager guestManager;


    private Date toSQLDate(LocalDate localDate) {
        if (localDate == null) return null;
        return new Date(ZonedDateTime.of(localDate.atStartOfDay(), ZoneId.systemDefault()).toInstant().toEpochMilli());
    }

    public ReservationManagerImpl(DataSource ds){
        this.jdbc = new JdbcTemplate(ds);
    }

    public void setRoomManager(RoomManager roomManager) {
        this.roomManager = roomManager;
    }

    public void setGuestManager(GuestManager guestManager) {
        this.guestManager = guestManager;
    }

    //insert part

    @Override
    public void createReservation(Reservation reservation) throws ValidationException {
        /**/
        if (reservation.getStartTime().isAfter(reservation.getEndTime()))
            throw new ValidationException("start time later than end time.");

        SimpleJdbcInsert insertReservation = new SimpleJdbcInsert(jdbc)
                .withTableName("reservations").usingGeneratedKeyColumns("reservationID");

        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("starttime",toSQLDate(reservation.getStartTime()))
                .addValue("endtime",toSQLDate(reservation.getEndTime()))
                .addValue("roomid_fk",reservation.getRoom().getRoomID())
                .addValue("guestid_fk",reservation.getGuest().getGuestID())
                .addValue("price",reservation.getPrice());

        Number id = insertReservation.executeAndReturnKey(parameters);
        reservation.setReservationID(id.longValue());
       /* */

        /* cez keyholder */ /*
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement("insert into reservations (STARTTIME, ENDTIME, ROOMID_FK, GUESTID_FK, PRICE) VALUES (?,?,?,?,?)", new String[]{"reservationid"});
            ps.setObject(1, reservation.getStartTime().format(DateTimeFormatter.ISO_LOCAL_DATE), Types.DATE);
            ps.setObject(2, reservation.getEndTime().format(DateTimeFormatter.ISO_LOCAL_DATE), Types.DATE);
            ps.setLong(3, reservation.getRoom().getRoomID());
            ps.setLong(4, reservation.getGuest().getGuestID());
            ps.setBigDecimal(5, reservation.getPrice());
            return ps;
        }, keyHolder);
        reservation.setReservationID(keyHolder.getKey().longValue());
        */
    }

    //update part

    @Override
    public void updateReservation(Reservation reservation) {
        jdbc.update("UPDATE reservations SET starttime=?,endtime=?,roomid_fk=?,guestid_fk=?,price=? WHERE reservationid=?",
                Date.valueOf(reservation.getStartTime()),
                Date.valueOf(reservation.getEndTime()),
                reservation.getRoom().getRoomID(),
                reservation.getGuest().getGuestID(),
                reservation.getPrice(),
                reservation.getReservationID());
    }

    @Override
    public void deleteReservation(Reservation reservation) {
        jdbc.update("DELETE FROM reservations WHERE reservationID=?",reservation.getReservationID());
    }

    //query part

    private RowMapper<Guest> guestMapper = (rs,rowNum) ->
            new Guest(rs.getLong("guestID"),rs.getString("phonenumber"),rs.getString("address"),rs.getString("fullname"));


    private RowMapper<Room> roomMapper = (rs,rowNum) ->
            new Room(rs.getLong("roomID"),rs.getInt("capacity"),rs.getString("roomNumber"));


    private RowMapper<Reservation> reservationMapper = (rs, rowNum) -> {
        return new Reservation(rs.getLong("reservationid"),
                rs.getDate("starttime").toLocalDate(),
                rs.getDate("endtime").toLocalDate(),
                roomManager.getRoomByID(rs.getLong("roomId_fk")),
                guestManager.getGuestByID(rs.getLong("guestId_fk")),
                rs.getBigDecimal("price"));

    };

    @Override
    public List<Reservation> findAllReservations() {
        return jdbc.query("SELECT * FROM reservations",reservationMapper);
    }

    @Override
    public Reservation getReservationByID(Long reservationID){
        List<Reservation> reservations = jdbc.query("SELECT * FROM reservations WHERE reservationID=?", reservationMapper, reservationID);
        if(reservations.isEmpty()) return null;
        return reservations.get(0);

    }

    @Override
    public List<Reservation> findGuestReservation(Guest guest) {
        return jdbc.query("SELECT * FROM reservations WHERE guestid_fk=?",reservationMapper,guest.getGuestID());
    }

    @Override
    public List<Reservation> findRoomReservation(Room room) {
        return jdbc.query("SELECT * FROM reservations WHERE roomid_fk=?",reservationMapper,room.getRoomID());
    }

    /**
     * TODO: implement correctly
     * doesnt work yet
     */
    @Override
    public List<Room> findEmptyRoom(LocalDate start, LocalDate ende) {
      /*
        return jdbc.query("SELECT * FROM room WHERE roomid NOT IN (SELECT roomid_fk " +
                        "FROM reservation WHERE ((starttime <= startT=? AND endtime > startT=?) " +
                        "OR (starttime > startT=? AND endtime <= endT=?) OR (starttime < endT=?) AND (endtime > endT=?)))",
                reservationMapper,Date.valueOf(start),Date.valueOf(start),Date.valueOf(start),Date.valueOf(ende),Date.valueOf(ende),Date.valueOf(ende));
*/

        List<Reservation> reservationsInRange = null;
        reservationsInRange = jdbc.query("SELECT * " +
                        "FROM reservations " +
                        "WHERE (((starttime <= ?) AND (endtime > ?)) OR " +
                        "((starttime > ?) AND (endtime <= ?)) OR " +
                        "((starttime < ?) AND (endtime > ?)))",
                reservationMapper,
                Date.valueOf(start),Date.valueOf(start),Date.valueOf(start),
                Date.valueOf(ende),Date.valueOf(ende),Date.valueOf(ende));

        List<Room> roomList = null;
        roomList = jdbc.query("SELECT * FROM rooms",roomMapper);
/*
        if (!reservationsInRange.isEmpty()) {
            for (Reservation res:reservationsInRange) {
                if(roomList.contains(res.getRoom())) {
                    roomList.remove(res.getRoom());
                }
            }
        }
        return roomList;
*/

        for (Reservation res:reservationsInRange) {
            if(roomList.contains(res.getRoom())) {
                roomList.remove(res.getRoom());
            }
        }

        return roomList;
    }
}