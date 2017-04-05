package cz.muni.fi.MIS.web;

import cz.muni.fi.MIS.RoomManager;
import cz.muni.fi.MIS.GuestManager;
import cz.muni.fi.MIS.ReservationManager;
import cz.muni.fi.MIS.Main.SpringConfig;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.sql.DataSource;

@WebListener
public class StartListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent ev) {
        System.out.println("Application initialized.");
        ServletContext servletContext = ev.getServletContext();
        ApplicationContext springContext = new AnnotationConfigApplicationContext(SpringConfig.class);
       // servletContext.setAttribute("roomManager",springContext.getBean("roomManager",RoomManager.class));
        servletContext.setAttribute("guestManager",springContext.getBean("guestManager", GuestManager.class));
       // servletContext.setAttribute("reservationManager",springContext.getBean("reservationManager",ReservationManager.class));
        System.out.println("Managers created.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent ev) {
        System.out.println("Application terminated.");
    }
}