package IPOS.SA.ACC.UI;

import IPOS.SA.ACC.Model.MerchantAccount;
import IPOS.SA.ACC.Service.AccountService;
import IPOS.SA.UI.AppFrame;
import IPOS.SA.UI.BaseFrame;
import IPOS.SA.UI.Refreshable;
import IPOS.SA.UI.ScreenRouter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Date;
import java.time.LocalDate;

/**
 * UI screen for creating and managing merchant accounts.
 * Supports two modes: CREATE for new accounts and MANAGE for editing existing ones.
 */
public class AccountManagement extends BaseFrame implements Refreshable {

    private final AccountService accountService;
    private final String mode; // Stores the editing mode for the form

    // text fields to enter the merchant details
    private JTextField merchantIdField;
    private JTextField usernameField;
    private JTextField companyNameField;
    private JTextField businessTypeField;
    private JTextField registrationNumberField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextField faxField;
    private JTextField addressField;
    private JTextField contactPersonField;
    private JTextField creditLimitField;
    private JTextField discountValueField;
    private JComboBox<String> discountTypeCombo;

    // Panel to display details regarding the flexible discounts
    private JPanel variableDiscountPanel;
    private JLabel variableDiscountLabel;

    // labels to show merchant account stats
    private JLabel balanceLabel;
    private JLabel statusLabel;
    private JLabel messageLabel;
    private JPanel StatusPanel;

    /**
     * Constructs an AccountManagement screen without a pre-selected merchant.
     *
     * @param fullname the display name of the logged-in user
     * @param role the role of the logged-in user
     * @param mode the editing mode: "CREATE" or "MANAGE"
     * @param router the screen router used for navigation
     */
    public AccountManagement(String fullname, String role, String mode, ScreenRouter router) {
        this(fullname, role, mode, null, router);
    }

    /**
     * Constructs an AccountManagement screen, optionally pre-loading a merchant account.
     *
     * @param fullname the display name of the logged-in user
     * @param role the role of the logged-in user
     * @param mode the editing mode: "CREATE" or "MANAGE"
     * @param merchantId the merchant ID to pre-load, or null to start with a blank form
     * @param router the screen router used for navigation
     */
    public AccountManagement(String fullname, String role, String mode, String merchantId, ScreenRouter router) {
        super(fullname, role, "Merchant Account Management", router);
        this.accountService = new AccountService();
        this.mode = mode;

        // Creates the main form panel for the GUI
        buildContent();

        // Pre-loads the account if a merchant ID was provided in MANAGE mode
        if (merchantId != null && !merchantId.isEmpty() && "MANAGE".equals(mode)) {
            merchantIdField.setText(merchantId);
            loadAccount();
        }
    }

    /**
     * Returns the header title displayed at the top of the screen.
     *
     * @return a screen title string based on the current mode
     */
    @Override
    protected String getHeaderTitle() {
        if (mode == null) return "Merchant Account Management";
        if (mode.equals("CREATE")) return "Create Merchant Account";
        return "Manage Merchant Account";
    }

