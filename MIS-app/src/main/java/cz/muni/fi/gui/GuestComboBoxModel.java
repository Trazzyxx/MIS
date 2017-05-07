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
public class GuestComboBoxModel extends DefaultComboBoxModel<String> {
   
    public GuestComboBoxModel(String[] guests){
     super(guests);
    }
    
    @Override
    public String getSelectedItem(){
        String selectedGuest = (String) super.getSelectedItem();
        return selectedGuest;
    }    
}
