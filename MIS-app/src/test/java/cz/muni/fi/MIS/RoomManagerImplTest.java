package cz.muni.fi.MIS;

import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

import java.util.Properties;

import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.DERBY;

/**
 * Created by D.Veliky on 15.3.2017.
 * TODO : - implement additional test methods for special cases of each method.
 */
public class RoomManagerImplTest {

    private RoomManagerImpl roomManager;
    private EmbeddedDatabase db;

    @Before
    public void setUp() throws Exception {
        db = new EmbeddedDatabaseBuilder().setType(DERBY).addScript("init.sql").build();
        roomManager = new RoomManagerImpl(db);
    }

    @After
    public void tearDown() throws Exception {
        db.shutdown();
    }

    /**
     * createRoom method tests.
     */
    @Test(expected = NullPointerException.class)
    public void testCreateRoomWithNull() throws Exception {
        roomManager.createRoom(null);
    }

    @Test
    //Tests whether the room with ID was created.
    public void testCreateRoomWithID() throws Exception {
        Room room = new Room();
        room.setCapacity(2);
        room.setRoomNumber("123");

        roomManager.createRoom(room);
        Room roomFromManager = roomManager.getRoomByID(room.getRoomID());

        assertThat(roomFromManager)
                .isNotNull()
                .isNotSameAs(room)
                .isEqualToComparingFieldByField(room);
    }

    /**
     * updateRoom method tests.
     */
    @Test(expected = NullPointerException.class)
    public void testUpdateRoomWithNull() throws Exception{
        roomManager.updateRoom(null);
    }

    @Test
    public void testUpdateRoomWithNewCapacity() throws Exception{
        Room room = new Room();
        room.setCapacity(2);
        room.setRoomNumber("1");

        roomManager.createRoom(room);

        Room roomFromManager = roomManager.getRoomByID(room.getRoomID());

        assertThat(roomFromManager)
                .isNotNull();

        assertThat(roomFromManager.getCapacity())
                .isNotNull()
                .isNotNegative()
                .isEqualTo(room.getCapacity());

        room.setCapacity(3);
        roomManager.updateRoom(room);

        roomFromManager = roomManager.getRoomByID(room.getRoomID());
        assertThat(roomFromManager.getCapacity())
                .isEqualTo(room.getCapacity());
    }

    /**
     * deleteRoom method tests.
     */
    @Test
    // Tests whether the room was deleted.
    public void testDeleteRoom() throws Exception {
        Room roomOne = new Room();
        Room roomTwo = new Room();

        roomOne.setRoomNumber("1");
        roomOne.setCapacity(2);

        roomTwo.setRoomNumber("2");
        roomTwo.setCapacity(3);

        roomManager.createRoom(roomOne);
        roomManager.createRoom(roomTwo);

        Room roomOneFromManager = roomManager.getRoomByID(roomOne.getRoomID());
        Room roomTwoFromManager = roomManager.getRoomByID(roomTwo.getRoomID());

        assertThat(roomOneFromManager)
                .isNotNull();
        assertThat(roomTwoFromManager)
                .isNotNull();

        roomManager.deleteRoom(roomTwo);
        roomOneFromManager = roomManager.getRoomByID(roomOneFromManager.getRoomID());
        roomTwoFromManager = roomManager.getRoomByID(roomTwoFromManager.getRoomID());

        assertThat(roomTwoFromManager)
                .isNull();
        assertThat(roomOneFromManager)
                .isNotNull();
    }

    /**
     * getRoomByID method tests.
     */
    @Test
    //Tests whether a room is in list and has correct ID.
    public void testGetRoomByID() throws Exception {
        Room room = new Room();

        room.setRoomNumber("1");
        room.setCapacity(2);

        roomManager.createRoom(room);

        Room roomFromManager = roomManager.getRoomByID(room.getRoomID());

        assertThat(roomFromManager)
                .isNotNull()
                .isEqualToComparingFieldByField(room);
    }

    /**
     * findAllRooms method tests.
     */
    @Test
    public void findAllRooms() throws Exception{
        Room roomOne = new Room();
        Room roomTwo = new Room();

        roomOne.setCapacity(2);
        roomOne.setRoomNumber("1");

        roomTwo.setCapacity(3);
        roomTwo.setRoomNumber("2");

        roomManager.createRoom(roomOne);
        roomManager.createRoom(roomTwo);

        assertThat(roomManager.getRoomByID(roomOne.getRoomID()))
                .isNotNull();
        assertThat(roomManager.getRoomByID(roomTwo.getRoomID()))
                .isNotNull();

        assertThat(roomManager.findAllRooms())
                .isNotNull()
                .isNotEmpty()
                .usingFieldByFieldElementComparator()
                .containsOnly(roomOne,roomTwo);
    }
}