    /**
     * Builds and arranges all UI components for this screen.
     * Produces a form panel on the left and a status/actions panel on the right.
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
        JLabel formTitle = new JLabel("ACCOUNT DETAILS");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formTitle.setForeground(Color.BLACK);
        formTitle.setBorder(new EmptyBorder(0, 0, 12, 0));

        // Creates a smaller panel inside the frame panel for the text feilds
        JPanel grid = new JPanel();
        grid.setLayout(new BoxLayout(grid, BoxLayout.Y_AXIS));
        grid.setBackground(Color.WHITE);

        // creates the data entry feilds
        usernameField = new JTextField();
        merchantIdField = createTextField();
        companyNameField = createTextField();
        businessTypeField = createTextField();
        registrationNumberField = createTextField();
        emailField = createTextField();
        phoneField = createTextField();
        faxField = createTextField();
        addressField = createTextField();
        contactPersonField = createTextField();
        creditLimitField = createTextField();
        discountValueField = createTextField();
        discountTypeCombo = new JComboBox<>(new String[]{"fixed", "flexible"});
        discountTypeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        discountTypeCombo.addActionListener(e -> toggleDiscountFields());

        // Shows the username field only in CREATE mode
        if ("CREATE".equals(mode)) {
            JPanel row1 = row(2);
            row1.add(fieldWrapper("MERCHANT ID", merchantIdField));
            row1.add(fieldWrapper("USERNAME", usernameField));
            grid.add(row1);
        } else {
            JPanel row1 = row(1);
            row1.add(fieldWrapper("MERCHANT ID", merchantIdField));
            grid.add(row1);
        }

        // Aligns the feilds neatly on the form panel
        JPanel row2 = row(2);
        row2.add(fieldWrapper("COMPANY NAME", companyNameField));
        row2.add(fieldWrapper("BUSINESS TYPE", businessTypeField));

        JPanel row3 = row(2);
        row3.add(fieldWrapper("REGISTRATION NUMBER", registrationNumberField));
        row3.add(fieldWrapper("EMAIL", emailField));

        JPanel row4 = row(1);
        row4.add(fieldWrapper("ADDRESS", addressField));

        JPanel row5 = row(2);
        row5.add(fieldWrapper("PHONE", phoneField));
        row5.add(fieldWrapper("FAX", faxField));

        JPanel row6 = row(2);
        row6.add(fieldWrapper("CREDIT LIMIT (£)", creditLimitField));

        // Discount type combo is built manually to match fieldWrapper label styling
        JPanel discountTypeWrapper = new JPanel(new BorderLayout(0, 4));
        discountTypeWrapper.setBackground(Color.WHITE);
        JLabel discountTypeLbl = new JLabel("DISCOUNT TYPE");
        discountTypeLbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        discountTypeLbl.setForeground(new Color(107, 114, 128));
        discountTypeWrapper.add(discountTypeLbl, BorderLayout.NORTH);
        discountTypeWrapper.add(discountTypeCombo, BorderLayout.CENTER);
        row6.add(discountTypeWrapper);

        // Adds the discoutn field to the form
        JPanel row7 = row(2);
        row7.add(fieldWrapper("DISCOUNT % (Fixed or Max Variable)", discountValueField));

        // Variable discount info panel, displayed only when flexible discount is selected
        variableDiscountPanel = new JPanel(new BorderLayout());
        variableDiscountPanel.setBackground(new Color(245, 247, 250));
        variableDiscountPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(221, 225, 231)),
                new EmptyBorder(8, 12, 8, 12)));
        variableDiscountLabel = new JLabel(
                "<html><b>Variable Discount Tiers:</b><br/>" +
                        "• Under £1,000 → 0%<br/>" +
                        "• £1,000 - £2,000 → 1%<br/>" +
                        "• Over £2,000 → 2%</html>");
        variableDiscountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        variableDiscountPanel.add(variableDiscountLabel);
        variableDiscountPanel.setVisible(false);
        variableDiscountPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        // Adds all the text fields to the grid with equal spacing between them
        grid.add(Box.createVerticalStrut(12));
        grid.add(row2);
        grid.add(Box.createVerticalStrut(12));
        grid.add(row3);
        grid.add(Box.createVerticalStrut(12));
        grid.add(row4);
        grid.add(Box.createVerticalStrut(12));
        grid.add(row5);
        grid.add(Box.createVerticalStrut(12));
        grid.add(row6);
        grid.add(Box.createVerticalStrut(12));
        grid.add(row7);
        grid.add(Box.createVerticalStrut(8));
        grid.add(variableDiscountPanel);

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

        // Creates the account balance label
        JLabel balanceTitle = new JLabel("ACCOUNT BALANCE");
        balanceTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        balanceTitle.setForeground(new Color(107, 114, 128));

        balanceLabel = new JLabel("0.00");
        balanceLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        balanceLabel.setForeground(new Color(17, 24, 39));
        balanceLabel.setBorder(new EmptyBorder(6, 0, 8, 0));

        // creates a wrapper panel to contain the status and balance labels
        StatusPanel = new JPanel();
        StatusPanel.setLayout(new BoxLayout(StatusPanel, BoxLayout.Y_AXIS));
        StatusPanel.setBackground(Color.WHITE);
        StatusPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(221, 225, 231), 1),
                new EmptyBorder(16, 16, 16, 16)));
        StatusPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        StatusPanel.add(statusTitle);
        StatusPanel.add(statusLabel);
        StatusPanel.add(balanceTitle);
        StatusPanel.add(balanceLabel);

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
            case "CREATE":
                addCreateButtons(actionsCard);
                break;
            case "MANAGE":
                addManageButtons(actionsCard);
                break;
        }

        // Adds the status panel and action buttons to the right hand side of the account management form
        rightColumn.add(StatusPanel);
        rightColumn.add(Box.createVerticalStrut(12));
        rightColumn.add(actionsCard);

        CenterPanel.add(formPanel,   BorderLayout.CENTER);
        CenterPanel.add(rightColumn, BorderLayout.EAST);
    }

    /**
     * Shows or hides the variable discount info panel based on the selected discount type.
     * The panel is visible only when "flexible" is selected.
     */
    private void toggleDiscountFields() {
        boolean isFixed = "fixed".equals(discountTypeCombo.getSelectedItem());
        variableDiscountPanel.setVisible(!isFixed);
        CenterPanel.revalidate();
        CenterPanel.repaint();
    }

