package cz.muni.fi.MIS;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Created by V.Mecko on 8.3.2017.
 */
public class Reservation {
    private Long reservationID;
    private LocalDate startTime;
    private LocalDate endTime;
    private Room room;
    private Guest guest;
    private BigDecimal price;

    public Reservation(){}

    public Reservation(Long reservationID, LocalDate startTime, LocalDate endTime, Room room, Guest guest, BigDecimal price){
        this.reservationID = reservationID;
        this.startTime = startTime;
        this.endTime = endTime;
        this.room = room;
        this.guest = guest;
        this.price = price;
    }

    public Reservation(Reservation reservation){
        this.setReservationID(reservation.getReservationID());
        this.setStartTime(reservation.getStartTime());
        this.setEndTime(reservation.getEndTime());
        this.setRoom(reservation.getRoom());
        this.setGuest(reservation.getGuest());
        this.setPrice(reservation.getPrice());
    }

    public Long getReservationID() {
        return reservationID;
    }

    public void setReservationID(Long reservationID) {
        this.reservationID = reservationID;
    }

    public LocalDate getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDate startTime) {
        this.startTime = startTime;
    }

    public LocalDate getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDate endTime) {
        this.endTime = endTime;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public Guest getGuest() {
        return guest;
    }

    public void setGuest(Guest guest) {
        this.guest = guest;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Reservation that = (Reservation) o;

        if (!reservationID.equals(that.reservationID)) return false;
        if (!startTime.equals(that.startTime)) return false;
        if (!endTime.equals(that.endTime)) return false;
        if (!room.equals(that.room)) return false;
        if (!guest.equals(that.guest)) return false;

        /* this might not be accurate (2.0 vs 2.00)
        so we might consider using compareTo()
        which compares "only" their numerical values */
        return price.equals(that.price);
    }

    @Override
    public int hashCode() {
        int hash = 7 * reservationID.hashCode();
        hash = 31 * hash + startTime.hashCode();
        hash = 31 * hash + endTime.hashCode();
        hash = 31 * hash + room.hashCode();
        hash = 31 * hash + guest.hashCode();
        hash = 31 * hash + price.hashCode();
        return hash;
    }

    @Override
    public String toString(){
        return "reservation ID:" + reservationID + " from " + startTime + " to " + endTime + "is about:\n"
                + "Guest:" + guest.toString() + "\n"
                + "Room:" + room.toString() + "\n"
                + "for " + price +" eur.";
    }
}