package IPOS.SA.UI;

import IPOS.SA.ACC.Model.StaffDashboardData;
import IPOS.SA.ACC.Service.StaffService;
import IPOS.SA.ACC.Model.OrderSummary;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class StaffDashboard extends BaseFrame implements Refreshable{
    private final StaffService dashboardService;

    public StaffDashboard(String fullname, String role, String username, ScreenRouter router) {
        super(fullname, role, username,"Staff Dashboard", router);
        this.dashboardService = new StaffService();
        createCenterContent();
    }

    private void createCenterContent() {
        loadDashboardData();
    }

    @Override
    protected String getHeaderTitle() {
        return "Welcome back, " + fullname;
    }

    private void loadDashboardData() {
        try {
            StaffDashboardData data = dashboardService.getDashboardData();

            // Clear CenterPanel
            CenterPanel.removeAll();

            // Create a main container with BoxLayout (vertical stacking)
            JPanel mainContainer = new JPanel();
            mainContainer.setLayout(new BoxLayout(mainContainer, BoxLayout.Y_AXIS));
            mainContainer.setBackground(new Color(245, 247, 250));

            // Add cards
            JPanel cardsPanel = createCardsPanel(data);
            mainContainer.add(cardsPanel);
            mainContainer.add(Box.createVerticalStrut(16));

            // Add orders table
            JPanel tablePanel = createOrdersTable(data.getRecentOrderList());
            mainContainer.add(tablePanel);

            // Add the main container to CenterPanel
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

    private JPanel buildCard(String title, String value, String subtitle,
                             Color bg, Color titleFg, Color subFg) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(bg);
        //card.setPreferredSize(new Dimension(350, 200));
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

        panel.add(title, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    @Override
    public void onShow() {
        loadDashboardData();
    }
}