package IPOS.SA.ACC.UI;

import IPOS.SA.ACC.Model.DiscountPlan;
import IPOS.SA.ACC.Model.FixedDiscountPlan;
import IPOS.SA.ACC.Model.MerchantAccount;
import IPOS.SA.ACC.Service.AccountService;
import IPOS.SA.UI.BaseFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AccountManagement extends BaseFrame {

    private final AccountService accountService;
    private final String mode;

    private JTextField merchantIdField;
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

    private JLabel balanceLabel;
    private JLabel statusLabel;
    private JLabel messageLabel;
    private JPanel StatusPanel;

    public AccountManagement(String fullname, String role, String mode) {
        this(fullname, role, mode, null);
    }

    public AccountManagement(String fullname, String role, String mode, String merchantId) {
        super(fullname, role, "Merchant Account Management");
        this.accountService = new AccountService();
        this.mode = mode;

        buildContent();

        if (merchantId != null && !merchantId.isEmpty() && "MANAGE".equals(mode)) {
            merchantIdField.setText(merchantId);
            loadAccount();
        }
    }

    @Override
    protected String getHeaderTitle() {
        if (mode == null) return "Merchant Account Management";
        if (mode.equals("CREATE")) {
            return "Create Merchant Account";
        } else {
            return "Manage Merchant Account";
        }
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

        JLabel formTitle = new JLabel("ACCOUNT DETAILS");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formTitle.setForeground(Color.BLACK);
        formTitle.setBorder(new EmptyBorder(0, 0, 12, 0));

        JPanel grid = new JPanel();
        grid.setLayout(new BoxLayout(grid, BoxLayout.Y_AXIS));
        grid.setBackground(Color.WHITE);

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

        JPanel row1 = row(2);
        row1.add(fieldWrapper("MERCHANT ID", merchantIdField));
        row1.add(fieldWrapper("COMPANY NAME", companyNameField));
        JPanel row2 = row(2);
        row2.add(fieldWrapper("BUSINESS TYPE", businessTypeField));
        row2.add(fieldWrapper("REGISTRATION NUMBER", registrationNumberField));
        JPanel row3 = row(1);
        row3.add(fieldWrapper("ADDRESS", addressField));
        JPanel row4 = row(2);
        row4.add(fieldWrapper("EMAIL", emailField));
        row4.add(fieldWrapper("PHONE", phoneField));
        JPanel row5 = row(2);
        row5.add(fieldWrapper("FAX", faxField));
        row5.add(fieldWrapper("CREDIT LIMIT (£)", creditLimitField));
        JPanel row6 = row(1);
        row6.add(fieldWrapper("FIXED DISCOUNT %", discountValueField));

        grid.add(row1);
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

        JLabel balanceTitle = new JLabel("ACCOUNT BALANCE");
        balanceTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        balanceTitle.setForeground(new Color(107, 114, 128));

        balanceLabel = new JLabel("0.00");
        balanceLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        balanceLabel.setForeground(new Color(17, 24, 39));
        balanceLabel.setBorder(new EmptyBorder(6, 0, 8, 0));

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
            case "CREATE":
                addCreateButtons(actionsCard);
                break;
            case "MANAGE":
                addManageButtons(actionsCard);
                break;
        }

        rightColumn.add(StatusPanel);
        rightColumn.add(Box.createVerticalStrut(12));
        rightColumn.add(actionsCard);

        CenterPanel.add(formPanel, BorderLayout.CENTER);
        CenterPanel.add(rightColumn, BorderLayout.EAST);
    }

    private void addCreateButtons(JPanel actionsCard) {
        JButton createBtn = actionButton("Create Account", new Color(30, 70, 90));
        JButton clearBtn  = actionButton("Clear",          new Color(107, 114, 128));
        JButton backBtn   = actionButton("← Back",         new Color(17, 24, 39));

        createBtn.addActionListener(e -> createAccount());
        clearBtn.addActionListener(e  -> clearForm());
        backBtn.addActionListener(e   -> { dispose(); new AdminDashboard(fullname, role); });

        actionsCard.add(createBtn); actionsCard.add(Box.createVerticalStrut(8));
        actionsCard.add(clearBtn);  actionsCard.add(Box.createVerticalStrut(8));
        actionsCard.add(backBtn);
    }

    private void addManageButtons(JPanel actionsCard) {
        JButton loadBtn           = actionButton("Load Account",        new Color(17, 24, 39));
        JButton updateBtn         = actionButton("Update Account",      new Color(30, 70, 90));
        JButton suspendBtn        = actionButton("Suspend Account",     new Color(127, 29, 29));
        JButton reinstateBtn      = actionButton("Reinstate Account",   new Color(20, 83, 45));
        JButton deleteDiscountBtn = actionButton("Delete Discount Plan", new Color(107, 114, 128));
        JButton deleteAccountBtn  = actionButton("Delete Account",      new Color(127, 29, 29));
        JButton clearBtn          = actionButton("Clear",               new Color(107, 114, 128));
        JButton backBtn           = actionButton("← Back",              new Color(17, 24, 39));

        loadBtn.addActionListener(e           -> loadAccount());
        updateBtn.addActionListener(e         -> updateAccount());
        suspendBtn.addActionListener(e        -> updateStatus("suspended"));
        reinstateBtn.addActionListener(e      -> updateStatus("normal"));
        deleteDiscountBtn.addActionListener(e -> deleteDiscountPlan());
        deleteAccountBtn.addActionListener(e  -> deleteAccount());
        clearBtn.addActionListener(e          -> clearForm());
        backBtn.addActionListener(e           -> {
            dispose();
            new AdminDashboard(fullname, role);
        });

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

        if (role.equals("Director of operations")) {
            JButton restoreBtn = actionButton("Restore from Default", new Color(20, 83, 45));
            restoreBtn.addActionListener(e -> restoreFromDefault());
            actionsCard.add(restoreBtn);
            actionsCard.add(Box.createVerticalStrut(8));
        }
    }

    // ── BUSINESS LOGIC ───────────────────────────────────────
    private void createAccount() {
        try {
            String id     = merchantIdField.getText().trim();
            String name   = companyNameField.getText().trim();
            double credit = Double.parseDouble(creditLimitField.getText().trim());
            double discount = Double.parseDouble(discountValueField.getText().trim());

            if (id.isEmpty())   { setMessage("Merchant ID is required.", false); return; }
            if (name.isEmpty()) { setMessage("Company name is required.", false); return; }
            if (credit < 0)     { setMessage("Credit limit cannot be negative.", false); return; }
            if (discount < 0)   { setMessage("Discount cannot be negative.", false); return; }

            DiscountPlan plan = new FixedDiscountPlan("Fixed Plan", discount);
            MerchantAccount account = new MerchantAccount(
                    id, name,
                    businessTypeField.getText().trim(),
                    registrationNumberField.getText().trim(),
                    emailField.getText().trim(),
                    phoneField.getText().trim(),
                    faxField.getText().trim(),
                    addressField.getText().trim(),
                    credit, plan
            );

            if (accountService.addAccount(account)) {
                setMessage("Account created successfully.", true);
                clearForm();
            } else {
                setMessage("Account ID already exists.", false);
            }
        } catch (NumberFormatException ex) {
            setMessage("Please enter valid numbers for Credit Limit and Discount.", false);
        } catch (Exception ex) {
            setMessage("Error: " + ex.getMessage(), false);
        }
    }

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

    private void updateAccount() {
        String id = merchantIdField.getText().trim();
        if (id.isEmpty()) { setMessage("Load an account first.", false); return; }
        try {
            MerchantAccount existing = accountService.getAccount(id);
            if (existing == null) { setMessage("Account not found.", false); return; }

            DiscountPlan plan = new FixedDiscountPlan("Fixed Plan",
                    Double.parseDouble(discountValueField.getText().trim()));

            MerchantAccount account = new MerchantAccount(
                    id, companyNameField.getText().trim(),
                    businessTypeField.getText().trim(),
                    registrationNumberField.getText().trim(),
                    emailField.getText().trim(),
                    phoneField.getText().trim(),
                    faxField.getText().trim(),
                    addressField.getText().trim(),
                    Double.parseDouble(creditLimitField.getText().trim()),
                    plan
            );

            account.setOutstandingBalance(existing.getOutstandingBalance());
            account.setStatus(existing.getStatus());

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
                    setMessage("Discount plan deleted successfully.", true);
                } else {
                    setMessage("Failed to delete discount plan.", false);
                }
            } catch (Exception ex) {
                setMessage("Error: " + ex.getMessage(), false);
            }
        }
    }

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

    // ── HELPERS ──────────────────────────────────────────────
    private void populateForm(MerchantAccount account) {
        merchantIdField.setText(account.getMerchantId());
        companyNameField.setText(account.getBusinessName());
        businessTypeField.setText(account.getBusinessType());
        registrationNumberField.setText(account.getRegistrationNumber());
        emailField.setText(account.getEmail());
        phoneField.setText(account.getPhone());
        faxField.setText(account.getFax());
        addressField.setText(account.getAddress());
        creditLimitField.setText(String.valueOf(account.getCreditLimit()));
        discountValueField.setText(String.valueOf(account.getDiscountPercentage()));
    }

    private void clearForm() {
        merchantIdField.setText("");
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
        balanceLabel.setText("0.00");
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
}