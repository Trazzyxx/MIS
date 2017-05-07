package cz.muni.fi.MIS;

import java.util.List;

/**
 * Created by V.Mecko on 8.3.2017.
 */
public interface RoomManager {
    void createRoom(Room room);

    void updateRoom(Room room);

    void deleteRoom(Room room);

    /**
     * function list room with given ID
     *
     * @param roomID ID of room
     * @return room object for given ID.
     */
    Room getRoomByID(Long roomID);
    
    /**
     * 
     * @param number number of Room
     * @return room object for given number
     */
    Room getRoomByNumber(String number);

    /**
     * function list all Rooms in saved in system.
     *
     * @return list of all rooms.
     */
    List<Room> findAllRooms();
}