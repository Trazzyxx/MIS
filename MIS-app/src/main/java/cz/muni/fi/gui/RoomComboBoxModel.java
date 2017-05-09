/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.gui;

import javax.swing.DefaultComboBoxModel;

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
