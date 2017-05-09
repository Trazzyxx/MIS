package cz.muni.fi.MIS;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import javax.sql.DataSource;
import javax.xml.bind.ValidationException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * Created by V.Mecko on 8.3.2017.
 */
public class ReservationManagerImpl implements ReservationManager {

    private JdbcTemplate jdbc;
    private RoomManager roomManager;
    private GuestManager guestManager;

    final static Logger log = LoggerFactory.getLogger(ReservationManagerImpl.class);


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
        if (reservation.getStartTime().isAfter(reservation.getEndTime())){
            log.info("Validation Exception throwed in createReservation.");
            throw new ValidationException("start time later than end time.");
        }
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
        log.info("Reservation {} created.",reservation);
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
        log.info("Reservation {} updated.",reservation);

    }

    @Override
    public void deleteReservation(Reservation reservation) {
        jdbc.update("DELETE FROM reservations WHERE reservationID=?",reservation.getReservationID());
        log.info("Reservation deleted.");
    }

    //query part
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
       // log.info("Listing all reservations.");
        return jdbc.query("SELECT * FROM reservations",reservationMapper);
    }

    @Override
    public Reservation getReservationByID(Long reservationID){
        List<Reservation> reservations = jdbc.query("SELECT * FROM reservations WHERE reservationID=?", reservationMapper, reservationID);
        if(reservations.isEmpty()){
            log.info("Reservation not found.");
            return null;
        }
        return reservations.get(0);

    }

    @Override
    public List<Reservation> findGuestReservation(Guest guest) {
        log.info("Finding reservation for given guest {}.",guest);
        return jdbc.query("SELECT * FROM reservations WHERE guestid_fk=?",reservationMapper,guest.getGuestID());
    }

    @Override
    public List<Reservation> findRoomReservation(Room room) {
        log.info("Finding reservation for given room {}.",room);
        return jdbc.query("SELECT * FROM reservations WHERE roomid_fk=?",reservationMapper,room.getRoomID());
    }


    @Override
    public List<Room> findEmptyRoom(LocalDate start, LocalDate ende) {
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

        for (Reservation res:reservationsInRange) {
            if(roomList.contains(res.getRoom())) {
                roomList.remove(res.getRoom());
            }
        }
        log.info("Empty rooms listed.");
        return roomList;
    }
}