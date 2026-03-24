package IPOS.SA.ORD;

import IPOS.SA.DB.OrderDBConnector;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;

public class OrderTrackingFrame extends JFrame {

    private static final Color DARK_NAVY = new Color(14, 37, 48);
    private static final Color ACCENT    = new Color(17, 54, 74);
    private static final Color BG        = new Color(245, 247, 250);

    private JTable orderTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField searchField;
    private JComboBox<String> statusFilter;

    private final OrderDBConnector orderDB = new OrderDBConnector();

    public OrderTrackingFrame() {
        setTitle("Order Tracking");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);
        buildUI();
        loadOrders();
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

        JLabel title = new JLabel("Order Tracking");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(Color.WHITE);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        controls.setOpaque(false);

        controls.add(whiteLabel("Search:"));
        searchField = new JTextField(14);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { applyFilter(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { applyFilter(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { applyFilter(); }
        });
        controls.add(searchField);

        controls.add(whiteLabel("Status:"));
        statusFilter = new JComboBox<>(new String[]{"All", "ACCEPTED", "BEING_PROCESSED", "DISPATCHED"});
        statusFilter.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusFilter.addActionListener(e -> applyFilter());
        controls.add(statusFilter);

        JButton refreshBtn = styledButton("Refresh");
        refreshBtn.addActionListener(e -> loadOrders());
        controls.add(refreshBtn);

        header.add(title, BorderLayout.WEST);
        header.add(controls, BorderLayout.EAST);
        return header;
    }

    private JPanel buildTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        String[] cols = {"Order ID", "Merchant ID", "Date", "Status", "Final Total"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        orderTable = new JTable(tableModel);
        orderTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        orderTable.setRowHeight(30);
        orderTable.setShowGrid(false);
        orderTable.setSelectionBackground(new Color(207, 226, 255));
        orderTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        orderTable.getTableHeader().setBackground(DARK_NAVY);
        orderTable.getTableHeader().setForeground(Color.WHITE);

        orderTable.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object val,
                                                           boolean sel, boolean foc, int row, int col) {
                JLabel lbl = new JLabel(val.toString(), SwingConstants.CENTER);
                lbl.setOpaque(true);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
                switch (val.toString()) {
                    case "ACCEPTED":        lbl.setBackground(new Color(255, 243, 205)); lbl.setForeground(new Color(133, 100, 4));  break;
                    case "BEING_PROCESSED": lbl.setBackground(new Color(207, 226, 255)); lbl.setForeground(new Color(10, 64, 168));  break;
                    case "DISPATCHED":      lbl.setBackground(new Color(198, 239, 206)); lbl.setForeground(new Color(0, 97, 0));     break;
                    default:               lbl.setBackground(Color.WHITE);              lbl.setForeground(Color.BLACK);
                }
                return lbl;
            }
        });

        sorter = new TableRowSorter<>(tableModel);
        orderTable.setRowSorter(sorter);

        JScrollPane scroll = new JScrollPane(orderTable);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 8));
        footer.setBackground(Color.WHITE);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)));

        JButton updateBtn = styledButton("Update Status");
        updateBtn.addActionListener(e -> updateSelectedOrderStatus());
        footer.add(updateBtn);
        return footer;
    }

    private void loadOrders() {
        tableModel.setRowCount(0);
        List<Object[]> rows = orderDB.getOrdersForDisplay();
        for (Object[] row : rows) {
            tableModel.addRow(row);
        }
    }

    private void applyFilter() {
        String text   = searchField.getText().trim();
        String status = (String) statusFilter.getSelectedItem();

        RowFilter<DefaultTableModel, Integer> textFilter   = null;
        RowFilter<DefaultTableModel, Integer> statusRowFilter = null;

        if (!text.isEmpty()) {
            textFilter = RowFilter.regexFilter("(?i)" + text, 0, 1);
        }
        if (!"All".equals(status)) {
            statusRowFilter = RowFilter.regexFilter("^" + status + "$", 3);
        }

        if (textFilter != null && statusRowFilter != null) {
            sorter.setRowFilter(RowFilter.andFilter(java.util.Arrays.asList(textFilter, statusRowFilter)));
        } else if (textFilter != null) {
            sorter.setRowFilter(textFilter);
        } else if (statusRowFilter != null) {
            sorter.setRowFilter(statusRowFilter);
        } else {
            sorter.setRowFilter(null);
        }
    }

    private void updateSelectedOrderStatus() {
        int row = orderTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an order to update.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = orderTable.convertRowIndexToModel(row);
        String orderId     = (String) tableModel.getValueAt(modelRow, 0);
        String currentStatus = (String) tableModel.getValueAt(modelRow, 3);

        OrderStatus[] options;
        switch (currentStatus) {
            case "ACCEPTED":        options = new OrderStatus[]{OrderStatus.BEING_PROCESSED}; break;
            case "BEING_PROCESSED": options = new OrderStatus[]{OrderStatus.DISPATCHED};      break;
            default:
                JOptionPane.showMessageDialog(this, "Order has already been dispatched.", "No Update Available", JOptionPane.INFORMATION_MESSAGE);
                return;
        }

        OrderStatus newStatus = (OrderStatus) JOptionPane.showInputDialog(
                this,
                "Update order " + orderId + " status to:",
                "Update Status",
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]
        );

        if (newStatus != null) {
            orderDB.updateOrderStatus(orderId, newStatus);
            tableModel.setValueAt(newStatus.name(), modelRow, 3);
        }
    }

    private JLabel whiteLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return lbl;
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
