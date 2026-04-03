package IPOS.SA.ACC.UI;

import IPOS.SA.ACC.Model.MerchantAccount;
import IPOS.SA.ACC.Service.AccountService;
import IPOS.SA.UI.BaseFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MerchantList extends BaseFrame {

    private final AccountService accountService;

    private JTable merchantTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JLabel statusLabel;

    public MerchantList(String fullname, String role) {
        super(fullname, role, "Merchant List");
        this.accountService = new AccountService();
        buildContent();
        loadMerchantData();
    }

    @Override
    protected String getHeaderTitle() {
        return "Merchant List";
    }

    private void buildContent() {
        CenterPanel.setLayout(new BorderLayout(0, 0));
        CenterPanel.setBackground(new Color(245, 247, 250));

        // TOP BAR
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(17, 24, 39));
        topBar.setBorder(new EmptyBorder(10, 16, 10, 16));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchPanel.setBackground(new Color(17, 24, 39));

        JLabel searchLabel = new JLabel("Search by Merchant ID or Business Name:");
        searchLabel.setForeground(Color.WHITE);
        searchLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        searchField = new JTextField(20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JButton searchButton  = new JButton("Search");
        JButton refreshButton = new JButton("Refresh");
        styleBtn(searchButton);
        styleBtn(refreshButton);

        searchButton.addActionListener(e  -> searchMerchants());
        refreshButton.addActionListener(e -> {
            searchField.setText("");
            loadMerchantData();
        });

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(refreshButton);
        topBar.add(searchPanel, BorderLayout.WEST);

        // TABLE
        String[] columns = {
                "Merchant ID", "Business Name", "Email", "Phone",
                "Credit Limit", "Balance", "Status"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
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

        merchantTable.setDefaultRenderer(Object.class,
                new javax.swing.table.DefaultTableCellRenderer() {
                    public Component getTableCellRendererComponent(JTable t, Object val,
                                                                   boolean sel, boolean foc, int row, int col) {
                        Component c = super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                        if (!sel) {
                            String status = tableModel.getValueAt(row, 6).toString();
                            if (status.equalsIgnoreCase("suspended")) {
                                c.setBackground(new Color(255, 240, 240));
                            } else if (status.equalsIgnoreCase("in_default")) {
                                c.setBackground(new Color(255, 220, 220));
                            } else {
                                c.setBackground(Color.WHITE);
                            }
                        }
                        return c;
                    }
                }
        );

        JScrollPane scroll = new JScrollPane(merchantTable);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        // BOTTOM BUTTONS
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(17, 24, 39));
        bottomPanel.setBorder(new EmptyBorder(10, 12, 10, 12));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonPanel.setBackground(new Color(17, 24, 39));

        JButton viewDetailsButton = new JButton("View Details");
        JButton createButton      = new JButton("Create Account");
        styleBtn(viewDetailsButton);
        styleBtn(createButton);

        viewDetailsButton.addActionListener(e -> {
            int row = merchantTable.getSelectedRow();
            if (row >= 0) {
                String merchantId = tableModel.getValueAt(row, 0).toString();
                dispose();
                new AccountManagement(fullname, role, "MANAGE", merchantId);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a merchant to view details.");
            }
        });

        buttonPanel.add(viewDetailsButton);

        statusLabel = new JLabel("Ready");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        statusLabel.setForeground(Color.WHITE);

        bottomPanel.add(buttonPanel,  BorderLayout.WEST);
        bottomPanel.add(statusLabel,  BorderLayout.EAST);

        CenterPanel.add(topBar,      BorderLayout.NORTH);
        CenterPanel.add(scroll,      BorderLayout.CENTER);
        CenterPanel.add(bottomPanel, BorderLayout.SOUTH);
    }

    // DATA METHODS
    private void loadMerchantData() {
        try {
            List<MerchantAccount> merchants = accountService.getAllAccounts();
            tableModel.setRowCount(0);

            for (MerchantAccount m : merchants) {
                String status = m.getStatus().toString();
                if (m.getOutstandingBalance() > m.getCreditLimit()) status = "OVERDUE";

                tableModel.addRow(new Object[]{
                        m.getMerchantId(),
                        m.getBusinessName(),
                        m.getEmail(),
                        m.getPhone(),
                        String.format("£%.2f", m.getCreditLimit()),
                        String.format("£%.2f", m.getOutstandingBalance()),
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
        String search = searchField.getText().trim().toLowerCase();
        if (search.isEmpty()) { loadMerchantData(); return; }

        try {
            List<MerchantAccount> all = accountService.getAllAccounts();
            List<MerchantAccount> filtered = new ArrayList<>();

            for (MerchantAccount m : all) {
                if (m.getMerchantId().toLowerCase().contains(search) ||
                        m.getBusinessName().toLowerCase().contains(search) ||
                        m.getEmail().toLowerCase().contains(search)) {
                    filtered.add(m);
                }
            }

            tableModel.setRowCount(0);
            for (MerchantAccount m : filtered) {
                String status = m.getStatus().toString();
                if (m.getOutstandingBalance() > m.getCreditLimit()) status = "OVERDUE";

                tableModel.addRow(new Object[]{
                        m.getMerchantId(),
                        m.getBusinessName(),
                        m.getEmail(),
                        m.getPhone(),
                        String.format("£%.2f", m.getCreditLimit()),
                        String.format("£%.2f", m.getOutstandingBalance()),
                        status
                });
            }
            statusLabel.setText(filtered.size() + " merchant(s) found");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error searching: " + ex.getMessage());
        }
    }

    // HELPER METHODS
    private void styleBtn(JButton btn) {
        btn.setBackground(new Color(30, 70, 90));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    }
}