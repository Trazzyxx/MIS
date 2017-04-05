package cz.muni.fi.MIS;

import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

import javax.xml.bind.ValidationException;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.DERBY;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Properties;


/**
 * Created by V.Mecko on 12.3.2017.
 * TODO : - implement improvements for findEmptyRoom method.
 * TODO : - implement argument condition for attributes inside of reservation object
 * TODO : - implement additional test methods for special cases of each method.
 *
 * Notes: @22.03.2017
 *        - its good to not use both libraries for testing (assertJ,unitJ)
 *        - its ok to put Guest/Room init to setUp() method.
 *
 */

public class ReservationManagerImplTest {
    private ReservationManagerImpl reservationManager;
    private GuestManager guestManager;
    private RoomManager roomManager;

    private EmbeddedDatabase db;
    @Before
    public void setUp() throws Exception {
        db = new EmbeddedDatabaseBuilder().setType(DERBY).addScript("init.sql").build();
        reservationManager = new ReservationManagerImpl(db);
        guestManager = new GuestManagerImpl(db);
        roomManager = new RoomManagerImpl(db);
        reservationManager.setGuestManager(guestManager);
        reservationManager.setRoomManager(roomManager);
    }

    @After
    public void tearDown() throws Exception {
        db.shutdown();
    }

    /**
     * createReservation method tests.
     */
    @Test(expected = NullPointerException.class)
    public void testCreateReservationWithNull() throws Exception {
        reservationManager.createReservation(null);
    }

    @Test
    public void testCreateReservationNormal() throws Exception {
        Reservation reservation = new Reservation();
        BigDecimal one = new BigDecimal(1);
        //guest for reservation
        Guest guest = new Guest();
        guest.setFullName("Jan Mrkva");
        guest.setAddress("Bratislava");
        guest.setPhoneNumber("123");
        //room for reservation
        Room room = new Room();
        room.setCapacity(3);
        room.setRoomNumber("1");

        reservation.setStartTime(LocalDate.now());
        reservation.setEndTime(LocalDate.now().plusDays(3L));
        reservation.setGuest(guest);
        reservation.setRoom(room);
        reservation.setPrice(one);
        roomManager.createRoom(room);
        guestManager.createGuest(guest);
        reservationManager.createReservation(reservation);
        //System.out.println("reservation = " + reservation);

        Reservation reservationFromManager = reservationManager.getReservationByID(reservation.getReservationID());
        //System.out.println("reservationFromManager = " + reservationFromManager);

        assertThat(reservationFromManager)
                .isNotNull()
                .isEqualToComparingFieldByFieldRecursively(reservation);
    }

    @Test(expected = ValidationException.class)
    public void testCreateReservationStartLaterThanEnd() throws Exception {
        Reservation reservation = new Reservation();
        BigDecimal one = new BigDecimal(1);
        //guest for reservation
        Guest guest = new Guest();
        guest.setFullName("Jan Mrkva");
        guest.setAddress("Bratislava");
        guest.setPhoneNumber("123");
        //room for reservation
        Room room = new Room();
        room.setCapacity(3);
        room.setRoomNumber("1");

        reservation.setStartTime(LocalDate.now().plusDays(3L));
        reservation.setEndTime(LocalDate.now());
        reservation.setGuest(guest);
        reservation.setRoom(room);
        reservation.setPrice(one);

        roomManager.createRoom(room);
        guestManager.createGuest(guest);

        reservationManager.createReservation(reservation);
    }


    /**
     * updateReservation method tests.
     */
    @Test(expected = NullPointerException.class)
    public void testUpdateReservationWithNull(){
        reservationManager.updateReservation(null);
    }

