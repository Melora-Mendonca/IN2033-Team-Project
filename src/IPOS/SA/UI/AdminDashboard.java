package IPOS.SA.UI;

import IPOS.SA.ACC.Model.AdminDashboardData;
import IPOS.SA.ACC.Model.LowStockItem;
import IPOS.SA.ACC.Model.OrderSummary;
import IPOS.SA.ACC.Service.AdminService;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminDashboard extends BaseFrame implements Refreshable{

    private final AdminService dashboardService;

    private JPanel CardsPanel;
    private JPanel tableWrapper;
    private JTable OrderStatusTable;
    private JTable LowStockTable;
    /**
     * Admin Dashboard screen for IPOS-SA.
     * Displayed after an Administrator logs in successfully.
     * Shows three summary stat cards and two data tables:
     * - Stat cards: low stock count, stock deliveries this month, overdue payments
     * - Recent Orders table: latest orders with colour-coded status column
     * - Low Stock Items table: catalogue items below their minimum stock level,
     *   highlighted in light red
     *
     * Data is loaded from the database via AdminService and refreshed
     * automatically each time the screen is shown.
     */
    public AdminDashboard(String fullname, String role, String username, ScreenRouter router) {
        super(fullname, role, username,"Admin Dashboard", router);
        this.dashboardService = new AdminService();

        loadDashboardData();
    }
    /**
     * Returns the personalised header title shown at the top of the screen.
     *
     * @return the header title string including the user's full name
     */
    @Override
    protected String getHeaderTitle() {
        return "Welcome back, " + fullname;
    }
    /**
     * Loads all dashboard data from the database and rebuilds the
     * stat cards and tables. Clears the panel before reloading
     * to prevent duplicate components when the screen is revisited.
     */
    private void loadDashboardData() {
        try {
            // Clear existing content before reloading
            CenterPanel.removeAll();

            AdminDashboardData data = dashboardService.getDashboardData();
            createCardsPanel(data);
            createTables(data);

            // Refresh the panel
            CenterPanel.revalidate();
            CenterPanel.repaint();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error loading dashboard data: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    /**
     * Loads all dashboard data from the database and rebuilds the
     * stat cards and tables. Clears the panel before reloading
     * to prevent duplicate components when the screen is revisited.
     */
    private void createCardsPanel(AdminDashboardData data) {
        CardsPanel = new JPanel(new GridLayout(1, 3, 16, 16));
        CardsPanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        CardsPanel.setBackground(new Color(245, 247, 250));

        CardsPanel.add(buildCard("Low Stock Items",
                String.valueOf(data.getLowStockCount()),
                "Below minimum level",
                new Color(81, 116, 136), new Color(11, 12, 41), new Color(11, 11, 10)));

        CardsPanel.add(buildCard("Stock Deliveries",
                String.valueOf(data.getStockDeliveriesCount()),
                "Recorded this month",
                new Color(17, 54, 74), new Color(245, 247, 248), new Color(200, 202, 207)));

        CardsPanel.add(buildCard("Overdue Payments",
                String.valueOf(data.getOverduePaymentsCount()),
                "Merchants overdue",
                new Color(11, 12, 41), new Color(230, 230, 234), new Color(167, 167, 170)));

        CenterPanel.add(CardsPanel, BorderLayout.NORTH);
    }
    /**
     * Builds a single stat card with a title, large numeric value and subtitle.
     *
     * @param title    the card heading (e.g. "Low Stock Items")
     * @param value    the numeric value to display prominently
     * @param subtitle the descriptive subtitle below the value
     * @param bg       the card background colour
     * @param titleFg  the foreground colour for the title and value labels
     * @param subFg    the foreground colour for the subtitle label
     * @return the assembled stat card panel
     */
    private JPanel buildCard(String title, String value, String subtitle,
                             Color bg, Color titleFg, Color subFg) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(bg);
        //card.setPreferredSize(new Dimension(400, 200));
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLbl.setForeground(titleFg);
        titleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel valueLbl = new JLabel(value);
        valueLbl.setFont(new Font("Segoe UI", Font.BOLD, 32));
        valueLbl.setForeground(titleFg);
        valueLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subLbl = new JLabel(subtitle);
        subLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        subLbl.setForeground(subFg);
        subLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(titleLbl);
        card.add(Box.createVerticalStrut(6));
        card.add(valueLbl);
        card.add(Box.createVerticalStrut(2));
        card.add(subLbl);

        return card;
    }
    /**
     * Creates the table wrapper panel and populates it with the
     * recent orders table on the left and low stock table on the right.
     *
     * @param data the dashboard data containing orders and low stock items
     */
    private void createTables(AdminDashboardData data) {
        tableWrapper = new JPanel(new GridLayout(1, 2, 16, 0));
        tableWrapper.setBackground(new Color(245, 247, 250));

        createOrdersTable(data.getRecentOrders());
        createStockTable(data.getLowStockItems());

        CenterPanel.add(tableWrapper, BorderLayout.CENTER);
    }
    /**
     * Builds the Recent Orders table and adds it to the table wrapper.
     * The Status column uses colour-coded labels matching the order workflow:
     * pending (purple), accepted (yellow), being_processed (blue),
     * dispatched (orange), delivered (green).
     *
     * @param orders the list of recent order summaries to display
     */
    private void createOrdersTable(List<OrderSummary> orders) {
        JPanel ordersPanel = new JPanel(new BorderLayout(0, 8));
        ordersPanel.setBackground(Color.WHITE);
        ordersPanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JLabel ordersTitle = new JLabel("Recent Orders");
        ordersTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        ordersTitle.setForeground(new Color(17, 24, 39));

        String[] orderCols = {"Order ID", "Merchant", "Date", "Status", "Total"};
        Object[][] orderData;

        if (orders == null || orders.isEmpty()) {
            orderData = new Object[][]{{"No orders found", "", "", "", ""}};
        } else {
            orderData = new Object[orders.size()][5];
            for (int i = 0; i < orders.size(); i++) {
                OrderSummary order = orders.get(i);
                orderData[i] = new Object[]{
                        order.getOrderId(),
                        order.getMerchantName(),
                        order.getOrderDate(),
                        order.getStatus(),
                        String.format("£%.2f", order.getTotalAmount())
                };
            }
        }

        OrderStatusTable = new JTable(new DefaultTableModel(orderData, orderCols) {
            public boolean isCellEditable(int row, int column) { return false; }
        });

        OrderStatusTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        OrderStatusTable.setRowHeight(30);
        OrderStatusTable.setShowGrid(false);
        OrderStatusTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        OrderStatusTable.getTableHeader().setBackground(new Color(17, 24, 39));
        OrderStatusTable.getTableHeader().setForeground(Color.WHITE);

        OrderStatusTable.getColumnModel().getColumn(3).setCellRenderer(
                new DefaultTableCellRenderer() {
                    public Component getTableCellRendererComponent(JTable t, Object val,
                                                                   boolean sel, boolean foc, int row, int col) {
                        JLabel lbl = new JLabel(val != null ? val.toString() : "", SwingConstants.CENTER);
                        lbl.setOpaque(true);
                        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
                        if (val != null) switch (val.toString()) {
                            case "pending":         lbl.setBackground(new Color(216, 186, 223)); lbl.setForeground(new Color(90, 4, 133));   break;
                            case "accepted":        lbl.setBackground(new Color(255, 243, 205)); lbl.setForeground(new Color(133, 100, 4));  break;
                            case "being_processed": lbl.setBackground(new Color(207, 226, 255)); lbl.setForeground(new Color(10, 64, 168));  break;
                            case "dispatched":      lbl.setBackground(new Color(255, 220, 185)); lbl.setForeground(new Color(168, 80, 10));  break;
                            case "delivered":       lbl.setBackground(new Color(198, 239, 206)); lbl.setForeground(new Color(0, 97, 0));     break;
                            default:                lbl.setBackground(Color.WHITE);              lbl.setForeground(Color.BLACK);
                        }
                        return lbl;
                    }
                }
        );

        JScrollPane scroll = new JScrollPane(OrderStatusTable);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        ordersPanel.add(ordersTitle, BorderLayout.NORTH);
        ordersPanel.add(scroll,      BorderLayout.CENTER);
        tableWrapper.add(ordersPanel);
    }
    /**
     * Builds the Low Stock Items table and adds it to the table wrapper.
     * Rows where current stock is below the minimum level are
     * highlighted in light red.
     *
     * @param lowStockItems the list of low stock items to display
     */
    private void createStockTable(List<LowStockItem> lowStockItems) {
        JPanel stockPanel = new JPanel(new BorderLayout(0, 8));
        stockPanel.setBackground(Color.WHITE);
        stockPanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JLabel stockTitle = new JLabel("Low Stock Items");
        stockTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        stockTitle.setForeground(new Color(17, 24, 39));

        String[] stockCols = {"Item ID", "Name", "Current Stock", "Min Level"};
        Object[][] stockData;

        if (lowStockItems == null || lowStockItems.isEmpty()) {
            stockData = new Object[][]{{"No low stock items", "", "", ""}};
        } else {
            stockData = new Object[lowStockItems.size()][4];
            for (int i = 0; i < lowStockItems.size(); i++) {
                LowStockItem item = lowStockItems.get(i);
                stockData[i] = new Object[]{
                        item.getItemId(),
                        item.getItemName(),
                        item.getCurrentStock(),
                        item.getMinLevel()
                };
            }
        }

        LowStockTable = new JTable(new DefaultTableModel(stockData, stockCols) {
            public boolean isCellEditable(int row, int column) { return false; }
        });

        LowStockTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        LowStockTable.setRowHeight(30);
        LowStockTable.setShowGrid(false);
        LowStockTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        LowStockTable.getTableHeader().setBackground(new Color(17, 24, 39));
        LowStockTable.getTableHeader().setForeground(Color.WHITE);

        if (lowStockItems != null && !lowStockItems.isEmpty()) {
            LowStockTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
                public Component getTableCellRendererComponent(JTable t, Object val,
                                                               boolean sel, boolean foc, int row, int col) {
                    Component c = super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                    if (!sel && row < lowStockItems.size()) {
                        LowStockItem item = lowStockItems.get(row);
                        c.setBackground(item.getCurrentStock() < item.getMinLevel()
                                ? new Color(255, 240, 240) : Color.WHITE);
                    }
                    return c;
                }
            });
        }

        JScrollPane scroll = new JScrollPane(LowStockTable);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        stockPanel.add(stockTitle, BorderLayout.NORTH);
        stockPanel.add(scroll, BorderLayout.CENTER);
        tableWrapper.add(stockPanel);
    }
    /**
     * Called by the screen router when this screen becomes visible.
     * Reloads all dashboard data so counts and tables are always current.
     */
    @Override
    public void onShow() {
        loadDashboardData();
    }
}