    /**
     * Adds action buttons for CREATE mode to the given panel.
     *
     * @param actionsCard the panel to add the buttons into
     */
    private void addCreateButtons(JPanel actionsCard) {
        JButton createBtn = actionButton("Create Account", new Color(30, 70, 90));
        JButton clearBtn  = actionButton("Clear",          new Color(107, 114, 128));
        JButton backBtn   = actionButton("← Back",         new Color(17, 24, 39));

        createBtn.addActionListener(e -> createAccount());
        clearBtn.addActionListener(e  -> clearForm());
        backBtn.addActionListener(e   -> router.goTo(AppFrame.SCREEN_MERCHANT_LIST));

        // adds the buttons to the actions card with even spacing
        actionsCard.add(createBtn);
        actionsCard.add(Box.createVerticalStrut(8));
        actionsCard.add(clearBtn);
        actionsCard.add(Box.createVerticalStrut(8));
        actionsCard.add(backBtn);
    }

    /**
     * Adds action buttons for MANAGE mode to the given panel.
     * Directors of Operations receive an additional "Restore from Default" button.
     *
     * @param actionsCard the panel to add the buttons into
     */
    private void addManageButtons(JPanel actionsCard) {
        // creates the buttons with colours and labels
        JButton loadBtn = actionButton("Load Account", new Color(17, 24, 39));
        JButton updateBtn = actionButton("Update Account", new Color(30, 70, 90));
        JButton suspendBtn = actionButton("Suspend Account", new Color(127, 29, 29));
        JButton reinstateBtn = actionButton("Reinstate Account", new Color(20, 83, 45));
        JButton deleteDiscountBtn = actionButton("Delete Discount Plan", new Color(107, 114, 128));
        JButton deleteAccountBtn = actionButton("Delete Account", new Color(127, 29, 29));
        JButton clearBtn = actionButton("Clear", new Color(107, 114, 128));
        JButton backBtn = actionButton("← Back", new Color(17, 24, 39));

        // Adds action listeners to the buttons so that they can call other forms or methods when clicked
        loadBtn.addActionListener(e           -> loadAccount());
        updateBtn.addActionListener(e         -> updateAccount());
        suspendBtn.addActionListener(e        -> updateStatus("suspended"));
        reinstateBtn.addActionListener(e      -> updateStatus("normal"));
        deleteDiscountBtn.addActionListener(e -> deleteDiscountPlan());
        deleteAccountBtn.addActionListener(e  -> deleteAccount());
        clearBtn.addActionListener(e          -> clearForm());
        backBtn.addActionListener(e           -> router.goTo(AppFrame.SCREEN_MERCHANT_LIST));

        // adds the buttons to the actions card with even spacing
        actionsCard.add(loadBtn);
        actionsCard.add(Box.createVerticalStrut(8));
        actionsCard.add(updateBtn);
        actionsCard.add(Box.createVerticalStrut(8));
        actionsCard.add(suspendBtn);
        actionsCard.add(Box.createVerticalStrut(8));
        actionsCard.add(reinstateBtn);
        actionsCard.add(Box.createVerticalStrut(8));
        actionsCard.add(deleteDiscountBtn);
        actionsCard.add(Box.createVerticalStrut(8));
        actionsCard.add(deleteAccountBtn);
        actionsCard.add(Box.createVerticalStrut(8));
        actionsCard.add(clearBtn);
        actionsCard.add(Box.createVerticalStrut(8));
        actionsCard.add(backBtn);
        actionsCard.add(Box.createVerticalStrut(8));

        // Extra restore button available only to the Director of Operations
        if (role.equals("Director of Operations")) {
            JButton restoreBtn = actionButton("Restore from Default", new Color(20, 83, 45));
            restoreBtn.addActionListener(e -> restoreFromDefault());
            actionsCard.add(restoreBtn);
            actionsCard.add(Box.createVerticalStrut(8));
        }
    }

