package IPOS.SA.ORD.UI;

import IPOS.SA.ACC.Service.AccountService;
import IPOS.SA.ORD.Service.InvoiceService;
import IPOS.SA.ORD.Service.OrderImportService;
import IPOS.SA.ORD.Service.OrderService;
import IPOS.SA.UI.AppFrame;
import IPOS.SA.UI.BaseFrame;
import IPOS.SA.UI.Refreshable;
import IPOS.SA.UI.ScreenRouter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
/**
 * Screen that displays a searchable, filterable list of orders.
 * Allows staff to view order details and manage the order workflow.
 *
 * Can operate in two modes:
 * - All orders — shows every order in the system (no merchant filter)
 * - Merchant orders — shows only orders for a specific merchant,
 *   set via the selectedMerchant in AppFrame when navigating from MerchantList
 *
 */
public class OrderManagement extends BaseFrame implements Refreshable {
    private final OrderService orderService;
    private final OrderImportService importService;
    private String merchantId;
    private JTable orderTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> statusFilter;
    private JLabel messageLabel;

    /**
     * Constructor; opens the order management screen showing all orders.
     * Called from the nav menu.
     *
     * @param fullname the full name of the logged-in user
     * @param role the role of the logged-in user
     * @param router the screen router used for navigation
     */
    public OrderManagement(String fullname, String role, ScreenRouter router) {
        this(fullname, role, null, router);
    }

    /**
     * Constructor — opens the order management screen filtered by merchant.
     * Called from MerchantList when a merchant is selected.
     *
     * @param fullname the full name of the logged-in user
     * @param role the role of the logged-in user
     * @param merchantId the merchant ID to filter by, or null for all orders
     * @param router the screen router used for navigation
     */
    public OrderManagement(String fullname, String role, String merchantId, ScreenRouter router) {
        super(fullname, role, "Order Management", router);
        this.merchantId    = merchantId;
        this.orderService  = new OrderService(new AccountService(), new InvoiceService());
        this.importService = new OrderImportService();
        buildContent();
        loadOrders();
    }
    /**
     * Returns the header title; shows the merchant ID if filtering by merchant.
     *
     * @return the header title string
     */
    @Override
    protected String getHeaderTitle() {
        if (merchantId != null) {
            return "Orders — " + merchantId;
        } else {
            return "Order Management";
        }
    }
    /**
     * Builds and arranges all UI components for this screen.
     * Includes a search bar, status filter, order table and action buttons.
     * Action buttons are hidden for Director of Operations (read-only role).
     */
    private void buildContent() {
        CenterPanel.setLayout(new BorderLayout(0, 0));
        CenterPanel.setBackground(new Color(245, 247, 250));

        // creates a Top bar with search controls
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(17, 24, 39));
        topBar.setBorder(new EmptyBorder(10, 16, 10, 16));

        // Adds a panel for the search button
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchPanel.setBackground(new Color(17, 24, 39));

        // Adds a label to the search bar
        JLabel searchLbl = new JLabel("Search:");
        searchLbl.setForeground(Color.WHITE);
        searchLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        // textfield stores the search value to use for the search
        searchField = new JTextField(16);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        // Triggers live search as user types
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { loadOrders(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { loadOrders(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { loadOrders(); }
        });

        JLabel filterLbl = new JLabel("Status:");
        filterLbl.setForeground(Color.WHITE);
        filterLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        // Adds a combo box to effectivley filter through the orders based on status
        statusFilter = new JComboBox<>(new String[]{
                "All", "pending", "accepted", "processing", "dispatched", "delivered"
        });
        statusFilter.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusFilter.addActionListener(e -> loadOrders());

        // Refresh button also relaods all the orders again
        JButton refreshBtn = new JButton("Refresh");
        styleBtn(refreshBtn);
        refreshBtn.addActionListener(e -> loadOrders());

        JButton syncBtn = new JButton("Sync from IPOS-CA");
        styleBtn(syncBtn);
        syncBtn.setBackground(new Color(30, 58, 138));
        syncBtn.addActionListener(e -> syncFromIPOSPU());

        // All the search buttons and text fields are added to the search panel at the top of the form
        searchPanel.add(searchLbl);
        searchPanel.add(searchField);
        searchPanel.add(filterLbl);
        searchPanel.add(statusFilter);
        searchPanel.add(refreshBtn);
        searchPanel.add(syncBtn);
        topBar.add(searchPanel, BorderLayout.WEST);

        // creates a Table setup – cells are not editable
        String[] cols = {
                "Order ID", "Merchant", "Date", "Status",
                "Total (£)", "Discount (£)", "Final (£)"
        };

        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        // A new table is created to store all of the order records from the database
        orderTable = new JTable(tableModel);
        orderTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        orderTable.setRowHeight(30);
        orderTable.setShowGrid(false);
        orderTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        orderTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        orderTable.getTableHeader().setBackground(new Color(17, 24, 39));
        orderTable.getTableHeader().setForeground(Color.WHITE);
        orderTable.getTableHeader().setReorderingAllowed(false);

        // Status colour coding
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

        // Sroll pane to scroll the table
        JScrollPane scroll = new JScrollPane(orderTable);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        // creates a bottom panel with action buttons and status label
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(17, 24, 39));
        bottomPanel.setBorder(new EmptyBorder(10, 12, 10, 12));

        // creates a panel to store all the buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonPanel.setBackground(new Color(17, 24, 39));

        // creates buttons to view, reject and process orders
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

        // Adds the buttons to the button panel
        buttonPanel.add(viewBtn);
        buttonPanel.add(acceptBtn);
        buttonPanel.add(processBtn);
        buttonPanel.add(deliverBtn);
        buttonPanel.add(rejectBtn);

        // if the user is a director, the vuttons are disabled as the director cannot process orders
        if (role.equals("Director of Operations")) {
            acceptBtn.setVisible(false);
            processBtn.setVisible(false);
            deliverBtn.setVisible(false);
            rejectBtn.setVisible(false);
        }

        // Adds a message label to notify user of any errors
        messageLabel = new JLabel(" ");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        messageLabel.setForeground(Color.WHITE);

        bottomPanel.add(buttonPanel,  BorderLayout.WEST);
        bottomPanel.add(messageLabel, BorderLayout.EAST);

        CenterPanel.add(topBar,      BorderLayout.NORTH);
        CenterPanel.add(scroll,      BorderLayout.CENTER);
        CenterPanel.add(bottomPanel, BorderLayout.SOUTH);
    }


