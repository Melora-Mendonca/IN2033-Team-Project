package IPOS.SA.ACC.UI;

import IPOS.SA.ACC.Model.MerchantAccount;
import IPOS.SA.ACC.Service.AccountService;
import IPOS.SA.ACC.Model.FixedDiscountPlan;
import IPOS.SA.ACC.Model.DiscountPlan;
import IPOS.SA.CAT.UI.Catalogue;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AccountManagement extends JFrame {
    private final AccountService accountService;
    private final String fullname;
    private final String role;
    private final String mode;

    // For the data entry fields on the form
    private JTextField merchantIdField;
    private JTextField businessNameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextField addressField;
    private JTextField creditLimitField;
    private JTextField discountValueField;

    // For the status panel that shows account status and balance
    private JLabel balanceLabel;
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

    public AccountManagement(String fullname, String role, String mode) {
        this.accountService = new AccountService();
        this.fullname = fullname;
        this.role = role;
        this.mode = mode;

        setTitle("Merchant Account Management");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setContentPane(MainPanel);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);

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

        headerLabel = new JLabel("Merchant Account Management");
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

        JLabel formTitle = new JLabel("ACCOUNT DETAILS");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formTitle.setForeground(Color.BLACK);
        formTitle.setBorder(new EmptyBorder(0, 0, 12, 0));

        JPanel grid = new JPanel();
        grid.setLayout(new BoxLayout(grid, BoxLayout.Y_AXIS));
        grid.setBackground(Color.WHITE);

        // Create all fields
        merchantIdField = createTextField();
        businessNameField = createTextField();
        emailField = createTextField();
        phoneField = createTextField();
        addressField = createTextField();
        creditLimitField = createTextField();
        discountValueField = createTextField();

        JPanel row1 = new JPanel(new GridLayout(1, 2, 12, 0));
        row1.setBackground(Color.WHITE);
        row1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        row1.add(fieldWrapper("MERCHANT ID", merchantIdField));
        row1.add(fieldWrapper("BUSINESS NAME", businessNameField));

        JPanel row2 = new JPanel(new GridLayout(1, 1));
        row2.setBackground(Color.WHITE);
        row2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        row2.add(fieldWrapper("ADDRESS", addressField));

        JPanel row3 = new JPanel(new GridLayout(1, 2, 12, 0));
        row3.setBackground(Color.WHITE);
        row3.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        row3.add(fieldWrapper("EMAIL", emailField));
        row3.add(fieldWrapper("PHONE", phoneField));

        JPanel row4 = new JPanel(new GridLayout(1, 2, 12, 0));
        row4.setBackground(Color.WHITE);
        row4.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        row4.add(fieldWrapper("CREDIT LIMIT (£)", creditLimitField));
        row4.add(fieldWrapper("FIXED DISCOUNT %", discountValueField));

        grid.add(row1);
        grid.add(Box.createVerticalStrut(12));
        grid.add(row2);
        grid.add(Box.createVerticalStrut(12));
        grid.add(row3);
        grid.add(Box.createVerticalStrut(12));
        grid.add(row4);

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

        rightColumn.add(StatusPanel);
        rightColumn.add(Box.createVerticalStrut(12));
        rightColumn.add(actionsCard);

        CenterPanel.add(rightColumn, BorderLayout.EAST);
    }

    private void addCreateButtons(JPanel actionsCard) {
        JButton createBtn = actionButton("Create Account", new Color(30, 70, 90));
        JButton clearBtn = actionButton("Clear", new Color(107, 114, 128));
        JButton backBtn = actionButton("← Back", new Color(17, 24, 39));

        createBtn.addActionListener(e -> createAccount());
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
        JButton suspendBtn = actionButton("Suspend Account", new Color(127, 29, 29));
        JButton reinstateBtn = actionButton("Reinstate Account", new Color(20, 83, 45));
        JButton deleteDiscountBtn = actionButton("Delete Discount Plan", new Color(107, 114, 128));
        JButton deleteAccountBtn = actionButton("Delete Account", new Color(127, 29, 29));
        JButton clearBtn = actionButton("Clear", new Color(107, 114, 128));
        JButton backBtn = actionButton("← Back", new Color(17, 24, 39));

        loadBtn.addActionListener(e -> loadAccount());
        updateBtn.addActionListener(e -> updateAccount());
        suspendBtn.addActionListener(e -> updateStatus("suspended"));
        reinstateBtn.addActionListener(e -> updateStatus("normal"));
        deleteDiscountBtn.addActionListener(e -> deleteDiscountPlan());
        deleteAccountBtn.addActionListener(e -> deleteAccount());
        clearBtn.addActionListener(e -> clearForm());
        backBtn.addActionListener(e -> {
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

        // Only show Restore from Default if director of operations
        if (role.equals("director_of_operations")) {
            JButton restoreBtn = actionButton("Restore from Default", new Color(20, 83, 45));
            restoreBtn.addActionListener(e -> restoreFromDefault());
            actionsCard.add(restoreBtn);
            actionsCard.add(Box.createVerticalStrut(8));
        }
    }

    // Business Logic Methods (using AccountService)
    private void createAccount() {
        try {
            String id = merchantIdField.getText().trim();
            String name = businessNameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String address = addressField.getText().trim();

            if (creditLimitField.getText().trim().isEmpty() || discountValueField.getText().trim().isEmpty()) {
                setMessage("Please enter credit limit and discount percentage.", false);
                return;
            }

            double credit = Double.parseDouble(creditLimitField.getText().trim());
            double discount = Double.parseDouble(discountValueField.getText().trim());

            // Validation
            if (id.isEmpty()) {
                setMessage("Merchant ID is required.", false);
                return;
            }
            if (name.isEmpty()) {
                setMessage("Business name is required.", false);
                return;
            }
            if (credit < 0) {
                setMessage("Credit limit cannot be negative.", false);
                return;
            }
            if (discount < 0) {
                setMessage("Discount cannot be negative.", false);
                return;
            }

            DiscountPlan plan = new FixedDiscountPlan("Fixed Plan", discount);
            MerchantAccount account = new MerchantAccount(id, name, email, phone, address, credit, plan);

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
        if (id.isEmpty()) {
            setMessage("Enter a Merchant ID to load.", false);
            return;
        }

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
        if (id.isEmpty()) {
            setMessage("Load an account first.", false);
            return;
        }

        try {
            MerchantAccount existingAccount = accountService.getAccount(id);
            if (existingAccount == null) {
                setMessage("Account not found.", false);
                return;
            }

            DiscountPlan plan = new FixedDiscountPlan("Fixed Plan",
                    Double.parseDouble(discountValueField.getText().trim()));

            MerchantAccount account = new MerchantAccount(
                    id,
                    businessNameField.getText().trim(),
                    emailField.getText().trim(),
                    phoneField.getText().trim(),
                    addressField.getText().trim(),
                    Double.parseDouble(creditLimitField.getText().trim()),
                    plan
            );

            account.setOutstandingBalance(existingAccount.getOutstandingBalance());
            account.setStatus(existingAccount.getStatus());

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
        if (id.isEmpty()) {
            setMessage("Load an account first.", false);
            return;
        }

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
        if (id.isEmpty()) {
            setMessage("Load an account first.", false);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete the discount plan for account " + id + "?",
                "Confirm Delete Discount", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

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
        if (id.isEmpty()) {
            setMessage("Load an account first.", false);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete merchant account " + id + "?\nThis cannot be undone.",
                "Confirm Delete Account", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

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
        if (id.isEmpty()) {
            setMessage("Load an account first.", false);
            return;
        }

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

    // Helper Methods
    private void populateForm(MerchantAccount account) {
        merchantIdField.setText(account.getMerchantId());
        businessNameField.setText(account.getBusinessName());
        emailField.setText(account.getEmail());
        phoneField.setText(account.getPhone());
        addressField.setText(account.getAddress());
        creditLimitField.setText(String.valueOf(account.getCreditLimit()));
        discountValueField.setText(String.valueOf(account.getDiscountPercentage()));
    }

    private void clearForm() {
        merchantIdField.setText("");
        businessNameField.setText("");
        emailField.setText("");
        phoneField.setText("");
        addressField.setText("");
        creditLimitField.setText("");
        discountValueField.setText("");
        balanceLabel.setText("0.00");
        statusLabel.setText("NORMAL");
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
            case "View All Merchants":
                dispose();
                new MerchantList(fullname, role);
                break;
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
                dispose();
                new StaffList(fullname, role);
                break;
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