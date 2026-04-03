package IPOS.SA.ORD.UI;

import IPOS.SA.ORD.Model.Invoice;
import IPOS.SA.DB.InvoiceDBConnector;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;

public class InvoiceListFrame extends JFrame {

    private static final Color DARK_NAVY = new Color(14, 37, 48);
    private static final Color ACCENT    = new Color(17, 54, 74);
    private static final Color BG        = new Color(245, 247, 250);

    private JTable invoiceTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField searchField;

    private final InvoiceDBConnector invoiceDB = new InvoiceDBConnector();

    public InvoiceListFrame() {
        setTitle("Invoices");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);
        buildUI();
        loadInvoices();
        setVisible(true);
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);

        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildTablePanel(), BorderLayout.CENTER);
        root.add(buildFooter(), BorderLayout.SOUTH);

        setContentPane(root);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(DARK_NAVY);
        header.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        JLabel title = new JLabel("Invoice Records");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(Color.WHITE);

        // Search bar
        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        searchRow.setOpaque(false);
        searchField = new JTextField(20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.setToolTipText("Search by Invoice ID or Merchant ID");
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { applyFilter(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { applyFilter(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { applyFilter(); }
        });
        JLabel searchLbl = new JLabel("Search:");
        searchLbl.setForeground(Color.WHITE);
        searchLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchRow.add(searchLbl);
        searchRow.add(searchField);

        JButton refreshBtn = styledButton("Refresh");
        refreshBtn.addActionListener(e -> loadInvoices());
        searchRow.add(refreshBtn);

        header.add(title, BorderLayout.WEST);
        header.add(searchRow, BorderLayout.EAST);
        return header;
    }

    private JPanel buildTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        String[] cols = {"Invoice ID", "Order ID", "Merchant ID", "Date", "Gross (£)", "Discount (£)", "Final Total (£)"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        invoiceTable = new JTable(tableModel);
        invoiceTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        invoiceTable.setRowHeight(30);
        invoiceTable.setShowGrid(false);
        invoiceTable.setSelectionBackground(new Color(207, 226, 255));
        invoiceTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        invoiceTable.getTableHeader().setBackground(DARK_NAVY);
        invoiceTable.getTableHeader().setForeground(Color.WHITE);

        sorter = new TableRowSorter<>(tableModel);
        invoiceTable.setRowSorter(sorter);

        // Double-click opens display frame
        invoiceTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) openSelectedInvoice();
            }
        });

        JScrollPane scroll = new JScrollPane(invoiceTable);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 8));
        footer.setBackground(Color.WHITE);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)));

        JButton viewBtn = styledButton("View Invoice");
        viewBtn.addActionListener(e -> openSelectedInvoice());
        footer.add(viewBtn);

        return footer;
    }

    private void loadInvoices() {
        tableModel.setRowCount(0);
        List<Object[]> rows = invoiceDB.getInvoicesForDisplay();
        for (Object[] row : rows) {
            tableModel.addRow(row);
        }
    }

    private void applyFilter() {
        String text = searchField.getText().trim();
        if (text.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            // Filter on Invoice ID (col 0) or Merchant ID (col 2)
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text, 0, 2));
        }
    }

    private void openSelectedInvoice() {
        int row = invoiceTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an invoice to view.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow   = invoiceTable.convertRowIndexToModel(row);
        String invoiceId = (String) tableModel.getValueAt(modelRow, 0);
        Invoice invoice = invoiceDB.getInvoiceById(invoiceId);
        if (invoice != null) {
            new InvoiceDisplayFrame(invoice);
        } else {
            JOptionPane.showMessageDialog(this, "Could not load invoice details.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JButton styledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(ACCENT);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
