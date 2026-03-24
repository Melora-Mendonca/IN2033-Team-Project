package IPOS.SA.ACC;


import IPOS.SA.ACC.UI.AdminDashboard;
import IPOS.SA.CAT.UI.Catalogue;
import IPOS.SA.ACC.UI.LoginForm;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class StaffAccountManagement extends JFrame {

    private final AccountService accountService;

    // For the user's role and full name that is displayed in the header
    private String fullname;
    private String role;
    private String mode;

    private JLabel statusLabel;

    private JLabel staffIdLabel;
    private JTextField staffIdField;
    private JLabel firstNameLabel;
    private JTextField firstNameField;
    private JLabel surNameLabel;
    private JTextField surNameField;
    private JLabel  emailLabel;
    private JTextField emailField;
    private JLabel  phoneLabel;
    private JTextField phoneField;
    private JLabel  addressLabel;
    private JTextField addressField;
    private JLabel  usernameLabel;
    private JTextField usernameField;

    private String selectedRole = "Administrator";


    // Panels for the form layout, header and navigation
    private JPanel MainPanel;
    private JPanel ContentPanel;
    private JPanel NavPanel;
    private JPanel HeaderPanel;
    private JPanel FooterPanel;
    private JPanel CenterPanel;
    private JPanel FormPanel;
    private JPanel StatusPanel;
    private JLabel headerLabel;
    private JLabel navIcon;
    private JButton logoutBtn;
    private JSeparator divider;

    private JLabel messageLabel;

    public StaffAccountManagement(String fullname, String role, AccountService accountService, String mode) {
        this.accountService = accountService;
        this.fullname = fullname;
        this.role = role;
        this.mode = mode;

        setTitle("Staff Account Management");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(MainPanel);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);

        // Creates the standard header and navigation panel across all the IPOS-SA forms
        createHeaderPanel();
        createNavPanel();
        createFormPanel();

        setVisible(true);

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
        headerLabel = new JLabel("Staff Account Management");
        headerLabel.setForeground(Color.BLACK);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));

        // Adds both labels to the inner panel, one under the other.
        textPanel.add(headerLabel);

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

        // Adds the logo to the navigation panel
        NavPanel.add(navIcon);
        NavPanel.add(Box.createVerticalStrut(16));

        // Adds nav buttons to the navigation panel
        NavPanel.add(buildNavButton("Overview",  false));
        NavPanel.add(Box.createVerticalStrut(4));
        NavPanel.add(buildNavButton("Catalogue", false));
        NavPanel.add(Box.createVerticalStrut(4));
        NavPanel.add(buildNavButton("Orders",    false));
        NavPanel.add(Box.createVerticalStrut(4));

        // Expandable sections for certain navigation options
        addExpandableNavItem(NavPanel, "Merchants", new String[]{
                "View Merchant Orders",
                "View Merchant Invoices"
        });

        addExpandableNavItem(NavPanel, "Accounts", new String[]{
                "Create Merchant Account",
                "Manage Merchant Accounts",
                "Commercial Applications"
        });

        addExpandableNavItem(NavPanel, "Staff", new String[]{
                "View All Staff",
                "Create Staff Account",
                "Manage Staff Account",
        });

        // Adds remaining option to the navigation panel
        NavPanel.add(buildNavButton("Reports",  false));  NavPanel.add(Box.createVerticalStrut(4));
        NavPanel.add(buildNavButton("Settings", false));  NavPanel.add(Box.createVerticalStrut(4));

        // Creates a divider to separate and format the navigation options
        divider = new JSeparator();
        divider.setForeground(Color.WHITE);
        divider.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        NavPanel.add(divider);
        NavPanel.add(Box.createVerticalGlue());

        // creates a log Out button at the base of the navigation panel
        logoutBtn = new JButton("→  Log out");
        logoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        logoutBtn.setForeground(new Color(200, 80, 80));
        logoutBtn.setBackground(new Color(14, 37, 48));
        logoutBtn.setBorderPainted(false);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        logoutBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        logoutBtn.setHorizontalAlignment(SwingConstants.LEFT);
        logoutBtn.addActionListener(e -> handleLogout());
        // Adds the button to the panel
        NavPanel.add(logoutBtn);
    }

    private void addExpandableNavItem(JPanel nav, String label, String[] subItems) {
        JButton mainBtn = new JButton(label);
        mainBtn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        mainBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        mainBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainBtn.setHorizontalAlignment(SwingConstants.LEFT);
        mainBtn.setFocusPainted(false);
        mainBtn.setBorderPainted(false);
        mainBtn.setBackground(new Color(14, 37, 48));
        mainBtn.setForeground(new Color(160, 190, 210));

        // Sub-items panel — hidden by default
        JPanel subPanel = new JPanel();
        subPanel.setLayout(new BoxLayout(subPanel, BoxLayout.Y_AXIS));
        subPanel.setBackground(new Color(10, 28, 38));
        subPanel.setVisible(false);

        for (String sub : subItems) {
            JButton subBtn = new JButton("    › " + sub);
            subBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            subBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
            subBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
            subBtn.setHorizontalAlignment(SwingConstants.LEFT);
            subBtn.setFocusPainted(false);
            subBtn.setBorderPainted(false);
            subBtn.setBackground(new Color(10, 28, 38));
            subBtn.setForeground(new Color(120, 160, 185));

            subBtn.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    subBtn.setForeground(Color.WHITE);
                    subBtn.setBackground(new Color(20, 50, 65));
                }
                public void mouseExited(java.awt.event.MouseEvent e) {
                    subBtn.setForeground(new Color(120, 160, 185));
                    subBtn.setBackground(new Color(10, 28, 38));
                }
            });

            subBtn.addActionListener(e -> handleSubNavClick(sub));
            subPanel.add(subBtn);
            subPanel.add(Box.createVerticalStrut(2));
        }

        // Toggle sub-panel on click
        mainBtn.addActionListener(e -> {
            boolean showing = subPanel.isVisible();
            subPanel.setVisible(!showing);
            mainBtn.setForeground(showing ? new Color(160, 190, 210) : Color.WHITE);
            mainBtn.setBackground(showing ? new Color(14, 37, 48) : new Color(20, 45, 60));
            nav.revalidate();
            nav.repaint();
        });

        nav.add(mainBtn);
        nav.add(subPanel);
        nav.add(Box.createVerticalStrut(4));
    }

    private void handleSubNavClick(String label) {
        switch (label) {
            case "Manage Merchant Accounts":
                dispose();
                new AccountManagement(fullname, role,  "MANAGE");
                break;
            case "Create Merchant Account":
                dispose();
                new AccountManagement(fullname, role, "CREATE");
                break;
            case "Commercial Applications":
                JOptionPane.showMessageDialog(this, "Commercial Applications — coming soon.");
                break;
            case "View All Staff":
            case "Create Staff Account":
                dispose();
                new StaffAccountManagement(fullname, role, new AccountService(), "CREATE");
                break;
            case "Manage Staff Account":
                dispose();
                new StaffAccountManagement(fullname, role, new AccountService(), "MANAGE");
                break;
            case "View Merchant Orders":
            case "View Merchant Invoices":
            default:
                JOptionPane.showMessageDialog(this, label + " — coming soon.");
                break;
        }
    }

    // Creates the button functionality for the items in the navigation panel
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

        btn.addActionListener(e -> {
            dispose();
            switch (label) {
                case "Catalogue":
                    new Catalogue(fullname, role);
                    dispose();
                    break;
                case "Overview":
                    new AdminDashboard(fullname, role);
                    dispose();
                    break;
            }
        });

        return btn;
    }

    // Manages the logout functionality for the logout button
    private void handleLogout() {
        dispose();
        new LoginForm();
    }

    private void createFormPanel() {
        CenterPanel.setLayout(new BorderLayout(0, 0));
        CenterPanel.setBackground(new Color(245, 247, 250));

        FormPanel.setLayout(new BorderLayout(0, 0));
        FormPanel.setBackground(Color.WHITE);
        FormPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(221, 225, 231), 1),
                new EmptyBorder(20, 20, 20, 20)));

        JLabel formTitle = new JLabel("ACCOUNT DETAILS");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formTitle.setForeground(Color.BLACK);
        formTitle.setBorder(new EmptyBorder(0, 0, 12, 0));

        JPanel grid = new JPanel();
        grid.setLayout(new BoxLayout(grid, BoxLayout.Y_AXIS));
        grid.setBackground(Color.WHITE);

        // Sets the label and the corresponding text entry field
        staffIdLabel = new JLabel("STAFF ID");
        staffIdLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
        staffIdLabel.setForeground(new Color(55, 65, 81));
        staffIdLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        staffIdField = new JTextField();
        staffIdField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        staffIdField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        staffIdField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(221, 225, 231)),
                BorderFactory.createEmptyBorder(0, 12, 0, 12)
        ));

        JComboBox<String> staffDropdown = new JComboBox<>(new String[]{
                "Administrator",
                "Director of Operations",
                "Senior Accountant",
                "Accountant",
                "Warehouse Employee",
                "Delivery Employee"
        });

        staffDropdown.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        staffDropdown.setPreferredSize(new Dimension(200, 32));
        staffDropdown.addActionListener(e -> {
                    selectedRole = staffDropdown.getSelectedItem().toString();
                });

            firstNameLabel = new JLabel("FIRST NAME");
            firstNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
            firstNameLabel.setForeground(new Color(55, 65, 81));
            firstNameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

            firstNameField = new JTextField();
            firstNameField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            firstNameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
            firstNameField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(221, 225, 231)),
                    BorderFactory.createEmptyBorder(0, 12, 0, 12)
            ));

            surNameLabel = new JLabel("SURNAME");
            surNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
            surNameLabel.setForeground(new Color(55, 65, 81));
            surNameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

            surNameField = new JTextField();
            surNameField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            surNameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
            surNameField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(221, 225, 231)),
                    BorderFactory.createEmptyBorder(0, 12, 0, 12)
            ));

            emailLabel = new JLabel("EMAIL");
            emailLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
            emailLabel.setForeground(new Color(55, 65, 81));
            emailLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

            emailField = new JTextField();
            emailField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
            emailField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(221, 225, 231)),
                    BorderFactory.createEmptyBorder(0, 12, 0, 12)
            ));

            phoneLabel = new JLabel("PHONE");
            phoneLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
            phoneLabel.setForeground(new Color(55, 65, 81));
            phoneLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

            phoneField = new JTextField();
            phoneField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            phoneField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
            phoneField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(221, 225, 231)),
                    BorderFactory.createEmptyBorder(0, 12, 0, 12)
            ));

            addressLabel = new JLabel("ADDRESS");
            addressLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
            addressLabel.setForeground(new Color(55, 65, 81));
            addressLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

            addressField = new JTextField();
            addressField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            addressField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
            addressField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(221, 225, 231)),
                    BorderFactory.createEmptyBorder(0, 12, 0, 12)
            ));

            usernameLabel = new JLabel("USERNAME");
            usernameLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
            usernameLabel.setForeground(new Color(55, 65, 81));
            usernameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

            usernameField = new JTextField();
            usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
            usernameField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(221, 225, 231)),
                    BorderFactory.createEmptyBorder(0, 12, 0, 12)
            ));

            JPanel row1 = new JPanel(new GridLayout(1, 1));
            row1.add(staffDropdown); // 👈 role selection here

            JPanel row2 = new JPanel(new GridLayout(1, 3, 12, 0));
            row2.setBackground(Color.WHITE);
            row2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
            row2.add(fieldWrapper("STAFF ID", staffIdField));
            row2.add(fieldWrapper("USERNAME", usernameField));

            // Row 2 —
            JPanel row3 = new JPanel(new GridLayout(1, 1));
            row3.setBackground(Color.WHITE);
            row3.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
            row3.add(fieldWrapper("FIRST NAME", firstNameField));
            row3.add(fieldWrapper("SURNAME", surNameField));

            JPanel row4 = new JPanel(new GridLayout(1, 2, 12, 0));
            row4.setBackground(Color.WHITE);
            row4.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
            row4.add(fieldWrapper("EMAIL", emailField));
            row4.add(fieldWrapper("PHONE", phoneField));

            // Row 3
            JPanel row5 = new JPanel(new GridLayout(1, 1, 12, 0));
            row5.setBackground(Color.WHITE);
            row5.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
            row5.add(fieldWrapper("ADDRESS", addressField));

            grid.add(row1);
            grid.add(Box.createVerticalStrut(12));
            grid.add(row2);
            grid.add(Box.createVerticalStrut(12));
            grid.add(row3);
            grid.add(Box.createVerticalStrut(12));
            grid.add(row4);
            grid.add(Box.createVerticalStrut(12));
            grid.add(row5);

            messageLabel = new JLabel(" ");
            messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            messageLabel.setBorder(new EmptyBorder(8, 0, 0, 0));

            FormPanel.add(formTitle, BorderLayout.NORTH);
            FormPanel.add(grid, BorderLayout.CENTER);
            FormPanel.add(messageLabel, BorderLayout.SOUTH);

            JPanel northWrapper = new JPanel(new BorderLayout());
            northWrapper.setBackground(new Color(245, 247, 250));
            northWrapper.add(FormPanel, BorderLayout.CENTER);

            CenterPanel.add(northWrapper, BorderLayout.CENTER);
    }

    // Helper to create a labelled field wrapper
    private JPanel fieldWrapper(String label, JTextField field) {
        JPanel wrapper = new JPanel(new BorderLayout(0, 4));
        wrapper.setBackground(Color.WHITE);

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lbl.setForeground(new Color(107, 114, 128));

        wrapper.add(lbl, BorderLayout.NORTH);
        wrapper.add(field, BorderLayout.CENTER);
        return wrapper;
    }

    // Helper to create a styled action button
    private JButton actionButton(String label, Color bg) {
        JButton btn = new JButton(label);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        return btn;
    }

    private void updateStatus(String status) {
        String id = staffIdField.getText().trim();
        if (id.isEmpty()) { messageLabel.setText("Load an account first."); return; }

        statusLabel.setText(status.toUpperCase());
        messageLabel.setText("Status updated to: " + status);
        messageLabel.setForeground(new Color(0, 97, 0));
    }
}
//            private void createAccount () {
//                try {
//                    String id = merchantIdField.getText().trim();
//                    String name = businessNameField.getText().trim();
//                    String email = emailField.getText().trim();
//                    String phone = phoneField.getText().trim();
//                    String address = addressField.getText().trim();
//
//                    double credit = Double.parseDouble(creditLimitField.getText().trim());
//                    double discount = Double.parseDouble(discountValueField.getText().trim());
//
//                    if (id.isEmpty() || name.isEmpty()) {
//                        messageLabel.setText("ID and Name required.");
//                        return;
//                    }
//                    if (credit < 0) {
//                        messageLabel.setText("Credit limit cannot be negative.");
//                        return;
//                    }
//                    if (discount < 0) {
//                        messageLabel.setText("Discount cannot be negative.");
//                        return;
//                    }
//                    if (accountService.accountExists(id)) {
//                        messageLabel.setText("Account already exists.");
//                        return;
//                    }
//
//                    DiscountPlan plan = new FixedDiscountPlan("Fixed Plan", discount);
//
//                    MerchantAccount account = new MerchantAccount(
//                            id,
//                            name,
//                            email,
//                            phone,
//                            address,
//                            credit,
//                            plan
//                    );
//
//                    accountService.addAccount(account);
//
//                    messageLabel.setText("Account created successfully.");
//
//                } catch (NumberFormatException ex) {
//                    messageLabel.setText("Invalid number input.");
//                } catch (Exception ex) {
//                    messageLabel.setText("Error: " + ex.getMessage());
//                }
//            }

//            private void clearForm () {
//                merchantIdField.setText("");
//                businessNameField.setText("");
//                emailField.setText("");
//                phoneField.setText("");
//                addressField.setText("");
//                creditLimitField.setText("");
//                discountValueField.setText("");
//                messageLabel.setText("");
//            }
