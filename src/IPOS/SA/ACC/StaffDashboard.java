package IPOS.SA.ACC;

import javax.swing.*;

public class StaffDashboard extends JFrame {
    private JPanel MainPanel;
    private JPanel ContentPanel;
    private JPanel HeaderPanel;
    private JPanel NavPanel;
    private JPanel FooterPanel;

    public StaffDashboard() {
        setTitle("Staff Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(MainPanel);
        pack();
        // Sets the frame location to the center of the screen
        setLocationRelativeTo(null);
        // Sets the frame to be visible when running
        setVisible(true);
    }
}
