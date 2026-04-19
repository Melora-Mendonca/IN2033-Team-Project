package IPOS.SA.ORD.UI;

import IPOS.SA.ORD.Model.Order;
import IPOS.SA.ORD.Model.OrderItem;
import IPOS.SA.ORD.OrderStatus;
import IPOS.SA.ACC.Service.AccountService;
import IPOS.SA.ORD.Service.InvoiceService;
import IPOS.SA.ORD.Service.OrderService;
import IPOS.SA.UI.BaseFrame;
import IPOS.SA.CAT.Service.catalogueService;
import IPOS.SA.UI.ScreenRouter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
/**
 * Order Processing screen for IPOS-SA.
 * Adapts its behaviour and layout based on the logged-in user's role
 *
 * The screen contains three panels:
 * - Orders table — shows relevant orders for the role
 * - Order Items table — shows the pick list for the selected order
 * - Action panel — either picking confirmation or dispatch form depending on role
 */
public class OrderProcessingFrame extends BaseFrame {
    private final OrderService orderService;
    private JTable orderTable;
    private DefaultTableModel tableModel;

    // Order items table (for picking list)
    private JTable orderItemsTable;
    private DefaultTableModel itemsTableModel;
    private JPanel orderDetailsPanel;

    // Dispatch fields (for Delivery Employee)
    private JTextField courierField;
    private JTextField refNoField;
    private JSpinner deliverySpinner;
    private JPanel dispatchPanel;

    // Picking fields (for Warehouse Employee)
    private JTextField quantityPickedField;
    private JButton markPickedButton;
    private JPanel pickingPanel;

    // Common components
    private JButton refreshButton;