    @Test
    public void testUpdateReservationWithNewGuest() throws Exception{
        Reservation reservation = new Reservation();
        BigDecimal one = new BigDecimal(1);
        //guest for reservation
        Guest guest = new Guest();
        guest.setFullName("Jan Mrkva");
        guest.setAddress("Bratislava");
        guest.setPhoneNumber("123");
        //guest for update
        Guest guestUpdate = new Guest();
        guestUpdate.setFullName("Jana Mrkvova");
        guestUpdate.setAddress("Bratislava");
        guestUpdate.setPhoneNumber("789");
        //room for reservation
        Room room = new Room();
        room.setCapacity(3);
        room.setRoomNumber("1");

        reservation.setStartTime(LocalDate.now());
        reservation.setEndTime(LocalDate.now().plusDays(3L));
        reservation.setGuest(guest);
        reservation.setRoom(room);
        reservation.setPrice(one);

        guestManager.createGuest(guest);
        guestManager.createGuest(guestUpdate);
        roomManager.createRoom(room);

        reservationManager.createReservation(reservation);

        Reservation resFromManager = reservationManager.getReservationByID(reservation.getReservationID());
        assertThat(resFromManager)
                .isNotNull();

        reservation.setGuest(guestUpdate);
        reservationManager.updateReservation(reservation);


        resFromManager = reservationManager.getReservationByID(reservation.getReservationID());
        assertThat(resFromManager)
                .isNotNull()
                .isEqualToComparingFieldByField(reservation);
    }

    /**
     * deleteReservation method tests.
     */
    @Test
    public void testDeleteReservation() throws Exception{
        Reservation reservation1 = new Reservation();
        Reservation reservation2 = new Reservation();
        BigDecimal one = new BigDecimal(1);

        //guests
        Guest guestJozef = new Guest();
        Guest guestJan = new Guest();

        guestJozef.setFullName("Jozef Karotka");
        guestJozef.setAddress("Bratislava");
        guestJozef.setPhoneNumber("789");

        guestJan.setFullName("Jan Mrkva");
        guestJan.setAddress("Žilina");
        guestJan.setPhoneNumber("123");

        //rooms
        Room roomOne = new Room();
        Room roomTwo = new Room();

        roomOne.setRoomNumber("1");
        roomOne.setCapacity(2);

        roomTwo.setRoomNumber("2");
        roomTwo.setCapacity(3);

        reservation1.setGuest(guestJozef);
        reservation1.setRoom(roomOne);
        reservation1.setStartTime(LocalDate.now());
        reservation1.setEndTime(LocalDate.now().plusDays(4L));
        reservation1.setPrice(one);

        reservation2.setGuest(guestJan);
        reservation2.setPrice(one);
        reservation2.setRoom(roomTwo);
        reservation2.setStartTime(LocalDate.now());
        reservation2.setEndTime(LocalDate.now().plusDays(3L));

        reservationManager.createReservation(reservation1);
        reservationManager.createReservation(reservation2);

        Reservation res1FromManager = reservationManager.getReservationByID(reservation1.getReservationID());
        Reservation res2FromManager = reservationManager.getReservationByID(reservation2.getReservationID());

        assertThat(res1FromManager)
                .isNotNull();
        assertThat(res2FromManager)
                .isNotNull();

        reservationManager.deleteReservation(reservation1);

        res1FromManager = reservationManager.getReservationByID(reservation1.getReservationID());

        assertThat(res1FromManager)
                .isNull();
        assertThat(res2FromManager)
                .isNotNull();

    }

