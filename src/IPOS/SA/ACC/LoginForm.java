package IPOS.SA.ACC;

import javax.swing.*;
import java.awt.*;

public class LoginForm extends JFrame{
    private JPanel MainPanel;
    private JPanel HeaderPanel;
    private JLabel headerIcon;
    private JLabel headerLabel;
    private JLabel logoImg;
    private JSeparator divider;
    private JLabel LabelLine1;
    private JLabel LabelLine2;
    private JLabel featureList;
    private JPanel roleRow;
    private JLabel titleLbl;
    private JLabel userLbl;
    private JTextField userField;
    private JLabel passLbl;
    private JPasswordField passField;
    private JButton loginBtn;
    private JPanel ContentPanel;
    private JPanel LogoPanel;
    private JPanel FooterPanel;
    private JPanel LoginPanel;
    private JLabel subtitleLb1;
    private String selectedRole = "Administrator";

    public LoginForm() {
        // Create the Frame
        setTitle("Login Form");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(MainPanel);
        pack();
        // Set the frame location to the center of the screen
        setLocationRelativeTo(null);

        // HEADER:
        // Header panel
        HeaderPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 24, 0));
        HeaderPanel.setBackground(new Color(14, 37, 48));
        HeaderPanel.setPreferredSize(new Dimension(1000, 54));

        ImageIcon Icon = new ImageIcon(new ImageIcon("data/Logo.png")
                .getImage().getScaledInstance(70, 50, Image.SCALE_SMOOTH));
        headerIcon = new JLabel(Icon);

        headerLabel = new JLabel("InfoPharma Ordering System");
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));

        // Vertically centre both in the header
        headerIcon.setAlignmentY(Component.CENTER_ALIGNMENT);
        headerLabel.setAlignmentY(Component.CENTER_ALIGNMENT);

        HeaderPanel.add(headerIcon);
        HeaderPanel.add(headerLabel);

        //LOGO PANEL
        LogoPanel.setLayout(new BoxLayout(LogoPanel, BoxLayout.Y_AXIS));
        LogoPanel.setBackground(new Color(14, 37, 48));
        LogoPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Logo — centred
        ImageIcon logoIcon = new ImageIcon(new ImageIcon("data/Logo.png")
                .getImage().getScaledInstance(120, 100, Image.SCALE_SMOOTH));
        logoImg = new JLabel(logoIcon);
        logoImg.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Divider line
        divider = new JSeparator();
        divider.setForeground(Color.WHITE);
        divider.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

        // Company name
        LabelLine1 = new JLabel("InfoPharma");
        LabelLine1.setFont(new Font("Segoe UI", Font.BOLD, 20));
        LabelLine1.setForeground(Color.WHITE);
        LabelLine1.setAlignmentX(Component.CENTER_ALIGNMENT);

        LabelLine2 = new JLabel("Server Portal");
        LabelLine2.setFont(new Font("Segoe UI", Font.BOLD, 20));
        LabelLine2.setForeground(Color.WHITE);
        LabelLine2.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Feature list - Could be removed if not needed
        String[] features = {
                "  Catalogue management",
                "  Order processing",
                "  Payment recording",
                "  Merchant accounts",
                "  Reports & analytics"
        };

        LogoPanel.add(logoImg);
        LogoPanel.add(Box.createVerticalStrut(20));
        LogoPanel.add(LabelLine1);
        LogoPanel.add(LabelLine2);
        LogoPanel.add(Box.createVerticalStrut(6));
        LogoPanel.add(Box.createVerticalStrut(20));
        LogoPanel.add(divider);
        LogoPanel.add(Box.createVerticalStrut(16));

        // Displays features - Could be removed if not needed
        for (String f : features) {
            featureList = new JLabel("● " + f);
            featureList.setFont(new Font("Arial", Font.PLAIN, 11));
            featureList.setForeground(new Color(179, 226, 255));
            featureList.setAlignmentX(Component.CENTER_ALIGNMENT);
            LogoPanel.add(featureList);
            LogoPanel.add(Box.createVerticalStrut(8));
        }

        // LOGIN PANEL
        LoginPanel.setLayout(new BoxLayout(LoginPanel, BoxLayout.Y_AXIS));
        LoginPanel.setBackground(new Color(245, 247, 250));
        LoginPanel.setBorder(BorderFactory.createEmptyBorder(30, 44, 30, 44));

        // Role buttons
        roleRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        roleRow.setOpaque(false);
        roleRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        ButtonGroup group = new ButtonGroup();
        String[] roles = {"Administrator", "Manager", "Staff"};
        for (String role : roles) {
            JToggleButton tb = new JToggleButton(role);
            tb.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            tb.setPreferredSize(new Dimension(120, 32));
            tb.setFocusPainted(false);
            tb.setBackground(Color.WHITE);
            tb.setForeground(new Color(107, 114, 128));
            tb.setBorder(BorderFactory.createLineBorder(new Color(221, 225, 231)));
            if (role.equals("Administrator"))
                tb.setSelected(true);
            group.add(tb);
            roleRow.add(tb);

            tb.addActionListener(e -> {
                subtitleLb1.setText("Sign in as " + role);
                subtitleLb1.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                subtitleLb1.setForeground(new Color(107, 114, 128));
                subtitleLb1.setAlignmentX(Component.LEFT_ALIGNMENT);
                selectedRole = role;
            });
        }

        // Title
        titleLbl = new JLabel("Welcome back");
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLbl.setForeground(new Color(17, 24, 39));
        //titleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Username
        userLbl = new JLabel("USERNAME");
        userLbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        userLbl.setForeground(new Color(55, 65, 81));
        userLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        userField = new JTextField();
        userField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        userField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        userField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(221, 225, 231)),
                BorderFactory.createEmptyBorder(0, 12, 0, 12)
        ));

        // Password
        passLbl = new JLabel("PASSWORD");
        passLbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        passLbl.setForeground(new Color(55, 65, 81));
        passLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        subtitleLb1 = new JLabel("Signing in as Administrator");
        subtitleLb1.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleLb1.setForeground(new Color(107, 114, 128));
        subtitleLb1.setAlignmentX(Component.LEFT_ALIGNMENT);

        passField = new JPasswordField();
        passField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        passField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        passField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(221, 225, 231)),
                BorderFactory.createEmptyBorder(0, 12, 0, 12)
        ));

// Login button
        loginBtn = new JButton("Sign In");
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        loginBtn.setBackground(new Color(17, 24, 39));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFocusPainted(false);
        loginBtn.setBorderPainted(false);
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        loginBtn.setAlignmentX(Component.LEFT_ALIGNMENT);

// Add everything to right panel
        LoginPanel.add(roleRow);
        LoginPanel.add(Box.createVerticalStrut(24));
        LoginPanel.add(titleLbl);
        LoginPanel.add(Box.createVerticalStrut(4));
        LoginPanel.add(subtitleLb1);
        LoginPanel.add(Box.createVerticalStrut(24));
        LoginPanel.add(userLbl);
        LoginPanel.add(Box.createVerticalStrut(6));
        LoginPanel.add(userField);
        LoginPanel.add(Box.createVerticalStrut(16));
        LoginPanel.add(passLbl);
        LoginPanel.add(Box.createVerticalStrut(6));
        LoginPanel.add(passField);
        LoginPanel.add(Box.createVerticalStrut(20));
        LoginPanel.add(loginBtn);
        LoginPanel.add(Box.createVerticalStrut(8));

        // Set the frame visible
        setVisible(true);
    }
}