    /**
     * Validates the form and creates a new merchant account.
     * A default password (username + "123") is hashed and stored.
     * On success the form is cleared; on failure an inline error message is shown.
     */
    private void createAccount() {
        try {
            String id = merchantIdField.getText().trim();
            String username = usernameField.getText().trim();
            String name = companyNameField.getText().trim();
            String email = emailField.getText().trim();
            String regNo = registrationNumberField.getText().trim();
            String phone = phoneField.getText().trim();
            String discountType = discountTypeCombo.getSelectedItem().toString();

            // Validates required fields
            if (id.isEmpty())       { setMessage("Merchant ID is required.", false);       return; }
            if (username.isEmpty()) { setMessage("Username is required.", false);           return; }
            if (name.isEmpty())     { setMessage("Company name is required.", false);       return; }
            if (email.isEmpty())    { setMessage("Email is required.", false);              return; }
            if (regNo.isEmpty())    { setMessage("Registration number is required.", false); return; }
            if (id.contains(" "))   { setMessage("Merchant ID cannot contain spaces.", false); return; }
            if (username.contains(" ")) { setMessage("Username cannot contain spaces.", false); return; }
            if (!email.contains("@") || !email.contains(".")) {
                setMessage("Please enter a valid email address.", false); return;
            }
            if (!phone.isEmpty() && !phone.matches("[0-9+\\-\\s()]+")) {
                setMessage("Phone number contains invalid characters.", false); return;
            }

            double credit   = Double.parseDouble(creditLimitField.getText().trim());
            double discount = Double.parseDouble(discountValueField.getText().trim());

            if (credit < 0)    { setMessage("Credit limit cannot be negative.", false); return; }
            if (discount < 0)  { setMessage("Discount cannot be negative.", false); return; }
            if (discount > 100){ setMessage("Discount cannot exceed 100%.", false); return; }

            // Splits the discount value into the correct typed field based on selected type
            double fixedDiscount    = "fixed".equals(discountType) ? discount : 0.0;
            double flexibleDiscount = "flexible".equals(discountType) ? discount : 0.0;

            // Hashes the password when stored in the database
            String defaultPassword = username + "123";
            String hashedPassword  = hashPassword(defaultPassword);

            // creates a new instance of the merchant account with the given details
            MerchantAccount account = new MerchantAccount(
                    id, name,
                    businessTypeField.getText().trim(),
                    registrationNumberField.getText().trim(),
                    email, phoneField.getText().trim(),
                    faxField.getText().trim(),
                    addressField.getText().trim(),
                    credit, 0.0, "normal",
                    discountType, fixedDiscount, flexibleDiscount,
                    Date.valueOf(LocalDate.now()), true,
                    Date.valueOf(LocalDate.now()), username
            );
            account.setPassword(hashedPassword);

            if (accountService.addAccount(account)) {
                setMessage("Account created successfully. Login details sent to " + email, true);
                clearForm();
            } else {
                setMessage("Merchant ID or Username already exists.", false);
            }
        } catch (NumberFormatException ex) {
            setMessage("Please enter valid numbers for Credit Limit and Discount.", false);
        } catch (Exception ex) {
            setMessage("Error: " + ex.getMessage(), false);
        }
    }

    /**
     * Loads an existing merchant account using the value in the Merchant ID field.
     * Populates all form fields and updates the balance and status display labels.
     */
    private void loadAccount() {
        String id = merchantIdField.getText().trim();
        if (id.isEmpty()) { setMessage("Enter a Merchant ID to load.", false); return; }
        try {
            MerchantAccount account = accountService.getAccount(id);
            if (account != null) {
                populateForm(account);
                balanceLabel.setText(String.format("%.2f", account.getOutstandingBalance()));
                statusLabel.setText(account.getStatus().toString());
                setMessage("Account loaded successfully.", true);
            } else {
                setMessage("Account not found.", false);
            }
        } catch (Exception ex) {
            setMessage("Error: " + ex.getMessage(), false);
        }
    }

