package IPOS.SA.ORD.UI;

import IPOS.SA.Comms.PUClient.CommsClient;
import IPOS.SA.ORD.Service.PaymentService;
import IPOS.SA.DB.InvoiceDBConnector;
import IPOS.SA.UI.BaseFrame;
import IPOS.SA.UI.Refreshable;
import IPOS.SA.UI.ScreenRouter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.awt.Dialog;
/**
 * Payment Recording screen for IPOS-SA.
 * Allows accounting staff to view all invoices, record payments against them
 * and view detailed invoice and payment history.
 *
 */
public class PaymentRecording extends BaseFrame implements Refreshable {
    private JTable invoiceTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> statusFilter;
    private JLabel messageLabel;
    private final PaymentService paymentService = new PaymentService();
    private final InvoiceDBConnector invoiceDB  = new InvoiceDBConnector();

    /**
     * Constructor; builds the payment recording screen and loads all invoices.
     *
     * @param fullname the full name of the logged-in user
     * @param role the role of the logged-in user
     * @param router the screen router used for navigation
     */
    public PaymentRecording(String fullname, String role, ScreenRouter router) {
        super(fullname, role, "Payment Recording", router);
        buildContent();
        loadInvoices();
    }
    /**
     * Returns the title displayed in the page header.
     *
     * @return the header title string
     */
    @Override
    protected String getHeaderTitle() {
        return "Payment Recording";
    }
    /**
     * Builds and arranges all UI components for this screen.
     * Includes a search bar, status filter, invoice table and action buttons.
     * The View Debtors button is only shown for Senior Accountant,
     * Administrator and Director of Operations.
     */
    private void buildContent() {
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

        // Refresh button also updates overdue days, and loads all the invoices before reloading
        JButton refreshBtn = new JButton("Refresh");
        styleActionButton(refreshBtn);
        refreshBtn.addActionListener(e -> {
            invoiceDB.updateOverdueDays();
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
        String[] cols = {"Invoice ID", "Order ID", "Merchant", "Invoice Date",
                "Due Date", "Total (£)", "Paid (£)", "Status", "Days Overdue"};
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

        // creates buttons to view invoice details, record payments and  view debtors
        JButton recordPaymentBtn = new JButton("Record Payment");
        JButton viewDetailsBtn   = new JButton("View Invoice Details");
        JButton debtorsBtn       = new JButton("View Debtors");

        styleActionButton(recordPaymentBtn);
        styleActionButton(viewDetailsBtn);
        styleActionButton(debtorsBtn);

        recordPaymentBtn.addActionListener(e -> showRecordPaymentDialog());
        viewDetailsBtn.addActionListener(e   -> viewInvoiceDetails());
        debtorsBtn.addActionListener(e       -> showDebtorsDialog());

        // Adds the buttons to the button panel
        buttonPanel.add(recordPaymentBtn);
        buttonPanel.add(viewDetailsBtn);

        // Only senior accountant, admin and director can view debtors
        if (role.equals("Senior Accountant") ||
                role.equals("Administrator") ||
                role.equals("Director of Operations")) {
            buttonPanel.add(debtorsBtn);
        }

        // Adds a message label to notify user of any errors
        messageLabel = new JLabel(" ");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        messageLabel.setForeground(Color.WHITE);

        bottomPanel.add(buttonPanel,  BorderLayout.WEST);
        bottomPanel.add(messageLabel, BorderLayout.EAST);

      // Adds to center panel from base frame
        CenterPanel.setLayout(new BorderLayout());
        CenterPanel.add(topBar,      BorderLayout.NORTH);
        CenterPanel.add(scroll,      BorderLayout.CENTER);
        CenterPanel.add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * Loads invoices from the database into the table.
     * Applies the current search text and status filter.
     */
    private void loadInvoices() {
        tableModel.setRowCount(0); // Clear existing rows before reloading
        try {
            // Read current search text and status filter — default to empty/All if not yet initialised
            String search = searchField != null ? searchField.getText().trim() : "";
            String status = statusFilter != null ? statusFilter.getSelectedItem().toString() : "All";

            List<Object[]> rows = paymentService.getAllInvoices(status, search);

            if (rows != null) {
                for (Object[] row : rows) {
                    tableModel.addRow(row);
                    // DEBUG: Print first row
                    if (tableModel.getRowCount() == 1) {
                        System.out.println("First row data: ");
                        for (Object obj : row) {
                            System.out.print(obj + " | ");
                        }
                        System.out.println();
                    }
                }
            }

            // Read current search text and status filter — default to empty/All if not yet initialised
            if (messageLabel != null)
                messageLabel.setText(tableModel.getRowCount() + " invoices loaded");

            // If no rows, show message
            if (tableModel.getRowCount() == 0) {
                System.out.println("WARNING: No invoices found in database!");
            }

        } catch (Exception e) {
            e.printStackTrace();
            if (messageLabel != null)
                messageLabel.setText("Error: " + e.getMessage());
        }
    }
    /**
     * Opens the Record Payment dialog for the selected invoice.
     * Shows invoice summary and input fields for amount, payment method
     * and reference number. For card payments, additional cardholder
     * fields are shown and the CommsClient payment API is called first.
     * Prevents recording payment against a fully paid invoice.
     */
    private void showRecordPaymentDialog() {
        int row = invoiceTable.getSelectedRow();
        if (row == -1) { setMessage("Select an invoice to record a payment.", false); return; }

        // Read selected invoice details from the table model
        String invoiceId = tableModel.getValueAt(row, 0).toString();
        String orderId   = tableModel.getValueAt(row, 1).toString();
        String merchant  = tableModel.getValueAt(row, 2).toString();
        double total     = Double.parseDouble(tableModel.getValueAt(row, 5).toString());
        double paid      = Double.parseDouble(tableModel.getValueAt(row, 6).toString());
        double remaining = total - paid; // How much is still owed on this invoice

        // Prevent recording a payment against an invoice that is already fully settled
        if (remaining <= 0) {
            setMessage("This invoice is already fully paid.", false);
            return;
        }

        // Prevent recording a payment against an invoice that is already fully settled
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Record Payment — " + invoiceId, Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(460, 420);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        // Fixed invoice summary fields shown at the top of the dialog
        JPanel fixedForm = new JPanel(new GridLayout(5, 2, 8, 8));
        fixedForm.setBorder(BorderFactory.createEmptyBorder(16, 16, 4, 16));
        fixedForm.setBackground(new Color(245, 247, 250));

        JTextField amountField    = new JTextField();
        JTextField referenceField = new JTextField();
        JComboBox<String> methodCombo = new JComboBox<>(new String[]{
                "bank_transfer", "cheque", "cash", "card"
        });

        // Reference label text changes dynamically based on the selected payment method
        JLabel referenceLabel = fieldLabel("Reference No:");

        fixedForm.add(fieldLabel("Invoice ID:"));     fixedForm.add(new JLabel(invoiceId));
        fixedForm.add(fieldLabel("Merchant:"));       fixedForm.add(new JLabel(merchant));
        fixedForm.add(fieldLabel("Remaining (£):"));  fixedForm.add(new JLabel(String.format("%.2f", remaining)));
        fixedForm.add(fieldLabel("Amount (£):"));     fixedForm.add(amountField);
        fixedForm.add(fieldLabel("Payment Method:")); fixedForm.add(methodCombo);

        // Card-only fields panel (shown/hidden dynamically)
        JPanel cardPanel = new JPanel(new GridLayout(5, 2, 8, 8));
        cardPanel.setBorder(BorderFactory.createEmptyBorder(0, 16, 4, 16));
        cardPanel.setBackground(new Color(245, 247, 250));
        cardPanel.setVisible(false); // Hidden by default

        JTextField fullNameField  = new JTextField();
        JTextField addressField  = new JTextField();
        JTextField cardFirstField  = new JTextField(4);
        JTextField cardLastField  = new JTextField(4);

        cardPanel.add(fieldLabel("Cardholder Name:")); cardPanel.add(fullNameField);
        cardPanel.add(fieldLabel("Billing Address:")); cardPanel.add(addressField);
        cardPanel.add(fieldLabel("Card First 4:"));    cardPanel.add(cardFirstField);
        cardPanel.add(fieldLabel("Card Last 4:"));     cardPanel.add(cardLastField);
        cardPanel.add(new JLabel(""));                 cardPanel.add(new JLabel(""));

        // Reference number row sits below the card feilds
        JPanel refPanel = new JPanel(new GridLayout(1, 2, 8, 8));
        refPanel.setBorder(BorderFactory.createEmptyBorder(0, 16, 8, 16));
        refPanel.setBackground(new Color(245, 247, 250));
        refPanel.add(referenceLabel);
        refPanel.add(referenceField);

        // Reference label text changes dynamically based on the selected payment method
        methodCombo.addActionListener(e -> {
            String method = methodCombo.getSelectedItem().toString();

            // Show card fields only for card payments
            cardPanel.setVisible(method.equals("card"));

            // Resize dialog to fit/hide the card panel
            dialog.pack();
            dialog.setLocationRelativeTo(this);

            // Update reference label to match the selected payment method
            if (method.equals("cheque")) {
                referenceLabel.setText("Cheque Number:");
            } else if (method.equals("bank_transfer")) {
                referenceLabel.setText("Bank Reference:");
            } else if (method.equals("card")) {
                referenceLabel.setText("Transaction ID:");
            } else {
                referenceLabel.setText("Receipt Number:");
            }
        });

        JButton saveBtn = new JButton("Record Payment");
        styleActionButton(saveBtn);

        saveBtn.addActionListener(e -> {
            try {
                double amount    = Double.parseDouble(amountField.getText().trim());
                String method    = methodCombo.getSelectedItem().toString();
                String reference = referenceField.getText().trim();

                // For card payments, calls the comms payment API first
                if (method.equals("card")) {
                    String cardFirst = cardFirstField.getText().trim();
                    String cardLast  = cardLastField.getText().trim();
                    String name      = fullNameField.getText().trim();
                    String address   = addressField.getText().trim();

                    // validate card digits are exactly 4 characters
                    if (cardFirst.length() != 4 || cardLast.length() != 4) {
                        JOptionPane.showMessageDialog(dialog, "Card digits must be exactly 4 characters each.");
                        return;
                    }
                    if (name.isEmpty() || address.isEmpty()) {
                        JOptionPane.showMessageDialog(dialog, "Cardholder name and billing address are required.");
                        return;
                    }

                    // Looks up merchant ID for this invoice
                    String[] ids = paymentService.getMerchantAndOrderForInvoice(invoiceId);
                    String merchantId = ids[0];

                    try {
                        // Attempt to process card payment via the IPOS-PU CommsClient API
                        CommsClient.processPayment(merchantId, orderId, name, address,
                                cardFirst, cardLast, amount);
                    } catch (Exception commsEx) {
                        int confirm = JOptionPane.showConfirmDialog(dialog,
                                "Card payment API unavailable: " + commsEx.getMessage()
                                + "\n\nRecord payment locally anyway?",
                                "Comms Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                        if (confirm != JOptionPane.YES_OPTION) return;
                    }
                }

                // Record the payment in the database and update invoice status
                paymentService.recordPayment(invoiceId, amount, method, reference);

                dialog.dispose();
                loadInvoices();
                setMessage("Payment of £" + String.format("%.2f", amount) + " recorded successfully.", true);

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter a valid amount.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
            }
        });

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setBackground(new Color(245, 247, 250));
        bottom.add(saveBtn);

        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(new Color(245, 247, 250));
        body.add(fixedForm);
        body.add(cardPanel);
        body.add(refPanel);

        dialog.add(body,   BorderLayout.CENTER);
        dialog.add(bottom, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    /**
     * Opens a popup showing full invoice details for the selected invoice.
     * Displays invoice metadata, order line items, payment totals
     * and complete payment history. Includes a print button.
     */
    private void viewInvoiceDetails() {
        int row = invoiceTable.getSelectedRow();
        if (row == -1) { setMessage("Select an invoice to view.", false); return; }

        String invoiceId = tableModel.getValueAt(row, 0).toString();
        String orderId   = tableModel.getValueAt(row, 1).toString();

        try {
            // Build a formatted text summary of the invoice details
            StringBuilder sb = new StringBuilder();
            sb.append("INVOICE DETAILS\n");
            sb.append("=".repeat(55)).append("\n");
            sb.append("Invoice ID:   ").append(tableModel.getValueAt(row, 0)).append("\n");
            sb.append("Order ID:     ").append(tableModel.getValueAt(row, 1)).append("\n");
            sb.append("Merchant:     ").append(tableModel.getValueAt(row, 2)).append("\n");
            sb.append("Invoice Date: ").append(tableModel.getValueAt(row, 3)).append("\n");
            sb.append("Due Date:     ").append(tableModel.getValueAt(row, 4)).append("\n");
            sb.append("-".repeat(55)).append("\n");
            sb.append("ITEMS:\n");

            // Load and format each order item
            List<Object[]> items = paymentService.getOrderItems(orderId);
            for (Object[] item : items) {
                sb.append(String.format("  %-30s %3s x £%8s = £%s\n",
                        item[0], item[1], item[2], item[3]));
            }

            sb.append("-".repeat(55)).append("\n");
            sb.append(String.format("Total:        £%s\n", tableModel.getValueAt(row, 5)));
            sb.append(String.format("Amount Paid:  £%s\n", tableModel.getValueAt(row, 6)));
            sb.append(String.format("Status:       %s\n",  tableModel.getValueAt(row, 7)));
            sb.append(String.format("Days Overdue: %s\n",  tableModel.getValueAt(row, 8)));
            sb.append("-".repeat(55)).append("\n");
            sb.append("PAYMENT HISTORY:\n");

            // Build a formatted text summary of the invoice details
            List<Object[]> payments = paymentService.getPaymentHistory(invoiceId);
            if (payments.isEmpty()) {
                sb.append("  No payments recorded.\n");
            } else {
                for (Object[] payment : payments) {
                    sb.append(String.format("  %s  £%s  %s  %s\n",
                            payment[0], payment[1], payment[2], payment[3]));
                }
            }

            JTextArea textArea = new JTextArea(sb.toString());
            textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            textArea.setEditable(false);
            textArea.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

            JScrollPane scroll = new JScrollPane(textArea);
            scroll.setPreferredSize(new Dimension(500, 420));

            JButton printBtn = new JButton("Print Invoice");
            styleActionButton(printBtn);

            // Use the JTextArea built-in print method to send to the system printer
            printBtn.addActionListener(ev -> {
                try { textArea.print(); }
                catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Print error: " + ex.getMessage());
                }
            });

            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            btnPanel.add(printBtn);

            JPanel panel = new JPanel(new BorderLayout());
            panel.add(scroll,   BorderLayout.CENTER);
            panel.add(btnPanel, BorderLayout.SOUTH);

            JOptionPane.showMessageDialog(this, panel,
                    "Invoice — " + invoiceId, JOptionPane.PLAIN_MESSAGE);

        } catch (Exception e) {
            setMessage("Error: " + e.getMessage(), false);
        }
    }
    /**
     * Opens a popup showing full invoice details for the selected invoice.
     * Displays invoice metadata, order line items, payment totals
     * and complete payment history. Includes a print button.
     */
    public void showPaymentHistoryDialog() {
        // Prompt staff to enter an invoice ID manually
        String invoiceId = JOptionPane.showInputDialog(this,
                "Enter Invoice ID to view payment history:",
                "Payment History",
                JOptionPane.QUESTION_MESSAGE);

        // Do nothing if the dialog was cancelled or left empty
        if (invoiceId == null || invoiceId.trim().isEmpty()) {
            return;
        }

        try {
            List<Object[]> payments = paymentService.getPaymentHistory(invoiceId.trim());

            // Show a message if no payments have been recorded for this invoice
            if (payments.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No payment history found for Invoice: " + invoiceId,
                        "No Records",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Build a table to display the payment history records
            String[] cols = {"Payment Date", "Amount (£)", "Payment Method", "Reference Number"};
            DefaultTableModel model = new DefaultTableModel(cols, 0) {
                public boolean isCellEditable(int r, int c) { return false; }
            };
            for (Object[] payment : payments) {
                model.addRow(payment);
            }

            JTable table = new JTable(model);
            table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            table.setRowHeight(25);
            table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
            table.getTableHeader().setBackground(new Color(17, 24, 39));
            table.getTableHeader().setForeground(Color.WHITE);

            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setPreferredSize(new Dimension(600, 300));

            JOptionPane.showMessageDialog(this, scrollPane,
                    "Payment History - Invoice: " + invoiceId,
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading payment history: " + e.getMessage());
        }
    }
    /**
     * Opens a dialog prompting for an invoice ID and shows its full payment history.
     * Accessible from the nav sidebar for Senior Accountant and above.
     * Shows a message if no payment history exists for the entered invoice.
     */
    public void showDebtorsDialog() {
        try {
            List<Object[]> debtors = paymentService.getDebtors();

            // Build a table to display merchants with outstanding balance
            String[] cols = {"Merchant ID", "Company", "Email", "Outstanding (£)", "Days Overdue", "Status"};
            DefaultTableModel debtorModel = new DefaultTableModel(cols, 0) {
                public boolean isCellEditable(int r, int c) { return false; }
            };
            for (Object[] debtor : debtors) debtorModel.addRow(debtor);

            JTable debtorTable = new JTable(debtorModel);
            debtorTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            debtorTable.setRowHeight(28);
            debtorTable.setShowGrid(false);
            debtorTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
            debtorTable.getTableHeader().setBackground(new Color(17, 24, 39));
            debtorTable.getTableHeader().setForeground(Color.WHITE);

            // colour code the days oversue column based on the days its been over due for
            debtorTable.getColumnModel().getColumn(4).setCellRenderer(
                    new DefaultTableCellRenderer() {
                        public Component getTableCellRendererComponent(JTable t, Object val,
                                                                       boolean sel, boolean foc, int row, int col) {
                            JLabel lbl = new JLabel(val != null ? val.toString() : "", SwingConstants.CENTER);
                            lbl.setOpaque(true);
                            lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
                            int days = val != null ? Integer.parseInt(val.toString()) : 0;
                            if      (days >= 30) { lbl.setBackground(new Color(255, 199, 206)); lbl.setForeground(new Color(156, 0, 6)); }
                            else if (days >= 15) { lbl.setBackground(new Color(255, 243, 205)); lbl.setForeground(new Color(133, 100, 4)); }
                            else                 { lbl.setBackground(Color.WHITE);              lbl.setForeground(Color.BLACK); }
                            return lbl;
                        }
                    }
            );

            JScrollPane scroll = new JScrollPane(debtorTable);
            scroll.setPreferredSize(new Dimension(700, 300));

            // Red for 30+ days (in default), yellow for 15+ days (suspended)
            JLabel legend = new JLabel(
                    "  \uD83D\uDD34 30+ days overdue (in default)   \uD83D\uDFE1 15+ days (suspended)   \u26AA Active debt");
            legend.setFont(new Font("Segoe UI", Font.PLAIN, 11));

            JButton reminderBtn = new JButton("Send Email Reminder to Selected");
            styleActionButton(reminderBtn);
            reminderBtn.addActionListener(ev -> {
                int sel = debtorTable.getSelectedRow();
                if (sel < 0) {
                    JOptionPane.showMessageDialog(null, "Select a merchant first.");
                    return;
                }

                // read te selected merchant's details from the table
                String companyName  = debtorModel.getValueAt(sel, 1).toString();
                String email        = debtorModel.getValueAt(sel, 2).toString();
                String outstanding  = debtorModel.getValueAt(sel, 3).toString();
                String daysOverdue  = debtorModel.getValueAt(sel, 4).toString();

                // Build the reminder email body
                String subject = "Payment Reminder — Outstanding Balance";
                String body    = "Dear " + companyName + ",\n\n"
                        + "This is a reminder that your account has an outstanding balance of £" + outstanding
                        + ", which is now " + daysOverdue + " day(s) overdue.\n\n"
                        + "Please contact us at your earliest convenience to arrange settlement.\n\n"
                        + "Kind regards,\nInfoPharma Ordering System";

                try {
                    // send the email via the IPOS_PU email service
                    CommsClient.sendEmail(email, subject, body);
                    JOptionPane.showMessageDialog(null,
                            "Reminder sent to " + email, "Sent", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception commsEx) {
                    JOptionPane.showMessageDialog(null,
                            "Could not send reminder: " + commsEx.getMessage(),
                            "Comms Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            btnRow.add(reminderBtn);

            JPanel panel = new JPanel(new BorderLayout(0, 8));
            panel.add(legend,  BorderLayout.NORTH);
            panel.add(scroll,  BorderLayout.CENTER);
            panel.add(btnRow,  BorderLayout.SOUTH);

            JOptionPane.showMessageDialog(this, panel,
                    "Debtors — " + debtors.size() + " merchant(s) with outstanding balances",
                    JOptionPane.WARNING_MESSAGE);

        } catch (Exception e) {
            setMessage("Error loading debtors: " + e.getMessage(), false);
        }
    }

    /**
     * Applies a consistent visual style to an action button.
     *
     * @param btn the button to style
     */
    private void styleActionButton(JButton btn) {
        btn.setBackground(new Color(30, 70, 90));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    }
    /**
     * Creates a bold field label for use in dialog forms.
     *
     * @param text the label text
     * @return the styled label
     */
    private JLabel fieldLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setForeground(new Color(55, 65, 81));
        return lbl;
    }
    /**
     * Displays a success or error message in the status bar.
     *
     * @param text    the message to display
     * @param success true for a green success message, false for a red error message
     */
    private void setMessage(String text, boolean success) {
        messageLabel.setText(text);
        messageLabel.setForeground(success ? new Color(0, 200, 100) : new Color(255, 100, 100));
    }
    /**
     * Called by the screen router when this screen becomes visible.
     * Reloads all invoices so the list is always current.
     */
    @Override
    public void onShow() {
        loadInvoices();
    }
}