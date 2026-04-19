package IPOS.SA.UI;

import IPOS.SA.ACC.Model.StaffDashboardData;
import IPOS.SA.ACC.Service.StaffService;
import IPOS.SA.ACC.Model.OrderSummary;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

// Claude AI was used in this class to help in the creation of the stat cards and the colour coded order tables //

/**
 * Staff Dashboard screen for IPOS-SA.
 * Displayed after any staff member logs in who is not an Administrator
 * or Director of Operations (Warehouse Employee, Delivery Employee,
 * Accountant or Senior Accountant).
 *
 * Shows three summary stat cards and a recent orders table:
 * - Stat cards: pending orders, recent orders (last 7 days), total value processed this month
 * - Recent Orders table: latest orders with colour-coded status column
 */
public class StaffDashboard extends BaseFrame implements Refreshable {

    /** Service used to load all dashboard data from the database. */
    private final StaffService dashboardService;

    /**
     * Constructor — builds the staff dashboard and loads all data.
     *
     * @param fullname the full name of the logged-in staff member
     * @param role     the role of the logged-in user
     * @param username the username of the logged-in user
     * @param router   the screen router used for navigation
     */
    public StaffDashboard(String fullname, String role, String username, ScreenRouter router) {
        super(fullname, role, username, "Staff Dashboard", router);
        this.dashboardService = new StaffService();
        createCenterContent();
    }

    /**
     * Triggers the initial data load when the screen is first built.
     */
    private void createCenterContent() {
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
     * stat cards and recent orders table. Clears the panel before
     * reloading to prevent duplicate components on revisit.
     */
    private void loadDashboardData() {
        try {
            StaffDashboardData data = dashboardService.getDashboardData();

            // Clear CenterPanel before rebuilding
            CenterPanel.removeAll();

            // Main container stacks cards and orders table vertically
            JPanel mainContainer = new JPanel();
            mainContainer.setLayout(new BoxLayout(mainContainer, BoxLayout.Y_AXIS));
            mainContainer.setBackground(new Color(245, 247, 250));

            JPanel cardsPanel = createCardsPanel(data);
            mainContainer.add(cardsPanel);
            mainContainer.add(Box.createVerticalStrut(16));

            JPanel tablePanel = createOrdersTable(data.getRecentOrderList());
            mainContainer.add(tablePanel);

            CenterPanel.add(mainContainer, BorderLayout.CENTER);

            // Force refresh
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
     * Builds the three summary stat cards at the top of the dashboard.
     * Shows pending orders count, recent orders count (last 7 days)
     * and total value processed this month.
     *
     * @param data the dashboard data containing the values for each card
     * @return the assembled cards panel
     */
    private JPanel createCardsPanel(StaffDashboardData data) {
        JPanel panel = new JPanel(new GridLayout(1, 3, 16, 16));
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        panel.setBackground(new Color(245, 247, 250));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 250));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(buildCard("Pending Orders",
                String.valueOf(data.getPendingOrders()),
                "Orders awaiting processing",
                new Color(81, 116, 136), new Color(11, 12, 41), new Color(11, 11, 10)));

        panel.add(buildCard("Recent Orders",
                String.valueOf(data.getRecentOrders()),
                "Last 7 days",
                new Color(17, 54, 74), new Color(245, 247, 248), new Color(200, 202, 207)));

        panel.add(buildCard("Total Value Processed",
                String.format("£%.2f", data.getTotalValueProcessed()),
                "This month",
                new Color(11, 12, 41), new Color(230, 230, 234), new Color(167, 167, 170)));

        return panel;
    }

    /**
     * Builds a single stat card with a title, large numeric value and subtitle.
     *
     * @param title    the card heading
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
        card.setMaximumSize(new Dimension(400, 200));
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
     * Builds the Recent Orders table showing the latest orders with a
     * colour-coded Status column matching the order workflow stages.
     * Shows a placeholder row if no orders exist.
     *
     * @param orders the list of recent order summaries to display
     * @return the assembled orders table panel
     */
    private JPanel createOrdersTable(java.util.List<OrderSummary> orders) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 400));

        JLabel title = new JLabel("Recent Orders");
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        title.setForeground(new Color(17, 24, 39));

        String[] orderCols = {"Order ID", "Merchant", "Date", "Status", "Total"};
        Object[][] orderData;

        if (orders == null || orders.isEmpty()) {
            orderData = new Object[][]{{"No orders found", "—", "—", "—", "—"}};
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

        JTable table = new JTable(new DefaultTableModel(orderData, orderCols) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });

        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(30);
        table.setShowGrid(false);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(17, 24, 39));
        table.getTableHeader().setForeground(Color.WHITE);

        // Color coded status column
        table.getColumnModel().getColumn(3).setCellRenderer(
                new DefaultTableCellRenderer() {
                    @Override
                    public Component getTableCellRendererComponent(JTable tbl, Object value,
                                                                   boolean isSelected, boolean hasFocus,
                                                                   int row, int col) {
                        JLabel label = new JLabel(value.toString(), SwingConstants.CENTER);
                        label.setOpaque(true);
                        label.setFont(new Font("Segoe UI", Font.BOLD, 11));

                        String status = value.toString();
                        switch (status) {
                            case "pending":
                                label.setBackground(new Color(216, 186, 223));
                                label.setForeground(new Color(90, 4, 133));
                                break;
                            case "accepted":
                                label.setBackground(new Color(255, 243, 205));
                                label.setForeground(new Color(133, 100, 4));
                                break;
                            case "processing":
                                label.setBackground(new Color(207, 226, 255));
                                label.setForeground(new Color(10, 64, 168));
                                break;
                            case "dispatched":
                                label.setBackground(new Color(255, 220, 185));
                                label.setForeground(new Color(168, 80, 10));
                                break;
                            case "delivered":
                                label.setBackground(new Color(198, 239, 206));
                                label.setForeground(new Color(0, 97, 0));
                                break;
                            default:
                                label.setBackground(Color.WHITE);
                                label.setForeground(Color.BLACK);
                        }
                        return label;
                    }
                }
        );

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        panel.add(title,  BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
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