    /**
     * Validates edited form data and saves changes to the loaded merchant account.
     * Preserves the existing balance, status, and password from the database.
     * Re-loads the account after a successful update to confirm the persisted state.
     */
    private void updateAccount() {
        String id  = merchantIdField.getText().trim();
        String name = companyNameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String discountType = discountTypeCombo.getSelectedItem().toString();

        // Validates required fields before attempting the update
        if (id.isEmpty())    { setMessage("Load an account first.", false);          return; }
        if (name.isEmpty())  { setMessage("Company name is required.", false);       return; }
        if (email.isEmpty()) { setMessage("Email is required.", false);              return; }
        if (!email.contains("@") || !email.contains(".")) {
            setMessage("Please enter a valid email address.", false); return;
        }
        if (!phone.isEmpty() && !phone.matches("[0-9+\\-\\s()]+")) {
            setMessage("Phone number contains invalid characters.", false); return;
        }
        if (creditLimitField.getText().trim().isEmpty()) {
            setMessage("Credit limit is required.", false); return;
        }
        if (discountValueField.getText().trim().isEmpty()) {
            setMessage("Discount value is required.", false); return;
        }

        try {
            double credit   = Double.parseDouble(creditLimitField.getText().trim());
            double discount = Double.parseDouble(discountValueField.getText().trim());

            if (credit < 0)         { setMessage("Credit limit cannot be negative.", false); return; }
            if (discount < 0 || discount > 100) {
                setMessage("Discount must be between 0 and 100.", false); return;
            }

            double fixedDiscount    = "fixed".equals(discountType) ? discount : 0.0;
            double flexibleDiscount = "flexible".equals(discountType) ? discount : 0.0;

            // Fetch the current record to preserve balance, status, and password
            MerchantAccount existing = accountService.getAccount(id);
            if (existing == null) { setMessage("Account not found.", false); return; }

            // Create/ update the merchant account instance to the new details
            MerchantAccount account = new MerchantAccount(
                    id,
                    companyNameField.getText().trim(),
                    emailField.getText().trim(),
                    phoneField.getText().trim(),
                    faxField.getText().trim(),
                    addressField.getText().trim(),
                    credit,
                    existing.getOutstandingBalance(),
                    existing.getAccountStatus(),
                    fixedDiscount,
                    username,
                    existing.getPassword()
            );
            account.setDiscountType(discountType);
            account.setFlexibleDiscountRate(flexibleDiscount);
            account.setOutstandingBalance(existing.getOutstandingBalance());
            account.setStatus(existing.getStatus());
            account.setPassword(existing.getPassword());

            if (accountService.updateAccount(account)) {
                setMessage("Account updated successfully.", true);
                loadAccount();
            } else {
                setMessage("Failed to update account.", false);
            }
        } catch (NumberFormatException ex) {
            setMessage("Please enter valid numbers for Credit Limit and Discount.", false);
        } catch (Exception ex) {
            setMessage("Error: " + ex.getMessage(), false);
        }
    }

    /**
     * Updates the account status of the currently loaded merchant.
     *
     * @param status the new status to apply, e.g. "suspended" or "normal"
     */
    private void updateStatus(String status) {
        String id = merchantIdField.getText().trim();
        if (id.isEmpty()) { setMessage("Load an account first.", false); return; }
        try {
            if (accountService.updateAccountStatus(id, status)) {
                statusLabel.setText(status);
                setMessage("Status updated to: " + status, true);
            } else {
                setMessage("Failed to update status.", false);
            }
        } catch (Exception ex) {
            setMessage("Error: " + ex.getMessage(), false);
        }
    }

