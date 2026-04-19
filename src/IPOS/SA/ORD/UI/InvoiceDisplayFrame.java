package IPOS.SA.ORD.UI;

import IPOS.SA.ORD.Model.Invoice;
import IPOS.SA.ORD.Model.OrderItem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.print.*;

// Pop-up window showing full invoice details including line items and payment history
public class InvoiceDisplayFrame extends JFrame {

    private static final Color DARK_NAVY = new Color(14, 37, 48);
    private static final Color ACCENT    = new Color(17, 54, 74);
    private static final Color BG        = new Color(245, 247, 250);

    private final Invoice invoice;

    public InvoiceDisplayFrame(Invoice invoice) {
        this.invoice = invoice;
        setTitle("Invoice — " + invoice.getInvoiceId());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(750, 550);  // reduce height
        setLocationRelativeTo(null);
        buildUI();
        setVisible(true);
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);
        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(new JScrollPane(buildInvoicePanel()), BorderLayout.CENTER);
        root.add(buildFooter(), BorderLayout.SOUTH);
        setContentPane(root);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(DARK_NAVY);
        header.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        JLabel title = new JLabel("Invoice Details");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.WEST);
        return header;
    }

    // The printable invoice panel; returned so it can be sent to the printer too
    private JPanel buildInvoicePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));

        // Title
        JLabel title = new JLabel("IPOS — Invoice");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(DARK_NAVY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(title);
        panel.add(Box.createVerticalStrut(16));

        // Metadata grid
        JPanel meta = new JPanel(new GridLayout(5, 2, 8, 4));
        meta.setBackground(Color.WHITE);
        meta.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        meta.setAlignmentX(Component.LEFT_ALIGNMENT);
        meta.add(boldLabel("Invoice ID:"));    meta.add(plainLabel(invoice.getInvoiceId()));
        meta.add(boldLabel("Order ID:"));      meta.add(plainLabel(invoice.getOrderId()));
        meta.add(boldLabel("Merchant ID:"));   meta.add(plainLabel(invoice.getMerchantId()));
        meta.add(boldLabel("Invoice Date:"));  meta.add(plainLabel(invoice.getInvoiceDate().toString()));
        meta.add(boldLabel("Due Date:"));      meta.add(plainLabel(invoice.getDueDate().toString()));
        panel.add(meta);
        panel.add(Box.createVerticalStrut(20));

        // Divider
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(200, 210, 220));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(sep);
        panel.add(Box.createVerticalStrut(16));

        // Line items table
        String[] cols = {"Item ID", "Description", "Quantity", "Unit Price (£)", "Line Total (£)"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        if (invoice.getItems() != null) {
            for (OrderItem item : invoice.getItems()) {
                model.addRow(new Object[]{
                        item.getItemId(),
                        getItemDescription(item.getItemId()),  // Get description from database
                        item.getQuantity(),
                        String.format("%.2f", item.getUnitPrice()),
                        String.format("%.2f", item.getLineTotal())
                });
            }
        }

        JTable itemsTable = new JTable(model);
        itemsTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        itemsTable.setRowHeight(28);
        itemsTable.setShowGrid(false);
        itemsTable.setEnabled(false);
        itemsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        itemsTable.getTableHeader().setBackground(DARK_NAVY);
        itemsTable.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scroll = new JScrollPane(itemsTable);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(220, 225, 230)));
        scroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        scroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        panel.add(scroll);
        panel.add(Box.createVerticalStrut(20));

        // Totals section
        JSeparator sep2 = new JSeparator();
        sep2.setForeground(new Color(200, 210, 220));
        sep2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep2.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(sep2);
        panel.add(Box.createVerticalStrut(12));

        JPanel totals = new JPanel(new GridLayout(4, 2, 4, 6));
        totals.setBackground(Color.WHITE);
        totals.setMaximumSize(new Dimension(400, 100));
        totals.setAlignmentX(Component.LEFT_ALIGNMENT);
        totals.add(boldLabel("Total Amount:"));
        totals.add(plainLabel(String.format("£%.2f", invoice.getTotalAmount())));
        totals.add(boldLabel("Amount Paid:"));
        totals.add(plainLabel(String.format("£%.2f", invoice.getAmountPaid())));
        totals.add(boldLabel("Outstanding Balance:"));
        totals.add(plainLabel(String.format("£%.2f", invoice.getOutstandingBalance())));

        JLabel statusKey = new JLabel("Status:");
        statusKey.setFont(new Font("Segoe UI", Font.BOLD, 14));
        statusKey.setForeground(DARK_NAVY);
        JLabel statusVal = new JLabel(invoice.getStatus().toUpperCase());
        statusVal.setFont(new Font("Segoe UI", Font.BOLD, 14));

        // Color code the status
        switch (invoice.getStatus()) {
            case "paid":
                statusVal.setForeground(new Color(0, 120, 0));
                break;
            case "overdue":
                statusVal.setForeground(new Color(200, 50, 50));
                break;
            case "partial":
                statusVal.setForeground(new Color(200, 120, 0));
                break;
            default:
                statusVal.setForeground(DARK_NAVY);
        }

        totals.add(statusKey);
        totals.add(statusVal);

        panel.add(totals);

        // Add overdue days warning if applicable
        if (invoice.isOverdue()) {
            JPanel warningPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            warningPanel.setBackground(Color.WHITE);
            JLabel warning = new JLabel("⚠️ This invoice is " + invoice.getDaysOverdue() + " days overdue.");
            warning.setFont(new Font("Segoe UI", Font.BOLD, 12));
            warning.setForeground(new Color(200, 50, 50));
            warningPanel.add(warning);
            panel.add(warningPanel);
        }

        return panel;
    }

    private String getItemDescription(String itemId) {
        try {
            IPOS.SA.DB.DBConnection db = new IPOS.SA.DB.DBConnection();
            java.sql.ResultSet rs = db.query("SELECT description FROM catalogue WHERE item_id = ?", itemId);
            if (rs.next()) {
                return rs.getString("description");
            }
        } catch (Exception e) {
            // Return item ID if description not found
        }
        return itemId;
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 8));
        footer.setBackground(Color.WHITE);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)));

        JButton printBtn = new JButton("Print Invoice");
        printBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        printBtn.setBackground(DARK_NAVY);
        printBtn.setForeground(Color.WHITE);
        printBtn.setFocusPainted(false);
        printBtn.setBorderPainted(false);
        printBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        printBtn.addActionListener(e -> printInvoice());

        JButton closeBtn = new JButton("Close");
        closeBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        closeBtn.setBackground(new Color(107, 114, 128));
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setFocusPainted(false);
        closeBtn.setBorderPainted(false);
        closeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        closeBtn.addActionListener(e -> dispose());
        footer.add(closeBtn);
        footer.add(printBtn);
        return footer;
    }

    private void printInvoice() {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setJobName("Invoice — " + invoice.getInvoiceId());

        job.setPrintable((graphics, pageFormat, pageIndex) -> {
            if (pageIndex > 0) return Printable.NO_SUCH_PAGE;

            Graphics2D g2d = (Graphics2D) graphics;
            g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

            // Scale to fit the page width
            double pageWidth  = pageFormat.getImageableWidth();
            double panelWidth = 700;
            double scale = pageWidth / panelWidth;
            g2d.scale(scale, scale);

            // Build a fresh panel sized for printing
            JPanel printPanel = buildPrintPanel();
            printPanel.setSize(700, 800);
            printPanel.doLayout();
            printPanel.print(g2d);

            return Printable.PAGE_EXISTS;
        });

        boolean doPrint = job.printDialog();
        if (doPrint) {
            try {
                job.print();
            } catch (PrinterException e) {
                JOptionPane.showMessageDialog(this, "Printing failed: " + e.getMessage(), "Print Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Builds a self-contained panel for the printer (no scroll panes)
    private JPanel buildPrintPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("IPOS — Invoice");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        panel.add(title);
        panel.add(Box.createVerticalStrut(10));

        panel.add(printRow("Invoice ID:", invoice.getInvoiceId()));
        panel.add(printRow("Order ID:",   invoice.getOrderId()));
        panel.add(printRow("Merchant ID:", invoice.getMerchantId()));
        panel.add(printRow("Invoice Date:", invoice.getInvoiceDate().toString()));
        panel.add(printRow("Due Date:",    invoice.getDueDate().toString()));
        panel.add(Box.createVerticalStrut(10));

        if (invoice.getItems() != null) {
            for (OrderItem item : invoice.getItems()) {
                panel.add(printRow(item.getItemId(),
                        getItemDescription(item.getItemId()) + " — Qty: " + item.getQuantity() +
                                "  Unit: £" + String.format("%.2f", item.getUnitPrice()) +
                                "  Total: £" + String.format("%.2f", item.getLineTotal())));
            }
        }
        panel.add(Box.createVerticalStrut(10));
        panel.add(printRow("Total Amount:", String.format("£%.2f", invoice.getTotalAmount())));
        panel.add(printRow("Amount Paid:",  String.format("£%.2f", invoice.getAmountPaid())));
        panel.add(printRow("Outstanding:",  String.format("£%.2f", invoice.getOutstandingBalance())));
        panel.add(printRow("Status:",       invoice.getStatus().toUpperCase()));

        if (invoice.isOverdue()) {
            panel.add(printRow("", "OVERDUE — " + invoice.getDaysOverdue() + " days overdue"));
        }

        return panel;
    }

    private JPanel printRow(String key, String value) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        row.setBackground(Color.WHITE);
        JLabel k = new JLabel(key);
        k.setFont(new Font("Segoe UI", Font.BOLD, 11));
        JLabel v = new JLabel(value);
        v.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        row.add(k);
        row.add(v);
        return row;
    }

    private JLabel boldLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        return lbl;
    }

    private JLabel plainLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        return lbl;
    }
}
