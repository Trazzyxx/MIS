package cz.muni.fi.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;
import java.util.Locale;

import static javax.swing.JOptionPane.INFORMATION_MESSAGE;

/**
 * Created by xmecko on 26.4.17.
 * @TODO: edit tags for localisation
 * @TODO: implement dialog windows for addReservation and such
 * @TODO: swingworker
 * @TODO: db connection
 */

public class AppForm {
    private JPanel panel1;
    private JTabbedPane Menu;
    private JButton aboutButton;
    private JTextField thisIsHomePageTextField;
    private JTable jTableReservations;
    private JButton AddResButton;
    private JButton EditResButton;
    private JButton DeleteReservationButton;
    private JButton FindGuestReservationButton;
    private JButton FindRoomReservationButton;
    private JButton AddRoomButton;
    private JButton EditRoomButton;
    private JButton DeleteRoomButton;
    private JButton DeleteGuestButton;
    private JButton EditGuestButton;
    private JButton AddGuestButton;
    private JButton FindEmptyRoomButton;
    private JTable jTableGuests;
    private JTable jTableRooms;

    public AppForm() {

        aboutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ResourceBundle resourceBundle = ResourceBundle.getBundle("texts");
                JOptionPane.showMessageDialog(null,"\t" + resourceBundle.getString("about.authors") + "\n"
                        + "\t" + resourceBundle.getString("about.version") + "\n"
                        + "\t" + resourceBundle.getString("about.edited"),resourceBundle.getString("About"),INFORMATION_MESSAGE);
            }
        });

        AddResButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        EditResButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });


        DeleteReservationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        FindGuestReservationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        FindRoomReservationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        FindEmptyRoomButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        AddGuestButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        EditGuestButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        DeleteGuestButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        AddRoomButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        EditRoomButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        DeleteRoomButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
    }


        public static void main(String[] args) {
            JTable jTableRooms = new JTable(RoomsTableModel);
            EventQueue.invokeLater( ()-> {
                        JFrame frame = new JFrame();

                /* Default OS skin*/
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (UnsupportedLookAndFeelException e) {
                    e.printStackTrace();
                }
                frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                frame.setTitle("MIS 1.1");
                frame.setContentPane(new AppForm().Menu);
                frame.setPreferredSize(new Dimension(800,600));
                frame.pack();
                frame.setVisible(true);
                    }
            );

        }
}