    /**
     * findAllReservations method tests.
     */
     /*
     * Testing if we wont get empty arraylist
     * from running method findAllReservations.
     */
    @Test
    public void testFindAllReservations() throws Exception {
        assertThat(reservationManager.findAllReservations()).isEmpty();

        Reservation reservation1 = new Reservation();
        Reservation reservation2 = new Reservation();
        BigDecimal one = new BigDecimal(1);
        reservation1.setPrice(one);
        reservation2.setPrice(one);

        // Guest set up
        Guest guestJan = new Guest();
        guestJan.setFullName("Jan");
        guestJan.setPhoneNumber("123");
        guestJan.setAddress("xyz");

        reservation1.setGuest(guestJan);
        reservation2.setGuest(guestJan);

        //room set up
        Room room = new Room();
        room.setCapacity(2);
        room.setRoomNumber("1");

        reservation1.setRoom(room);
        reservation2.setRoom(room);

        reservation1.setStartTime(LocalDate.now());
        reservation2.setStartTime(LocalDate.now());

        reservation1.setEndTime(LocalDate.now().plusDays(3L));
        reservation2.setEndTime(LocalDate.now().plusDays(4L));

        //now we add reservations to manager
        guestManager.createGuest(guestJan);
        roomManager.createRoom(room);
        reservationManager.createReservation(reservation1);
        reservationManager.createReservation(reservation2);

        assertThat(reservationManager.getReservationByID(reservation1.getReservationID()))
                .isNotNull();
        assertThat(reservationManager.getReservationByID(reservation2.getReservationID()))
                .isNotNull();

        assertThat(reservationManager.findAllReservations())
                .isNotNull()
                .isNotEmpty()
                .usingFieldByFieldElementComparator()
                .containsOnly(reservation1,reservation2);


    }

    /**
     *getReservationByID method tests.
     */
    @Test
    public void testGetReservationByID() throws Exception{
        Reservation reservation = new Reservation();
        BigDecimal one = new BigDecimal(1);
        //guest for reservation
        Guest guest = new Guest();
        guest.setFullName("Jan Mrkva");
        guest.setAddress("Bratislava");
        guest.setPhoneNumber("123");
        //room for reservation
        Room room = new Room();
        room.setCapacity(3);
        room.setRoomNumber("1");

        reservation.setStartTime(LocalDate.now());
        reservation.setEndTime(LocalDate.now().plusDays(3L));
        reservation.setGuest(guest);
        reservation.setRoom(room);
        reservation.setPrice(one);

        guestManager.createGuest(guest);
        roomManager.createRoom(room);
        reservationManager.createReservation(reservation);

        Reservation resFromManager = reservationManager.getReservationByID(reservation.getReservationID());
        assertThat(resFromManager)
                .isNotNull()
                .isEqualToComparingFieldByField(reservation);

    }

    /**
     * findGuestReservation method tests.
     */
    @Test
    public void testFindGuestReservation() throws Exception{
        Reservation reservation1 = new Reservation();
        Reservation reservation2 = new Reservation();
        BigDecimal one = new BigDecimal(1);
        reservation1.setPrice(one);
        reservation2.setPrice(one);


        // Guest set up
        Guest guestJan = new Guest();
        guestJan.setFullName("Jan Mrkva");
        guestJan.setPhoneNumber("123");
        guestJan.setAddress("xyz");

        Guest guestPavol = new Guest();
        guestPavol.setFullName("Pavol Mrkva");
        guestPavol.setPhoneNumber("789");
        guestPavol.setAddress("Bratislava");

        reservation1.setGuest(guestJan);
        reservation2.setGuest(guestPavol);

        //room set up
        Room room = new Room();
        room.setCapacity(2);
        room.setRoomNumber("1");

        Room roomTwo = new Room();
        roomTwo.setCapacity(3);
        room.setRoomNumber("2");

        reservation1.setRoom(room);
        reservation2.setRoom(room);

        reservation1.setStartTime(LocalDate.now());
        reservation2.setStartTime(LocalDate.now());

        reservation1.setEndTime(LocalDate.now().plusDays(3L));
        reservation2.setEndTime(LocalDate.now().plusDays(4L));

        guestManager.createGuest(guestJan);
        guestManager.createGuest(guestPavol);
        roomManager.createRoom(room);

        reservationManager.createReservation(reservation2);


        //we check if the guest dont have any reservation created yet.
        assertThat(reservationManager.findGuestReservation(guestJan))
                .isNotNull()
                .isEmpty();

        reservationManager.createReservation(reservation1);

        assertThat(reservationManager.getReservationByID(reservation1.getReservationID()))
                .isNotNull();
        assertThat(reservationManager.getReservationByID(reservation2.getReservationID()))
                .isNotNull();

        assertThat(reservationManager.findGuestReservation(guestJan))
                .isNotNull()
                .containsOnly(reservation1);

        assertThat(reservationManager.findGuestReservation(guestPavol))
                .isNotNull()
                .containsOnly(reservation2);

    }

