package IPOS.SA.ORD.UI;

import IPOS.SA.ORD.Service.OrderService;
import IPOS.SA.ACC.Service.AccountService;
import IPOS.SA.ORD.Service.InvoiceService;
import IPOS.SA.UI.BaseFrame;
import IPOS.SA.UI.ScreenRouter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;

// Lets warehouse/delivery staff view and update dispatch details for orders
public class OrderTrackingFrame extends BaseFrame {

    private final OrderService orderService;
    private JTable orderTable;
    private DefaultTableModel tableModel;

    public OrderTrackingFrame(String fullname, String role, ScreenRouter router) {
        super(fullname, role, "Order Tracking", router);
        this.orderService = new OrderService(new AccountService(), new InvoiceService());
        buildUI();
        loadOrders();
    }

    @Override
    protected String getHeaderTitle() {
        return "Order Tracking";
    }

    private void buildUI() {
        CenterPanel.setLayout(new BorderLayout());
        CenterPanel.setBackground(new Color(245, 247, 250));

        String[] cols = {"Order ID", "Merchant", "Date", "Status", "Amount", "Dispatched", "Courier", "Ref No", "Est. Delivery"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        orderTable = new JTable(tableModel);
        styleTable(orderTable);

        orderTable.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                JLabel lbl = new JLabel(val.toString(), SwingConstants.CENTER);
                lbl.setOpaque(true);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
                switch (val.toString()) {
                    case "pending":    lbl.setBackground(new Color(255, 243, 205)); lbl.setForeground(new Color(133, 100, 4));  break;
                    case "accepted":   lbl.setBackground(new Color(207, 226, 255)); lbl.setForeground(new Color(10, 64, 168));  break;
                    case "processing": lbl.setBackground(new Color(207, 226, 255)); lbl.setForeground(new Color(10, 64, 168));  break;
                    case "dispatched": lbl.setBackground(new Color(198, 239, 206)); lbl.setForeground(new Color(0, 97, 0));     break;
                    case "delivered":  lbl.setBackground(new Color(198, 239, 206)); lbl.setForeground(new Color(0, 97, 0));     break;
                    default:           lbl.setBackground(Color.WHITE);              lbl.setForeground(Color.BLACK);
                }
                return lbl;
            }
        });

        JScrollPane scroll = new JScrollPane(orderTable);
        CenterPanel.add(scroll, BorderLayout.CENTER);
    }

    private void loadOrders() {
        try {
            List<Object[]> orders = orderService.getAllOrders();
            tableModel.setRowCount(0);
            for (Object[] order : orders) tableModel.addRow(order);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void styleTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(32);
        table.setShowGrid(false);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(14, 37, 48));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setReorderingAllowed(false);
    }
}