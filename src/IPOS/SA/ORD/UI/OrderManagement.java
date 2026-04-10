package IPOS.SA.ORD.UI;

import IPOS.SA.ACC.Service.AccountService;
import IPOS.SA.ORD.Service.InvoiceService;
import IPOS.SA.ORD.Service.OrderService;
import IPOS.SA.UI.BaseFrame;
import IPOS.SA.UI.Refreshable;
import IPOS.SA.UI.ScreenRouter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class OrderManagement extends BaseFrame implements Refreshable {

    private final OrderService orderService;
    private final String merchantId;

    private JTable orderTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> statusFilter;
    private JLabel messageLabel;

    // Called from nav — no merchant filter
    public OrderManagement(String fullname, String role, ScreenRouter router) {
        this(fullname, role, null, router);
    }

    // Called from MerchantList — filtered by merchant
    public OrderManagement(String fullname, String role, String merchantId, ScreenRouter router) {
        super(fullname, role, "Order Management", router);
        this.merchantId   = merchantId;
        this.orderService = new OrderService(new AccountService(), new InvoiceService());
        buildContent();
        loadOrders();
    }

    @Override
    protected String getHeaderTitle() {
        if (merchantId != null) {
            return "Orders — " + merchantId;
        } else {
            return "Order Management";
        }
    }

    private void buildContent() {
        CenterPanel.setLayout(new BorderLayout(0, 0));
        CenterPanel.setBackground(new Color(245, 247, 250));

        // ── TOP BAR ──────────────────────────────────────────
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(17, 24, 39));
        topBar.setBorder(new EmptyBorder(10, 16, 10, 16));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchPanel.setBackground(new Color(17, 24, 39));

        JLabel searchLbl = new JLabel("Search:");
        searchLbl.setForeground(Color.WHITE);
        searchLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        searchField = new JTextField(16);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { loadOrders(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { loadOrders(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { loadOrders(); }
        });

        JLabel filterLbl = new JLabel("Status:");
        filterLbl.setForeground(Color.WHITE);
        filterLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        statusFilter = new JComboBox<>(new String[]{
                "All", "pending", "accepted", "processing", "dispatched", "delivered"
        });
        statusFilter.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusFilter.addActionListener(e -> loadOrders());

        JButton refreshBtn = new JButton("Refresh");
        styleBtn(refreshBtn);
        refreshBtn.addActionListener(e -> loadOrders());

        searchPanel.add(searchLbl);
        searchPanel.add(searchField);
        searchPanel.add(filterLbl);
        searchPanel.add(statusFilter);
        searchPanel.add(refreshBtn);
        topBar.add(searchPanel, BorderLayout.WEST);

        // ── TABLE ─────────────────────────────────────────────
        String[] cols = {
                "Order ID", "Merchant", "Date", "Status",
                "Total (£)", "Discount (£)", "Final (£)"
        };

        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        orderTable = new JTable(tableModel);
        orderTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        orderTable.setRowHeight(30);
        orderTable.setShowGrid(false);
        orderTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        orderTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        orderTable.getTableHeader().setBackground(new Color(17, 24, 39));
        orderTable.getTableHeader().setForeground(Color.WHITE);
        orderTable.getTableHeader().setReorderingAllowed(false);

        orderTable.getColumnModel().getColumn(3).setCellRenderer(
                new DefaultTableCellRenderer() {
                    public Component getTableCellRendererComponent(JTable t, Object val,
                                                                   boolean sel, boolean foc, int row, int col) {
                        JLabel lbl = new JLabel(val != null ? val.toString() : "", SwingConstants.CENTER);
                        lbl.setOpaque(true);
                        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
                        if (val != null) {
                            String status = val.toString().toLowerCase();
                            switch (status) {
                                case "pending":
                                    lbl.setBackground(new Color(216, 186, 223));
                                    lbl.setForeground(new Color(90, 4, 133));
                                    break;
                                case "accepted":
                                    lbl.setBackground(new Color(255, 243, 205));
                                    lbl.setForeground(new Color(133, 100, 4));
                                    break;
                                case "processing":
                                    lbl.setBackground(new Color(207, 226, 255));
                                    lbl.setForeground(new Color(10, 64, 168));
                                    break;
                                case "dispatched":
                                    lbl.setBackground(new Color(255, 220, 185));
                                    lbl.setForeground(new Color(168, 80, 10));
                                    break;
                                case "delivered":
                                    lbl.setBackground(new Color(198, 239, 206));
                                    lbl.setForeground(new Color(0, 97, 0));
                                    break;
                                default:
                                    lbl.setBackground(Color.WHITE);
                                    lbl.setForeground(Color.BLACK);
                            }
                        }
                        return lbl;
                    }
                }
        );

        JScrollPane scroll = new JScrollPane(orderTable);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        // ── BOTTOM BUTTONS ────────────────────────────────────
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(17, 24, 39));
        bottomPanel.setBorder(new EmptyBorder(10, 12, 10, 12));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonPanel.setBackground(new Color(17, 24, 39));

        JButton viewBtn    = new JButton("View Details");
        JButton acceptBtn  = new JButton("Accept");
        JButton processBtn = new JButton("Mark Processing");
        JButton deliverBtn = new JButton("Mark Delivered");
        JButton rejectBtn  = new JButton("Reject");

        styleBtn(viewBtn);
        styleBtn(acceptBtn);
        styleBtn(processBtn);
        styleBtn(deliverBtn);
        styleBtn(rejectBtn);

        acceptBtn.setBackground(new Color(20, 83, 45));
        rejectBtn.setBackground(new Color(127, 29, 29));

        viewBtn.addActionListener(e    -> viewOrderDetails());
        acceptBtn.addActionListener(e  -> acceptOrder());
        processBtn.addActionListener(e -> updateStatus("processing"));
        deliverBtn.addActionListener(e -> updateStatus("delivered"));
        rejectBtn.addActionListener(e  -> rejectOrder());

        buttonPanel.add(viewBtn);
        buttonPanel.add(acceptBtn);
        buttonPanel.add(processBtn);
        buttonPanel.add(deliverBtn);
        buttonPanel.add(rejectBtn);

        if (role.equals("Director of Operations")) {
            acceptBtn.setVisible(false);
            processBtn.setVisible(false);
            deliverBtn.setVisible(false);
            rejectBtn.setVisible(false);
            // Only keep viewBtn visible
        }

        messageLabel = new JLabel(" ");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        messageLabel.setForeground(Color.WHITE);

        bottomPanel.add(buttonPanel,  BorderLayout.WEST);
        bottomPanel.add(messageLabel, BorderLayout.EAST);

        CenterPanel.add(topBar,      BorderLayout.NORTH);
        CenterPanel.add(scroll,      BorderLayout.CENTER);
        CenterPanel.add(bottomPanel, BorderLayout.SOUTH);
    }

    // ── DATA METHODS ─────────────────────────────────────────
    private void loadOrders() {
        tableModel.setRowCount(0);
        try {
            String search = searchField != null ? searchField.getText().trim() : "";
            String status = statusFilter != null
                    ? statusFilter.getSelectedItem().toString() : "All";

            List<Object[]> orders;
            if (merchantId != null) {
                orders = orderService.getMerchantFilteredOrders(merchantId, status, search);
            } else {
                orders = orderService.getFilteredOrders(status, search);
            }

            for (Object[] order : orders) tableModel.addRow(order);

            if (messageLabel != null)
                messageLabel.setText(tableModel.getRowCount() + " orders loaded");
        } catch (Exception e) {
            if (messageLabel != null)
                messageLabel.setText("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void viewOrderDetails() {
        int row = orderTable.getSelectedRow();
        if (row == -1) { setMsg("Select an order to view.", false); return; }

        String orderId = tableModel.getValueAt(row, 0).toString();

        try {
            String details = orderService.getOrderDetailsText(orderId);

            JTextArea textArea = new JTextArea(details);
            textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            textArea.setEditable(false);
            textArea.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

            JScrollPane scroll = new JScrollPane(textArea);
            scroll.setPreferredSize(new Dimension(520, 400));

            JOptionPane.showMessageDialog(this, scroll,
                    "Order — " + orderId, JOptionPane.PLAIN_MESSAGE);

        } catch (Exception e) {
            setMsg("Error: " + e.getMessage(), false);
        }
    }

    private void acceptOrder() {
        int row = orderTable.getSelectedRow();
        if (row == -1) { setMsg("Select an order first.", false); return; }

        String orderId      = tableModel.getValueAt(row, 0).toString();
        String currentStatus = tableModel.getValueAt(row, 3).toString().toLowerCase();

        if (!currentStatus.equals("pending")) {
            setMsg("Only pending orders can be accepted.", false);
            return;
        }

        try {
            if (orderService.acceptOrder(orderId)) {
                tableModel.setValueAt("accepted", row, 3);
                setMsg("Order " + orderId + " accepted. Merchant balance updated.", true);
            } else {
                setMsg("Failed to accept order.", false);
            }
        } catch (Exception e) {
            setMsg("Error: " + e.getMessage(), false);
        }
    }

    private void updateStatus(String newStatus) {
        int row = orderTable.getSelectedRow();
        if (row == -1) { setMsg("Select an order first.", false); return; }

        String orderId       = tableModel.getValueAt(row, 0).toString();
        String currentStatus = tableModel.getValueAt(row, 3).toString().toLowerCase();

        if (!isValidTransition(currentStatus, newStatus)) {
            setMsg("Cannot change from " + currentStatus + " to " + newStatus + ".", false);
            return;
        }

        try {
            if (orderService.updateStatus(orderId, newStatus)) {
                tableModel.setValueAt(newStatus, row, 3);
                setMsg("Order " + orderId + " updated to " + newStatus + ".", true);
            } else {
                setMsg("Failed to update order.", false);
            }
        } catch (Exception e) {
            setMsg("Error: " + e.getMessage(), false);
        }
    }

    private void rejectOrder() {
        int row = orderTable.getSelectedRow();
        if (row == -1) { setMsg("Select an order first.", false); return; }

        String orderId       = tableModel.getValueAt(row, 0).toString();
        String currentStatus = tableModel.getValueAt(row, 3).toString().toLowerCase();

        if (!currentStatus.equals("pending")) {
            setMsg("Only pending orders can be rejected.", false);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to reject order " + orderId + "?",
                "Confirm Rejection", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            if (orderService.rejectOrder(orderId)) {
                tableModel.setValueAt("rejected", row, 3);
                setMsg("Order " + orderId + " rejected.", true);
            } else {
                setMsg("Failed to reject order.", false);
            }
        } catch (Exception e) {
            setMsg("Error: " + e.getMessage(), false);
        }
    }

    private boolean isValidTransition(String current, String next) {
        switch (current) {
            case "pending":    return next.equals("accepted");
            case "accepted":   return next.equals("processing");
            case "processing": return next.equals("dispatched") || next.equals("delivered");
            case "dispatched": return next.equals("delivered");
            default:           return false;
        }
    }

    // ── HELPERS ──────────────────────────────────────────────
    private void styleBtn(JButton btn) {
        btn.setBackground(new Color(30, 70, 90));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    }

    private void setMsg(String text, boolean success) {
        messageLabel.setText(text);
        messageLabel.setForeground(success
                ? new Color(0, 200, 100)
                : new Color(255, 100, 100));
    }

    @Override
    public void onShow() {
        loadOrders();
    }
}
