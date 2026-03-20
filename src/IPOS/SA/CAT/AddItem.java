package IPOS.SA.CAT;

import IPOS.SA.ACC.AccountManagement;
import IPOS.SA.ACC.AccountService;
import IPOS.SA.ACC.AdminDashboard;
import IPOS.SA.ACC.LoginForm;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AddItem extends JFrame {
    private final String fullname;
    private final String role;
    private JLabel itemIdLabel;
    private JTextField itemIdField;
    private JLabel descriptionLabel;
    private JTextField descriptionField;
    private JLabel unitLabel;
    private JTextField unitField;
    private JLabel costLabel;
    private JTextField costField;
    private JLabel packageLabel;
    private JTextField packageField;
    private JLabel units_per_packLabel;
    private JTextField units_per_packField;
    private JLabel availabilityLabel;
    private JTextField availabilityField;
    private JLabel stock_limitLabel;
    private JTextField stock_limitField;
    private JPanel MainPanel;
    private JPanel NavPanel;
    private JPanel ContentPanel;
    private JPanel FooterPanel;
    private JPanel HeaderPanel;
    private JPanel CenterPanel;
    private JPanel FormPanel;
    private JLabel headerLabel;
    private JLabel navIcon;
    private JButton logoutBtn;
    private JSeparator divider;
    private JLabel messageLabel;
    public AddItem(String fullname, String role) {
        this.fullname = fullname;
        this.role = role;

        createHeaderPanel();
        createNavPanel();
    }
    private void createHeaderPanel() {
        HeaderPanel.setLayout(new BoxLayout(HeaderPanel, BoxLayout.X_AXIS));
        HeaderPanel.setPreferredSize(new Dimension(1000, 54));
        HeaderPanel.setBackground(new Color(240, 252, 255));
        HeaderPanel.setBorder(BorderFactory.createEmptyBorder(0, 24, 0, 24));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        headerLabel = new JLabel("Catalogue");
        headerLabel.setForeground(Color.BLACK);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));

        textPanel.add(headerLabel);
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

    private void createCenterPanel() {
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
        itemIdLabel = new JLabel("ITEM ID");
        itemIdLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
        itemIdLabel.setForeground(new Color(55, 65, 81));
        itemIdLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        itemIdField = new JTextField();
        itemIdField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        itemIdField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        itemIdField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(221, 225, 231)),
                BorderFactory.createEmptyBorder(0, 12, 0, 12)
        ));

        descriptionLabel = new JLabel("DESCRIPTION");
        descriptionLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
        descriptionLabel.setForeground(new Color(55, 65, 81));
        descriptionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        descriptionField = new JTextField();
        descriptionField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descriptionField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        descriptionField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(221, 225, 231)),
                BorderFactory.createEmptyBorder(0, 12, 0, 12)
        ));

        packageLabel = new JLabel("PACKAGE TYPE");
        packageLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
        packageLabel.setForeground(new Color(55, 65, 81));
        packageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        packageField = new JTextField();
        packageField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        packageField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        packageField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(221, 225, 231)),
                BorderFactory.createEmptyBorder(0, 12, 0, 12)
        ));

        unitLabel = new JLabel("UNIT");
        unitLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
        unitLabel.setForeground(new Color(55, 65, 81));
        unitLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        unitField = new JTextField();
        unitField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        unitField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        unitField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(221, 225, 231)),
                BorderFactory.createEmptyBorder(0, 12, 0, 12)
        ));

        units_per_packLabel = new JLabel("UNITS PER PACK");
        units_per_packLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
        units_per_packLabel.setForeground(new Color(55, 65, 81));
        units_per_packLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        units_per_packField = new JTextField();
        units_per_packField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        units_per_packField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        units_per_packField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(221, 225, 231)),
                BorderFactory.createEmptyBorder(0, 12, 0, 12)
        ));

        costLabel = new JLabel("PACKAGE COST");
        costLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
        costLabel.setForeground(new Color(55, 65, 81));
        costLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        costField = new JTextField();
        costField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        costField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        costField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(221, 225, 231)),
                BorderFactory.createEmptyBorder(0, 12, 0, 12)
        ));

        availabilityLabel = new JLabel("AVAILABILITY");
        availabilityLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
        availabilityLabel.setForeground(new Color(55, 65, 81));
        availabilityLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        availabilityField = new JTextField();
        availabilityField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        availabilityField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        availabilityField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(221, 225, 231)),
                BorderFactory.createEmptyBorder(0, 12, 0, 12)
        ));

        stock_limitLabel = new JLabel("STOCK LIMIT");
        stock_limitLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
        stock_limitLabel.setForeground(new Color(55, 65, 81));
        stock_limitLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        stock_limitField = new JTextField();
        stock_limitField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        stock_limitField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        stock_limitField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(221, 225, 231)),
                BorderFactory.createEmptyBorder(0, 12, 0, 12)
        ));

        JPanel row1 = new JPanel(new GridLayout(1, 2, 12, 0));
        row1.setBackground(Color.WHITE);
        row1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        row1.add(fieldWrapper("ITEM ID", itemIdField));
        row1.add(fieldWrapper("DESCRIPTION", descriptionField));

        // Row 2 — address full width
        JPanel row2 = new JPanel(new GridLayout(1, 1));
        row2.setBackground(Color.WHITE);
        row2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        row2.add(fieldWrapper("PACKAGE TYPE", packageField ));
        row2.add(fieldWrapper("UNIT", unitField));

        JPanel row3 = new JPanel(new GridLayout(1, 2, 12, 0));
        row3.setBackground(Color.WHITE);
        row3.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        row3.add(fieldWrapper("UNIT PER PACK", units_per_packField));
        row3.add(fieldWrapper("PACKAGE COST", costField));

        // Row 3
        JPanel row4 = new JPanel(new GridLayout(1, 2, 12, 0));
        row4.setBackground(Color.WHITE);
        row4.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        row4.add(fieldWrapper("AVAILABILITY", availabilityField));
        row4.add(fieldWrapper("STOCK LIMIT", stock_limitField));

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
    }}
