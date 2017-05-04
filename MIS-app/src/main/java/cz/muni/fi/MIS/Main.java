package cz.muni.fi.MIS;

/* spring approach */
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import javax.xml.bind.ValidationException;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/* old approach
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import javax.sql.rowset.serial.SerialRef;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
*/
/**
 * Created by V.Mecko on 8.3.2017.
 *
 * Overview of how do things work:
 * 1) start 2 terminals a & b.
 * a1) cd %JAVA_HOME%\db\bin for Win or cd %JAVA_HOME$/db/bin for linux
 * a2) startNetworkServer , if it doesnt work because of security reasons
 *     try "startNetworkServer -noSecurityManager"
 * b1) cd %JAVA_HOME%\db\bin\ or slash instead of reverse slash for linux
 * b2) ij
 * b3) create DB with:
 *      "connect 'jdbc:derby://localhost:1527/MISDB;user=user;password=user;create=true';"
 * b4) CREATE TABLE ... (arg typ PRIMARY KEY GENERATED ALWAYS AS IDENTITY,arg2,arg3,...);
 *
 *
 * ROOM
 CREATE TABLE rooms (roomID BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
 capacity INT,
 roomNumber VARCHAR(30));
 GUEST
 CREATE TABLE guests (guestID BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
 phoneNumber VARCHAR(50),
 address VARCHAR(50),
 fullName VARCHAR(50));
 RESERVATION
 CREATE TABLE reservations(reservationID BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
 startTime DATE,
 endTime DATE,
 roomID_FK BIGINT REFERENCES rooms(roomID) ON DELETE CASCADE,
 guestID_FK BIGINT REFERENCES guests(guestID) ON DELETE CASCADE,
 price DECIMAL
 );
 view databaze z intelliJ = view - tool windows database
 = alt+insert , datasource - derby (remote)
 = dat URL + credentials
 */
public class Main {
    public static void main(String[] args) throws IOException {
        /**
         * embedded DB approach in wexik repo
         */

       /*spring-JDBC approach*/
        ApplicationContext context = new AnnotationConfigApplicationContext(SpringConfig.class);
        RoomManager roomManager = context.getBean(RoomManager.class);
        GuestManager guestManager = context.getBean(GuestManager.class);
        ReservationManager reservationManager = context.getBean(ReservationManager.class);


        Room roomOne = new Room(null, 2,"888");
        roomManager.createRoom(roomOne);
        
        Guest guesto= new Guest(null,"123","Blava","Vlado mec");
        guestManager.createGuest(guesto);
        
        Guest resto = new Guest(null,"333","aaa","resto");
        guestManager.createGuest(resto);
        
        Guest guestJan = new Guest(null, "123 456 7890","Brno 123","Jan Mrkva");

            guestManager.createGuest(guestJan);

        guestJan.setPhoneNumber("111 111");
        guestManager.updateGuest(guestJan);

        BigDecimal one = new BigDecimal(1);

        Reservation reservation = new Reservation(null, LocalDate.now(),LocalDate.now().plusDays(3L),roomOne,guesto,one);
        try{
            reservationManager.createReservation(reservation);
        } catch(ValidationException e){
            e.printStackTrace();
        }
        
        Reservation reservation1 = new Reservation(null, LocalDate.now(),LocalDate.now().plusDays(3L),roomOne,resto,one);
        try{
            reservationManager.createReservation(reservation1);
        } catch(ValidationException e){
            e.printStackTrace();
        }
        
        Reservation reservation2 = new Reservation(null, LocalDate.now(),LocalDate.now().plusDays(3L),roomOne,guestJan,one);
        try{
            reservationManager.createReservation(reservation2);
        } catch(ValidationException e){
            e.printStackTrace();
        }
        
        Reservation reservation3 = new Reservation(null, LocalDate.now(),LocalDate.now().plusDays(3L),roomOne,guestJan,one);
        try{
            reservationManager.createReservation(reservation3);
        } catch(ValidationException e){
            e.printStackTrace();
        }
        


        /** print all info from db **/

        roomManager.findAllRooms().forEach(System.out::println);
        guestManager.listAllGuests().forEach(System.out::println);
        reservationManager.findAllReservations().forEach(System.out::println);

        guestManager.deleteGuest(guestJan);


        guestManager.listAllGuests().forEach(System.out::println);

    }



    /**
     * Configuration for Spring-JDBC
     */
    @Configuration
    @EnableTransactionManagement  //transcation management under methods with tag @Transactional
    @PropertySource("classpath:myconf.properties") //load conf from myconf.properties
    public static class SpringConfig{

        @Autowired
        Environment env;

        @Bean
        public DataSource dataSource(){
            BasicDataSource bds = new BasicDataSource(); //apache connection DB pooling
            bds.setDriverClassName(env.getProperty("jdbc.driver"));
            bds.setUrl(env.getProperty("jdbc.url"));
            bds.setUsername(env.getProperty("jdbc.user"));
            bds.setPassword(env.getProperty("jdbc.password"));
            return bds;
        }

        @Bean
        public PlatformTransactionManager transactionManager(){
            return new DataSourceTransactionManager(dataSource());
        }

        @Bean
        public GuestManager guestManager(){
            return new GuestManagerImpl(dataSource());
        }

        @Bean
        public RoomManager roomManager(){
            return new RoomManagerImpl(dataSource());
        }

        @Bean
        public ReservationManager reservationManager(){
            ReservationManagerImpl reservationManager = new ReservationManagerImpl(dataSource());
            reservationManager.setGuestManager(guestManager());
            reservationManager.setRoomManager(roomManager());
            return reservationManager;

        }
    }

        /* old approach by doing classic JDBC operations
        Properties myconf = new Properties();
        myconf.load(Main.class.getResourceAsStream("/myconf.properties"));
        BasicDataSource ds = new BasicDataSource();
        ds.setUrl(myconf.getProperty("jdbc.url"));
        ds.setUsername(myconf.getProperty("jdbc.user"));
        ds.setPassword(myconf.getProperty("jdbc.password"));
        GuestManager guestManager = new GuestManagerImpl(ds);
        /**
         * testovaci vypis
            /*
        List<Guest> allGuests = guestManager.listAllGuests();
        allGuests.forEach(System.out::println);
        try(Connection con = ds.getConnection()) {
            for (String line : Files.readAllLines(Paths.get("src", "main", "resources", "init.sql"))) {
                if(line.trim().isEmpty()) continue;
                if(line.endsWith(";")) line=line.substring(0,line.length()-1);
                //without log for now cause dependency cant be found.
                // log.debug("executing \"{}\"",line);
                //THIS THROWS exception.
                try (PreparedStatement st1 = con.prepareStatement(line)) {
                    st1.execute();
                }
            }
        } catch (SQLException e) {
            System.err.println("Can't get datasource connection in Main method.");
        }
    }*/
}