    /**
     * Prompts the user to confirm, then removes the discount plan for the loaded merchant.
     * Resets the discount fields to defaults (0.0 / "fixed") on success.
     */
    private void deleteDiscountPlan() {
        String id = merchantIdField.getText().trim();
        if (id.isEmpty()) { setMessage("Load an account first.", false); return; }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete the discount plan for account " + id + "?",
                "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (accountService.deleteDiscountPlan(id)) {
                    discountValueField.setText("0.0");
                    discountTypeCombo.setSelectedItem("fixed");
                    setMessage("Discount plan deleted successfully.", true);
                } else {
                    setMessage("Failed to delete discount plan.", false);
                }
            } catch (Exception ex) {
                setMessage("Error: " + ex.getMessage(), false);
            }
        }
    }

    /**
     * Prompts the user to confirm, then permanently deletes the loaded merchant account.
     * Clears the form on success to prevent further actions on the deleted record.
     */
    private void deleteAccount() {
        String id = merchantIdField.getText().trim();
        if (id.isEmpty()) { setMessage("Load an account first.", false); return; }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete merchant account " + id + "?\nThis cannot be undone.",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (accountService.deleteAccount(id)) {
                    setMessage("Account deleted successfully.", true);
                    clearForm();
                } else {
                    setMessage("Failed to delete account.", false);
                }
            } catch (Exception ex) {
                setMessage("Error: " + ex.getMessage(), false);
            }
        }
    }

    /**
     * Restores a merchant account to "normal" status.
     * The service will reject the restore if an outstanding balance still exists.
     */
    private void restoreFromDefault() {
        String id = merchantIdField.getText().trim();
        if (id.isEmpty()) { setMessage("Load an account first.", false); return; }
        try {
            if (accountService.restoreFromDefault(id)) {
                statusLabel.setText("normal");
                setMessage("Account restored to normal successfully.", true);
            } else {
                setMessage("Cannot restore — outstanding balance must be cleared first.", false);
            }
        } catch (Exception ex) {
            setMessage("Error: " + ex.getMessage(), false);
        }
    }

    /**
     * Populates all form fields from a loaded MerchantAccount object.
     * Also sets the discount type combo and toggles the variable discount panel.
     *
     * @param account the merchant account whose data should fill the form
     */
    private void populateForm(MerchantAccount account) {
        merchantIdField.setText(account.getMerchantId());
        usernameField.setText(account.getUsername());
        companyNameField.setText(account.getBusinessName());
        businessTypeField.setText(account.getBusinessType());
        registrationNumberField.setText(account.getRegistrationNumber());
        emailField.setText(account.getEmail());
        phoneField.setText(account.getPhone());
        faxField.setText(account.getFax());
        addressField.setText(account.getAddress());
        creditLimitField.setText(String.valueOf(account.getCreditLimit()));

        // Sets discount type and value, default to fixed if not selected
        String discountType = account.getDiscountType();
        if (discountType == null) discountType = "fixed";
        discountTypeCombo.setSelectedItem(discountType);

        if ("fixed".equals(discountType)) {
            discountValueField.setText(String.valueOf(account.getFixedDiscountRate()));
        } else {
            discountValueField.setText(String.valueOf(account.getFlexibleDiscountRate()));
        }
        toggleDiscountFields();
    }

    /**
     * Resets all form fields to empty/default values and clears the status display.
     */
    private void clearForm() {
        merchantIdField.setText("");
        usernameField.setText("");
        companyNameField.setText("");
        businessTypeField.setText("");
        registrationNumberField.setText("");
        emailField.setText("");
        phoneField.setText("");
        faxField.setText("");
        addressField.setText("");
        contactPersonField.setText("");
        creditLimitField.setText("");
        discountValueField.setText("");
        discountTypeCombo.setSelectedItem("fixed");
        variableDiscountPanel.setVisible(false);
        balanceLabel.setText("0.00");
        statusLabel.setText("--");
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
     * Creates a styled full-width action button for the right-column actions card.
     *
     * @param label the text displayed on the button
     * @param bg the background colour of the button
     * @return a configured JButton ready to be added to an actions panel
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
     * Hashes a plain-text password using SHA-256 and returns it as a hex string.
     * Falls back to returning the original password if hashing fails.
     *
     * @param password the plain-text password to hash
     * @return the SHA-256 hex digest, or the original string on failure
     */
    private String hashPassword(String password) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return password;
        }
    }

    /**
     * Called by the screen router when this screen becomes visible.
     * Auto-loads the account if a merchant was selected from the merchant list.
     * Otherwise clears the form to present a fresh state.
     */
    @Override
    public void onShow() {
        String merchantId = AppFrame.getInstance().getSelectedMerchant();
        if (merchantId != null && !merchantId.isEmpty() && "MANAGE".equals(mode)) {
            merchantIdField.setText(merchantId);
            loadAccount();
        } else {
            clearForm();
        }
    }
}