    /**
     * Loads orders from the database into the table.
     * If merchantId is set, loads only that merchant's orders.
     * Otherwise loads all orders. Applies the current search text
     * and status filter to the query.
     */
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
    /**
     * Displays a popup showing the full details of the selected order.
     * Shows order metadata, line items and dispatch details if available.
     */
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
    /**
     * Accepts a pending order.
     * Updates the order status, increases the merchant's outstanding balance,
     * reduces stock and generates an invoice automatically.
     * Only pending orders can be accepted.
     */
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
    /**
     * Updates the status of the selected order to a new status.
     * Validates that the transition follows the correct workflow order.
     * Invalid transitions are blocked with an error message.
     *
     * @param newStatus the new status to apply
     */
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
    /**
     * Rejects a pending order after confirmation.
     * Shows a confirmation dialog before rejecting.
     * Only pending orders can be rejected.
     */
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
    /**
     * Imports undelivered orders from IPOS-PU on a background thread.
     * Updates the status bar with the number of orders imported.
     * Reloads the order table after syncing completes.
     */
    private void syncFromIPOSPU() {
        setMsg("Syncing from IPOS-PU...", true);
        new Thread(() -> {
            try {
                int count = importService.importUndeliveredOrders();
                SwingUtilities.invokeLater(() -> {
                    loadOrders();
                    if (count == 0) {
                        setMsg("No new orders from IPOS-PU.", true);
                    } else {
                        setMsg("Imported " + count + " new order" + (count == 1 ? "" : "s") + " from IPOS-PU.", true);
                    }
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() ->
                        setMsg("Sync failed: " + e.getMessage(), false));
            }
        }).start();
    }
    /**
     * Imports undelivered orders from IPOS-PU on a background thread.
     * Updates the status bar with the number of orders imported.
     * Reloads the order table after syncing completes.
     */
    private boolean isValidTransition(String current, String next) {
        switch (current) {
            case "pending":
                return next.equals("accepted");
            case "accepted":
                return next.equals("processing");
            case "processing":
                return next.equals("dispatched");
            case "dispatched":
                return next.equals("delivered");
            default:
                return false;
        }
    }

    /**
     * Applies a consistent visual style to an action button.
     *
     * @param btn the button to style
     */
    private void styleBtn(JButton btn) {
        btn.setBackground(new Color(30, 70, 90));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    }
    /**
     * Displays a success or error message in the status bar.
     *
     * @param text the message to display
     * @param success true for a green success message, false for a red error message
     */
    private void setMsg(String text, boolean success) {
        messageLabel.setText(text);
        messageLabel.setForeground(success
                ? new Color(0, 200, 100)
                : new Color(255, 100, 100));
    }
    /**
     * Called by the screen router when this screen becomes visible.
     * Reads the currently selected merchant from AppFrame and reloads
     * the order list accordingly. Clears the merchant filter if none selected.
     */
    public void onShow() {
        this.merchantId = AppFrame.getInstance().getSelectedMerchant();
        loadOrders();
    }
}
