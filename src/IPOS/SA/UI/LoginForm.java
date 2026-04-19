package IPOS.SA.UI;

import IPOS.SA.ACC.Model.User;
import IPOS.SA.ACC.Service.AccountService;
import IPOS.SA.ACC.Service.AuthenticationService;

import javax.swing.*;
import java.awt.*;
import java.util.List;

// Claude AI was used in the role toggle buttons, login flow and with the stock warning pop up
/**
 * Login screen for IPOS-SA.
 * The first screen shown when the application starts.
 * Extends JPanel and is registered as a card in AppFrame's CardLayout.
 *
 * Displays the InfoPharma branding on the left and a login form on the right.
 * Staff select their role using toggle buttons before entering credentials.
 * The Staff button reveals a dropdown for selecting specific staff roles.
 *
 * On successful login:
 * - Runs automatic account status checks
 * - Loads all user screens into AppFrame
 * - Navigates to the correct dashboard
 * - Shows a low stock warning if applicable
 */
public class LoginForm extends JPanel {

    private final AuthenticationService authService;
    private final ScreenRouter router;

    private JPanel HeaderPanel;
    private JPanel LogoPanel;
    private JPanel LoginPanel;
    private JLabel headerLabel;
    private JLabel logoImg;
    private JSeparator divider;
    private JLabel LabelLine1;
    private JLabel LabelLine2;
    private JLabel featureList;
    private JLabel titleLbl;
    private JLabel userLbl;
    private JTextField userField;
    private JLabel passLbl;
    private JPasswordField passField;
    private JButton loginBtn;
    private JLabel subtitleLb1;
    private JLabel statusLbl;
    private String selectedRole = "Administrator";

