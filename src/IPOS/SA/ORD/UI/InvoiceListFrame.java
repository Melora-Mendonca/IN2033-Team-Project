package IPOS.SA.ORD.UI;

import IPOS.SA.ORD.Service.InvoiceService;
import IPOS.SA.ORD.Model.Invoice;
import IPOS.SA.UI.BaseFrame;
import IPOS.SA.UI.Refreshable;
import IPOS.SA.UI.ScreenRouter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;

public class InvoiceListFrame extends BaseFrame implements Refreshable {

    private final InvoiceService invoiceService;
    private final String merchantId;

    private JTable invoiceTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField searchField;
    private JComboBox<String> statusFilter;
    private JLabel messageLabel;

    // Called from nav — no merchant filter
    public InvoiceListFrame(String fullname, String role, ScreenRouter router) {
        this(fullname, role, null, router);
    }

    // Called from MerchantList — filtered by merchant
    public InvoiceListFrame(String fullname, String role, String merchantId, ScreenRouter router) {
        super(fullname, role, "Invoice Management", router);
        this.merchantId     = merchantId;
        this.invoiceService = new InvoiceService();
        buildContent();
        loadInvoices();
    }

    @Override
    protected String getHeaderTitle() {
        if (merchantId != null) {
            return "Invoices — " + merchantId;
        } else {
            return "Invoice Management";
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
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { loadInvoices(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { loadInvoices(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { loadInvoices(); }
        });

        JLabel filterLbl = new JLabel("Status:");
        filterLbl.setForeground(Color.WHITE);
        filterLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        statusFilter = new JComboBox<>(new String[]{
                "All", "unpaid", "partial", "paid", "overdue"
        });
        statusFilter.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusFilter.addActionListener(e -> loadInvoices());

        JButton refreshBtn = new JButton("Refresh");
        styleBtn(refreshBtn);
        refreshBtn.addActionListener(e -> {
            invoiceService.updateOverdueDays();
            loadInvoices();
        });

        searchPanel.add(searchLbl);
        searchPanel.add(searchField);
        searchPanel.add(filterLbl);
        searchPanel.add(statusFilter);
        searchPanel.add(refreshBtn);
        topBar.add(searchPanel, BorderLayout.WEST);

        // ── TABLE ─────────────────────────────────────────────
        String[] cols = {
                "Invoice ID", "Order ID", "Merchant", "Invoice Date",
                "Due Date", "Total (£)", "Paid (£)", "Status", "Days Overdue"
        };

        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        invoiceTable = new JTable(tableModel);
        invoiceTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        invoiceTable.setRowHeight(30);
        invoiceTable.setShowGrid(false);
        invoiceTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        invoiceTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        invoiceTable.getTableHeader().setBackground(new Color(17, 24, 39));
        invoiceTable.getTableHeader().setForeground(Color.WHITE);
        invoiceTable.getTableHeader().setReorderingAllowed(false);

        sorter = new TableRowSorter<>(tableModel);
        invoiceTable.setRowSorter(sorter);

        // Status colour coding
        invoiceTable.getColumnModel().getColumn(7).setCellRenderer(
                new DefaultTableCellRenderer() {
                    public Component getTableCellRendererComponent(JTable t, Object val,
                                                                   boolean sel, boolean foc, int row, int col) {
                        JLabel lbl = new JLabel(val != null ? val.toString() : "", SwingConstants.CENTER);
                        lbl.setOpaque(true);
                        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
                        if (val != null) switch (val.toString()) {
                            case "paid":    lbl.setBackground(new Color(198, 239, 206)); lbl.setForeground(new Color(0, 97, 0));    break;
                            case "partial": lbl.setBackground(new Color(207, 226, 255)); lbl.setForeground(new Color(10, 64, 168)); break;
                            case "overdue": lbl.setBackground(new Color(255, 199, 206)); lbl.setForeground(new Color(156, 0, 6));   break;
                            case "unpaid":  lbl.setBackground(new Color(255, 243, 205)); lbl.setForeground(new Color(133, 100, 4)); break;
                            default:        lbl.setBackground(Color.WHITE);              lbl.setForeground(Color.BLACK);
                        }
                        return lbl;
                    }
                }
        );

        // Double click opens invoice details
        invoiceTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) openSelectedInvoice();
            }
        });

        JScrollPane scroll = new JScrollPane(invoiceTable);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        // ── BOTTOM BUTTONS ────────────────────────────────────
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(17, 24, 39));
        bottomPanel.setBorder(new EmptyBorder(10, 12, 10, 12));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonPanel.setBackground(new Color(17, 24, 39));

        JButton viewBtn     = new JButton("View Invoice");
        JButton generateBtn = new JButton("Generate Invoice");
        styleBtn(viewBtn);
        styleBtn(generateBtn);

        viewBtn.addActionListener(e     -> openSelectedInvoice());
        generateBtn.addActionListener(e -> generateInvoiceForOrder());

        buttonPanel.add(viewBtn);
        buttonPanel.add(generateBtn);

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
    private void loadInvoices() {
        tableModel.setRowCount(0);
        try {
            String search = searchField != null ? searchField.getText().trim() : "";
            String status = statusFilter != null
                    ? statusFilter.getSelectedItem().toString() : "All";

            List<Object[]> rows;
            if (merchantId != null) {
                rows = invoiceService.getMerchantInvoices(merchantId, status, search);
            } else {
                rows = invoiceService.getAllInvoices(status, search);
            }

            for (Object[] row : rows) tableModel.addRow(row);

            if (messageLabel != null)
                messageLabel.setText(tableModel.getRowCount() + " invoices loaded");
        } catch (Exception e) {
            if (messageLabel != null)
                messageLabel.setText("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void openSelectedInvoice() {
        int row = invoiceTable.getSelectedRow();
        if (row < 0) {
            setMsg("Select an invoice to view.", false);
            return;
        }
        int modelRow  = invoiceTable.convertRowIndexToModel(row);
        String invoiceId = tableModel.getValueAt(modelRow, 0).toString();

        try {
            Invoice invoice = invoiceService.getInvoiceById(invoiceId);
            if (invoice != null) {
                new InvoiceDisplayFrame(invoice);
            } else {
                setMsg("Could not load invoice details.", false);
            }
        } catch (Exception e) {
            setMsg("Error: " + e.getMessage(), false);
        }
    }

    private void generateInvoiceForOrder() {
        String orderId = JOptionPane.showInputDialog(this,
                "Enter Order ID to generate invoice for:",
                "Generate Invoice", JOptionPane.PLAIN_MESSAGE);

        if (orderId == null || orderId.trim().isEmpty()) return;

        try {
            // Check invoice doesn't already exist
            List<Object[]> existing = invoiceService.getAllInvoices("All", orderId.trim());
            for (Object[] row : existing) {
                if (row[1].toString().equals(orderId.trim())) {
                    setMsg("Invoice already exists for order " + orderId + ".", false);
                    return;
                }
            }

            invoiceService.generateInvoiceForOrder(orderId.trim());
            loadInvoices();
            setMsg("Invoice generated for order " + orderId + ".", true);

        } catch (Exception e) {
            setMsg("Error: " + e.getMessage(), false);
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
        loadInvoices();
    }
}