package cz.muni.fi.MIS;

import java.util.List;

/**
 * Created by V.Mecko on 8.3.2017.
 */
public interface GuestManager {
    void createGuest(Guest guest);

    void updateGuest(Guest guest);

    void deleteGuest(Guest guest);

    /**
     * function list all Guests from hotel.
     *
     * @return list of guests accomodate in hotel
     */
    List<Guest> listAllGuests();

    /**
     *
     * @param guestID ID of guest
     * @return Guest with given ID
     */
    Guest getGuestByID(Long guestID);

    List<Guest> findGuestByName(String fullName);
    
    /**
     * @brief this method is used in GUI of app to determine unique guests without working with id.
     * 
     * @param fullName of guest
     * @param phoneNum of guest
     * @return unique guest
     */
    Guest getGuestByNameAndPhoneNum(String fullName, String phoneNum);
}