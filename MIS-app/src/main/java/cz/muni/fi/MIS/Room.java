package cz.muni.fi.MIS;

/**
 * Created by V.Mecko on 8.3.2017.
 */
public class Room {
    private Long roomID;
    private Integer capacity;
    private String roomNumber;

    public Room() {}

    public Room(Long roomID, Integer capacity, String roomNumber){
        this.roomID = roomID;
        this.capacity = capacity;
        this.roomNumber = roomNumber;
    }

    public Room(Room room){
        this.setRoomID(room.getRoomID());
        this.setCapacity(room.getCapacity());
        this.setRoomNumber(room.getRoomNumber());
    }

    public Long getRoomID(){
        return roomID;
    }

    public void setRoomID(Long ID){
        roomID = ID;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Room room = (Room) o;

        if (!roomID.equals(room.roomID)) return false;
        if (!capacity.equals(room.capacity)) return false;
        return (roomNumber.equals(room.roomNumber));
    }

    @Override
    public int hashCode() {
        int hash = 7 * capacity.hashCode();
        hash = 31 * hash + roomID.hashCode();
        hash = 31 * hash + roomNumber.hashCode();
        return hash;
    }

    @Override
    public String toString(){
        return "Room ID:" + roomID + " is room " + roomNumber + " with capacity of " + capacity;
    }
}