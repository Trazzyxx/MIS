package cz.muni.fi.MIS;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by V.Mecko on 8.3.2017.
 */
public class RoomManagerImpl implements RoomManager {

    private JdbcTemplate jdbc;

    final static Logger log = LoggerFactory.getLogger(RoomManagerImpl.class);

    public RoomManagerImpl(DataSource ds){
        this.jdbc = new JdbcTemplate(ds);
    }

    @Override
    public void createRoom(Room room) {
        SimpleJdbcInsert insertRoom = new SimpleJdbcInsert(jdbc)
                .withTableName("rooms").usingGeneratedKeyColumns("roomid");

        SqlParameterSource paramaters = new MapSqlParameterSource()
                .addValue("capacity",room.getCapacity())
                .addValue("roomnumber",room.getRoomNumber());

        Number id = insertRoom.executeAndReturnKey(paramaters);
        room.setRoomID(id.longValue());
        log.info("Room {} Created.",room);
    }

    @Override
    public void updateRoom(Room room) {
        jdbc.update("UPDATE rooms SET capacity = ?, roomnumber = ? WHERE roomid = ?",
                room.getCapacity(),
                room.getRoomNumber(),
                room.getRoomID());
        log.info("Room {} updated.",room);
    }

    @Override
    public void deleteRoom(Room room) {
        jdbc.update("DELETE FROM rooms WHERE roomid=?",room.getRoomID());
        log.info("Room deleted.");
    }


    private RowMapper<Room> roomMapper = (rs,rowNum) ->
            new Room(rs.getLong("roomID"),rs.getInt("capacity"),rs.getString("roomNumber"));

    @Override
    public Room getRoomByID(Long roomID) {
        List<Room> rooms = jdbc.query("SELECT * FROM rooms WHERE roomID=?", roomMapper, roomID);
        if(rooms.isEmpty()){
            log.info("Can't find room.");
            return null;
        }
        return rooms.get(0);
    }

    @Transactional
    @Override
    public List<Room> findAllRooms() {
        log.info("Listing all rooms.");
        return jdbc.query("SELECT * FROM rooms",roomMapper);
    }
}