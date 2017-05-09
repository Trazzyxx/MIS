package cz.muni.fi.MIS;

/* spring approach */
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import javax.xml.bind.ValidationException;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Created by V.Mecko on 8.3.2017.
 *
 * Overview of how do things work:
 * 1) start terminals.
 * 2) cd %JAVA_HOME%\db\bin for Win or cd %JAVA_HOME$/db/bin for linux
 * 3) startNetworkServer , if it doesnt work because of security reasons
 *     try "startNetworkServer -noSecurityManager"
 *
 * view databaze z intelliJ = view - tool windows database
 * = alt+insert , datasource - derby (remote)
 * = dat URL + credentials
 */
public class Main {
    public static void main(String[] args) throws IOException {

       /*spring-JDBC approach*/
        ApplicationContext context = new AnnotationConfigApplicationContext(SpringConfig.class);
        RoomManager roomManager = context.getBean(RoomManager.class);
        GuestManager guestManager = context.getBean(GuestManager.class);
        ReservationManager reservationManager = context.getBean(ReservationManager.class);
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
}