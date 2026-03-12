package IPOS.SA.ACC;

import javax.swing.*;
import java.awt.*;

public class StaffDashboard extends JFrame {
    private JPanel MainPanel;
    private JPanel ContentPanel;
    private JPanel HeaderPanel;
    private JPanel NavPanel;
    private JPanel FooterPanel;
    private JLabel headerLabel;
    private JLabel headerIcon;
    private JLabel headerSubTitle;

    public StaffDashboard() {
        setTitle("Staff Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(MainPanel);
        // Sets the form size to the size of the display
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        // Sets the frame location to the center of the screen
        setLocationRelativeTo(null);
        // Sets the frame to be visible when running
        setVisible(true);

        // HEADER:
        createHeaderPanel();
    }

    private void createHeaderPanel() {
        HeaderPanel.setLayout(new BoxLayout(HeaderPanel, BoxLayout.X_AXIS));
        HeaderPanel.setPreferredSize(new Dimension(1000, 54));
        HeaderPanel.setBackground(new Color(240, 252, 255));
        HeaderPanel.setBorder(BorderFactory.createEmptyBorder(0, 24, 0, 24));

        // Logo icon
        ImageIcon Icon = new ImageIcon(new ImageIcon("data/Logo.png")
                .getImage().getScaledInstance(70, 50, Image.SCALE_SMOOTH));
        headerIcon = new JLabel(Icon);

        // Nested panel stacks title and subtitle vertically
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        // Creates a Label beside the logo, with the user's name
        headerLabel = new JLabel("Welcome back, USERNAME");
        headerLabel.setForeground(Color.BLACK);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));

        // Adds a smaller subtitle below the username to show the role
        headerSubTitle = new JLabel("Staff");
        headerSubTitle.setForeground(new Color(107, 114, 128));
        headerSubTitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        // Adds both labels to the inner panel, one under the other.
        textPanel.add(headerLabel);
        textPanel.add(headerSubTitle);

        // X_AXIS puts icon and textPanel side by side
        HeaderPanel.add(headerIcon);
        HeaderPanel.add(Box.createHorizontalStrut(12));
        HeaderPanel.add(textPanel);
    }
}
