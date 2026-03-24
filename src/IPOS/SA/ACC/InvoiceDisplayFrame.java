package IPOS.SA.ACC;

import IPOS.SA.ORD.OrderItem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.print.*;

public class InvoiceDisplayFrame extends JFrame {

    private static final Color DARK_NAVY = new Color(14, 37, 48);
    private static final Color ACCENT    = new Color(17, 54, 74);
    private static final Color BG        = new Color(245, 247, 250);

    private final Invoice invoice;

    public InvoiceDisplayFrame(Invoice invoice) {
        this.invoice = invoice;
        setTitle("Invoice — " + invoice.getInvoiceId());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(750, 600);
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

    // The printable invoice panel — returned so it can be sent to the printer too
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
        JPanel meta = new JPanel(new GridLayout(4, 2, 8, 4));
        meta.setBackground(Color.WHITE);
        meta.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        meta.setAlignmentX(Component.LEFT_ALIGNMENT);
        meta.add(boldLabel("Invoice ID:"));   meta.add(plainLabel(invoice.getInvoiceId()));
        meta.add(boldLabel("Order ID:"));     meta.add(plainLabel(invoice.getOrderId()));
        meta.add(boldLabel("Merchant ID:"));  meta.add(plainLabel(invoice.getMerchantId()));
        meta.add(boldLabel("Date Issued:"));  meta.add(plainLabel(invoice.getIssueDate().toString()));
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
        String[] cols = {"Item ID", "Quantity", "Unit Price (£)", "Line Total (£)"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        for (OrderItem item : invoice.getItems()) {
            model.addRow(new Object[]{
                    item.getItemId(),
                    item.getQuantity(),
                    String.format("%.2f", item.getUnitPrice()),
                    String.format("%.2f", item.getLineTotal())
            });
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

        JPanel totals = new JPanel(new GridLayout(3, 2, 4, 6));
        totals.setBackground(Color.WHITE);
        totals.setMaximumSize(new Dimension(400, 80));
        totals.setAlignmentX(Component.LEFT_ALIGNMENT);
        totals.add(boldLabel("Gross Total:"));
        totals.add(plainLabel(String.format("£%.2f", invoice.getGrossTotal())));
        totals.add(boldLabel("Discount Applied:"));
        totals.add(plainLabel(String.format("£%.2f", invoice.getDiscountAmount())));

        JLabel finalKey = new JLabel("Final Total:");
        finalKey.setFont(new Font("Segoe UI", Font.BOLD, 15));
        finalKey.setForeground(DARK_NAVY);
        JLabel finalVal = new JLabel(String.format("£%.2f", invoice.getFinalTotal()));
        finalVal.setFont(new Font("Segoe UI", Font.BOLD, 15));
        finalVal.setForeground(ACCENT);
        totals.add(finalKey);
        totals.add(finalVal);
        panel.add(totals);

        return panel;
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
            double scale      = pageWidth / panelWidth;
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
        panel.add(printRow("Merchant:",   invoice.getMerchantId()));
        panel.add(printRow("Date:",       invoice.getIssueDate().toString()));
        panel.add(Box.createVerticalStrut(10));

        for (OrderItem item : invoice.getItems()) {
            panel.add(printRow(item.getItemId(),
                    "Qty: " + item.getQuantity() +
                    "  Unit: £" + String.format("%.2f", item.getUnitPrice()) +
                    "  Total: £" + String.format("%.2f", item.getLineTotal())));
        }
        panel.add(Box.createVerticalStrut(10));
        panel.add(printRow("Gross Total:", String.format("£%.2f", invoice.getGrossTotal())));
        panel.add(printRow("Discount:",    String.format("£%.2f", invoice.getDiscountAmount())));
        panel.add(printRow("Final Total:", String.format("£%.2f", invoice.getFinalTotal())));
        return panel;
    }

    private JPanel printRow(String key, String value) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        row.setBackground(Color.WHITE);
        JLabel k = new JLabel(key); k.setFont(new Font("Segoe UI", Font.BOLD, 11));
        JLabel v = new JLabel(value); v.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        row.add(k); row.add(v);
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
