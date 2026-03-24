package IPOS.SA.ACC;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

import IPOS.SA.DB.LoginDBConnector;

// A public class that builds and manages the GUI for the login form - setting the buttons, logos and labels associated with the form.
public class LoginForm extends JFrame {
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

    private void createHeader() {
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

    private void createLogoPanel() {
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

    private void createFeatureList() {
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

    private void createLoginPanel() {
        LoginPanel.setLayout(new BoxLayout(LoginPanel, BoxLayout.Y_AXIS));
        LoginPanel.setBackground(new Color(245, 247, 250));
        LoginPanel.setBorder(BorderFactory.createEmptyBorder(30, 44, 30, 44));

        // ── ROLE SELECTION ───────────────────────────────────────
        roleRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        roleRow.setOpaque(false);
        roleRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        ButtonGroup group = new ButtonGroup();

        JToggleButton adminBtn = new JToggleButton("Administrator");
        JToggleButton directorBtn = new JToggleButton("Director of Operations");
        JToggleButton staffBtn = new JToggleButton("Staff");

        styleToggleButton(adminBtn);
        styleToggleButton(directorBtn);
        styleToggleButton(staffBtn);

        adminBtn.setSelected(true);
        selectedRole = "administrator";

        group.add(adminBtn);
        group.add(directorBtn);
        group.add(staffBtn);

        roleRow.add(adminBtn);
        roleRow.add(directorBtn);
        roleRow.add(staffBtn);

        // Staff dropdown — hidden by default
        JPanel staffPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4));
        staffPanel.setOpaque(false);
        staffPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        staffPanel.setVisible(false);

        JLabel staffTypeLabel = new JLabel("Staff type:");
        staffTypeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        staffTypeLabel.setForeground(new Color(55, 65, 81));

        JComboBox<String> staffDropdown = new JComboBox<>(new String[]{
                "Senior Accountant",
                "Accountant",
                "Warehouse Employee",
                "Delivery Employee"
        });
        staffDropdown.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        staffDropdown.setPreferredSize(new Dimension(200, 32));
        staffDropdown.addActionListener(e -> {
            selectedRole = staffDropdown.getSelectedItem().toString();
            subtitleLb1.setText("Sign in as " + formatRole(selectedRole));
        });

        staffPanel.add(staffTypeLabel);
        staffPanel.add(staffDropdown);

        // Action listeners for role buttons
        adminBtn.addActionListener(e -> {
            selectedRole = "administrator";
            subtitleLb1.setText("Sign in as Administrator");
            staffPanel.setVisible(false);
            LoginPanel.revalidate();
            LoginPanel.repaint();
        });

        directorBtn.addActionListener(e -> {
            selectedRole = "director_of_operations";
            subtitleLb1.setText("Sign in as Director of Operations");
            staffPanel.setVisible(false);
            LoginPanel.revalidate();
            LoginPanel.repaint();
        });

        staffBtn.addActionListener(e -> {
            selectedRole = staffDropdown.getSelectedItem().toString();
            subtitleLb1.setText("Sign in as " + formatRole(selectedRole));
            staffPanel.setVisible(true);
            LoginPanel.revalidate();
            LoginPanel.repaint();
        });

        // ── TITLE ────────────────────────────────────────────────
        titleLbl = new JLabel("Welcome back");
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLbl.setForeground(new Color(17, 24, 39));
        titleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        subtitleLb1 = new JLabel("Sign in as Administrator");
        subtitleLb1.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleLb1.setForeground(new Color(107, 114, 128));

        // ── USERNAME ─────────────────────────────────────────────
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

        // ── PASSWORD ─────────────────────────────────────────────
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

        // ── STATUS ───────────────────────────────────────────────
        statusLbl = new JLabel("");
        statusLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        statusLbl.setForeground(new Color(218, 30, 40));
        statusLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        loginUser();

        // ── ASSEMBLE ─────────────────────────────────────────────
        LoginPanel.add(roleRow);
        LoginPanel.add(Box.createVerticalStrut(8));
        LoginPanel.add(staffPanel);
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

    private void loginUser() {
        loginBtn = new JButton("Sign In");
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        loginBtn.setBackground(new Color(17, 24, 39));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFocusPainted(false);
        loginBtn.setBorderPainted(false);
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        loginBtn.setAlignmentX(Component.LEFT_ALIGNMENT);

        loginBtn.addActionListener(e -> {
            String username = userField.getText().trim();
            String password = new String(passField.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                statusLbl.setText("Please enter your username and password.");
                return;
            }

            LoginDBConnector connector = new LoginDBConnector();
            User user = connector.authenticate(username, password, selectedRole);

            if (user != null) {
                dispose();

                // Check low stock warnings for admin and director
                List<String> warnings = new ArrayList<>();
                if (user.getRole().equals("administrator") ||
                        user.getRole().equals("director_of_operations")) {
                    warnings = connector.getStockWarnings();
                }

                switch (user.getRole()) {
                    case "administrator": {
                        AdminDashboard dashboard = new AdminDashboard(user.getFullName(), user.getRole());
                        if (!warnings.isEmpty()) showStockWarning(dashboard, warnings);
                        break;
                    }
                    case "director_of_operations": {
                        ManagerDashboard dashboard = new ManagerDashboard(user.getFullName(), user.getRole());
                        if (!warnings.isEmpty()) showStockWarning(dashboard, warnings);
                        break;
                    }
                    case "senior_accountant":
                    case "accountant":
                    case "warehouse_employee":
                    case "delivery_employee":
                        new StaffDashboard(user.getFullName(), user.getRole());
                        break;
                    default:
                        statusLbl.setText("Role not recognised.");
                        break;
                }
            } else {
                statusLbl.setText("Invalid username, password or role.");
            }
        });
    }

    private void styleToggleButton(JToggleButton btn) {
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setPreferredSize(new Dimension(160, 32));
        btn.setFocusPainted(false);
        btn.setBackground(Color.WHITE);
        btn.setForeground(new Color(107, 114, 128));
        btn.setBorder(BorderFactory.createLineBorder(new Color(221, 225, 231)));
    }

    private String formatRole(String role) {
        String[] words = role.split("_");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            sb.append(Character.toUpperCase(word.charAt(0)))
                    .append(word.substring(1))
                    .append(" ");
        }
        return sb.toString().trim();
    }

    private void showStockWarning(JFrame parent, List<String> warnings) {
        StringBuilder sb = new StringBuilder();
        sb.append("The following items are below minimum stock level:\n\n");
        for (String w : warnings) {
            sb.append("  •  ").append(w).append("\n");
        }
        sb.append("\nPlease arrange stock deliveries.");

        JOptionPane.showMessageDialog(
                parent,
                sb.toString(),
                "Low Stock Warning",
                JOptionPane.WARNING_MESSAGE
        );
    }
}