    public LoginForm(ScreenRouter router) {
        this.router = router;
        this.authService = new AuthenticationService();

        setLayout(new BorderLayout());

        JPanel root = new JPanel(new BorderLayout());

        // Header
        HeaderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 24, 0));
        HeaderPanel.setPreferredSize(new Dimension(1000, 54));
        HeaderPanel.setBackground(new Color(14, 37, 48));
        root.add(HeaderPanel, BorderLayout.NORTH);

        // Content
        JPanel content = new JPanel(new BorderLayout());

        // Logo panel - left side
        LogoPanel = new JPanel();
        LogoPanel.setBackground(new Color(14, 37, 48));
        content.add(LogoPanel, BorderLayout.CENTER);

        // Login panel - right side
        LoginPanel = new JPanel();
        LoginPanel.setPreferredSize(new Dimension(480, 600));
        LoginPanel.setBackground(new Color(245, 247, 250));
        content.add(LoginPanel, BorderLayout.EAST);

        root.add(content, BorderLayout.CENTER);
        add(root, BorderLayout.CENTER);

        createHeader();
        createLogoPanel();
        createLoginPanel();
    }
    /**
     * Builds the top header bar with the InfoPharma logo and system name.
     */
    private void createHeader() {
        HeaderPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 24, 0));
        HeaderPanel.setPreferredSize(new Dimension(1000, 54));
        HeaderPanel.setBackground(new Color(14, 37, 48));

        ImageIcon Icon = new ImageIcon(new ImageIcon("data/Logo.png")
                .getImage().getScaledInstance(70, 50, Image.SCALE_SMOOTH));
        JLabel headerIcon = new JLabel(Icon);

        headerLabel = new JLabel("InfoPharma Ordering System");
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));

        headerIcon.setAlignmentY(Component.CENTER_ALIGNMENT);
        headerLabel.setAlignmentY(Component.CENTER_ALIGNMENT);

        HeaderPanel.add(headerIcon);
        HeaderPanel.add(headerLabel);
    }
    /**
     * Builds the left branding panel with the InfoPharma logo,
     * title text and system feature list.
     */
    private void createLogoPanel() {
        divider = new JSeparator();
        divider.setForeground(Color.WHITE);
        divider.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

        LogoPanel.setLayout(new BoxLayout(LogoPanel, BoxLayout.Y_AXIS));
        LogoPanel.setBackground(new Color(14, 37, 48));
        LogoPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        ImageIcon logoIcon = new ImageIcon(new ImageIcon("data/Logo.png")
                .getImage().getScaledInstance(120, 100, Image.SCALE_SMOOTH));
        logoImg = new JLabel(logoIcon);
        logoImg.setAlignmentX(Component.CENTER_ALIGNMENT);

        LabelLine1 = new JLabel("InfoPharma");
        LabelLine1.setFont(new Font("Segoe UI", Font.BOLD, 20));
        LabelLine1.setForeground(Color.WHITE);
        LabelLine1.setAlignmentX(Component.CENTER_ALIGNMENT);

        LabelLine2 = new JLabel("Server Portal");
        LabelLine2.setFont(new Font("Segoe UI", Font.BOLD, 20));
        LabelLine2.setForeground(Color.WHITE);
        LabelLine2.setAlignmentX(Component.CENTER_ALIGNMENT);

        LogoPanel.add(logoImg);
        LogoPanel.add(Box.createVerticalStrut(20));
        LogoPanel.add(LabelLine1);
        LogoPanel.add(LabelLine2);
        LogoPanel.add(Box.createVerticalStrut(6));
        LogoPanel.add(Box.createVerticalStrut(20));
        LogoPanel.add(divider);
        LogoPanel.add(Box.createVerticalStrut(16));

        createFeatureList();
    }
    /**
     * Populates the branding panel with a bullet list of system features.
     */
    private void createFeatureList() {
        String[] features = {
                "  Catalogue management",
                "  Order processing",
                "  Payment recording",
                "  Merchant accounts",
                "  Reports & analytics"
        };

        for (String f : features) {
            featureList = new JLabel("● " + f);
            featureList.setFont(new Font("Arial", Font.PLAIN, 11));
            featureList.setForeground(new Color(179, 226, 255));
            featureList.setAlignmentX(Component.CENTER_ALIGNMENT);
            LogoPanel.add(featureList);
            LogoPanel.add(Box.createVerticalStrut(8));
        }
    }
    /**
     * Populates the branding panel with a bullet list of system features.
     */
    private void createLoginPanel() {
        LoginPanel.setLayout(new BoxLayout(LoginPanel, BoxLayout.Y_AXIS));
        LoginPanel.setBackground(new Color(245, 247, 250));
        LoginPanel.setBorder(BorderFactory.createEmptyBorder(30, 44, 30, 44));

        createRoleSelection();
        createLoginForm();
    }
    /**
     * Creates the role selection section with toggle buttons for
     * Administrator, Director and Staff.
     * The Staff button reveals a dropdown to choose a specific staff role.
     */
    private void createRoleSelection() {
        JPanel roleRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 0));
        roleRow.setOpaque(false);
        roleRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        ButtonGroup group = new ButtonGroup();

        JToggleButton adminBtn = new JToggleButton("Administrator");
        JToggleButton directorBtn = new JToggleButton("Director");
        JToggleButton staffBtn = new JToggleButton("Staff");

        adminBtn.setPreferredSize(new Dimension(120, 32));
        directorBtn.setPreferredSize(new Dimension(120, 32));
        staffBtn.setPreferredSize(new Dimension(80, 32));

        styleToggleButton(adminBtn);
        styleToggleButton(directorBtn);
        styleToggleButton(staffBtn);

        adminBtn.setSelected(true);
        selectedRole = "Administrator";

        group.add(adminBtn);
        group.add(directorBtn);
        group.add(staffBtn);

        roleRow.add(adminBtn);
        roleRow.add(directorBtn);
        roleRow.add(staffBtn);

        JPanel staffPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4));
        staffPanel.setOpaque(false);
        staffPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        staffPanel.setVisible(false);

        JLabel staffTypeLabel = new JLabel("Staff type:");
        staffTypeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        staffTypeLabel.setForeground(new Color(55, 65, 81));

        JComboBox<String> staffDropdown = new JComboBox<>(new String[]{
                "Senior Accountant", "Accountant", "Warehouse Employee", "Delivery Employee"
        });
        staffDropdown.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        staffDropdown.setPreferredSize(new Dimension(200, 32));
        staffDropdown.addActionListener(e -> {
            selectedRole = staffDropdown.getSelectedItem().toString();
            if (subtitleLb1 != null) subtitleLb1.setText("Sign in as " + selectedRole);
        });

        staffPanel.add(staffTypeLabel);
        staffPanel.add(staffDropdown);

        adminBtn.addActionListener(e -> {
            selectedRole = "Administrator";
            if (subtitleLb1 != null) subtitleLb1.setText("Sign in as Administrator");
            staffPanel.setVisible(false);
            LoginPanel.revalidate();
            LoginPanel.repaint();
        });

        directorBtn.addActionListener(e -> {
            selectedRole = "Director of Operations";
            if (subtitleLb1 != null) subtitleLb1.setText("Sign in as Director of Operations");
            staffPanel.setVisible(false);
            LoginPanel.revalidate();
            LoginPanel.repaint();
        });

        staffBtn.addActionListener(e -> {
            selectedRole = staffDropdown.getSelectedItem().toString();
            if (subtitleLb1 != null) subtitleLb1.setText("Sign in as " + selectedRole);
            staffPanel.setVisible(true);
            LoginPanel.revalidate();
            LoginPanel.repaint();
        });

        LoginPanel.add(roleRow);
        LoginPanel.add(Box.createVerticalStrut(8));
        LoginPanel.add(staffPanel);
    }
    /**
     * Creates the login form with username and password fields,
     * a show/hide password toggle and the Sign In button.
     * Pressing Enter in either field triggers the login action.
     */
    private void createLoginForm() {
        titleLbl = new JLabel("Welcome back");
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLbl.setForeground(new Color(17, 24, 39));
        titleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        subtitleLb1 = new JLabel("Sign in as Administrator");
        subtitleLb1.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleLb1.setForeground(new Color(107, 114, 128));

        userLbl = new JLabel("USERNAME");
        userLbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        userLbl.setForeground(new Color(55, 65, 81));
        userLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        userField = new JTextField();
        userField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        userField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        userField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(221, 225, 231)),
                BorderFactory.createEmptyBorder(0, 12, 0, 12)));

        passLbl = new JLabel("PASSWORD");
        passLbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        passLbl.setForeground(new Color(55, 65, 81));
        passLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Create panel to hold password field and button
        JPanel passwordPanel = new JPanel(new BorderLayout());
        passwordPanel.setOpaque(false);
        passwordPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        passField = new JPasswordField();
        passField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        passField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(221, 225, 231)),
                BorderFactory.createEmptyBorder(0, 12, 0, 12)));
        passField.setEchoChar('•');

        JButton showPasswordBtn = new JButton("👁");
        showPasswordBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        showPasswordBtn.setFocusPainted(false);
        showPasswordBtn.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
        showPasswordBtn.setBackground(Color.WHITE);
        showPasswordBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        showPasswordBtn.setToolTipText("Show Password");

        final boolean[] passwordVisible = {false};

        showPasswordBtn.addActionListener(e -> {
            passwordVisible[0] = !passwordVisible[0];
            if (passwordVisible[0]) {
                passField.setEchoChar((char) 0);
                showPasswordBtn.setText("●");
                showPasswordBtn.setToolTipText("Hide Password");
            } else {
                passField.setEchoChar('•');
                showPasswordBtn.setText("●");
                showPasswordBtn.setToolTipText("Show Password");
            }
        });

        passwordPanel.add(passField, BorderLayout.CENTER);
        passwordPanel.add(showPasswordBtn, BorderLayout.EAST);

        statusLbl = new JLabel("");
        statusLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        statusLbl.setForeground(new Color(218, 30, 40));
        statusLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        userField.addActionListener(e -> handleLogin());
        passField.addActionListener(e -> handleLogin());

        createLoginButton();

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
        LoginPanel.add(passwordPanel);
        LoginPanel.add(Box.createVerticalStrut(20));
        LoginPanel.add(loginBtn);
        LoginPanel.add(Box.createVerticalStrut(8));
        LoginPanel.add(statusLbl);
    }
    /**
     * Creates and styles the Sign In button.
     */
    private void createLoginButton() {
        loginBtn = new JButton("Sign In");
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        loginBtn.setBackground(new Color(17, 24, 39));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFocusPainted(false);
        loginBtn.setBorderPainted(false);
        loginBtn.setOpaque(true);
        loginBtn.setContentAreaFilled(true);
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        loginBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginBtn.addActionListener(e -> handleLogin());
    }
    /**
     * Handles the login action when Sign In is clicked or Enter is pressed.
     * Validates input fields, authenticates via AuthenticationService,
     * then loads screens and navigates to the correct dashboard.
     */
    private void handleLogin() {
        String username = userField.getText().trim();
        String password = new String(passField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            statusLbl.setText("Please enter your username and password.");
            return;
        }

        if (username.length() < 3) {
            statusLbl.setText("Username must be at least 3 characters.");
            return;
        }

        if (password.length() < 6) {
            statusLbl.setText("Password must be at least 6 characters.");
            return;
        }

        if (username.contains(" ")) {
            statusLbl.setText("Username cannot contain spaces.");
            return;
        }

        if (password.contains(" ")) {
            statusLbl.setText("Password cannot contain spaces.");
            return;
        }

        User user = authService.authenticate(username, password, selectedRole);

        if (user != null) {
            System.out.println("Login successful!");
            // Clear fields
            userField.setText("");
            passField.setText("");
            statusLbl.setText("");
            // Load screens and navigate
            AppFrame.getInstance().loadUserScreens(user.getFullName(), user.getRole(), user.getUsername());
            navigateToDashboard(user);
        } else {
            statusLbl.setText("Invalid username, password or role.");
        }
    }
    /**
     * Navigates the logged-in user to their appropriate dashboard.
     * For Administrators and Directors — runs auto account status checks
     * and loads stock warnings before navigating.
     * Stock warnings are shown after the dashboard loads using invokeLater
     * to ensure the dashboard is fully visible first.
     *
     * @param user the authenticated user
     */
    private void navigateToDashboard(User user) {
        List<String> warnings = null;
        if (user.getRole().equals("Administrator") ||
                user.getRole().equals("Director of Operations")) {
            try {
                AccountService accountService = new AccountService();
                accountService.AutoUpdateStatus();
                warnings = authService.getStockWarnings();
            } catch (Exception e) {
                System.err.println("Error getting warnings: " + e.getMessage());
            }
        }

        switch (user.getRole()) {
            case "Administrator":
                router.goTo(AppFrame.SCREEN_ADMIN_DASHBOARD);
                break;
            case "Director of Operations":
                router.goTo(AppFrame.SCREEN_MANAGER_DASHBOARD);
                break;
            default:
                router.goTo(AppFrame.SCREEN_STAFF_DASHBOARD);
                break;
        }

        if (warnings != null && !warnings.isEmpty()) {
            final List<String> finalWarnings = warnings;
            SwingUtilities.invokeLater(() -> showStockWarning(finalWarnings));
        }
    }
    /**
     * Displays a warning dialog listing catalogue items below minimum stock level.
     * Called after successful login for admin and director roles.
     *
     * @param warnings list of warning messages for low stock items
     */
    private void showStockWarning(List<String> warnings) {
        StringBuilder sb = new StringBuilder();
        sb.append("The following items are below minimum stock level:\n\n");
        for (String w : warnings) sb.append("  •  ").append(w).append("\n");
        sb.append("\nPlease arrange stock deliveries.");
        JOptionPane.showMessageDialog(this, sb.toString(), "Low Stock Warning", JOptionPane.WARNING_MESSAGE);
    }

    private void styleToggleButton(JToggleButton btn) {
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setFocusPainted(false);
        btn.setBackground(Color.WHITE);
        btn.setForeground(new Color(107, 114, 128));
        btn.setBorder(BorderFactory.createLineBorder(new Color(221, 225, 231)));
    }
}
