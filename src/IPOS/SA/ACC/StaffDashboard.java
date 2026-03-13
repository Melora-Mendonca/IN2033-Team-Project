package IPOS.SA.ACC;

import javax.swing.*;
import java.awt.*;

public class StaffDashboard extends JFrame {
    private String fullname;
    private JPanel MainPanel;
    private JPanel ContentPanel;
    private JPanel HeaderPanel;
    private JPanel FooterPanel;
    private JPanel CenterPanel;
    private JPanel NavPanel;
    private JLabel headerLabel;
    private JLabel headerSubTitle;
    private JLabel navIcon;
    private JButton logoutBtn;
    private JSeparator divider;
    private JPanel tableWrapper;

    public StaffDashboard(String fullname) {
        this.fullname = fullname;
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
        createNavPanel();
    }

    private void createHeaderPanel() {
        HeaderPanel.setLayout(new BoxLayout(HeaderPanel, BoxLayout.X_AXIS));
        HeaderPanel.setPreferredSize(new Dimension(1000, 54));
        HeaderPanel.setBackground(new Color(240, 252, 255));
        HeaderPanel.setBorder(BorderFactory.createEmptyBorder(0, 24, 0, 24));

        // Nested panel stacks title and subtitle vertically
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        // Creates a Label beside the logo, with the user's name
        headerLabel = new JLabel("Welcome back, " + fullname);
        headerLabel.setForeground(Color.BLACK);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));

        // Adds a smaller subtitle below the username to show the role
        headerSubTitle = new JLabel("Staff");
        headerSubTitle.setForeground(new Color(107, 114, 128));
        headerSubTitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        // Adds both labels to the inner panel, one under the other.
        textPanel.add(headerLabel);
        textPanel.add(headerSubTitle);

        // Adds the text panel to the header
        HeaderPanel.add(textPanel);
    }

    // Creates the Navigation Panel
    private void createNavPanel() {
        NavPanel.setLayout(new BoxLayout(NavPanel, BoxLayout.Y_AXIS));
        NavPanel.setBackground(new Color(14, 37, 48));
        NavPanel.setBorder(BorderFactory.createEmptyBorder(20, 16, 20, 16));

        // Logo icon
        ImageIcon Icon = new ImageIcon(new ImageIcon("data/Logo.png")
                .getImage().getScaledInstance(80, 60, Image.SCALE_SMOOTH));
        navIcon = new JLabel(Icon);

        NavPanel.add(navIcon);

        // generates Navigation buttons — Overview is active by default
        String[] navItems = {"Overview", "Orders", "Finance", "Settings"};
        for (String item : navItems) {
            NavPanel.add(buildNavButton(item, item.equals("Overview")));
            NavPanel.add(Box.createVerticalStrut(4));
        }

        // Creates Divider line separating the logo and label from the list of features.
        divider = new JSeparator();
        divider.setForeground(Color.WHITE); // Sets a colour for the divider, with a size for the divider thickness.
        divider.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

        NavPanel.add(divider);
        // Pushes logout to the bottom
        NavPanel.add(Box.createVerticalGlue());

        // Logout button
        logoutBtn = new JButton("[]→ Log out");
        logoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        logoutBtn.setForeground(new Color(200, 80, 80));
        logoutBtn.setBackground(new Color(14, 37, 48));
        logoutBtn.setBorderPainted(false);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        logoutBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        logoutBtn.setHorizontalAlignment(SwingConstants.LEFT);
        logoutBtn.addActionListener(e -> handleLogout());

        // Adds logout button to the navigation panel
        NavPanel.add(logoutBtn);
    }

    // Creates the button funtionality for the items in the navigation panel
    private JButton buildNavButton(String label, boolean active) {
        JButton btn = new JButton(label);
        btn.setFont(new Font("Segoe UI", active ? Font.BOLD : Font.PLAIN, 13));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setBackground(active ? new Color(30, 70, 90) : new Color(14, 37, 48));
        btn.setForeground(active ? Color.WHITE : new Color(160, 190, 210));
        return btn;
    }

    // Manages the logout funtionality for the logout button
    private void handleLogout() {
        dispose();
        new LoginForm();
    }
}
