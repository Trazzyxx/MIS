/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.gui;

import cz.muni.fi.MIS.Main;
import cz.muni.fi.MIS.ReservationManager;
import cz.muni.fi.MIS.Room;
import cz.muni.fi.MIS.RoomManager;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 *
 * @author Vladko
 */
public class RoomComboBoxModel extends DefaultComboBoxModel<String> {
   
    public RoomComboBoxModel(String[] rooms){
     super(rooms);
    }
    
    @Override
    public String getSelectedItem(){
        String selectedRoom = (String) super.getSelectedItem();
        return selectedRoom;
    }    
}
