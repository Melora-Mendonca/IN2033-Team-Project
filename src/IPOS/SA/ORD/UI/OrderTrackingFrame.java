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
/**
 * Read-only order tracking screen for IPOS-SA.
 * Displays all orders in the system with their full dispatch details.
 * Used by Delivery Employees and Directors to track order progress.
 *
 * This screen is view-only — no action buttons are provided.
 * Order management actions are performed in OrderManagement or OrderProcessingFrame.
 */
public class OrderTrackingFrame extends BaseFrame {

    private final OrderService orderService;
    private JTable orderTable;
    private DefaultTableModel tableModel;
    /**
     * Constructor — builds the order tracking screen and loads all orders.
     *
     * @param fullname the full name of the logged-in user
     * @param role  the role of the logged-in user
     * @param router the screen router used for navigation
     */
    public OrderTrackingFrame(String fullname, String role, ScreenRouter router) {
        super(fullname, role, "Order Tracking", router);
        this.orderService = new OrderService(new AccountService(), new InvoiceService());
        buildUI();
        loadOrders();
    }
    /**
     * Returns the title displayed in the page header.
     *
     * @return the header title string
     */
    @Override
    protected String getHeaderTitle() {
        return "Order Tracking";
    }
    /**
     * Builds the order tracking table with all columns including dispatch details.
     * The Status column uses colour-coded labels matching the order workflow.
     */
    private void buildUI() {
        CenterPanel.setLayout(new BorderLayout());
        CenterPanel.setBackground(new Color(245, 247, 250));

        // creates a Table setup
        String[] cols = {"Order ID", "Merchant", "Date", "Status", "Amount", "Dispatched", "Courier", "Ref No", "Est. Delivery"};

        // Table cells are not directly editable
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        orderTable = new JTable(tableModel);
        styleTable(orderTable);

        // Colour codes the status column based on the status of the order
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

        // Sroll pane to scroll the table
        JScrollPane scroll = new JScrollPane(orderTable);
        CenterPanel.add(scroll, BorderLayout.CENTER);
    }
    /**
     * Loads all orders from the database and populates the tracking table.
     */
    private void loadOrders() {
        try {
            List<Object[]> orders = orderService.getAllOrders();
            tableModel.setRowCount(0);
            for (Object[] order : orders) tableModel.addRow(order);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Applies a consistent visual style to the order table.
     *
     * @param table the table to style
     */
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