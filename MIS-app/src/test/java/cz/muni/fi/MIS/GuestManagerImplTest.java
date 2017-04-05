package cz.muni.fi.MIS;

import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.DERBY;

/**
 * Created by V.Mecko on 12.3.2017.
 * TODO : - implement additional test methods for special cases of each method.
 */
public class GuestManagerImplTest {

    private GuestManagerImpl guestManager;
    private EmbeddedDatabase db;

    @Before
    public void setUp() throws Exception {
        db = new EmbeddedDatabaseBuilder().setType(DERBY).addScript("init.sql").build();
        guestManager = new GuestManagerImpl(db);

    }

    /**
     * createGuest method tests.
     */

    @After
    public void tearDown() throws Exception {
        db.shutdown();
    }

    @Test(expected = NullPointerException.class)
    public void testCreateGuestWithNull() throws Exception {
        guestManager.createGuest(null);
    }

    @Test
    public void testCreateGuestWithID() throws Exception {
        Guest guest = new Guest();
        guest.setFullName("Jan Mrkva");
        guest.setAddress("Žilina");
        guest.setPhoneNumber("0900 100 500");
        guestManager.createGuest(guest);

        Guest guestFromManager = guestManager.getGuestByID(guest.getGuestID());

        assertNotNull(guestFromManager);
        /**
         * Junit way of comparing variables from manager and original object
         * For assertJ way of comparing variables see similar method in RoomManagerImplTest.
         */
        assertEquals(guest.getGuestID(),guestFromManager.getGuestID());
        assertEquals(guest.getAddress(),guestFromManager.getAddress());
        assertEquals(guest.getPhoneNumber(),guestFromManager.getPhoneNumber());
        assertEquals(guest.getFullName(),guestFromManager.getFullName());
    }

    /**
     * updateGuest method tests.
     */

    @Test(expected = NullPointerException.class)
    public void testUpdateGuestWithNull() throws Exception{
        guestManager.updateGuest(null);
    }

    @Test
    public void testUpdateGuestWithNewPhone() throws Exception{
        Guest guest = new Guest();
        guest.setFullName("Jan Mrkva");
        guest.setAddress("Žilina");
        guest.setPhoneNumber("123");
        guestManager.createGuest(guest); //we need to use this method to insert guest to GuestManager

        Guest guestFromManager = guestManager.getGuestByID(guest.getGuestID());

        assertThat(guestFromManager).isNotNull();

        //phoneNumber created is correct
        assertThat(guestFromManager.getPhoneNumber())
                .isEqualTo(guest.getPhoneNumber());

        String PHONE_NUMBER = "321";
        guest.setPhoneNumber(PHONE_NUMBER);

        guestManager.updateGuest(guest);
        guestFromManager = guestManager.getGuestByID(guest.getGuestID());
        assertThat(guestFromManager.getPhoneNumber(),is(equalTo(PHONE_NUMBER)));

    }


    /**
     * deleteGuest method tests.
     */
    @Test
    /*
     * test which determines if the guest was deleted (correctly).
     * also determines if the other guests stayed in the list.
     */
    public void testDeleteGuest() throws Exception{
        Guest guestJozef = new Guest();
        Guest guestJan = new Guest();

        guestJozef.setFullName("Jozef Karotka");
        guestJozef.setAddress("Bratislava");
        guestJozef.setPhoneNumber("789");

        guestJan.setFullName("Jan Mrkva");
        guestJan.setAddress("Žilina");
        guestJan.setPhoneNumber("123");

        guestManager.createGuest(guestJozef);
        guestManager.createGuest(guestJan);

        Guest guestJozefFromManager = guestManager.getGuestByID(guestJozef.getGuestID());
        Guest guestJanFromManager = guestManager.getGuestByID(guestJan.getGuestID());

        assertThat(guestJozefFromManager)
                .isNotNull();
        assertThat(guestJanFromManager)
                .isNotNull();

        guestManager.deleteGuest(guestJozefFromManager);
        guestJozefFromManager = guestManager.getGuestByID(guestJozefFromManager.getGuestID());

        assertThat(guestJozefFromManager).isNull();
        assertThat(guestJanFromManager)
                .isNotNull();
    }

    /**
     *  getGuestByID method tests.
     */
    @Test
    public void testGetGuestByID() throws Exception{
        Guest guest = new Guest();
        guest.setFullName("Jan Mrkva");
        guest.setAddress("Žilina");
        guest.setPhoneNumber("123");

        guestManager.createGuest(guest);

        assertThat(guestManager.getGuestByID(guest.getGuestID()))
                .isNotNull()
                .isEqualToComparingFieldByField(guest);
    }

    /**
     * findGuestByName method tests.
     */
    @Test
    /*
     * Testing if we wont get empty list
     * and if the findGuestByName method
     * return List of Guests.
     */
    public void testFindGuestByName() throws Exception{
        Guest guestJan = new Guest();
        guestJan.setFullName("Jan Mrkva");
        guestJan.setAddress("Žilina");
        guestJan.setPhoneNumber("123");

        Guest guestPavol = new Guest();
        guestPavol.setFullName("Pavol Mrkva");
        guestPavol.setAddress("Bratislava");
        guestPavol.setPhoneNumber("789");

        guestManager.createGuest(guestJan);
        guestManager.createGuest(guestPavol);

        Guest guestJanFromManager = guestManager.getGuestByID(guestJan.getGuestID());
        Guest guestPavolFromManager = guestManager.getGuestByID(guestPavol.getGuestID());
        assertThat(guestJanFromManager)
                .isNotNull();
        assertThat(guestPavolFromManager)
                .isNotNull();

        List<Guest> hotelGuests = new ArrayList<Guest>();
        hotelGuests = guestManager.findGuestByName("Jan Mrkva");
        assertThat(hotelGuests)
                .isNotEmpty()
                .containsOnly(guestJan);
    }

    /**
     * listAllGuests method tests.
     */
    @Test
    public void testListAllGuests() throws Exception{
        Guest guestJan = new Guest();
        guestJan.setFullName("Jan Mrkva");
        guestJan.setAddress("Žilina");
        guestJan.setPhoneNumber("123");

        Guest guestPavol = new Guest();
        guestPavol.setFullName("Pavol Mrkva");
        guestPavol.setAddress("Bratislava");
        guestPavol.setPhoneNumber("789");

        guestManager.createGuest(guestJan);
        guestManager.createGuest(guestPavol);

        Guest guestJanFromManager = guestManager.getGuestByID(guestJan.getGuestID());
        Guest guestPavolFromManager = guestManager.getGuestByID(guestPavol.getGuestID());

        assertThat(guestJanFromManager)
                .isNotNull();
        assertThat(guestPavolFromManager)
                .isNotNull();

        assertThat(guestManager.listAllGuests())
                .isNotEmpty()
                .isNotNull()
                .usingFieldByFieldElementComparator()
                .containsOnly(guestJan,guestPavol);
    }
}