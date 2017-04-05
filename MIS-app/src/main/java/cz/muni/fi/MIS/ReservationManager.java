package cz.muni.fi.MIS;

import javax.xml.bind.ValidationException;
import java.time.LocalDate;
import java.util.List;

/**
 * Created by V.Mecko on 8.3.2017.
 */
public interface ReservationManager {
    void createReservation(Reservation reservation) throws ValidationException;

    void updateReservation(Reservation reservation);

    void deleteReservation(Reservation reservation);

    /**
     * Function list all Reservations saved in system.
     * @return list of all reservations.
     */
    List<Reservation> findAllReservations();

    /**
     * Function takes Guest object and return all reservations for given guest.
     *
     * @param guest Guest accomodate in hotel
     * @return All guests in hotel
     */
    List<Reservation> findGuestReservation(Guest guest);

    /**
     * Function takes room object and return all reservations for that room.
     *
     * @param room Room in hotel
     * @return All reservations from given room.
     */
    List<Reservation> findRoomReservation(Room room);

    /**
     * Function finds all empty rooms in hotel and list them.
     * @return List of empty rooms.
     */
    List<Room> findEmptyRoom(LocalDate start, LocalDate end);

    /**
     * With given reservation ID function return reservation for that ID
     *
     * @param reservationID reservation ID
     * @return reservation with ID = reservationID
     */
    Reservation getReservationByID(Long reservationID);
}