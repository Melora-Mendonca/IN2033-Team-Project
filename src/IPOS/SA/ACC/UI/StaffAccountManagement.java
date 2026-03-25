package IPOS.SA.ACC.UI;

import IPOS.SA.ACC.Service.StaffAccountService;
import IPOS.SA.CAT.UI.Catalogue;
import IPOS.SA.ACC.Model.Staff;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class StaffAccountManagement extends JFrame {

    private final StaffAccountService accountService;

    // For the user's role and full name that is displayed in the header
    private String fullname;
    private String role;
    private String mode;

    private JTextField staffIdField;
    private JTextField usernameField;
    private JTextField firstNameField;
    private JTextField surNameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextField addressField;
    private JComboBox<String> roleDropdown;

    private String selectedRole = "Administrator";
    private JLabel statusLabel;
    private JLabel messageLabel;


    // Panels for the form layout, header and navigation
    private JPanel MainPanel;
    private JPanel ContentPanel;
    private JPanel NavPanel;
    private JPanel HeaderPanel;
    private JPanel CenterPanel;
    private JPanel FormPanel;
    private JPanel StatusPanel;
    private JPanel FooterPanel;
    private JLabel headerLabel;
    private JLabel navIcon;
    private JButton logoutBtn;
    private JSeparator divider;


    public StaffAccountManagement(String fullname, String role, String mode) {
        this.accountService = new StaffAccountService();
        this.fullname = fullname;
        this.role = role;
        this.mode = mode;

        setTitle("Staff Account Management");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(MainPanel);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);

        // Creates the standard header and navigation panel across all the IPOS-SA forms
        initializeUI();

        setVisible(true);

    }

    private void initializeUI() {
        createHeaderPanel();
        createNavPanel();
        createFormPanel();
        createStatusPanel();
    }

    private void createHeaderPanel() {
        HeaderPanel.setLayout(new BoxLayout(HeaderPanel, BoxLayout.X_AXIS));
        HeaderPanel.setPreferredSize(new Dimension(1000, 54));
        HeaderPanel.setBackground(new Color(240, 252, 255));
        HeaderPanel.setBorder(BorderFactory.createEmptyBorder(0, 24, 0, 24));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        headerLabel = new JLabel("Staff Account Management");
        headerLabel.setForeground(Color.BLACK);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));

        textPanel.add(headerLabel);
        HeaderPanel.add(textPanel);
    }
    private void createNavPanel() {
        NavPanel.setLayout(new BoxLayout(NavPanel, BoxLayout.Y_AXIS));
        NavPanel.setBackground(new Color(14, 37, 48));
        NavPanel.setBorder(BorderFactory.createEmptyBorder(20, 16, 20, 16));
        NavPanel.setPreferredSize(new Dimension(220, 0));

        ImageIcon Icon = new ImageIcon(new ImageIcon("data/Logo.png")
                .getImage().getScaledInstance(80, 60, Image.SCALE_SMOOTH));
        navIcon = new JLabel(Icon);

        NavPanel.add(navIcon);
        NavPanel.add(Box.createVerticalStrut(16));

        NavPanel.add(buildNavButton("Overview", false));
        NavPanel.add(Box.createVerticalStrut(4));
        NavPanel.add(buildNavButton("Catalogue", false));
        NavPanel.add(Box.createVerticalStrut(4));
        NavPanel.add(buildNavButton("Orders", false));
        NavPanel.add(Box.createVerticalStrut(4));

        addExpandableNavItem(NavPanel, "Merchants", new String[]{
                "View Merchant Orders", "View Merchant Invoices"
        });

        addExpandableNavItem(NavPanel, "Accounts", new String[]{
                "Create Merchant Account", "Manage Merchant Accounts", "Commercial Applications"
        });

        addExpandableNavItem(NavPanel, "Staff", new String[]{
                "View All Staff", "Create Staff Account", "Manage Staff Account",
        });

        NavPanel.add(buildNavButton("Reports", false));
        NavPanel.add(Box.createVerticalStrut(4));
        NavPanel.add(buildNavButton("Settings", false));
        NavPanel.add(Box.createVerticalStrut(4));

        divider = new JSeparator();
        divider.setForeground(Color.WHITE);
        divider.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        NavPanel.add(divider);
        NavPanel.add(Box.createVerticalGlue());

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
        NavPanel.add(logoutBtn);
    }

    private void createFormPanel() {
        CenterPanel = new JPanel(new BorderLayout());
        CenterPanel.setBackground(new Color(245, 247, 250));
        CenterPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        FormPanel = new JPanel(new BorderLayout());
        FormPanel.setBackground(Color.WHITE);
        FormPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(221, 225, 231), 1),
                new EmptyBorder(20, 20, 20, 20)));

        JLabel formTitle = new JLabel("STAFF ACCOUNT DETAILS");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formTitle.setForeground(Color.BLACK);
        formTitle.setBorder(new EmptyBorder(0, 0, 12, 0));

        JPanel grid = new JPanel();
        grid.setLayout(new BoxLayout(grid, BoxLayout.Y_AXIS));
        grid.setBackground(Color.WHITE);

        // Create fields
        staffIdField = createTextField();
        usernameField = createTextField();
        firstNameField = createTextField();
        surNameField = createTextField();
        emailField = createTextField();
        phoneField = createTextField();
        addressField = createTextField();

        roleDropdown = new JComboBox<>(new String[]{
                "Administrator",
                "Director of Operations",
                "Senior Accountant",
                "Accountant",
                "Warehouse Employee",
                "Delivery Employee"
        });
        roleDropdown.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        roleDropdown.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        roleDropdown.addActionListener(e -> {
            selectedRole = roleDropdown.getSelectedItem().toString();
        });

        // Row 1 - Role selection
        JPanel row1 = new JPanel(new GridLayout(1, 1));
        row1.setBackground(Color.WHITE);
        row1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        row1.add(fieldWrapper("ROLE", roleDropdown));

        // Row 2 - Staff ID and Username
        JPanel row2 = new JPanel(new GridLayout(1, 2, 12, 0));
        row2.setBackground(Color.WHITE);
        row2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        row2.add(fieldWrapper("STAFF ID", staffIdField));
        row2.add(fieldWrapper("USERNAME", usernameField));

        // Row 3 - First Name and Surname
        JPanel row3 = new JPanel(new GridLayout(1, 2, 12, 0));
        row3.setBackground(Color.WHITE);
        row3.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        row3.add(fieldWrapper("FIRST NAME", firstNameField));
        row3.add(fieldWrapper("SURNAME", surNameField));

        // Row 4 - Email and Phone
        JPanel row4 = new JPanel(new GridLayout(1, 2, 12, 0));
        row4.setBackground(Color.WHITE);
        row4.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        row4.add(fieldWrapper("EMAIL", emailField));
        row4.add(fieldWrapper("PHONE", phoneField));

        // Row 5 - Address
        JPanel row5 = new JPanel(new GridLayout(1, 1));
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

        CenterPanel.add(FormPanel, BorderLayout.CENTER);

        ContentPanel.setLayout(new BorderLayout());
        ContentPanel.add(CenterPanel, BorderLayout.CENTER);
    }

    private void createStatusPanel() {
        JPanel rightColumn = new JPanel();
        rightColumn.setLayout(new BoxLayout(rightColumn, BoxLayout.Y_AXIS));
        rightColumn.setBackground(new Color(245, 247, 250));
        rightColumn.setPreferredSize(new Dimension(200, 0));

        JLabel statusTitle = new JLabel("ACCOUNT STATUS");
        statusTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        statusTitle.setForeground(new Color(107, 114, 128));

        statusLabel = new JLabel("--");
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        statusLabel.setForeground(new Color(17, 24, 39));
        statusLabel.setBorder(new EmptyBorder(6, 0, 8, 0));

        JPanel statusPanel = new JPanel();
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));
        statusPanel.setBackground(Color.WHITE);
        statusPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(221, 225, 231), 1),
                new EmptyBorder(16, 16, 16, 16)));
        statusPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        statusPanel.add(statusTitle);
        statusPanel.add(statusLabel);

        // Actions card
        JPanel actionsCard = new JPanel();
        actionsCard.setLayout(new BoxLayout(actionsCard, BoxLayout.Y_AXIS));
        actionsCard.setBackground(Color.WHITE);
        actionsCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(221, 225, 231), 1),
                new EmptyBorder(16, 16, 16, 16)));

        JLabel actionsTitle = new JLabel("ACTIONS");
        actionsTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        actionsTitle.setForeground(new Color(107, 114, 128));
        actionsTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        switch (mode) {
            case "CREATE":
                addCreateButtons(actionsCard);
                break;
            case "MANAGE":
                addManageButtons(actionsCard);
                break;
        }

        rightColumn.add(statusPanel);
        rightColumn.add(Box.createVerticalStrut(12));
        rightColumn.add(actionsCard);

        CenterPanel.add(rightColumn, BorderLayout.EAST);
    }

    private void addCreateButtons(JPanel actionsCard) {
        JButton createBtn = actionButton("Create Account", new Color(30, 70, 90));
        JButton clearBtn = actionButton("Clear", new Color(107, 114, 128));
        JButton backBtn = actionButton("← Back", new Color(17, 24, 39));

        createBtn.addActionListener(e -> createStaff());
        clearBtn.addActionListener(e -> clearForm());
        backBtn.addActionListener(e -> {
            dispose();
            new AdminDashboard(fullname, role);
        });

        actionsCard.add(createBtn);
        actionsCard.add(Box.createVerticalStrut(8));
        actionsCard.add(clearBtn);
        actionsCard.add(Box.createVerticalStrut(8));
        actionsCard.add(backBtn);
    }

    private void addManageButtons(JPanel actionsCard) {
        JButton loadBtn = actionButton("Load Account", new Color(17, 24, 39));
        JButton updateBtn = actionButton("Update Account", new Color(30, 70, 90));
        JButton deactivateBtn = actionButton("Deactivate Account", new Color(127, 29, 29));
        JButton reactivateBtn = actionButton("Reactivate Account", new Color(20, 83, 45));
        JButton clearBtn = actionButton("Clear", new Color(107, 114, 128));
        JButton backBtn = actionButton("← Back", new Color(17, 24, 39));

        loadBtn.addActionListener(e -> loadStaff());
        updateBtn.addActionListener(e -> updateStaff());
        deactivateBtn.addActionListener(e -> deactivateStaff());
        reactivateBtn.addActionListener(e -> reactivateStaff());
        clearBtn.addActionListener(e -> clearForm());
        backBtn.addActionListener(e -> {
            dispose();
            new AdminDashboard(fullname, role);
        });

        actionsCard.add(loadBtn);
        actionsCard.add(Box.createVerticalStrut(8));
        actionsCard.add(updateBtn);
        actionsCard.add(Box.createVerticalStrut(8));
        actionsCard.add(deactivateBtn);
        actionsCard.add(Box.createVerticalStrut(8));
        actionsCard.add(reactivateBtn);
        actionsCard.add(Box.createVerticalStrut(8));
        actionsCard.add(clearBtn);
        actionsCard.add(Box.createVerticalStrut(8));
        actionsCard.add(backBtn);
    }

    // Business Logic Methods
    private void createStaff() {
        try {
            String staffId = staffIdField.getText().trim();
            String username = usernameField.getText().trim();
            String firstName = firstNameField.getText().trim();
            String surName = surNameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String address = addressField.getText().trim();

            // Validation
            if (staffId.isEmpty()) {
                setMessage("Staff ID is required.", false);
                return;
            }
            if (username.isEmpty()) {
                setMessage("Username is required.", false);
                return;
            }
            if (firstName.isEmpty() || surName.isEmpty()) {
                setMessage("First name and surname are required.", false);
                return;
            }

            Staff staff = new Staff(staffId, username, firstName, surName,
                    email, phone, address, selectedRole);

            if (accountService.createStaff(staff)) {
                setMessage("Staff account created successfully.", true);
                clearForm();
            } else {
                setMessage("Staff ID or Username already exists.", false);
            }

        } catch (Exception ex) {
            setMessage("Error: " + ex.getMessage(), false);
            ex.printStackTrace();
        }
    }

    private void loadStaff() {
        String staffId = staffIdField.getText().trim();
        if (staffId.isEmpty()) {
            setMessage("Enter a Staff ID to load.", false);
            return;
        }

        try {
            Staff staff = accountService.loadStaff(staffId);

            if (staff != null) {
                populateForm(staff);
                statusLabel.setText(staff.isActive() ? "ACTIVE" : "INACTIVE");
                setMessage("Staff loaded successfully.", true);
            } else {
                setMessage("Staff not found.", false);
            }
        } catch (Exception ex) {
            setMessage("Error: " + ex.getMessage(), false);
            ex.printStackTrace();
        }
    }

    private void updateStaff() {
        String staffId = staffIdField.getText().trim();
        if (staffId.isEmpty()) {
            setMessage("Load a staff account first.", false);
            return;
        }

        try {
            Staff existingStaff = accountService.loadStaff(staffId);
            if (existingStaff == null) {
                setMessage("Staff not found.", false);
                return;
            }

            Staff staff = new Staff(
                    staffId,
                    usernameField.getText().trim(),
                    firstNameField.getText().trim(),
                    surNameField.getText().trim(),
                    emailField.getText().trim(),
                    phoneField.getText().trim(),
                    addressField.getText().trim(),
                    selectedRole
            );
            staff.setActive(existingStaff.isActive());

            if (accountService.updateStaff(staff)) {
                setMessage("Staff updated successfully.", true);
                loadStaff();
            } else {
                setMessage("Failed to update staff.", false);
            }

        } catch (Exception ex) {
            setMessage("Error: " + ex.getMessage(), false);
            ex.printStackTrace();
        }
    }

    private void deactivateStaff() {
        String staffId = staffIdField.getText().trim();
        if (staffId.isEmpty()) {
            setMessage("Load a staff account first.", false);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to deactivate staff account " + staffId + "?",
                "Confirm Deactivation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (accountService.deactivateStaff(staffId)) {
                    statusLabel.setText("INACTIVE");
                    setMessage("Staff deactivated successfully.", true);
                } else {
                    setMessage("Failed to deactivate staff.", false);
                }
            } catch (Exception ex) {
                setMessage("Error: " + ex.getMessage(), false);
                ex.printStackTrace();
            }
        }
    }

    private void reactivateStaff() {
        String staffId = staffIdField.getText().trim();
        if (staffId.isEmpty()) {
            setMessage("Load a staff account first.", false);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to reactivate staff account " + staffId + "?",
                "Confirm Reactivation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (accountService.reactivateStaff(staffId)) {
                    statusLabel.setText("ACTIVE");
                    setMessage("Staff reactivated successfully.", true);
                } else {
                    setMessage("Failed to reactivate staff.", false);
                }
            } catch (Exception ex) {
                setMessage("Error: " + ex.getMessage(), false);
                ex.printStackTrace();
            }
        }
    }

    // Helper Methods
    private void populateForm(Staff staff) {
        staffIdField.setText(staff.getStaffId());
        usernameField.setText(staff.getUsername());
        firstNameField.setText(staff.getFirstName());
        surNameField.setText(staff.getSurName());
        emailField.setText(staff.getEmail());
        phoneField.setText(staff.getPhone());
        addressField.setText(staff.getAddress());
        roleDropdown.setSelectedItem(staff.getRole());
        selectedRole = staff.getRole();
    }

    private void clearForm() {
        staffIdField.setText("");
        usernameField.setText("");
        firstNameField.setText("");
        surNameField.setText("");
        emailField.setText("");
        phoneField.setText("");
        addressField.setText("");
        roleDropdown.setSelectedIndex(0);
        selectedRole = "Administrator";
        statusLabel.setText("--");
        setMessage("", true);
    }

    private void setMessage(String text, boolean success) {
        messageLabel.setText(text);
        messageLabel.setForeground(success ? new Color(0, 97, 0) : new Color(200, 80, 80));
    }

    // UI Helper Methods
    private JTextField createTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(221, 225, 231)),
                BorderFactory.createEmptyBorder(0, 12, 0, 12)));
        return field;
    }

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

    private JPanel fieldWrapper(String label, JComboBox<String> comboBox) {
        JPanel wrapper = new JPanel(new BorderLayout(0, 4));
        wrapper.setBackground(Color.WHITE);
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lbl.setForeground(new Color(107, 114, 128));
        wrapper.add(lbl, BorderLayout.NORTH);
        wrapper.add(comboBox, BorderLayout.CENTER);
        return wrapper;
    }

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
                new AccountManagement(fullname, role, "MANAGE");
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
                new StaffAccountManagement(fullname, role, "CREATE");
                break;
            case "Manage Staff Account":
                dispose();
                new StaffAccountManagement(fullname, role, "MANAGE");
                break;
            case "View Merchant Orders":
            case "View Merchant Invoices":
            default:
                JOptionPane.showMessageDialog(this, label + " — coming soon.");
                break;
        }
    }

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
                    break;
                case "Overview":
                    new AdminDashboard(fullname, role);
                    break;
            }
        });

        return btn;
    }

    private void handleLogout() {
        dispose();
        new LoginForm();
    }
}