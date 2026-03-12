package IPOS.SA.ACC;

import javax.swing.*;
import java.awt.*;

public class AdminDashboard extends JFrame{
    private JPanel MainPanel;
    private JPanel ContentPanel;
    private JPanel HeaderPanel;
    private JPanel NavPanel;
    private JPanel FooterPanel;
    private JPanel CardsPanel;

    public AdminDashboard() {
        setTitle("Admin Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(MainPanel);
        pack();
        // Sets the frame location to the center of the screen
        setLocationRelativeTo(null);

        // Sets the frame to be visible when running
        setVisible(true);
    }

}
