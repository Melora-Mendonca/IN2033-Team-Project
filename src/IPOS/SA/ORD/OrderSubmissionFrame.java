package IPOS.SA.ORD;

import IPOS.SA.ACC.Model.FixedDiscountPlan;
import IPOS.SA.ACC.Model.Invoice;
import IPOS.SA.ACC.Model.MerchantAccount;
import IPOS.SA.ACC.Service.InvoiceService;
import IPOS.SA.CAT.Model.CatalogueItem;
import IPOS.SA.DB.InvoiceDBConnector;
import IPOS.SA.DB.OrderDBConnector;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OrderSubmissionFrame extends JFrame {

    private static final Color BG        = new Color(245, 247, 250);
    private static final Color DARK_NAVY = new Color(14, 37, 48);
    private static final Color ACCENT    = new Color(17, 54, 74);

    private final List<CatalogueItem> catalogueItems = new ArrayList<>();

    private JTable catalogueTable;
    private DefaultTableModel catalogueModel;

    private JTable basketTable;
    private DefaultTableModel basketModel;

    private JLabel grossTotalLbl;
    private JLabel discountLbl;
    private JLabel finalTotalLbl;

    private JTextField merchantIdField;
    private JSpinner discountSpinner;
    private JSpinner qtySpinner;

    public OrderSubmissionFrame() {
        setTitle("New Order");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1200, 750);
        setLocationRelativeTo(null);

        loadCatalogueItems();
        buildUI();
        setVisible(true);
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);

        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildBody(), BorderLayout.CENTER);
        root.add(buildFooter(), BorderLayout.SOUTH);

        setContentPane(root);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(DARK_NAVY);
        header.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        JLabel title = new JLabel("New Order Submission");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.WEST);

        return header;
    }

    private JPanel buildBody() {
        JPanel body = new JPanel(new GridLayout(1, 2, 16, 0));
        body.setBackground(BG);
        body.setBorder(BorderFactory.createEmptyBorder(16, 16, 8, 16));
        body.add(buildCataloguePanel());
        body.add(buildBasketPanel());
        return body;
    }

    private JPanel buildCataloguePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel title = new JLabel("Catalogue  (double-click or select + Add to Order)");
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        title.setForeground(new Color(17, 24, 39));

        String[] cols = {"Item ID", "Description", "Price (£)", "In Stock"};
        catalogueModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
            public Class<?> getColumnClass(int c) {
                if (c == 2) return Double.class;
                if (c == 3) return Integer.class;
                return String.class;
            }
        };
        for (CatalogueItem item : catalogueItems) {
            catalogueModel.addRow(new Object[]{
                    item.getItemId(),
                    item.getDescription(),
                    item.getPackageCost(),
                    item.getAvailabilityPacks()
            });
        }

        catalogueTable = new JTable(catalogueModel);
        styleTable(catalogueTable);
        catalogueTable.getColumnModel().getColumn(2).setCellRenderer(priceCellRenderer());

        catalogueTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) addSelectedItemToBasket();
            }
        });

        JScrollPane scroll = new JScrollPane(catalogueTable);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        JPanel addRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        addRow.setBackground(Color.WHITE);
        addRow.add(new JLabel("Qty:"));
        qtySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 9999, 1));
        qtySpinner.setPreferredSize(new Dimension(70, 28));
        addRow.add(qtySpinner);

        JButton addBtn = styledButton("Add to Order", ACCENT);
        addBtn.addActionListener(e -> addSelectedItemToBasket());
        addRow.add(addBtn);

        panel.add(title, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        panel.add(addRow, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildBasketPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel title = new JLabel("Order Basket");
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        title.setForeground(new Color(17, 24, 39));

        String[] cols = {"Item ID", "Description", "Qty", "Unit Price (£)", "Line Total (£)"};
        basketModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
            public Class<?> getColumnClass(int c) {
                if (c == 2) return Integer.class;
                if (c == 3 || c == 4) return Double.class;
                return String.class;
            }
        };

        basketTable = new JTable(basketModel);
        styleTable(basketTable);
        basketTable.getColumnModel().getColumn(3).setCellRenderer(priceCellRenderer());
        basketTable.getColumnModel().getColumn(4).setCellRenderer(priceCellRenderer());

        JScrollPane scroll = new JScrollPane(basketTable);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        JPanel removeRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        removeRow.setBackground(Color.WHITE);
        JButton removeBtn = styledButton("Remove Selected", new Color(180, 50, 50));
        removeBtn.addActionListener(e -> removeSelectedFromBasket());
        removeRow.add(removeBtn);

        panel.add(title, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        panel.add(removeRow, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(Color.WHITE);
        footer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(12, 20, 12, 20)
        ));

        JPanel inputs = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        inputs.setBackground(Color.WHITE);
        inputs.add(new JLabel("Merchant ID:"));
        merchantIdField = new JTextField(14);
        merchantIdField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        inputs.add(merchantIdField);

        inputs.add(new JLabel("Discount %:"));
        discountSpinner = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 100.0, 0.5));
        discountSpinner.setPreferredSize(new Dimension(80, 28));
        discountSpinner.addChangeListener(e -> refreshTotals());
        inputs.add(discountSpinner);

        JPanel totals = new JPanel(new GridLayout(3, 2, 4, 2));
        totals.setBackground(Color.WHITE);
        totals.add(label("Gross Total:"));    grossTotalLbl  = valueLabel("£0.00"); totals.add(grossTotalLbl);
        totals.add(label("Discount:"));       discountLbl    = valueLabel("£0.00"); totals.add(discountLbl);
        totals.add(label("Final Total:"));    finalTotalLbl  = valueLabel("£0.00"); totals.add(finalTotalLbl);
        finalTotalLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        finalTotalLbl.setForeground(ACCENT);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setBackground(Color.WHITE);
        JButton submitBtn = styledButton("Submit Order", DARK_NAVY);
        submitBtn.setPreferredSize(new Dimension(140, 36));
        submitBtn.addActionListener(e -> submitOrder());
        actions.add(submitBtn);

        footer.add(inputs, BorderLayout.WEST);
        footer.add(totals, BorderLayout.CENTER);
        footer.add(actions, BorderLayout.EAST);
        return footer;
    }

    private void addSelectedItemToBasket() {
        int row = catalogueTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an item from the catalogue.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String itemId      = (String)  catalogueModel.getValueAt(row, 0);
        String description = (String)  catalogueModel.getValueAt(row, 1);
        double unitPrice   = (Double)  catalogueModel.getValueAt(row, 2);
        int    stock       = (Integer) catalogueModel.getValueAt(row, 3);
        int    qty         = (Integer) qtySpinner.getValue();

        if (qty > stock) {
            JOptionPane.showMessageDialog(this, "Requested quantity exceeds available stock (" + stock + " packs).",
                    "Insufficient Stock", JOptionPane.WARNING_MESSAGE);
            return;
        }

        for (int i = 0; i < basketModel.getRowCount(); i++) {
            if (basketModel.getValueAt(i, 0).equals(itemId)) {
                int existing = (Integer) basketModel.getValueAt(i, 2);
                int newQty   = existing + qty;
                if (newQty > stock) {
                    JOptionPane.showMessageDialog(this, "Total quantity would exceed stock (" + stock + " packs).",
                            "Insufficient Stock", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                basketModel.setValueAt(newQty, i, 2);
                basketModel.setValueAt(newQty * unitPrice, i, 4);
                refreshTotals();
                return;
            }
        }

        basketModel.addRow(new Object[]{itemId, description, qty, unitPrice, qty * unitPrice});
        refreshTotals();
    }

    private void removeSelectedFromBasket() {
        int row = basketTable.getSelectedRow();
        if (row >= 0) {
            basketModel.removeRow(row);
            refreshTotals();
        }
    }

    private void refreshTotals() {
        double gross = 0;
        for (int i = 0; i < basketModel.getRowCount(); i++) {
            gross += (Double) basketModel.getValueAt(i, 4);
        }
        double discountPct    = ((Number) discountSpinner.getValue()).doubleValue();
        double discountAmount = gross * (discountPct / 100.0);
        double finalTotal     = gross - discountAmount;

        grossTotalLbl.setText(String.format("£%.2f", gross));
        discountLbl.setText(String.format("£%.2f (%.1f%%)", discountAmount, discountPct));
        finalTotalLbl.setText(String.format("£%.2f", finalTotal));
    }

    private void submitOrder() {
        String merchantId = merchantIdField.getText().trim();
        if (merchantId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a Merchant ID.", "Missing Input", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (basketModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Please add at least one item to the order.", "Empty Basket", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<OrderItem> items = new ArrayList<>();
        for (int i = 0; i < basketModel.getRowCount(); i++) {
            String itemId    = (String)  basketModel.getValueAt(i, 0);
            int    qty       = (Integer) basketModel.getValueAt(i, 2);
            double unitPrice = (Double)  basketModel.getValueAt(i, 3);
            items.add(new OrderItem(itemId, qty, unitPrice));
        }

        String orderId = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Order order = new Order(orderId, merchantId, LocalDate.now(), items);

        double grossTotal     = order.calculateOrderTotal();
        double discountPct    = ((Number) discountSpinner.getValue()).doubleValue();
        double discountAmount = grossTotal * (discountPct / 100.0);
        double finalTotal     = grossTotal - discountAmount;

        MerchantAccount account = new MerchantAccount(
                merchantId, merchantId, "", "", "",
                Double.MAX_VALUE,
                new FixedDiscountPlan("Custom", discountPct)
        );

        OrderDBConnector    orderDB       = new OrderDBConnector();
        InvoiceDBConnector  invoiceDB     = new InvoiceDBConnector();
        InvoiceService invoiceService = new InvoiceService();

        orderDB.saveOrder(order, grossTotal, discountAmount, finalTotal);

        for (OrderItem item : items) {
            orderDB.reduceStock(item.getItemId(), item.getQuantity());
        }

        Invoice invoice = invoiceService.generateInvoice(order, account);
        invoiceDB.saveInvoice(invoice);

        JOptionPane.showMessageDialog(this,
                "Order placed successfully!\nOrder ID:   " + orderId + "\nInvoice ID: " + invoice.getInvoiceId(),
                "Order Submitted", JOptionPane.INFORMATION_MESSAGE);

        basketModel.setRowCount(0);
        merchantIdField.setText("");
        discountSpinner.setValue(0.0);
        refreshTotals();
    }

    private void loadCatalogueItems() {
        try {
            IPOS.SA.DB.DBConnection db = new IPOS.SA.DB.DBConnection();
            java.sql.Connection conn = db.getConn();
            java.sql.PreparedStatement stmt = conn.prepareStatement(
                    "SELECT item_id, description, package_type, unit, units_in_pack, package_cost, availability_packs, stock_limit_packs FROM Catalogue_Items");
            java.sql.ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                catalogueItems.add(new CatalogueItem(
                        rs.getString("item_id"),
                        rs.getString("description"),
                        rs.getString("package_type"),
                        rs.getString("unit"),
                        rs.getInt("units_in_pack"),
                        rs.getDouble("package_cost"),
                        rs.getInt("availability_packs"),
                        rs.getInt("stock_limit_packs")
                ));
            }
            conn.close();
        } catch (Exception e) {
            loadSampleCatalogueItems();
        }

        if (catalogueItems.isEmpty()) {
            loadSampleCatalogueItems();
        }
    }

    private void loadSampleCatalogueItems() {
        catalogueItems.add(new CatalogueItem("100 00001", "Paracetamol 500mg", "Box",    "Caps",  16,  2.99,  850, 300));
        catalogueItems.add(new CatalogueItem("100 00002", "Aspirin 75mg",      "Box",    "Tabs",  28,  1.89,  620, 500));
        catalogueItems.add(new CatalogueItem("100 00003", "Analgin 500mg",     "Box",    "Tabs",  20,  3.49,  310, 200));
        catalogueItems.add(new CatalogueItem("200 00004", "Iodine Tincture",   "Bottle", "ml",   100,  4.25,  180, 200));
        catalogueItems.add(new CatalogueItem("200 00005", "Rhynol Nasal",      "Bottle", "ml",    10,  5.10,  420, 300));
        catalogueItems.add(new CatalogueItem("100 00006", "Ibuprofen 200mg",   "Box",    "Tabs",  24,  2.49,  700, 400));
        catalogueItems.add(new CatalogueItem("200 00007", "Hydrogen Peroxide", "Bottle", "ml",   200,  3.75,  260, 150));
        catalogueItems.add(new CatalogueItem("300 00008", "Vitamin C 1000mg",  "Box",    "Tabs",  30,  6.99, 1100, 500));
        catalogueItems.add(new CatalogueItem("300 00009", "Vitamin D3 1000IU", "Box",    "Caps",  60,  7.49,  900, 400));
        catalogueItems.add(new CatalogueItem("400 00010", "Omeprazole 20mg",   "Box",    "Caps",  28,  8.99,  340, 200));
        catalogueItems.add(new CatalogueItem("400 00011", "Loratadine 10mg",   "Box",    "Tabs",  30,  4.79,  480, 250));
        catalogueItems.add(new CatalogueItem("400 00012", "Metformin 500mg",   "Box",    "Tabs",  60,  9.99,  220, 150));
        catalogueItems.add(new CatalogueItem("500 00013", "Amoxicillin 500mg", "Box",    "Caps",  21, 14.99,  190, 100));
    }

    private DefaultTableCellRenderer priceCellRenderer() {
        return new DefaultTableCellRenderer() {
            public void setValue(Object value) {
                setText(value == null ? "" : String.format("£%.2f", ((Number) value).doubleValue()));
            }
        };
    }

    private void styleTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(28);
        table.setShowGrid(false);
        table.setSelectionBackground(new Color(207, 226, 255));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(DARK_NAVY);
        table.getTableHeader().setForeground(Color.WHITE);
    }

    private JButton styledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setOpaque(true);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JLabel label(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        return lbl;
    }

    private JLabel valueLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return lbl;
    }
}
