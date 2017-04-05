package cz.muni.fi.MIS;

/**
 * Created by V.Mecko on 8.3.2017.
 */
public class Guest {

    private Long guestID;
    private String phoneNumber;
    private String address;
    private String fullName;

    public Guest() {}

    public Guest(Long guestID, String phoneNumber, String address, String fullName){
        this.guestID = guestID;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.fullName = fullName;
    }

    public Guest(Guest guest){
        this.setGuestID(guest.getGuestID());
        this.setPhoneNumber(guest.getPhoneNumber());
        this.setAddress(guest.getAddress());
        this.setFullName(guest.getFullName());
    }

    public Long getGuestID() {
        return guestID;
    }

    public void setGuestID(Long guestID) {
        this.guestID = guestID;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Guest guest = (Guest) o;

        if (!guestID.equals(guest.guestID)) return false;
        if (phoneNumber != null ? !phoneNumber.equals(guest.phoneNumber) : guest.phoneNumber != null) return false;
        if (!address.equals(guest.address)) return false;
        return fullName.equals(guest.fullName);
    }

    @Override
    public int hashCode() {
        int hash = 7 * guestID.hashCode();
        hash = 31 * hash + (phoneNumber != null ? phoneNumber.hashCode() : 0); //if phoneNumber is null then we add 0
        hash = 31 * hash + address.hashCode();
        hash = 31 * hash + fullName.hashCode();
        return hash;
    }

    @Override
    public String toString(){
        return "Guest ID:" + guestID + " " + fullName + " from " + address + " has phoneNumber " + phoneNumber + ".";
    }
}