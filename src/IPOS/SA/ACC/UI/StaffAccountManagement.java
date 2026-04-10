package IPOS.SA.ACC.UI;

import IPOS.SA.ACC.Model.Staff;
import IPOS.SA.ACC.Service.StaffAccountService;
import IPOS.SA.UI.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class StaffAccountManagement extends BaseFrame implements Refreshable {

    private final StaffAccountService accountService;
    private final String mode;
    private final String staffIdToLoad;

    private JTextField staffIdField;
    private JTextField usernameField;
    private JTextField firstNameField;
    private JTextField surNameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextField addressField;
    private JComboBox<String> roleDropdown;

    private String selectedRole = "administrator";
    private JLabel statusLabel;
    private JLabel messageLabel;

    public StaffAccountManagement(String fullname, String role, String mode, ScreenRouter router) {
        this(fullname, role, mode, null, router);
    }

    public StaffAccountManagement(String fullname, String role, String mode, String staffId, ScreenRouter router) {
        super(fullname, role, "Staff Account Management", router);
        this.accountService = new StaffAccountService();
        this.mode           = mode;
        this.staffIdToLoad  = staffId;

        buildContent();

        if (staffIdToLoad != null && !staffIdToLoad.isEmpty() && "MANAGE".equals(mode)) {
            staffIdField.setText(staffIdToLoad);
            staffIdField.setEnabled(false);
            loadStaff();
        }
    }

    @Override
    protected String getHeaderTitle() {
        return "Staff Account Management";
    }

    private void buildContent() {
        CenterPanel.setLayout(new BorderLayout());
        CenterPanel.setBackground(new Color(245, 247, 250));

        // ── FORM PANEL ───────────────────────────────────────
        JPanel formPanel = new JPanel(new BorderLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(221, 225, 231), 1),
                new EmptyBorder(20, 20, 20, 20)));

        JLabel formTitle = new JLabel("STAFF ACCOUNT DETAILS");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formTitle.setForeground(Color.BLACK);
        formTitle.setBorder(new EmptyBorder(0, 0, 12, 0));

        JPanel grid = new JPanel();
        grid.setLayout(new BoxLayout(grid, BoxLayout.Y_AXIS));
        grid.setBackground(Color.WHITE);

        staffIdField = createTextField();
        usernameField   = createTextField();
        firstNameField  = createTextField();
        surNameField    = createTextField();
        emailField      = createTextField();
        phoneField      = createTextField();
        addressField    = createTextField();

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
        roleDropdown.addActionListener(e ->
                selectedRole = roleDropdown.getSelectedItem().toString());

        JPanel row1 = row(1);
        row1.add(fieldWrapper("ROLE",       roleDropdown));
        row1.add(fieldWrapper("USERNAME",   usernameField));
        JPanel row2 = row(2);
        row2.add(fieldWrapper("FIRST NAME", firstNameField));
        row2.add(fieldWrapper("SURNAME",    surNameField));
        JPanel row3 = row(2);
        row3.add(fieldWrapper("EMAIL",      emailField));
        row3.add(fieldWrapper("PHONE",      phoneField));
        JPanel row4 = row(1); row4.add(fieldWrapper("ADDRESS",    addressField));

        grid.add(row1); grid.add(Box.createVerticalStrut(12));
        grid.add(row2); grid.add(Box.createVerticalStrut(12));
        grid.add(row3); grid.add(Box.createVerticalStrut(12));
        grid.add(row4);


        messageLabel = new JLabel(" ");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        messageLabel.setBorder(new EmptyBorder(8, 0, 0, 0));

        formPanel.add(formTitle,    BorderLayout.NORTH);
        formPanel.add(grid,         BorderLayout.CENTER);
        formPanel.add(messageLabel, BorderLayout.SOUTH);

        // ── RIGHT COLUMN ─────────────────────────────────────
        JPanel rightColumn = new JPanel();
        rightColumn.setLayout(new BoxLayout(rightColumn, BoxLayout.Y_AXIS));
        rightColumn.setBackground(new Color(245, 247, 250));
        rightColumn.setPreferredSize(new Dimension(210, 0));

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
        actionsCard.add(actionsTitle);
        actionsCard.add(Box.createVerticalStrut(10));

        switch (mode) {
            case "CREATE": addCreateButtons(actionsCard); break;
            case "MANAGE": addManageButtons(actionsCard); break;
        }

        rightColumn.add(statusPanel);
        rightColumn.add(Box.createVerticalStrut(12));
        rightColumn.add(actionsCard);

        CenterPanel.add(formPanel,   BorderLayout.CENTER);
        CenterPanel.add(rightColumn, BorderLayout.EAST);
    }

    private void addCreateButtons(JPanel actionsCard) {
        JButton createBtn = actionButton("Create Account", new Color(30, 70, 90));
        JButton clearBtn  = actionButton("Clear",          new Color(107, 114, 128));
        JButton backBtn   = actionButton("← Back",         new Color(17, 24, 39));

        createBtn.addActionListener(e -> createStaff());
        clearBtn.addActionListener(e  -> clearForm());
        backBtn.addActionListener(e -> router.goTo(AppFrame.SCREEN_STAFF_LIST));

        actionsCard.add(createBtn); actionsCard.add(Box.createVerticalStrut(8));
        actionsCard.add(clearBtn);  actionsCard.add(Box.createVerticalStrut(8));
        actionsCard.add(backBtn);
    }

    private void addManageButtons(JPanel actionsCard) {
        JButton loadBtn   = actionButton("Load Account",   new Color(17, 24, 39));
        JButton updateBtn = actionButton("Update Account", new Color(30, 70, 90));
        JButton deleteBtn = actionButton("Delete Account", new Color(127, 29, 29));
        JButton clearBtn  = actionButton("Clear",          new Color(107, 114, 128));
        JButton backBtn   = actionButton("← Back",         new Color(17, 24, 39));

        loadBtn.addActionListener(e   -> loadStaff());
        updateBtn.addActionListener(e -> updateStaff());
        deleteBtn.addActionListener(e -> deactivateStaff());
        clearBtn.addActionListener(e  -> clearForm());
        backBtn.addActionListener(e -> router.goTo(AppFrame.SCREEN_STAFF_LIST));

        actionsCard.add(loadBtn);   actionsCard.add(Box.createVerticalStrut(8));
        actionsCard.add(updateBtn); actionsCard.add(Box.createVerticalStrut(8));
        actionsCard.add(deleteBtn); actionsCard.add(Box.createVerticalStrut(8));
        actionsCard.add(clearBtn);  actionsCard.add(Box.createVerticalStrut(8));
        actionsCard.add(backBtn);
    }

    // ── BUSINESS LOGIC ───────────────────────────────────────
    private void createStaff() {
        try {
            String username  = usernameField.getText().trim();
            String firstName = firstNameField.getText().trim();
            String surName   = surNameField.getText().trim();
            String email     = emailField.getText().trim();
            String phone     = phoneField.getText().trim();

            // Required field checks
            if (username.isEmpty()) {
                setMessage("Username is required.", false); return;
            }
            if (firstName.isEmpty()) {
                setMessage("First name is required.", false); return;
            }
            if (surName.isEmpty()) {
                setMessage("Surname is required.", false); return;
            }

            // Username format — no spaces
            if (username.contains(" ")) {
                setMessage("Username cannot contain spaces.", false); return;
            }

            // Username minimum length
            if (username.length() < 3) {
                setMessage("Username must be at least 3 characters.", false); return;
            }

            // First name — letters only
            if (!firstName.matches("[a-zA-Z\\s-]+")) {
                setMessage("First name can only contain letters.", false); return;
            }

            // Surname — letters only
            if (!surName.matches("[a-zA-Z\\s-]+")) {
                setMessage("Surname can only contain letters.", false); return;
            }

            // Email format check if provided
            if (!email.isEmpty() && (!email.contains("@") || !email.contains("."))) {
                setMessage("Please enter a valid email address.", false); return;
            }

            // Phone format check if provided
            if (!phone.isEmpty() && !phone.matches("[0-9+\\-\\s()]+")) {
                setMessage("Phone number contains invalid characters.", false); return;
            }

            Staff staff = new Staff("", username, firstName, surName,
                    email, phone, addressField.getText().trim(), selectedRole);

            if (accountService.createStaff(staff)) {
                clearForm();
                setMessage("Staff account created. Default password: " + username + "123", true);
            } else {
                setMessage("Username already exists.", false);
            }
        } catch (Exception ex) {
            setMessage("Error: " + ex.getMessage(), false);
        }
    }

    private void updateStaff() {
        String id = staffIdField.getText().trim();
        if (id.isEmpty()) { setMessage("Load a staff account first.", false); return; }

        String username  = usernameField.getText().trim();
        String firstName = firstNameField.getText().trim();
        String surName   = surNameField.getText().trim();
        String email     = emailField.getText().trim();
        String phone     = phoneField.getText().trim();

        // Required field checks
        if (username.isEmpty()) {
            setMessage("Username is required.", false); return;
        }
        if (firstName.isEmpty()) {
            setMessage("First name is required.", false); return;
        }
        if (surName.isEmpty()) {
            setMessage("Surname is required.", false); return;
        }

        // Username format — no spaces
        if (username.contains(" ")) {
            setMessage("Username cannot contain spaces.", false); return;
        }

        // First name — letters only
        if (!firstName.matches("[a-zA-Z\\s-]+")) {
            setMessage("First name can only contain letters.", false); return;
        }

        // Surname — letters only
        if (!surName.matches("[a-zA-Z\\s-]+")) {
            setMessage("Surname can only contain letters.", false); return;
        }

        // Email format check if provided
        if (!email.isEmpty() && (!email.contains("@") || !email.contains("."))) {
            setMessage("Please enter a valid email address.", false); return;
        }

        // Phone format check if provided
        if (!phone.isEmpty() && !phone.matches("[0-9+\\-\\s()]+")) {
            setMessage("Phone number contains invalid characters.", false); return;
        }

        try {
            Staff existing = accountService.loadStaff(id);
            if (existing == null) { setMessage("Staff not found.", false); return; }

            Staff staff = new Staff(id, username, firstName, surName,
                    email, phone, addressField.getText().trim(), selectedRole);
            staff.setActive(existing.isActive());

            if (accountService.updateStaff(staff)) {
                setMessage("Staff updated successfully.", true);
                loadStaff();
            } else {
                setMessage("Failed to update staff.", false);
            }
        } catch (Exception ex) {
            setMessage("Error: " + ex.getMessage(), false);
        }
    }

    private void loadStaff() {
        String id = staffIdField.getText().trim();
        if (id.isEmpty()) { setMessage("Enter a Staff ID to load.", false); return; }
        try {
            Staff staff = accountService.loadStaff(id);
            if (staff != null) {
                populateForm(staff);
                statusLabel.setText(staff.isActive() ? "ACTIVE" : "INACTIVE");
                setMessage("Staff loaded successfully.", true);
            } else {
                setMessage("Staff not found.", false);
            }
        } catch (Exception ex) {
            setMessage("Error: " + ex.getMessage(), false);
        }
    }


    private void deactivateStaff() {
        String id = staffIdField.getText().trim();
        if (id.isEmpty()) { setMessage("Load a staff account first.", false); return; }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete staff account " + id + "?",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (accountService.deleteStaff(id)) {
                    statusLabel.setText("INACTIVE");
                    setMessage("Staff deleted successfully.", true);
                } else {
                    setMessage("Failed to delete staff.", false);
                }
            } catch (Exception ex) {
                setMessage("Error: " + ex.getMessage(), false);
            }
        }
    }

    // ── HELPERS ──────────────────────────────────────────────
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

    private JTextField createTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(221, 225, 231)),
                BorderFactory.createEmptyBorder(0, 12, 0, 12)));
        return field;
    }

    private JPanel row(int cols) {
        JPanel p = new JPanel(new GridLayout(1, cols, 12, 0));
        p.setBackground(Color.WHITE);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        return p;
    }

    private JPanel fieldWrapper(String label, JTextField field) {
        JPanel wrapper = new JPanel(new BorderLayout(0, 4));
        wrapper.setBackground(Color.WHITE);
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lbl.setForeground(new Color(107, 114, 128));
        wrapper.add(lbl,   BorderLayout.NORTH);
        wrapper.add(field, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel fieldWrapper(String label, JComboBox<String> combo) {
        JPanel wrapper = new JPanel(new BorderLayout(0, 4));
        wrapper.setBackground(Color.WHITE);
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lbl.setForeground(new Color(107, 114, 128));
        wrapper.add(lbl,   BorderLayout.NORTH);
        wrapper.add(combo, BorderLayout.CENTER);
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

    @Override
    public void onShow() {
        clearForm();
    }
}