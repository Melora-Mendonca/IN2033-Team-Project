package IPOS.SA.ACC;

import IPOS.SA.CAT.Catalogue;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AccountManagement extends JFrame {

    private final AccountService accountService;

    // For the user's role and full name that is displayed in the header
    private String fullname;
    private String role;

    // For the data entry fields on the form
    private JLabel merchantIdLabel;
    private JLabel businessNameLabel;
    private JLabel emailLabel;
    private JLabel phoneLabel;
    private JLabel addressLabel;
    private JLabel creditLimitLabel;
    private JLabel discountValueLabel;
    private JTextField merchantIdField;
    private JTextField businessNameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextField addressField;
    private JTextField creditLimitField;
    private JTextField discountValueField;

    // For the status panel that shows account status and balance
    private JLabel balanceTitle;
    private JLabel balanceLabel;
    private JLabel statusTitle;
    private JLabel statusLabel;
    private JLabel messageLabel;

    // Panels for the form layout, header and navigation
    private JPanel MainPanel;
    private JPanel ContentPanel;
    private JPanel NavPanel;
    private JPanel HeaderPanel;
    private JPanel FooterPanel;
    private JPanel CenterPanel;
    private JPanel statusCard;
    private JPanel FormPanel;
    private JPanel StatusPanel;
    private JLabel headerLabel;
    private JLabel navIcon;
    private JLabel headerSubTitle;
    private JButton logoutBtn;
    private JSeparator divider;


    public AccountManagement(String fullname, String role, AccountService accountService) {
        this.accountService = accountService;
        this.fullname = fullname;
        this.role = role;

        setTitle("Account Management");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(MainPanel);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);

        // Creates the standard header and navigation panel across all the IPOS-SA forms
        createHeaderPanel();
        createNavPanel();
        createFormPanel();
        createStatusPanel();

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
        headerLabel = new JLabel("Merchant Account Management");
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
                new AccountManagement(fullname, role, new AccountService());
            case "Create Merchant Account":
                dispose();
                new AccountManagement(fullname, role, new AccountService());
                break;
            case "Commercial Applications":
                JOptionPane.showMessageDialog(this, "Commercial Applications — coming soon.");
                break;
            case "View All Staff":
            case "Create Staff Account":
                dispose();
                //new StaffManagement(fullname, role);
            case "Manage Staff Account":
                dispose();
                //new StaffManagement(fullname, role);

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
                case "Accounts":
                    AccountService accountService = new AccountService();
                    new AccountManagement(fullname, role, accountService);
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
        merchantIdLabel = new JLabel("MERCHANT ID");
        merchantIdLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
        merchantIdLabel.setForeground(new Color(55, 65, 81));
        merchantIdLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        merchantIdField = new JTextField();
        merchantIdField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        merchantIdField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        merchantIdField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(221, 225, 231)),
                BorderFactory.createEmptyBorder(0, 12, 0, 12)
        ));

        businessNameLabel = new JLabel("BUSINESS NAME");
        businessNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
        businessNameLabel.setForeground(new Color(55, 65, 81));
        businessNameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        businessNameField = new JTextField();
        businessNameField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        businessNameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        businessNameField.setBorder(BorderFactory.createCompoundBorder(
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

        creditLimitLabel = new JLabel("CREDIT LIMIT");
        creditLimitLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
        creditLimitLabel.setForeground(new Color(55, 65, 81));
        creditLimitLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        creditLimitField = new JTextField();
        creditLimitField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        creditLimitField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        creditLimitField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(221, 225, 231)),
                BorderFactory.createEmptyBorder(0, 12, 0, 12)
        ));

        discountValueLabel = new JLabel("FIXED DISCOUNT VALUE");
        discountValueLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
        discountValueLabel.setForeground(new Color(55, 65, 81));
        discountValueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        discountValueField = new JTextField();
        discountValueField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        discountValueField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        discountValueField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(221, 225, 231)),
                BorderFactory.createEmptyBorder(0, 12, 0, 12)
        ));

        JPanel row1 = new JPanel(new GridLayout(1, 2, 12, 0));
        row1.setBackground(Color.WHITE);
        row1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        row1.add(fieldWrapper("MERCHANT ID", merchantIdField));
        row1.add(fieldWrapper("BUSINESS NAME", businessNameField));

        // Row 2 — address full width
        JPanel row2 = new JPanel(new GridLayout(1, 1));
        row2.setBackground(Color.WHITE);
        row2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        row2.add(fieldWrapper("ADDRESS", addressField));

        JPanel row3 = new JPanel(new GridLayout(1, 2, 12, 0));
        row3.setBackground(Color.WHITE);
        row3.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        row3.add(fieldWrapper("EMAIL", emailField));
        row3.add(fieldWrapper("PHONE", phoneField));

        // Row 3
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

        JPanel northWrapper = new JPanel(new BorderLayout());
        northWrapper.setBackground(new Color(245, 247, 250));
        northWrapper.add(FormPanel, BorderLayout.CENTER);

        CenterPanel.add(northWrapper, BorderLayout.CENTER);
    }

    private void createStatusPanel(){
        JPanel rightColumn = new JPanel();
        rightColumn.setLayout(new BoxLayout(rightColumn, BoxLayout.Y_AXIS));
        rightColumn.setBackground(new Color(245, 247, 250));
        rightColumn.setPreferredSize(new Dimension(200, 0));

        statusTitle = new JLabel("ACCOUNT STATUS");
        statusTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        statusTitle.setForeground(new Color(107, 114, 128));

        statusLabel = new JLabel("--");
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        statusLabel.setForeground(new Color(17, 24, 39));
        statusLabel.setBorder(new EmptyBorder(6, 0, 8, 0));

        balanceTitle = new JLabel("ACCOUNT BALANCE");
        balanceTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        balanceTitle.setForeground(new Color(107, 114, 128));

        balanceLabel = new JLabel("0.0");
        balanceLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        balanceLabel.setForeground(new Color(17, 24, 39));
        balanceLabel.setBorder(new EmptyBorder(6, 0, 8, 0));

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

        JButton loadBtn      = actionButton("Load Account", new Color(17, 24, 39));
        JButton createBtn    = actionButton("Create Account", new Color(30, 70, 90));
        JButton updateBtn    = actionButton("Update Account", new Color(30, 70, 90));
        JButton suspendBtn   = actionButton("Suspend Account", new Color(127, 29, 29));
        JButton reinstateBtn = actionButton("Reinstate Account", new Color(20, 83, 45));
        JButton clearBtn     = actionButton("Clear", new Color(107, 114, 128));

        loadBtn.addActionListener(e      -> loadAccount());
        createBtn.addActionListener(e    -> createAccount());
        updateBtn.addActionListener(e    -> updateAccount());
        suspendBtn.addActionListener(e   -> updateStatus("suspended"));
        reinstateBtn.addActionListener(e -> updateStatus("normal"));
        clearBtn.addActionListener(e     -> clearForm());

        actionsCard.add(actionsTitle);
        actionsCard.add(Box.createVerticalStrut(10));
        actionsCard.add(loadBtn);
        actionsCard.add(Box.createVerticalStrut(8));
        actionsCard.add(createBtn);
        actionsCard.add(Box.createVerticalStrut(8));
        actionsCard.add(updateBtn);
        actionsCard.add(Box.createVerticalStrut(8));
        actionsCard.add(suspendBtn);
        actionsCard.add(Box.createVerticalStrut(8));
        actionsCard.add(reinstateBtn);
        actionsCard.add(Box.createVerticalStrut(8));
        actionsCard.add(clearBtn);

        rightColumn.add(StatusPanel);
        rightColumn.add(Box.createVerticalStrut(12));
        rightColumn.add(actionsCard);

        CenterPanel.add(rightColumn, BorderLayout.EAST);
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
        String id = merchantIdField.getText().trim();
        if (id.isEmpty()) { messageLabel.setText("Load an account first."); return; }

        MerchantAccount acc = accountService.getAccount(id);
        if (acc == null) { messageLabel.setText("Account not found."); return; }

        statusLabel.setText(status.toUpperCase());
        messageLabel.setText("Status updated to: " + status);
        messageLabel.setForeground(new Color(0, 97, 0));
    }

    private void createAccount() {
        try {
            String id = merchantIdField.getText().trim();
            String name = businessNameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String address = addressField.getText().trim();
            double credit = Double.parseDouble(creditLimitField.getText().trim());
            double discount = Double.parseDouble(discountValueField.getText().trim());

            if (id.isEmpty()) {
                messageLabel.setText("Merchant ID is required.");
                return;
            }
            if (name.isEmpty()) {
                messageLabel.setText("Business name is required.");
                return;
            }
            if (credit < 0) {
                messageLabel.setText("Credit limit cannot be negative.");
                return;
            }
            if (discount < 0) {
                messageLabel.setText("Discount cannot be negative.");
                return;
            }
            if (accountService.accountExists(id)) {
                messageLabel.setText("Account already exists.");
                return;
            }

            DiscountPlan plan = new FixedDiscountPlan("Fixed Plan", discount);

            MerchantAccount account = new MerchantAccount(
                    id, name, email, phone, address, credit, plan
            );

            accountService.addAccount(account);

            messageLabel.setText("Account created.");
            populate(account);

        } catch (Exception ex) {
            messageLabel.setText("Error: " + ex.getMessage());
        }
    }


    private void loadAccount() {
        MerchantAccount acc = accountService.getAccount(merchantIdField.getText().trim());

        if (acc == null) {
            messageLabel.setText("Account not found.");
            return;
        }

        populate(acc);
        messageLabel.setText("Loaded.");
    }

    private void updateAccount() {
        try {
            MerchantAccount acc = accountService.getAccount(merchantIdField.getText().trim());

            if (acc == null) {
                messageLabel.setText("Load account first.");
                return;
            }

            acc.setBusinessName(businessNameField.getText());
            acc.setEmail(emailField.getText());
            acc.setPhone(phoneField.getText());
            acc.setAddress(addressField.getText());
            acc.setCreditLimit(Double.parseDouble(creditLimitField.getText()));

            double discount = Double.parseDouble(discountValueField.getText());
            acc.setDiscountPlan(new FixedDiscountPlan("Fixed Plan", discount));

            populate(acc);
            messageLabel.setText("Updated.");

        } catch (Exception ex) {
            messageLabel.setText("Error: " + ex.getMessage());
        }
    }

    private void populate(MerchantAccount acc) {
        merchantIdField.setText(acc.getMerchantId());
        businessNameField.setText(acc.getBusinessName());
        emailField.setText(acc.getEmail());
        phoneField.setText(acc.getPhone());
        addressField.setText(acc.getAddress());
        creditLimitField.setText(String.valueOf(acc.getCreditLimit()));
        if (acc.getDiscountPlan() instanceof FixedDiscountPlan) {
            discountValueField.setText(String.valueOf(((FixedDiscountPlan) acc.getDiscountPlan()).getPercentage()));
        }
        balanceLabel.setText(String.valueOf(acc.getOutstandingBalance()));
        statusLabel.setText(acc.getStatus().name());
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
        messageLabel.setText("");
    }
}