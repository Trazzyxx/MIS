/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.gui;

import cz.muni.fi.MIS.Guest;
import cz.muni.fi.MIS.GuestManager;
import cz.muni.fi.MIS.Main;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.SwingWorker;
import javax.swing.table.AbstractTableModel;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 *
 * @author Vladko
 */
public class GuestTableModel extends AbstractTableModel{
    private static final ResourceBundle texts = ResourceBundle.getBundle("texts");
    public static final int GUEST_COLUMN = 4;
    private GuestManager guestManager;
    
    private List<Guest> guests = new ArrayList<>();

    private class RetrieveSwingWorker extends SwingWorker<Void,Void> {        
        @Override    
        protected Void doInBackground() throws Exception {
            guests = guestManager.listAllGuests();
            return null;
        }
        
        @Override    
        protected void done() {
            fireTableRowsInserted(0, getRowCount() - 1);
        }
     }
    
    public GuestTableModel() {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(Main.SpringConfig.class);        
        guestManager = ctx.getBean(texts.getString("guestManager"), GuestManager.class);
        
        RetrieveSwingWorker retrieveSwingWorker = new RetrieveSwingWorker();
        retrieveSwingWorker.execute();
    }
    
    private class CreateSwingWorker extends SwingWorker<Void,Void> {
        private Guest guest;
        
        public CreateSwingWorker(Guest guest) {
            this.guest = guest;
        }
        
        @Override    
        protected Void doInBackground() throws Exception {
            guestManager.createGuest(guest);
            guests.add(guest);
            return null;
        }
        
        @Override    
        protected void done() {
            int lastRow = getRowCount() - 1;
            fireTableRowsInserted(lastRow, lastRow);
        }
    }
    
    private class UpdateSwingWorker extends SwingWorker<Void,Void> {
        private Guest guest;
        private int rowIndex;
        private int columnIndex;
        
        public UpdateSwingWorker(Guest guest, int rowIndex, int columnIndex) {
            this.guest = guest;
            this.rowIndex = rowIndex;
            this.columnIndex = columnIndex;
        }
        
        @Override    
        protected Void doInBackground() throws Exception {
            guestManager.updateGuest(guest);
            guests.set(rowIndex, guest);
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
            Guest guest = guests.get(row);
            guests.remove(row);
            guestManager.deleteGuest(guest);
            return null;
        }
        
        @Override    
        protected void done() {
            fireTableRowsDeleted(row, row);
        }
    }
    
    @Override
    public int getRowCount() {
        return guests.size();
    }
 
    @Override
    public int getColumnCount() {
        return GUEST_COLUMN;
    }
 
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Guest guest = guests.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return guest.getGuestID();
            case 1:
                return guest.getPhoneNumber();
            case 2:
                return guest.getAddress();
            case 3:
                return guest.getFullName();
            default:
                throw new IllegalArgumentException(texts.getString("wrong column index"));
        }
    }
    
    @Override
    public String getColumnName(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return texts.getString("id");
            case 1:
                return texts.getString("phoneNumber");
            case 2:
                return texts.getString("address");
            case 3:
                return texts.getString("fullname");
            default:
                throw new IllegalArgumentException(texts.getString("wrong column index"));
        }
    }
    
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return Long.class;
            case 1:
                return String.class;
            case 2:
                return String.class;
            case 3:
                return String.class;
            default:
                throw new IllegalArgumentException(texts.getString("wrong column index"));
        }
    }
    
    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Guest guest = guests.get(rowIndex); // swingworker
        switch (columnIndex) {
            case 0:
                guest.setGuestID((Long) aValue);
                break;
            case 1:
                guest.setPhoneNumber((String) aValue);
                break;
            case 2:
                guest.setAddress((String) aValue);
                break;
            case 3:
                guest.setFullName((String) aValue);
                break;
            default:
                throw new IllegalArgumentException(texts.getString("wrong column index"));
        }
        UpdateSwingWorker updateSwingWorker = new UpdateSwingWorker(guest, rowIndex, columnIndex);
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
                return true;
            default:
                throw new IllegalArgumentException(texts.getString("wrong column index."));
        }
    }
    
    public void addRow(Guest guest) {
        CreateSwingWorker createSwingWorker = new CreateSwingWorker(guest);
        createSwingWorker.execute();
    }
    
    public void removeRow(int row) {
        DeleteSwingWorker deleteSwingWorker = new DeleteSwingWorker(row);
        deleteSwingWorker.execute();
    }
    
    public Guest getRow(int row) {
        return guests.get(row);
    }
    
 }

