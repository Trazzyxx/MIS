package cz.muni.fi.gui;

import cz.muni.fi.MIS.Room;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by V.Mecko on 3.5.2017.
 */

public class RoomsTableModel extends AbstractTableModel {
    private List<Room> rooms = new ArrayList<Room>();
    public static final int ROOM_COLUMN = 3;

    @Override
    public int getRowCount(){
        return rooms.size();
    }

    @Override
    public int getColumnCount(){
        return ROOM_COLUMN;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex){
        Room room = rooms.get(rowIndex);
        switch(columnIndex){
            case 0:
                return room.getRoomID();
            case 1:
                return room.getCapacity()
            case 2:
                return room.getRoomNumber();
            default:
                throw new IllegalArgumentException("worked with bad columnIndex.");
        }
    }
}
