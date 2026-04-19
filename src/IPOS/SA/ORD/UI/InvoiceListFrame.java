package IPOS.SA.ORD.UI;

import IPOS.SA.ORD.Service.InvoiceService;
import IPOS.SA.ORD.Model.Invoice;
import IPOS.SA.UI.AppFrame;
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
/**
 * Screen that displays a searchable, filterable list of invoices.
 * Can operate in two modes:
 * - All invoices — shows every invoice in the system (no merchant filter)
 * - Merchant invoices — shows only invoices for a specific merchant,
 *   set via the selectedMerchant in AppFrame when navigating from MerchantList
 *
 * Data is refreshed via onShow() each time the screen becomes visible.
 */
public class InvoiceListFrame extends BaseFrame implements Refreshable {

    private final InvoiceService invoiceService;
    private String merchantId;
    private JTable invoiceTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField searchField;
    private JComboBox<String> statusFilter;
    private JLabel messageLabel;

    /**
     * Constructor; opens the invoice list showing all invoices.
     * Called from the nav menu.
     *
     * @param fullname the full name of the logged-in user
     * @param role the role of the logged-in user
     * @param router the screen router used for navigation
     */
    public InvoiceListFrame(String fullname, String role, ScreenRouter router) {
        this(fullname, role, null, router);
    }

    /**
     * Constructor; opens the invoice list filtered by a specific merchant.
     * Called from MerchantList when a merchant is selected.
     *
     * @param fullname the full name of the logged-in user
     * @param role the role of the logged-in user
     * @param merchantId the merchant ID to filter by, or null for all invoices
     * @param router the screen router used for navigation
     */
    public InvoiceListFrame(String fullname, String role, String merchantId, ScreenRouter router) {
        super(fullname, role, "Invoice Management", router);
        this.merchantId = merchantId;
        this.invoiceService = new InvoiceService();
        buildContent();
        loadInvoices();
    }
    /**
     * Returns the header title; shows the merchant ID if filtering by merchant.
     *
     * @return the header title string
     */
    @Override
    protected String getHeaderTitle() {
        if (merchantId != null) {
            return "Invoices — " + merchantId;
        } else {
            return "Invoice Management";
        }
    }
    /**
     * Builds and arranges all UI components for this screen.
     * Includes a search bar, status filter, invoice table and action buttons.
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

        // Refresh button also updates overdue days before reloading
        JButton refreshBtn = new JButton("Refresh");
        styleBtn(refreshBtn);
        refreshBtn.addActionListener(e -> {
            invoiceService.updateOverdueDays();
            loadInvoices();
        });

        // All the search buttons and text fields are added to the search panel at the top of the form
        searchPanel.add(searchLbl);
        searchPanel.add(searchField);
        searchPanel.add(filterLbl);
        searchPanel.add(statusFilter);
        searchPanel.add(refreshBtn);
        topBar.add(searchPanel, BorderLayout.WEST);

        // creates a Table setup – cells are not editable
        String[] cols = {
                "Invoice ID", "Order ID", "Merchant", "Invoice Date",
                "Due Date", "Total (£)", "Paid (£)", "Status", "Days Overdue"
        };

        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        // A new table is created to store all of the invoice records from the database
        invoiceTable = new JTable(tableModel);
        invoiceTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        invoiceTable.setRowHeight(30);
        invoiceTable.setShowGrid(false);
        invoiceTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        invoiceTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        invoiceTable.getTableHeader().setBackground(new Color(17, 24, 39));
        invoiceTable.getTableHeader().setForeground(Color.WHITE);
        invoiceTable.getTableHeader().setReorderingAllowed(false);

        // Enable column sorting
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

        // Sroll pane to scroll the table
        JScrollPane scroll = new JScrollPane(invoiceTable);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        // creates a bottom panel with action buttons and status label
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(17, 24, 39));
        bottomPanel.setBorder(new EmptyBorder(10, 12, 10, 12));

        // creates a panel to store all the buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonPanel.setBackground(new Color(17, 24, 39));

        // creates buttons to view and generate invoices
        JButton viewBtn     = new JButton("View Invoice");
        JButton generateBtn = new JButton("Generate Invoice");
        styleBtn(viewBtn);
        styleBtn(generateBtn);

        viewBtn.addActionListener(e     -> openSelectedInvoice());
        generateBtn.addActionListener(e -> generateInvoiceForOrder());

        // Adds the buttons to the button panel
        buttonPanel.add(viewBtn);
        buttonPanel.add(generateBtn);

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
     * Loads invoices from the database into the table.
     * If merchantId is set, loads only that merchant's invoices.
     * Otherwise loads all invoices. Applies the current search text
     * and status filter to the query.
     */
    private void loadInvoices() {
        // clears the table of all records
        tableModel.setRowCount(0);
        try {

            // uses the search filter to identify the records that match the given critieria and add them to a list
            String search = searchField != null ? searchField.getText().trim() : "";
            String status = statusFilter != null
                    ? statusFilter.getSelectedItem().toString() : "All";

            List<Object[]> rows;
            if (merchantId != null) {
                rows = invoiceService.getMerchantInvoices(merchantId, status, search);
            } else {
                rows = invoiceService.getAllInvoices(status, search);
            }

            // each item in the list is iterated and added to the table again effectively making it appear as if the table was filtered
            for (Object[] row : rows) tableModel.addRow(row);

            if (messageLabel != null)
                messageLabel.setText(tableModel.getRowCount() + " invoices loaded");
        } catch (Exception e) {
            if (messageLabel != null)
                messageLabel.setText("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    /**
     * Opens the InvoiceDisplayFrame popup for the selected invoice row.
     * Converts the view row index to the model index to handle sorting correctly.
     */
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
    /**
     * Prompts staff to enter an order ID and generates an invoice for it.
     * Checks that an invoice does not already exist for the order
     * before generating a new one.
     */
    private void generateInvoiceForOrder() {
        String orderId = JOptionPane.showInputDialog(this,
                "Enter Order ID to generate invoice for:",
                "Generate Invoice", JOptionPane.PLAIN_MESSAGE);

        if (orderId == null || orderId.trim().isEmpty()) return;

        try {
            // Checks invoice doesn't already exist
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
     * @param text    the message to display
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
     * the invoice list accordingly. Clears the merchant filter if none selected.
     */
    public void onShow() {
        this.merchantId = AppFrame.getInstance().getSelectedMerchant();
        loadInvoices();
    }
}