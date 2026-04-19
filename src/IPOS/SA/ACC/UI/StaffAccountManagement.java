package IPOS.SA.ACC.UI;

import IPOS.SA.ACC.Model.Staff;
import IPOS.SA.ACC.Service.StaffAccountService;
import IPOS.SA.UI.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Form screen for creating and managing staff accounts in IPOS-SA.
 * Operates in two modes:
 * - CREATE ; shows a blank form with password fields to create a new staff account
 * - MANAGE ; loads an existing staff account by ID for viewing, updating or deleting
 *
 * Accessible to Administrators only.
 */
public class StaffAccountManagement extends BaseFrame implements Refreshable {

    private final StaffAccountService accountService;
    private final String mode; // the current mode, either create or manage
    private final String staffIdToLoad;

    private JTextField staffIdField;
    private JTextField usernameField;
    private JTextField firstNameField;
    private JTextField surNameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextField addressField;
    private JComboBox<String> roleDropdown;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;

    private String selectedRole = "Administrator";
    private JLabel statusLabel;
    private JLabel messageLabel;

    /**
     * Constructor ; opens the form without a pre-loaded staff ID.
     * Used when navigating directly from the nav menu.
     *
     * @param fullname the full name of the logged-in user
     * @param role the role of the logged-in user
     * @param mode the form mode ; "CREATE" or "MANAGE"
     * @param router the screen router used for navigation
     */
    public StaffAccountManagement(String fullname, String role, String mode, ScreenRouter router) {
        this(fullname, role, mode, null, router);
    }

    /**
     * Constructor ; opens the form and auto-loads a staff account.
     * Used when navigating from the staff list with a selected staff member.
     *
     * @param fullname the full name of the logged-in user
     * @param role the role of the logged-in user
     * @param mode the form mode ; "CREATE" or "MANAGE"
     * @param staffId the staff ID to auto-load (can be null)
     * @param router the screen router used for navigation
     */
    public StaffAccountManagement(String fullname, String role, String mode, String staffId, ScreenRouter router) {
        super(fullname, role, "Staff Account Management", router);
        this.accountService = new StaffAccountService();
        this.mode           = mode;
        this.staffIdToLoad  = staffId;

        // Creates the main form panel for the GUI
        buildContent();

        // Auto loads the staff account if an ID was passed in
        if (staffIdToLoad != null && !staffIdToLoad.isEmpty() && "MANAGE".equals(mode)) {
            staffIdField.setText(staffIdToLoad);
            staffIdField.setEnabled(false);
            loadStaff();
        }
    }

    /**
     * Returns the title displayed in the page header.
     *
     * @return the header title string
     */
    @Override
    protected String getHeaderTitle() {
        return "Staff Account Management";
    }

    /**
     * Builds and arranges all UI components for this screen.
     * Creates the form panel on the left and the actions panel on the right.
     * Password fields are only shown in CREATE mode.
     * Role dropdown is disabled in MANAGE mode.
     */

