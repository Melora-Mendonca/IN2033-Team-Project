package IPOS.SA.ACC;

import javax.swing.*;
import java.awt.*;

import IPOS.SA.DB.LoginDBConnector;

// A public class that builds and manages the GUI for the login form - setting the buttons, logos and labels associated with the form.
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
    private JLabel statusLbl;
    private String selectedRole = "Administrator";

    public LoginForm() {
        // Creates the Frame and gives it a title
        setTitle("Login Form");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(MainPanel);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        // Sets the frame location to the center of the screen
        setLocationRelativeTo(null);

        // HEADER:
        createHeader();

        //LOGO PANEL
        createLogoPanel();

        // LOGIN PANEL
        createLoginPanel();

        // Sets the frame to be visible when running
        setVisible(true);
    }

    private void createHeader(){
        // Sets the layout and size of the header panel, so other components within the panel can be correctly aligned
        HeaderPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 24, 0));
        HeaderPanel.setPreferredSize(new Dimension(1000, 54));
        // Gives the panel a colour for the background
        HeaderPanel.setBackground(new Color(14, 37, 48));

        // A new JLabel is created to store a logo image for the header, places next to the title
        ImageIcon Icon = new ImageIcon(new ImageIcon("data/Logo.png")
                .getImage().getScaledInstance(70, 50, Image.SCALE_SMOOTH));
        headerIcon = new JLabel(Icon);

        // A label is created to name the system with the font colour and style set
        headerLabel = new JLabel("InfoPharma Ordering System");
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));

        // Vertically centres both in the header
        headerIcon.setAlignmentY(Component.CENTER_ALIGNMENT);
        headerLabel.setAlignmentY(Component.CENTER_ALIGNMENT);

        // Adds the Icon and the label to the header panel in their set positions
        HeaderPanel.add(headerIcon);
        HeaderPanel.add(headerLabel);
    }

    private void createLogoPanel(){
        // Creates Divider line separating the logo and label from the list of features.
        divider = new JSeparator();
        divider.setForeground(Color.WHITE); // Sets a colour for the divider, with a size for the divider thickness.
        divider.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

        // Sets the layout and background of the logo panel, so other components within the panel can be correctly aligned
        LogoPanel.setLayout(new BoxLayout(LogoPanel, BoxLayout.Y_AXIS));
        LogoPanel.setBackground(new Color(14, 37, 48));
        // A border is set for the panel so that content does not overflow to the login panel
        LogoPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // A new JLabel is created to store a logo image, and is centered to look more professional
        ImageIcon logoIcon = new ImageIcon(new ImageIcon("data/Logo.png")
                .getImage().getScaledInstance(120, 100, Image.SCALE_SMOOTH));
        logoImg = new JLabel(logoIcon);
        logoImg.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Creates a two line label to display the company name with a professional font style, and alignment. Colour is based on the decided IPOS colour palate
        LabelLine1 = new JLabel("InfoPharma");
        LabelLine1.setFont(new Font("Segoe UI", Font.BOLD, 20));
        LabelLine1.setForeground(Color.WHITE);
        LabelLine1.setAlignmentX(Component.CENTER_ALIGNMENT);

        LabelLine2 = new JLabel("Server Portal");
        LabelLine2.setFont(new Font("Segoe UI", Font.BOLD, 20));
        LabelLine2.setForeground(Color.WHITE);
        LabelLine2.setAlignmentX(Component.CENTER_ALIGNMENT);

        // The following lines all add the designed components to the Logo panel in the correct positions with accurate alignment and spacing
        LogoPanel.add(logoImg);
        LogoPanel.add(Box.createVerticalStrut(20)); // The following line creates invisible boxes of set heights to force certain gaps between components
        LogoPanel.add(LabelLine1);
        LogoPanel.add(LabelLine2);
        LogoPanel.add(Box.createVerticalStrut(6));
        LogoPanel.add(Box.createVerticalStrut(20));
        LogoPanel.add(divider);
        LogoPanel.add(Box.createVerticalStrut(16));

        createFeatureList();
    }

    private void createFeatureList(){
        // Creates Divider line separating the logo and label from the list of features.
        divider = new JSeparator();
        divider.setForeground(Color.WHITE); // Sets a colour for the divider, with a size for the divider thickness.
        divider.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

        // Features list - Could be removed if not needed
        String[] features = {
                "  Catalogue management",
                "  Order processing",
                "  Payment recording",
                "  Merchant accounts",
                "  Reports & analytics"
        };

        // Displays the list of features - Could be removed if not needed
        for (String f : features) {
            featureList = new JLabel("● " + f);
            featureList.setFont(new Font("Arial", Font.PLAIN, 11));
            featureList.setForeground(new Color(179, 226, 255));
            featureList.setAlignmentX(Component.CENTER_ALIGNMENT);
            LogoPanel.add(featureList);
            LogoPanel.add(Box.createVerticalStrut(8));
        }
    }

    private void createLoginPanel(){
        // Sets the layout and background of the login panel, so other components within the panel can be correctly aligned
        LoginPanel.setLayout(new BoxLayout(LoginPanel, BoxLayout.Y_AXIS));
        LoginPanel.setBackground(new Color(245, 247, 250));
        // A border is set for the panel so that content does not overflow out of the login panel
        LoginPanel.setBorder(BorderFactory.createEmptyBorder(30, 44, 30, 44));

        // Creates a panel to store all the user role buttons
        roleRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        roleRow.setOpaque(false);
        roleRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        // Creates a group of buttons that when clicked, will display the login form for that user, and will assist in directing the user, once they are fully logged in.
        // Standard professional font and colours are used for the buttons, with borders and colour changing when clicked.
        ButtonGroup group = new ButtonGroup();
        String[] roles = {"administrator", "manager", "staff"};
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

            // Action listener updates the sign-in text to the appropriate user role for consistency
            tb.addActionListener(e -> {
                subtitleLb1.setText("Sign in as " + role);
                subtitleLb1.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                subtitleLb1.setForeground(new Color(107, 114, 128));
                subtitleLb1.setAlignmentX(Component.LEFT_ALIGNMENT);
                selectedRole = role;
            });
        }

        //  Sets the Title label for the login panel
        titleLbl = new JLabel("Welcome back");
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLbl.setForeground(new Color(17, 24, 39));
        titleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Adds a subtitle to inform the user of what the current role is set to
        subtitleLb1 = new JLabel("Sign in as Administrator");
        subtitleLb1.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleLb1.setForeground(new Color(107, 114, 128));
        //subtitleLb1.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Sets the Username label and the corresponding text entry field
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

        // Sets the Password label with a corresponding password field, that hides the text being entered for privacy.
        passLbl = new JLabel("PASSWORD");
        passLbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        passLbl.setForeground(new Color(55, 65, 81));
        passLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        passField = new JPasswordField();
        passField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        passField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        passField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(221, 225, 231)),
                BorderFactory.createEmptyBorder(0, 12, 0, 12)
        ));

        statusLbl = new JLabel("");
        statusLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        statusLbl.setForeground(new Color(218, 30, 40));
        statusLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        loginUser();

        // Add all components to login panel at the set locations and with accurate alignment.
        // Invisible components are used again for formatting and professional appearance
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
        LoginPanel.add(statusLbl);
    }

    private void loginUser(){
        // Creates a Login button that directs the user to the appropriate landing page upon logging in
        loginBtn = new JButton("Sign In");
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        loginBtn.setBackground(new Color(17, 24, 39));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFocusPainted(false);
        loginBtn.setBorderPainted(false);
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        loginBtn.setAlignmentX(Component.LEFT_ALIGNMENT);

        loginBtn.addActionListener(e -> {
            String username = userField.getText();
            String password = new String(passField.getPassword());

            if (username.isEmpty() || password.isEmpty()){
                statusLbl.setText("Sign In Failed, please enter your username and password.");
                return;
            }

            LoginDBConnector connector = new LoginDBConnector();
            System.out.println("Selected role: " + selectedRole);
            System.out.println("Username: " + username);
            System.out.println("Password: " + password);
            User user = connector.authenticate(username, password, selectedRole);

            if (user != null) {
                dispose();
                switch (user.getRole()) {
                    case "administrator":
                        AdminDashboard adminDashboard = new AdminDashboard(user.getFullName());
                        adminDashboard.setVisible(true);
                        break;
                    case "manager":
                        ManagerDashboard managerDashboard = new ManagerDashboard(user.getFullName());
                        managerDashboard.setVisible(true);
                        break;
                    case "staff":
                        StaffDashboard staffDashboard = new StaffDashboard(user.getFullName());
                        staffDashboard.setVisible(true);
                        break;
                }
            } else {
                statusLbl.setText("Invalid username, password or role.");
            }
        });
    }
}