    /**
     * findRoomReservation method tests.
     */
    @Test
    public void testFindRoomReservation() throws Exception{
        Reservation reservation1 = new Reservation();
        Reservation reservation2 = new Reservation();
        BigDecimal one = new BigDecimal(1);
        reservation1.setPrice(one);
        reservation2.setPrice(one);


        // Guest set up
        Guest guestJan = new Guest();
        guestJan.setFullName("Jan Mrkva");
        guestJan.setPhoneNumber("123");
        guestJan.setAddress("xyz");

        Guest guestPavol = new Guest();
        guestPavol.setFullName("Pavol Mrkva");
        guestPavol.setPhoneNumber("789");
        guestPavol.setAddress("Bratislava");

        reservation1.setGuest(guestJan);
        reservation2.setGuest(guestPavol);

        //room set up
        Room room = new Room();
        room.setCapacity(2);
        room.setRoomNumber("1");

        reservation1.setRoom(room);
        reservation2.setRoom(room);

        reservation1.setStartTime(LocalDate.now().plusMonths(2L));
        reservation1.setEndTime(LocalDate.now().plusMonths(3L).plusDays(3L));

        reservation2.setStartTime(LocalDate.now());
        reservation2.setEndTime(LocalDate.now().plusDays(4L));

        roomManager.createRoom(room);
        guestManager.createGuest(guestJan);
        guestManager.createGuest(guestPavol);

        //we check if the room doesnt have any reservation created yet.
        assertThat(reservationManager.findRoomReservation(room))
                .isNotNull()
                .isEmpty();

        //now we add reservations to manager
        reservationManager.createReservation(reservation1);
        reservationManager.createReservation(reservation2);

        assertThat(reservationManager.getReservationByID(reservation1.getReservationID()))
                .isNotNull();
        assertThat(reservationManager.getReservationByID(reservation2.getReservationID()))
                .isNotNull();

        assertThat(reservationManager.findRoomReservation(room))
                .isNotNull()
                .containsOnly(reservation1,reservation2);
    }

    /**
     * findEmptyRoom method tests.
     */
    @Test
    public void testFindEmptyRoomForToday() throws Exception{
        Reservation reservation = new Reservation();
        BigDecimal one = new BigDecimal(1);
        //guest for reservation
        Guest guest = new Guest();
        guest.setFullName("Jan Mrkva");
        guest.setAddress("Bratislava");
        guest.setPhoneNumber("123");
        //room for reservation
        Room room = new Room();
        room.setCapacity(3);
        room.setRoomNumber("1");

        Room roomfree = new Room();
        roomfree.setRoomNumber("123");
        roomfree.setCapacity(2);

        Room roomfree1 = new Room();
        roomfree1.setRoomNumber("321");
        roomfree1.setCapacity(1);

        reservation.setStartTime(LocalDate.now());
        reservation.setEndTime(LocalDate.now().plusDays(3L));
        reservation.setGuest(guest);
        reservation.setRoom(room);
        reservation.setPrice(one);

        roomManager.createRoom(room);
        roomManager.createRoom(roomfree);
        roomManager.createRoom(roomfree1);
        guestManager.createGuest(guest);
        reservationManager.createReservation(reservation);

        assertThat(reservationManager.findEmptyRoom(LocalDate.now(),LocalDate.now()))
                .isNotNull()
                .isNotEmpty()
                .containsOnly(roomfree,roomfree1);
    }

}