    private void buildContent() {
        CenterPanel.setLayout(new BorderLayout());
        CenterPanel.setBackground(new Color(245, 247, 250));

        // Creates an empty panel for the form to sit in
        JPanel formPanel = new JPanel(new BorderLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(221, 225, 231), 1),
                new EmptyBorder(20, 20, 20, 20)));

        // Adds a title label to the form
        JLabel formTitle = new JLabel("STAFF ACCOUNT DETAILS");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formTitle.setForeground(Color.BLACK);
        formTitle.setBorder(new EmptyBorder(0, 0, 12, 0));

        // Creates a smaller panel inside the frame panel for the text feilds
        JPanel grid = new JPanel();
        grid.setLayout(new BoxLayout(grid, BoxLayout.Y_AXIS));
        grid.setBackground(Color.WHITE);

        // creates the data entry feilds
        staffIdField = createTextField();
        usernameField = createTextField();
        firstNameField = createTextField();
        surNameField = createTextField();
        emailField = createTextField();
        phoneField = createTextField();
        addressField = createTextField();
        passwordField = createPasswordField();
        confirmPasswordField = createPasswordField();

        // Creates a dropdown list for all user roles
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

        // Aligns the feilds neatly on the form panel
        JPanel row1 = row(2);
        row1.add(fieldWrapper("ROLE", roleDropdown));
        row1.add(fieldWrapper("USERNAME", usernameField));

        JPanel row2 = row(2);
        row2.add(fieldWrapper("FIRST NAME", firstNameField));
        row2.add(fieldWrapper("SURNAME", surNameField));

        JPanel row3 = row(2);
        row3.add(fieldWrapper("EMAIL", emailField));
        row3.add(fieldWrapper("PHONE", phoneField));

        JPanel row4 = row(1);
        row4.add(fieldWrapper("ADDRESS", addressField));

        // Adds all the text fields to the grid with equal spacing between them
        grid.add(row1);
        grid.add(Box.createVerticalStrut(12));
        grid.add(row2);
        grid.add(Box.createVerticalStrut(12));
        grid.add(row3);
        grid.add(Box.createVerticalStrut(12));
        grid.add(row4);
        grid.add(Box.createVerticalStrut(12));

        // If the user is in create mode, then only they can create a password for the account user
        if ("CREATE".equals(mode)) {
            JPanel row5 = row(2);
            row5.add(fieldWrapperPassword("PASSWORD", passwordField));
            row5.add(fieldWrapperPassword("CONFIRM PASSWORD", confirmPasswordField));
            grid.add(row5); grid.add(Box.createVerticalStrut(12));
        }

        // message label created to show any errors at the bottom of the form
        messageLabel = new JLabel(" ");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        messageLabel.setBorder(new EmptyBorder(8, 0, 0, 0));

        // Adds the title, form grid and error label all to the main form panel
        formPanel.add(formTitle,    BorderLayout.NORTH);
        formPanel.add(grid,         BorderLayout.CENTER);
        formPanel.add(messageLabel, BorderLayout.SOUTH);

        // Right column containing the status card and actions card
        JPanel rightColumn = new JPanel();
        rightColumn.setLayout(new BoxLayout(rightColumn, BoxLayout.Y_AXIS));
        rightColumn.setBackground(new Color(245, 247, 250));
        rightColumn.setPreferredSize(new Dimension(210, 0));

        // Creates the status label
        JLabel statusTitle = new JLabel("ACCOUNT STATUS");
        statusTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        statusTitle.setForeground(new Color(107, 114, 128));

        statusLabel = new JLabel("--");
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        statusLabel.setForeground(new Color(17, 24, 39));
        statusLabel.setBorder(new EmptyBorder(6, 0, 8, 0));

        // creates a wrapper panel to contain the status label
        JPanel statusPanel = new JPanel();
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));
        statusPanel.setBackground(Color.WHITE);
        statusPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(221, 225, 231), 1),
                new EmptyBorder(16, 16, 16, 16)));
        statusPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        statusPanel.add(statusTitle);
        statusPanel.add(statusLabel);

        // Creates a panel to store the action buttons
        JPanel actionsCard = new JPanel();
        actionsCard.setLayout(new BoxLayout(actionsCard, BoxLayout.Y_AXIS));
        actionsCard.setBackground(Color.WHITE);
        actionsCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(221, 225, 231), 1),
                new EmptyBorder(16, 16, 16, 16)));

        // Creates a label to identify the buttons
        JLabel actionsTitle = new JLabel("ACTIONS");
        actionsTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        actionsTitle.setForeground(new Color(107, 114, 128));
        actionsTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        actionsCard.add(actionsTitle);
        actionsCard.add(Box.createVerticalStrut(10));

        // Uses the editing mode to decide which buttons to show the user based on how the form is being used
        switch (mode) {
            case "CREATE": addCreateButtons(actionsCard); break;
            case "MANAGE":
                addManageButtons(actionsCard);
                roleDropdown.setEnabled(false);
                break;
        }

        // Adds the status panel and action buttons to the right hand side of the account management form
        rightColumn.add(statusPanel);
        rightColumn.add(Box.createVerticalStrut(12));
        rightColumn.add(actionsCard);

        CenterPanel.add(formPanel,   BorderLayout.CENTER);
        CenterPanel.add(rightColumn, BorderLayout.EAST);
    }

    /**
     * Adds action buttons for CREATE mode ; Create Account, Clear and Back.
     *
     * @param actionsCard the panel to add the buttons to
     */
    private void addCreateButtons(JPanel actionsCard) {
        JButton createBtn = actionButton("Create Account", new Color(30, 70, 90));
        JButton clearBtn  = actionButton("Clear",          new Color(107, 114, 128));
        JButton backBtn   = actionButton("← Back",         new Color(17, 24, 39));

        createBtn.addActionListener(e -> createStaff());
        clearBtn.addActionListener(e  -> clearForm());
        backBtn.addActionListener(e   -> router.goTo(AppFrame.SCREEN_STAFF_LIST));

        actionsCard.add(createBtn);
        actionsCard.add(Box.createVerticalStrut(8));
        actionsCard.add(clearBtn);
        actionsCard.add(Box.createVerticalStrut(8));
        actionsCard.add(backBtn);
    }

    /**
     * Adds action buttons for MANAGE mode ;
     * Load Account, Update Account, Delete Account, Clear and Back.
     *
     * @param actionsCard the panel to add the buttons to
     */
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
        backBtn.addActionListener(e   -> router.goTo(AppFrame.SCREEN_STAFF_LIST));

        actionsCard.add(loadBtn);   actionsCard.add(Box.createVerticalStrut(8));
        actionsCard.add(updateBtn); actionsCard.add(Box.createVerticalStrut(8));
        actionsCard.add(deleteBtn); actionsCard.add(Box.createVerticalStrut(8));
        actionsCard.add(clearBtn);  actionsCard.add(Box.createVerticalStrut(8));
        actionsCard.add(backBtn);
    }

    /**
     * Handles the Create Account action.
     * Validates all input fields and creates a new staff account
     * via the service layer if validation passes.
     */
    private void createStaff() {
        try {
            String username  = usernameField.getText().trim();
            String firstName = firstNameField.getText().trim();
            String surName   = surNameField.getText().trim();
            String email     = emailField.getText().trim();
            String phone     = phoneField.getText().trim();
            String password  = new String(passwordField.getPassword()).trim();
            String confirm   = new String(confirmPasswordField.getPassword()).trim();

            // Required field checks
            if (username.isEmpty())  { setMessage("Username is required.", false); return; }
            if (firstName.isEmpty()) { setMessage("First name is required.", false); return; }
            if (surName.isEmpty())   { setMessage("Surname is required.", false); return; }
            if (password.isEmpty())  { setMessage("Password is required.", false); return; }

            // Password validation
            if (!password.equals(confirm)) { setMessage("Passwords do not match.", false); return; }
            if (password.length() < 6) { setMessage("Password must be at least 6 characters.", false); return; }

            // Username format validation
            if (username.contains(" ")) { setMessage("Username cannot contain spaces.", false); return; }
            if (username.length() < 3)  { setMessage("Username must be at least 3 characters.", false); return; }

            // Name format ; letters only
            if (!firstName.matches("[a-zA-Z\\s-]+")) { setMessage("First name can only contain letters.", false); return; }
            if (!surName.matches("[a-zA-Z\\s-]+"))   { setMessage("Surname can only contain letters.", false); return; }

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

            if (accountService.createStaff(staff, password)) {
                clearForm();
                setMessage("Staff account created successfully.", true);
            } else {
                setMessage("Username already exists.", false);
            }
        } catch (Exception ex) {
            setMessage("Error: " + ex.getMessage(), false);
        }
    }

    /**
     * Handles the Update Account action.
     * Validates all input fields and updates the existing staff record
     * via the service layer if validation passes.
     */
    private void updateStaff() {
        String id        = staffIdField.getText().trim();
        String username  = usernameField.getText().trim();
        String firstName = firstNameField.getText().trim();
        String surName   = surNameField.getText().trim();
        String email     = emailField.getText().trim();
        String phone     = phoneField.getText().trim();

        // Required field checks
        if (id.isEmpty())        { setMessage("Load a staff account first.", false); return; }
        if (username.isEmpty())  { setMessage("Username is required.", false); return; }
        if (firstName.isEmpty()) { setMessage("First name is required.", false); return; }
        if (surName.isEmpty())   { setMessage("Surname is required.", false); return; }

        // Username format check
        if (username.contains(" ")) { setMessage("Username cannot contain spaces.", false); return; }

        // Name format ; letters only
        if (!firstName.matches("[a-zA-Z\\s-]+")) { setMessage("First name can only contain letters.", false); return; }
        if (!surName.matches("[a-zA-Z\\s-]+"))   { setMessage("Surname can only contain letters.", false); return; }

        // Email format check
        if (!email.isEmpty() && (!email.contains("@") || !email.contains("."))) {
            setMessage("Please enter a valid email address.", false); return;
        }

        // Phone format check
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

    /**
     * Loads an existing staff account by ID and populates all form fields.
     * Updates the status label to show ACTIVE or INACTIVE.
     */
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
    /**
     * Handles the Delete Account action.
     * Shows a confirmation dialog before deactivating the staff account.
     */
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

    /**
     * Populates all form fields with data from an existing staff account.
     *
     * @param staff the staff account to populate the form with
     */
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

    /**
     * Resets all form fields to empty/default values and clears the status display.
     */
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
        if (passwordField != null)        passwordField.setText("");
        if (confirmPasswordField != null) confirmPasswordField.setText("");
        setMessage("", true);
    }

    /**
     * Sets the inline feedback message below the form.
     *
     * @param text the message to display
     * @param success true for a green success message, false for a red error message
     */
    private void setMessage(String text, boolean success) {
        messageLabel.setText(text);
        messageLabel.setForeground(success ? new Color(0, 97, 0) : new Color(200, 80, 80));
    }

    /**
     * Creates a styled JTextField with consistent font, border, and height.
     *
     * @return a new JTextField with shared form styling applied
     */
    private JTextField createTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(221, 225, 231)),
                BorderFactory.createEmptyBorder(0, 12, 0, 12)));
        return field;
    }

    /**
     * Creates a styled password input field.
     *
     * @return the styled password field
     */
    private JPasswordField createPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(221, 225, 231)),
                BorderFactory.createEmptyBorder(0, 12, 0, 12)));
        return field;
    }

    /**
     * Creates a horizontal row panel with a GridLayout for the specified number of columns.
     *
     * @param cols the number of columns in this row
     * @return a configured JPanel for use as a form row
     */
    private JPanel row(int cols) {
        JPanel p = new JPanel(new GridLayout(1, cols, 12, 0));
        p.setBackground(Color.WHITE);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        return p;
    }

    /**
     * Wraps a text field with a labelled caption panel, stacking the label above the input.
     *
     * @param label the uppercase caption text shown above the field
     * @param field the text input to wrap
     * @return a JPanel containing the label and field arranged vertically
     */
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

    /**
     * Wraps a combo box with a label above it.
     *
     * @param label the field label
     * @param combo the combo box to wrap
     * @return the wrapped panel
     */
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

    /**
     * Wraps a password field with a label above it.
     *
     * @param label the field label
     * @param field the password field to wrap
     * @return the wrapped panel
     */
    private JPanel fieldWrapperPassword(String label, JPasswordField field) {
        JPanel wrapper = new JPanel(new BorderLayout(0, 4));
        wrapper.setBackground(Color.WHITE);
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lbl.setForeground(new Color(107, 114, 128));
        wrapper.add(lbl,   BorderLayout.NORTH);
        wrapper.add(field, BorderLayout.CENTER);
        return wrapper;
    }

    /**
     * Creates a styled action button with the given label and background colour.
     *
     * @param label the button label
     * @param bg the button background colour
     * @return the styled button
     */
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

    /**
     * Called by the screen router when this screen becomes visible.
     * In MANAGE mode ; reads the selected staff ID from AppFrame
     * and auto-loads the staff account.
     * In CREATE mode ; clears the form ready for new input.
     */
    @Override
    public void onShow() {
        String staffId = AppFrame.getInstance().getSelectedMerchant();
        if (staffId != null && !staffId.isEmpty() && "MANAGE".equals(mode)) {
            staffIdField.setText(staffId);
            loadStaff();
        } else {
            clearForm();
        }
    }
}