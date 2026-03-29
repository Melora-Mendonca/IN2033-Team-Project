package IPOS.SA.ACC.UI;

import IPOS.SA.ACC.Model.MerchantAccount;
import IPOS.SA.ACC.Service.AccountService;
import IPOS.SA.CAT.UI.Catalogue;
import IPOS.SA.RPT.UI.ReportForm;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MerchantList extends JFrame {
    private final AccountService accountService;
    private final String fullname;
    private final String role;

    // GUI Components
    private JTable merchantTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton refreshButton;
    private JButton searchButton;
    private JButton viewDetailsButton;
    private JLabel statusLabel;

    // Navigation panels
    private JPanel MainPanel;
    private JPanel NavPanel;
    private JPanel ContentPanel;
    private JPanel HeaderPanel;
    private JPanel CenterPanel;
    private JPanel FooterPanel;
    private JLabel headerLabel;
    private JLabel navIcon;
    private JButton logoutBtn;
    private JSeparator divider;

    public MerchantList(String fullname, String role) {
        this.accountService = new AccountService();
        this.fullname = fullname;
        this.role = role;

        setTitle("Merchant List");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setContentPane(MainPanel);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);

        initializeUI();
        loadMerchantData();

        setVisible(true);
    }

    private void initializeUI() {
        createHeaderPanel();
        createNavPanel();
        createContentPanel();
    }

    private void createHeaderPanel() {
        HeaderPanel.setLayout(new BoxLayout(HeaderPanel, BoxLayout.X_AXIS));
        HeaderPanel.setPreferredSize(new Dimension(1000, 54));
        HeaderPanel.setBackground(new Color(240, 252, 255));
        HeaderPanel.setBorder(BorderFactory.createEmptyBorder(0, 24, 0, 24));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        headerLabel = new JLabel("Merchant List");
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
                "View All Merchants", "Create Merchant Account", "Manage Merchant Accounts", "Commercial Applications"
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

    private void createContentPanel() {
        ContentPanel.setLayout(new BorderLayout(0, 0));
        ContentPanel.setBackground(new Color(245, 247, 250));

        // Top Panel — search
        JPanel topControlPanel = new JPanel(new BorderLayout());
        topControlPanel.setBackground(new Color(17, 24, 39));
        topControlPanel.setBorder(new EmptyBorder(10, 16, 10, 16));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchPanel.setBackground(new Color(17, 24, 39));

        JLabel searchLabel = new JLabel("Search by Merchant ID or Business Name:");
        searchLabel.setForeground(Color.WHITE);
        searchLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        searchField = new JTextField(20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        searchButton = new JButton("Search");
        refreshButton = new JButton("Refresh");
        styleButton(searchButton);
        styleButton(refreshButton);

        searchButton.addActionListener(e -> searchMerchants());
        refreshButton.addActionListener(e -> {
            searchField.setText("");
            loadMerchantData();
        });

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(refreshButton);

        topControlPanel.add(searchPanel, BorderLayout.WEST);

        // Table
        String[] columns = {
                "Merchant ID", "Business Name", "Email", "Phone",
                "Credit Limit", "Balance", "Status"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        merchantTable = new JTable(tableModel);
        merchantTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        merchantTable.setRowHeight(30);
        merchantTable.setShowGrid(false);
        merchantTable.setFillsViewportHeight(true);
        merchantTable.getTableHeader().setReorderingAllowed(false);
        merchantTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        merchantTable.getTableHeader().setBackground(new Color(17, 24, 39));
        merchantTable.getTableHeader().setForeground(Color.WHITE);

        // Color code overdue merchants (balance > credit limit)
        merchantTable.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (!isSelected) {
                    String status = (String) tableModel.getValueAt(row, 6);
                    if ("SUSPENDED".equals(status) || "suspended".equalsIgnoreCase(status)) {
                        c.setBackground(new Color(255, 240, 240));
                    } else {
                        c.setBackground(Color.WHITE);
                    }
                }
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(merchantTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        // Bottom buttons
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(17, 24, 39));
        bottomPanel.setBorder(new EmptyBorder(10, 12, 10, 12));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonPanel.setBackground(new Color(17, 24, 39));

        viewDetailsButton = new JButton("View Details");
        styleButton(viewDetailsButton);

        viewDetailsButton.addActionListener(e -> {
            int selectedRow = merchantTable.getSelectedRow();
            if (selectedRow >= 0) {
                String merchantId = (String) merchantTable.getValueAt(selectedRow, 0);
                dispose();
                new AccountManagement(fullname, role, "MANAGE", merchantId);  // Pass merchantId
            } else {
                JOptionPane.showMessageDialog(this, "Please select a merchant to view details.");
            }
        });

        buttonPanel.add(viewDetailsButton);

        statusLabel = new JLabel("Ready");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        statusLabel.setForeground(Color.WHITE);

        bottomPanel.add(buttonPanel, BorderLayout.WEST);
        bottomPanel.add(statusLabel, BorderLayout.EAST);

        ContentPanel.add(topControlPanel, BorderLayout.NORTH);
        ContentPanel.add(scrollPane, BorderLayout.CENTER);
        ContentPanel.add(bottomPanel, BorderLayout.SOUTH);
    }

    private void styleButton(JButton btn) {
        btn.setBackground(new Color(30, 70, 90));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    }

    private void loadMerchantData() {
        try {
            List<MerchantAccount> merchants = accountService.getAllAccounts();
            tableModel.setRowCount(0);

            for (MerchantAccount merchant : merchants) {
                String status = merchant.getStatus().toString();
                // Highlight overdue merchants with a visual indicator
                if (merchant.getOutstandingBalance() > merchant.getCreditLimit()) {
                    status = "OVERDUE";
                }

                tableModel.addRow(new Object[]{
                        merchant.getMerchantId(),
                        merchant.getBusinessName(),
                        merchant.getEmail(),
                        merchant.getPhone(),
                        String.format("£%.2f", merchant.getCreditLimit()),
                        String.format("£%.2f", merchant.getOutstandingBalance()),
                        status
                });
            }

            statusLabel.setText(merchants.size() + " merchants loaded");

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading merchant data: " + ex.getMessage());
            statusLabel.setText("Error loading data");
        }
    }

    private void searchMerchants() {
        String searchText = searchField.getText().trim().toLowerCase();

        if (searchText.isEmpty()) {
            loadMerchantData();
            return;
        }

        try {
            List<MerchantAccount> allMerchants = accountService.getAllAccounts();
            List<MerchantAccount> filteredMerchants = new java.util.ArrayList<>();

            for (MerchantAccount merchant : allMerchants) {
                if (merchant.getMerchantId().toLowerCase().contains(searchText) ||
                        merchant.getBusinessName().toLowerCase().contains(searchText) ||
                        merchant.getEmail().toLowerCase().contains(searchText)) {
                    filteredMerchants.add(merchant);
                }
            }

            tableModel.setRowCount(0);

            for (MerchantAccount merchant : filteredMerchants) {
                String status = merchant.getStatus().toString();
                if (merchant.getOutstandingBalance() > merchant.getCreditLimit()) {
                    status = "OVERDUE";
                }

                tableModel.addRow(new Object[]{
                        merchant.getMerchantId(),
                        merchant.getBusinessName(),
                        merchant.getEmail(),
                        merchant.getPhone(),
                        String.format("£%.2f", merchant.getCreditLimit()),
                        String.format("£%.2f", merchant.getOutstandingBalance()),
                        status
                });
            }

            statusLabel.setText(filteredMerchants.size() + " merchant(s) found");

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error searching merchants: " + ex.getMessage());
        }
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
                case "Reports":
                    new ReportForm(fullname, role);
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