    /**
     * Constructor — builds the screen and configures it for the logged-in role.
     *
     * @param fullname the full name of the logged-in user
     * @param role the role of the logged-in user
     * @param router the screen router used for navigation
     */
    public OrderProcessingFrame(String fullname, String role, ScreenRouter router) {
        super(fullname, role, getTitleByRole(role), router);
        this.orderService = new OrderService(new AccountService(), new InvoiceService());
        buildContent();
        configureByRole();
        loadOrders();
    }
    /**
     * Returns the window title based on the user's role.
     *
     * @param role the user's role
     * @return the appropriate title string
     */
    private static String getTitleByRole(String role) {
        if (role.equals("Warehouse Employee")) {
            return "Order Picking & Packing";
        } else if (role.equals("Delivery Employee")) {
            return "Order Dispatch";
        }
        return "Order Processing";
    }
    /**
     * Returns the header title based on the user's role.
     *
     * @return the header title string
     */
    @Override
    protected String getHeaderTitle() {
        if (role.equals("Warehouse Employee")) {
            return "Order Picking & Packing";
        } else if (role.equals("Delivery Employee")) {
            return "Order Dispatch";
        }
        return "Order Processing";
    }
    /**
     * Returns the header title based on the user's role.
     *
     * @return the header title string
     */
    private void buildContent() {
        CenterPanel.setLayout(new BorderLayout());
        CenterPanel.setBackground(new Color(245, 247, 250));

        // Create order details panel FIRST (so itemsTableModel is initialized)
        orderDetailsPanel = createOrderDetailsPanel();

        // Create table panel (which has the selection listener)
        JPanel tablePanel = createTablePanel();

        // Main content panel that holds orders table and order items
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setBackground(Color.WHITE);
        mainContent.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        mainContent.add(tablePanel, BorderLayout.CENTER);
        mainContent.add(orderDetailsPanel, BorderLayout.SOUTH);

        CenterPanel.add(mainContent, BorderLayout.CENTER);

        // Creates the picking panel or the dispatch panel based on the user
        pickingPanel = createPickingPanel();
        dispatchPanel = createDispatchPanel();

        CenterPanel.add(dispatchPanel, BorderLayout.SOUTH);
    }
    /**
     * Swaps the action panel at the bottom based on the user's role.
     * Warehouse Employee sees the picking panel.
     * Delivery Employee sees the dispatch panel.
     */
    private void configureByRole() {
        if (role.equals("Warehouse Employee")) {
            CenterPanel.remove(dispatchPanel);
            CenterPanel.add(pickingPanel, BorderLayout.SOUTH);
            CenterPanel.revalidate();
            CenterPanel.repaint();
        } else if (role.equals("Delivery Employee")) {
            CenterPanel.remove(pickingPanel);
            CenterPanel.add(dispatchPanel, BorderLayout.SOUTH);
            CenterPanel.revalidate();
            CenterPanel.repaint();
        }
    }
    /**
     * Builds the orders table panel showing orders relevant to the current role.
     * Includes a title, refresh button and colour-coded status column.
     * Selecting a row loads the corresponding order items in the pick list.
     *
     * @return the orders table panel
     */
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(221, 225, 231), 1),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)));

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(Color.WHITE);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        JLabel title = new JLabel(getTableTitle());
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        title.setForeground(new Color(17, 24, 39));

        refreshButton = new JButton("Refresh");
        refreshButton.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        refreshButton.setBackground(new Color(240, 252, 255));
        refreshButton.setFocusPainted(false);
        refreshButton.addActionListener(e -> {
            loadOrders();
            loadItems();
                }
        );

        titlePanel.add(title, BorderLayout.WEST);
        titlePanel.add(refreshButton, BorderLayout.EAST);

        String[] cols = {"Order ID", "Merchant", "Date", "Status", "Amount"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        orderTable = new JTable(tableModel);
        styleTable(orderTable);

        orderTable.getColumnModel().getColumn(3).setCellRenderer(
                new javax.swing.table.DefaultTableCellRenderer() {
                    public Component getTableCellRendererComponent(JTable t, Object val,
                                                                   boolean sel, boolean foc, int row, int col) {
                        JLabel lbl = new JLabel(val != null ? val.toString() : "", SwingConstants.CENTER);
                        lbl.setOpaque(true);
                        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
                        if (val != null) {
                            String status = val.toString().toLowerCase();
                            switch (status) {
                                case "pending":
                                    lbl.setBackground(new Color(255, 243, 205));
                                    lbl.setForeground(new Color(133, 100, 4));
                                    break;
                                case "accepted":
                                    lbl.setBackground(new Color(207, 226, 255));
                                    lbl.setForeground(new Color(10, 64, 168));
                                    break;
                                case "processing":
                                    lbl.setBackground(new Color(207, 226, 255));
                                    lbl.setForeground(new Color(10, 64, 168));
                                    break;
                                case "dispatched":
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

        orderTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                clearInputFields();
                loadItems();
            }
        });

        JScrollPane scroll = new JScrollPane(orderTable);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        panel.add(titlePanel, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }
    /**
     * Returns the orders table title based on the user's role.
     *
     * @return the table title string
     */
    private String getTableTitle() {
        if (role.equals("Warehouse Employee")) {
            return "Orders Ready for Picking";
        } else if (role.equals("Delivery Employee")) {
            return "Orders Ready for Dispatch";
        }
        return "All Orders";
    }
    // ── ORDER ITEMS PANEL (Pick List) ───────────────────────────────────
    /**
     * Builds the order items pick list panel.
     * Shows the items and quantities for the selected order.
     * Used by the Warehouse Employee to verify what needs to be picked.
     *
     * @return the order items panel
     */
    private JPanel createOrderDetailsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(221, 225, 231), 1),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)));

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(Color.WHITE);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        JLabel title = new JLabel("Order Items - Pick List");
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        title.setForeground(new Color(17, 24, 39));

        titlePanel.add(title, BorderLayout.WEST);

        String[] cols = {"Product ID", "Description", "Quantity to Pick", "Unit Price"};
        itemsTableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        orderItemsTable = new JTable(itemsTableModel);
        orderItemsTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        orderItemsTable.setRowHeight(30);
        orderItemsTable.setShowGrid(false);
        orderItemsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        orderItemsTable.getTableHeader().setBackground(new Color(17, 24, 39));
        orderItemsTable.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scroll = new JScrollPane(orderItemsTable);
        scroll.setPreferredSize(new Dimension(0, 150));

        panel.add(titlePanel, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }
    /**
     * Builds the picking confirmation panel for Warehouse Employee.
     * Shows an instruction label and a Confirm Picked and Packed button.
     *
     * @return the picking action panel
     */
    private JPanel createPickingPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(221, 225, 231), 1),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)));

        JLabel title = new JLabel("Pick & Pack Order");
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        title.setForeground(new Color(17, 24, 39));
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        JPanel fieldsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        fieldsPanel.setBackground(Color.WHITE);

        JLabel infoLbl = new JLabel("Use the pick list above to see items and quantities to pick.");
        infoLbl.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        infoLbl.setForeground(new Color(107, 114, 128));

        markPickedButton = new JButton("Confirm Picked & Packed");
        markPickedButton.setBackground(new Color(21, 128, 61));
        markPickedButton.setForeground(Color.WHITE);
        markPickedButton.setFocusPainted(false);
        markPickedButton.setBorderPainted(false);
        markPickedButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        markPickedButton.addActionListener(e -> markAsPicked());

        fieldsPanel.add(infoLbl);
        fieldsPanel.add(Box.createHorizontalStrut(20));
        fieldsPanel.add(markPickedButton);

        panel.add(title, BorderLayout.NORTH);
        panel.add(fieldsPanel, BorderLayout.CENTER);
        return panel;
    }
    /**
     * Builds the dispatch form panel for Delivery Employee.
     * Contains fields for courier name, reference number, expected delivery
     * date and a Mark as Dispatched button.
     * Required fields are marked with an asterisk in red.
     *
     * @return the dispatch action panel
     */
    private JPanel createDispatchPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(221, 225, 231), 1),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)));

        JLabel title = new JLabel("Dispatch Order");
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        title.setForeground(new Color(17, 24, 39));
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        JPanel fieldsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        fieldsPanel.setBackground(Color.WHITE);

        JLabel courierLbl = new JLabel("Courier:*");
        courierLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        courierLbl.setForeground(Color.RED);
        courierField = new JTextField(15);
        courierField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        courierField.setToolTipText("Required - Name of courier service");

        JLabel refLbl = new JLabel("Ref No:*");
        refLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        refLbl.setForeground(Color.RED);
        refNoField = new JTextField(15);
        refNoField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        refNoField.setToolTipText("Required - Courier tracking/reference number");

        JLabel dateLbl = new JLabel("Est. Delivery:*");
        dateLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dateLbl.setForeground(Color.RED);
        deliverySpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor editor = new JSpinner.DateEditor(deliverySpinner, "yyyy-MM-dd");
        deliverySpinner.setEditor(editor);
        deliverySpinner.setValue(java.util.Date.from(
                LocalDate.now().plusDays(3)
                        .atStartOfDay(java.time.ZoneId.systemDefault()).toInstant()));
        deliverySpinner.setPreferredSize(new Dimension(110, 28));

        JButton dispatchBtn = new JButton("Mark as Dispatched");
        dispatchBtn.setBackground(new Color(30, 70, 90));
        dispatchBtn.setForeground(Color.WHITE);
        dispatchBtn.setFocusPainted(false);
        dispatchBtn.setBorderPainted(false);
        dispatchBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        dispatchBtn.addActionListener(e -> dispatchOrder());

        fieldsPanel.add(courierLbl);
        fieldsPanel.add(courierField);
        fieldsPanel.add(refLbl);
        fieldsPanel.add(refNoField);
        fieldsPanel.add(dateLbl);
        fieldsPanel.add(deliverySpinner);
        fieldsPanel.add(dispatchBtn);

        panel.add(title, BorderLayout.NORTH);
        panel.add(fieldsPanel, BorderLayout.CENTER);
        return panel;
    }
    /**
     * Loads orders from the database filtered by the current role.
     * Warehouse Employee sees pending, accepted and processing orders.
     * Delivery Employee sees only processing orders.
     * Other roles see all orders.
     * Shows a message if no relevant orders are found.
     */
    private void loadOrders() {
        try {
            List<Object[]> allOrders = orderService.getAllOrders();
            tableModel.setRowCount(0);

            List<Object[]> filteredOrders;

            if (role.equals("Warehouse Employee")) {
                filteredOrders = allOrders.stream()
                        .filter(order -> {
                            String status = order[3].toString().toLowerCase();
                            return status.equals("pending") ||
                                    status.equals("accepted") ||
                                    status.equals("processing");
                        })
                        .collect(Collectors.toList());
            } else if (role.equals("Delivery Employee")) {
                filteredOrders = allOrders.stream()
                        .filter(order -> order[3].toString().toLowerCase().equals("processing"))
                        .collect(Collectors.toList());
            } else {
                filteredOrders = allOrders;
            }

            for (Object[] order : filteredOrders) {
                tableModel.addRow(order);
            }

            if (filteredOrders.isEmpty()) {
                String message = role.equals("Warehouse Employee") ?
                        "No orders ready for picking." :
                        "No orders ready for dispatch.";
                JOptionPane.showMessageDialog(this, message);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading orders: " + e.getMessage());
        }
    }
    /**
     * Loads orders from the database filtered by the current role.
     * Warehouse Employee sees pending, accepted and processing orders.
     * Delivery Employee sees only processing orders.
     * Other roles see all orders.
     * Shows a message if no relevant orders are found.
     */
    private void loadItems() {
        itemsTableModel.setRowCount(0);

        int row = orderTable.getSelectedRow();
        if (row < 0) return;

        String orderId = tableModel.getValueAt(row, 0).toString();

        try {
            Order order = orderService.getOrderDetails(orderId);
            if (order != null && order.getItems() != null) {
                for (OrderItem item : order.getItems()) {
                    itemsTableModel.addRow(new Object[]{
                            item.getItemId(),
                            getItemDescription(item.getItemId()),
                            item.getQuantity(),
                            String.format("£%.2f", item.getUnitPrice())
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Looks up the description of a catalogue item by its ID.
     *
     * @param itemId the catalogue item ID
     * @return the item description, or "Unknown" if not found
     */
    private String getItemDescription(String itemId) {
        try {
            catalogueService catService = new catalogueService();
            var item = catService.loadItem(itemId);
            return item != null ? item.getDescription() : "Unknown";
        } catch (Exception e) {
            return "Unknown";
        }
    }
    /**
     * Marks the selected order as picked and packed.
     * Validates the order is in pending or accepted status.
     * Shows a confirmation dialog listing all items to pick.
     * On confirmation — reduces catalogue stock for all items and
     * advances the order status to processing.
     */
    private void markAsPicked() {
        int row = orderTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an order to mark as picked.");
            return;
        }

        String orderId = tableModel.getValueAt(row, 0).toString();
        String status = tableModel.getValueAt(row, 3).toString().toLowerCase();

        if (!status.equals("pending") && !status.equals("accepted")) {
            JOptionPane.showMessageDialog(this,
                    "Only 'pending' or 'accepted' orders can be picked.");
            return;
        }

        // Confirm with warehouse employee
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you have picked ALL items for order " + orderId + "?\n" +
                        "Check the pick list above to verify quantities.",
                "Confirm Picking",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            Order order = orderService.getOrderDetails(orderId);

            if (order == null) {
                throw new Exception("Order not found: " + orderId);
            }

            // Reduce stock for each item using the quantities from the order
            catalogueService catService = new catalogueService();
            for (OrderItem item : order.getItems()) {
                String productId = item.getItemId();
                int orderedQuantity = item.getQuantity();  // ← Use the order's quantity
                boolean updated = catService.UpdateCatalogue(productId, orderedQuantity);
                if (!updated) {
                    throw new Exception("Failed to update stock for product: " + productId);
                }
            }

            orderService.updateStatus(orderId, "processing");

            tableModel.setValueAt("processing", row, 3);

            JOptionPane.showMessageDialog(this,
                    "Order " + orderId + " has been picked and packed.\n" +
                            "Stock has been reduced for " + order.getItems().size() + " product(s).");

            loadOrders();
            loadItems();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error processing order: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    /**
     * Dispatches the selected order.
     * Validates the order is in processing status and all courier
     * details have been entered. Updates the order with dispatch
     * details and advances the status to dispatched.
     */
    private void dispatchOrder() {
        int row = orderTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an order to dispatch.");
            return;
        }

        String orderId = tableModel.getValueAt(row, 0).toString();
        String status = tableModel.getValueAt(row, 3).toString().toLowerCase();

        if (!status.equals("processing")) {
            JOptionPane.showMessageDialog(this,
                    "Only orders with status 'processing' can be dispatched.\n" +
                            "Warehouse must pick and pack the order first.");
            return;
        }

        String courier = courierField.getText().trim();
        String refNo = refNoField.getText().trim();

        if (courier.isEmpty() || refNo.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter all courier details (marked with *).");
            return;
        }

        java.util.Date deliveryDate = (java.util.Date) deliverySpinner.getValue();

        try {
            orderService.updateOrderStatus(orderId, OrderStatus.DISPATCHED,
                    fullname, courier, refNo,
                    deliveryDate.toInstant()
                            .atZone(java.time.ZoneId.systemDefault()).toLocalDate());

            tableModel.setValueAt("dispatched", row, 3);
            JOptionPane.showMessageDialog(this,
                    "Order " + orderId + " marked as dispatched.\n" +
                            "Courier: " + courier + "\n" +
                            "Tracking Ref: " + refNo);

            clearInputFields();
            loadOrders();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
    /**
     * Clears all input fields in the active action panel.
     * Resets courier fields for Delivery Employee.
     * Resets expected delivery date to 3 days from today.
     */
    private void clearInputFields() {
        if (role.equals("Warehouse Employee")) {
            if (quantityPickedField != null) {
                quantityPickedField.setText("");
            }
        } else if (role.equals("Delivery Employee")) {
            if (courierField != null) {
                courierField.setText("");
            }
            if (refNoField != null) {
                refNoField.setText("");
            }
            if (deliverySpinner != null) {
                deliverySpinner.setValue(java.util.Date.from(
                        LocalDate.now().plusDays(3)
                                .atStartOfDay(java.time.ZoneId.systemDefault()).toInstant()));
            }
        }
    }
    /**
     * Applies a consistent visual style to a JTable.
     *
     * @param table the table to style
     */
    private void styleTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(30);
        table.setShowGrid(false);
        table.setSelectionBackground(new Color(207, 226, 255));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(17, 24, 39));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setReorderingAllowed(false);
    }
}
