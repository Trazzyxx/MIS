package cz.muni.fi.MIS.web;

import cz.muni.fi.MIS.Guest;
import cz.muni.fi.MIS.GuestException;
import cz.muni.fi.MIS.GuestManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet for managing books.
 */
@WebServlet(ListServlet.URL_MAPPING + "*")
public class ListServlet extends HttpServlet {

    private static final String LIST_JSP = "/list.jsp";
    public static final String URL_MAPPING = "/guests/";

    private final static Logger log = LoggerFactory.getLogger(ListServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        showGuestsList(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        String action = request.getPathInfo();
        switch (action) {
            case "/add":
                //load of POST parameters from db
                String fullName = request.getParameter("fullName");
                String address = request.getParameter("address");
                String phoneNumber = request.getParameter("phoneNumber");

                if (fullName == null || fullName.length() == 0 ||
                        address == null || address.length() == 0 ||
                        phoneNumber == null || phoneNumber.length() == 0) {
                    request.setAttribute("error", "You need to fill all fields.");
                    showGuestsList(request, response);
                    return;
                }

                Guest guest = new Guest(null,phoneNumber,address,fullName);
                getGuestManager().createGuest(guest);
                log.debug("created {}",guest);
                //redirect-after-POST - security for multi-send formular
                response.sendRedirect(request.getContextPath()+URL_MAPPING);
                return;
            case "/delete":
                Long guestID = Long.valueOf(request.getParameter("guestID"));
                Guest guestDelete = getGuestManager().getGuestByID(guestID);
                getGuestManager().deleteGuest(guestDelete);
                log.debug("deleted guest with ID {}",guestID);
                response.sendRedirect(request.getContextPath()+URL_MAPPING);
                return;
            case "/edit":
                Long guestForEditID = Long.valueOf(request.getParameter("guestID"));
                Guest guestBeforeEdit = getGuestManager().getGuestByID(guestForEditID);

                String editFullName = request.getParameter("fullName");
                String editAddress = request.getParameter("address");
                String editPhoneNumber = request.getParameter("phoneNumber");

                if(editFullName == null && editAddress == null && editPhoneNumber == null){
                    request.setAttribute("error","To make edit happen, you need to change atleast one field.");
                    showGuestsList(request, response);
                    return;
                }

                if(editFullName != null)  guestBeforeEdit.setFullName(editFullName);
                if(editAddress != null) guestBeforeEdit.setAddress(editAddress);
                if(editPhoneNumber != null) guestBeforeEdit.setPhoneNumber(editPhoneNumber);

                getGuestManager().updateGuest(guestBeforeEdit);
                log.debug("Guest with ID {} edited.",guestBeforeEdit.getGuestID());
                response.sendRedirect(request.getContextPath()+URL_MAPPING);
                return;
            default:
                log.error("Unknown action " + action);
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Unknown action " + action);
        }
    }

    /**
     * Gets GuestManager from ServletContext, where it was stored by {@link StartListener}.
     *
     * @return GuestManager instance
     */
    private GuestManager getGuestManager() {
        return (GuestManager) getServletContext().getAttribute("guestManager");
    }

    /**
     * Stores the list of books to request attribute "books" and forwards to the JSP to display it.
     */
    private void showGuestsList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            try {
                request.setAttribute("guests", getGuestManager().listAllGuests());
                request.getRequestDispatcher(LIST_JSP).forward(request, response);
            }catch(NullPointerException e){
                e.printStackTrace();
            }
    }

}