package IPOS.SA.ORD.UI;

import IPOS.SA.ORD.Model.Order;
import IPOS.SA.ORD.Model.OrderItem;
import IPOS.SA.ORD.OrderStatus;
import IPOS.SA.ACC.Service.AccountService;
import IPOS.SA.ORD.Service.InvoiceService;
import IPOS.SA.ORD.Service.OrderService;
import IPOS.SA.UI.BaseFrame;
import IPOS.SA.CAT.Service.catalogueService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class OrderProcessingFrame extends BaseFrame {

    private final OrderService orderService;

    private JTable orderTable;
    private DefaultTableModel tableModel;

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

    public OrderProcessingFrame(String fullname, String role) {
        super(fullname, role, getTitleByRole(role));
        this.orderService = new OrderService(new AccountService(), new InvoiceService());
        buildContent();
        configureByRole();
        loadOrders();
    }

    private static String getTitleByRole(String role) {
        if (role.equals("Warehouse Employee")) {
            return "Order Picking & Packing";
        } else if (role.equals("Delivery Employee")) {
            return "Order Dispatch";
        }
        return "Order Processing";
    }

    @Override
    protected String getHeaderTitle() {
        if (role.equals("Warehouse Employee")) {
            return "Order Picking & Packing";
        } else if (role.equals("Delivery Employee")) {
            return "Order Dispatch";
        }
        return "Order Processing";
    }

    private void buildContent() {
        CenterPanel.setLayout(new BorderLayout());
        CenterPanel.setBackground(new Color(245, 247, 250));

        CenterPanel.add(createTablePanel(), BorderLayout.CENTER);

        // Create both panels (one will be hidden based on role)
        pickingPanel = createPickingPanel();
        dispatchPanel = createDispatchPanel();

        // Start with dispatch panel as default (will be replaced in configureByRole)
        CenterPanel.add(dispatchPanel, BorderLayout.SOUTH);
    }

    private void configureByRole() {
        if (role.equals("Warehouse Employee")) {
            // Remove dispatch panel, add picking panel
            CenterPanel.remove(dispatchPanel);
            CenterPanel.add(pickingPanel, BorderLayout.SOUTH);

            // Hide courier/dispatch fields (already handled by using different panel)
            // Refresh the view
            CenterPanel.revalidate();
            CenterPanel.repaint();

        } else if (role.equals("Delivery Employee")) {
            // Keep dispatch panel, ensure picking panel not added
            CenterPanel.remove(pickingPanel);
            CenterPanel.add(dispatchPanel, BorderLayout.SOUTH);
            CenterPanel.revalidate();
            CenterPanel.repaint();
        }
        // Admin/Manager would see both (you can add that later)
    }

    // ── TABLE PANEL (modified with role-based filtering) ──────────
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(221, 225, 231), 1),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)));

        // Title panel with refresh button
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
        refreshButton.addActionListener(e -> loadOrders());

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
                        if (val != null) switch (val.toString().toLowerCase()) {
                            case "pending":    lbl.setBackground(new Color(255, 243, 205)); lbl.setForeground(new Color(133, 100, 4));  break;
                            case "accepted":   lbl.setBackground(new Color(207, 226, 255)); lbl.setForeground(new Color(10, 64, 168)); break;
                            case "processing": lbl.setBackground(new Color(207, 226, 255)); lbl.setForeground(new Color(10, 64, 168)); break;
                            case "dispatched": lbl.setBackground(new Color(198, 239, 206)); lbl.setForeground(new Color(0, 97, 0));    break;
                            default:           lbl.setBackground(Color.WHITE);              lbl.setForeground(Color.BLACK);
                        }
                        return lbl;
                    }
                }
        );

        // Add selection listener to clear fields when row changes
        orderTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                clearInputFields();
            }
        });

        JScrollPane scroll = new JScrollPane(orderTable);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        panel.add(titlePanel, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private String getTableTitle() {
        if (role.equals("Warehouse Employee")) {
            return "Orders Ready for Picking";
        } else if (role.equals("Delivery Employee")) {
            return "Orders Ready for Dispatch";
        }
        return "All Orders";
    }

    // ── PICKING PANEL (for Warehouse Employee) ────────────────────
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

        JLabel quantityLbl = new JLabel("Quantity Picked:");
        quantityLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        quantityPickedField = new JTextField(10);
        quantityPickedField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        quantityPickedField.setToolTipText("Enter the quantity picked from stock");

        JLabel infoLbl = new JLabel("(Stock will be reduced automatically)");
        infoLbl.setFont(new Font("Segoe UI", Font.ITALIC, 10));
        infoLbl.setForeground(new Color(107, 114, 128));

        markPickedButton = new JButton("✓ Mark as Picked & Packed");
        markPickedButton.setBackground(new Color(21, 128, 61));
        markPickedButton.setForeground(Color.WHITE);
        markPickedButton.setFocusPainted(false);
        markPickedButton.setBorderPainted(false);
        markPickedButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        markPickedButton.addActionListener(e -> markAsPicked());

        fieldsPanel.add(quantityLbl);
        fieldsPanel.add(quantityPickedField);
        fieldsPanel.add(infoLbl);
        fieldsPanel.add(Box.createHorizontalStrut(20));
        fieldsPanel.add(markPickedButton);

        panel.add(title, BorderLayout.NORTH);
        panel.add(fieldsPanel, BorderLayout.CENTER);
        return panel;
    }

    // ── DISPATCH PANEL (for Delivery Employee) ────────────────────
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

    // ── BUSINESS LOGIC with ROLE-BASED FILTERING ──────────────────
    private void loadOrders() {
        try {
            List<Object[]> allOrders = orderService.getAllOrders();
            tableModel.setRowCount(0);

            List<Object[]> filteredOrders;

            if (role.equals("Warehouse Employee")) {
                // Warehouse sees: pending, accepted, or processing (not yet picked)
                filteredOrders = allOrders.stream()
                        .filter(order -> {
                            String status = order[3].toString().toLowerCase();
                            return status.equals("pending") ||
                                    status.equals("accepted") ||
                                    status.equals("processing");
                        })
                        .collect(Collectors.toList());
            }
            else if (role.equals("Delivery Employee")) {
                // Delivery sees: only orders that are 'processing' (ready for dispatch)
                filteredOrders = allOrders.stream()
                        .filter(order -> order[3].toString().toLowerCase().equals("processing"))
                        .collect(Collectors.toList());
            }
            else {
                // Admin/Manager sees all
                filteredOrders = allOrders;
            }

            for (Object[] order : filteredOrders) {
                tableModel.addRow(order);
            }

            // Show message if no orders available for this role
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

    private void markAsPicked() {
        int row = orderTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an order to mark as picked.");
            return;
        }

        String orderId = tableModel.getValueAt(row, 0).toString();
        String status = tableModel.getValueAt(row, 3).toString();

        if (!status.equalsIgnoreCase("pending") && !status.equalsIgnoreCase("accepted")) {
            JOptionPane.showMessageDialog(this,
                    "Only 'pending' or 'accepted' orders can be picked.");
            return;
        }

        String quantityText = quantityPickedField.getText().trim();
        if (quantityText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter the quantity picked.");
            return;
        }

        int quantityPicked;
        try {
            quantityPicked = Integer.parseInt(quantityText);
            if (quantityPicked <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid positive quantity.");
            return;
        }

        try {
            // Get full order details with items
            Order order = orderService.getOrderDetails(orderId);

            if (order == null) {
                throw new Exception("Order not found: " + orderId);
            }

            // Reduce stock for each item in the order
            catalogueService catService = new catalogueService();
            for (OrderItem item : order.getItems()) {
                String productId = item.getItemId();
                int orderedQuantity = item.getQuantity();

                // Use your existing UpdateCatalogue method
                boolean updated = catService.UpdateCatalogue(productId, orderedQuantity);

                if (!updated) {
                    throw new Exception("Failed to update stock for product: " + productId);
                }
            }

            // Update order status to "processing" (being processed)
            orderService.updateStatus(orderId, "processing");

            // Update the table
            tableModel.setValueAt("processing", row, 3);

            JOptionPane.showMessageDialog(this,
                    "Order " + orderId + " has been picked and packed.\n" +
                            "Stock has been reduced for " + order.getItems().size() + " product(s).");

            // Clear input and refresh
            quantityPickedField.setText("");
            loadOrders(); // This will refresh and remove the order from view if it no longer matches filter

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error processing order: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void dispatchOrder() {
        int row = orderTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an order to dispatch.");
            return;
        }

        String orderId = tableModel.getValueAt(row, 0).toString();
        String status = tableModel.getValueAt(row, 3).toString();

        // Only allow dispatch of orders that are 'processing'
        if (!status.equalsIgnoreCase("processing")) {
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

            // Refresh orders (remove this order from view)
            loadOrders();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

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

    // ── HELPERS ──────────────────────────────────────────────
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
