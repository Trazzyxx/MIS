package cz.muni.fi.gui;
import cz.muni.fi.MIS.Guest;
import cz.muni.fi.MIS.GuestManager;
import cz.muni.fi.MIS.Main;
import cz.muni.fi.MIS.Reservation;
import cz.muni.fi.MIS.ReservationManager;
import cz.muni.fi.MIS.Room;
import cz.muni.fi.MIS.RoomManager;
import java.math.BigDecimal;
import java.time.LocalDate;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.SwingWorker;
import javax.xml.bind.ValidationException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Created by V.Mecko on 3.5.2017.
 */
public class ReservationTableModel extends AbstractTableModel {
    private static final ResourceBundle texts = ResourceBundle.getBundle("texts");
    public static final int RSV_COLUMNS = 9;
    
    protected ApplicationContext ctx;
    protected ReservationManager reservationManager;
    
    protected List<Reservation> reservations = new ArrayList<>();

    public ReservationTableModel() {
        ctx = new AnnotationConfigApplicationContext(Main.SpringConfig.class); 
        reservationManager = ctx.getBean(texts.getString("reservationManager"), ReservationManager.class);
        
        RetrieveSwingWorker retrieveSwingWorker = new RetrieveSwingWorker();
        retrieveSwingWorker.execute();
    }
    
    private class CreateSwingWorker extends SwingWorker<Void,Void> {
        private Reservation reservation;
        
        public CreateSwingWorker(Reservation reservation) {
            this.reservation = reservation;
        }
        
        @Override    
        protected Void doInBackground() throws Exception {
            reservationManager.createReservation(reservation);
            reservations.add(reservation);
            return null;
        }
        
        @Override    
        protected void done() {
            int lastRow = getRowCount() - 1;
            fireTableRowsInserted(lastRow, lastRow);
        }
    }
    
    private class RetrieveSwingWorker extends SwingWorker<Void,Void> {        
        @Override    
        protected Void doInBackground() throws Exception {
            reservations = reservationManager.findAllReservations();
            return null;
        }
        
        @Override    
        protected void done() {
            fireTableRowsInserted(0, getRowCount() - 1);
        }
    }
    
    private class UpdateSwingWorker extends SwingWorker<Void,Void> {
        private Reservation reservation;
        private int rowIndex;
        private int columnIndex;
        
        public UpdateSwingWorker(Reservation reservation, int rowIndex, int columnIndex) {
            this.reservation=reservation;
            this.rowIndex = rowIndex;
            this.columnIndex = columnIndex;
        }
        
        @Override    
        protected Void doInBackground() throws Exception {
            reservationManager.updateReservation(reservation);
            reservations.set(rowIndex, reservation);
            return null;
        }
        
        @Override    
        protected void done() {
            fireTableCellUpdated(rowIndex, columnIndex);
        }
    }
    
    private class DeleteSwingWorker extends SwingWorker<Void,Void> {        
        private int row;
        
        public DeleteSwingWorker(int row) {
            this.row = row;
        }
        
        @Override    
        protected Void doInBackground() throws Exception {
            Reservation resForDelete = reservations.get(row);
            reservations.remove(row);
            reservationManager.deleteReservation(resForDelete);
            return null;
        }
        
        @Override    
        protected void done() {
            fireTableRowsDeleted(row, row);
        }
    }
 
    @Override
    public int getRowCount() {
        return reservations.size();
    }
 
    @Override
    public int getColumnCount() {
        return RSV_COLUMNS;
    }
 
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Reservation res= reservations.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return res.getReservationID();
            case 1:
                return res.getStartTime();
            case 2:
                return res.getEndTime();
            case 3:
                return res.getRoom().getCapacity();
            case 4:
                return res.getRoom().getRoomNumber();
            case 5:
                return res.getGuest().getPhoneNumber();
            case 6:
                return res.getGuest().getAddress();
            case 7:
                return res.getGuest().getFullName();
            case 8:
                return res.getPrice();
            default:
                throw new IllegalArgumentException(texts.getString("working with bad column index."));
        }
    }
    
    @Override
    public String getColumnName(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return texts.getString("id");
            case 1:
                return texts.getString("startTime");
            case 2:
                return texts.getString("endTime");
            case 3:
                return texts.getString("capacity");
            case 4:
                return texts.getString("roomNumber");
            case 5:
                return texts.getString("phoneNumber");
            case 6:
                return texts.getString("address");
            case 7:
                return texts.getString("fullname");
            case 8:
                return texts.getString("price");
            default:
                throw new IllegalArgumentException(texts.getString("working with bad column index."));
        }
    }
    
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return Long.class;
            case 1:
                return LocalDate.class;
            case 2:
                return LocalDate.class;
            case 3:
                return Integer.class;
            case 4:
                return String.class;
            case 5:
                return String.class;
            case 6:
                return String.class;
            case 7:
                return String.class;
            case 8:
                return BigDecimal.class;
            default:
                throw new IllegalArgumentException(texts.getString("working with bad column index."));
        }
    }
    
    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Reservation res= reservations.get(rowIndex);
        switch (columnIndex) {
            case 0:
                res.setReservationID((Long) aValue);
                break;
            case 1:
                res.setStartTime((LocalDate) aValue);
                break;
            case 2:
                res.setEndTime((LocalDate) aValue);
                break;
            case 3:
                res.getRoom().setCapacity((Integer) aValue);
            case 4:
                res.getRoom().setRoomNumber((String) aValue);
            case 5:
                res.getGuest().setPhoneNumber((String) aValue);
            case 6:
                res.getGuest().setAddress((String) aValue);
            case 7:
                res.getGuest().setFullName((String) aValue);
            case 8:
                res.setPrice((BigDecimal) aValue);
            default:
                throw new IllegalArgumentException(texts.getString("Working with bad column index."));
        }
        UpdateSwingWorker updateSwingWorker = new UpdateSwingWorker(res, rowIndex, columnIndex);
        updateSwingWorker.execute();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return false;
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
                return true;
            default:
                throw new IllegalArgumentException(texts.getString("Working with bad column index."));
        }
    }
    
    public void addRow(Reservation res) {
        CreateSwingWorker createSwingWorker = new CreateSwingWorker(res);
        createSwingWorker.execute();
    }
    
    public void removeRow(int row) {
        DeleteSwingWorker deleteSwingWorker = new DeleteSwingWorker(row);
        deleteSwingWorker.execute();
    }
    
}
