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

        // CardsPanel sits at the top of ContentPanel, 3 equal columns
        CardsPanel.setLayout(new GridLayout(1, 3, 16, 0));
        CardsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        // Add the three stat cards
        CardsPanel.add(buildCard("Total Orders", "0", "Orders this month",
                new Color(17, 24, 39), Color.WHITE));
        CardsPanel.add(buildCard("Low Stock Items", "0", "Below minimum level",
                new Color(255, 248, 230), new Color(133, 100, 4)));
        CardsPanel.add(buildCard("Stock Deliveries", "0", "This month",
                new Color(245, 247, 250), new Color(17, 24, 39)));
    }

    private JPanel buildCard(String title, String value, String subtitle,
                             Color bg, Color titleFg) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(bg);
        card.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        titleLbl.setForeground(titleFg);
        titleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel valueLbl = new JLabel(value);
        valueLbl.setFont(new Font("Segoe UI", Font.BOLD, 32));
        valueLbl.setForeground(titleFg);
        valueLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subLbl = new JLabel(subtitle);
        subLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        subLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(titleLbl);
        card.add(Box.createVerticalStrut(10));
        card.add(valueLbl);
        card.add(Box.createVerticalStrut(4));
        card.add(subLbl);

        return card;
    }

}
