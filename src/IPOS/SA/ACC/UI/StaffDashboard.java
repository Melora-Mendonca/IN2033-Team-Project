package IPOS.SA.ACC.UI;

import IPOS.SA.ACC.Model.StaffDashboardData;
import IPOS.SA.ACC.Service.StaffService;
import IPOS.SA.ACC.Model.OrderSummary;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class StaffDashboard extends JFrame {
    private final StaffService dashboardService;
    private final String fullname;
    private final String role;

    private JPanel MainPanel;
    private JPanel ContentPanel;
    private JPanel HeaderPanel;
    private JPanel FooterPanel;
    private JPanel CenterPanel;
    private JPanel CardsPanel;
    private JPanel NavPanel;
    private JLabel headerLabel;
    private JLabel headerSubTitle;
    private JLabel navIcon;
    private JButton logoutBtn;
    private JSeparator divider;
    private JPanel tableWrapper;
    private JTable ordersTable;
    private JScrollPane ordersScroll;
    private JPanel ordersPanel;
    private JLabel ordersTitle;

    public StaffDashboard(String fullname, String role) {
        this.dashboardService = new StaffService();
        this.fullname = fullname;
        this.role = role;

        setTitle("Staff Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(MainPanel);
        // Sets the form size to the size of the display
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        // Sets the frame location to the center of the screen
        setLocationRelativeTo(null);

        initializeUI();

        setVisible(true);
    }

    private void initializeUI() {
        createHeaderPanel();
        createNavPanel();
        createCenterPanel();
        loadDashboardData();
    }

    private void createHeaderPanel() {
        HeaderPanel.setLayout(new BoxLayout(HeaderPanel, BoxLayout.X_AXIS));
        HeaderPanel.setPreferredSize(new Dimension(1000, 54));
        HeaderPanel.setBackground(new Color(240, 252, 255));
        HeaderPanel.setBorder(BorderFactory.createEmptyBorder(0, 24, 0, 24));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        headerLabel = new JLabel("Welcome back, " + fullname);
        headerLabel.setForeground(Color.BLACK);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));

        String displayRole = formatRoleForDisplay(role);
        headerSubTitle = new JLabel(displayRole);
        headerSubTitle.setForeground(new Color(107, 114, 128));
        headerSubTitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        textPanel.add(headerLabel);
        textPanel.add(headerSubTitle);
        HeaderPanel.add(textPanel);
    }

    private String formatRoleForDisplay(String role) {
        if (role == null || role.isEmpty()) return "Staff";

        String[] words = role.split("_");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            if (word.length() > 0) {
                sb.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1))
                        .append(" ");
            }
        }
        return sb.toString().trim();
    }

    private void createNavPanel() {
        NavPanel.setLayout(new BoxLayout(NavPanel, BoxLayout.Y_AXIS));
        NavPanel.setBackground(new Color(14, 37, 48));
        NavPanel.setBorder(BorderFactory.createEmptyBorder(20, 16, 20, 16));
        NavPanel.setPreferredSize(new Dimension(220, 0));

        ImageIcon Icon = new ImageIcon(new ImageIcon("data/Logo.png")
                .getImage().getScaledInstance(80, 60, Image.SCALE_SMOOTH));
        navIcon = new JLabel(Icon);

        NavPanel.add(navIcon);
        NavPanel.add(Box.createVerticalStrut(16));

        String[] navItems = {"Overview", "Orders", "Finance", "Settings"};
        for (String item : navItems) {
            NavPanel.add(buildNavButton(item, item.equals("Overview")));
            NavPanel.add(Box.createVerticalStrut(4));
        }

        divider = new JSeparator();
        divider.setForeground(Color.WHITE);
        divider.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

        NavPanel.add(divider);
        NavPanel.add(Box.createVerticalGlue());

        logoutBtn = new JButton("→  Log out");
        logoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        logoutBtn.setForeground(new Color(200, 80, 80));
        logoutBtn.setBackground(new Color(14, 37, 48));
        logoutBtn.setBorderPainted(false);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        logoutBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        logoutBtn.setHorizontalAlignment(SwingConstants.LEFT);
        logoutBtn.addActionListener(e -> handleLogout());

        NavPanel.add(logoutBtn);
    }

    private void createCenterPanel() {
        CenterPanel = new JPanel(new BorderLayout());
        CenterPanel.setBackground(new Color(245, 247, 250));
        CenterPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        ContentPanel.setLayout(new BorderLayout());
        ContentPanel.add(CenterPanel, BorderLayout.CENTER);
    }

    private void loadDashboardData() {
        try {
            StaffDashboardData data = dashboardService.getDashboardData();
            createCardsPanel(data);
            createOrdersTable(data.getRecentOrderList());

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error loading dashboard data: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void createCardsPanel(StaffDashboardData data) {
        CardsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 16));
        CardsPanel.setBackground(new Color(245, 247, 250));

        CardsPanel.add(buildCard("Pending Orders",
                String.valueOf(data.getPendingOrders()),
                "Orders awaiting processing",
                new Color(81, 116, 136), new Color(11, 12, 41), new Color(11, 11, 10)));

        CardsPanel.add(buildCard("Recent Orders",
                String.valueOf(data.getRecentOrders()),
                "Last 7 days",
                new Color(17, 54, 74), new Color(245, 247, 248), new Color(200, 202, 207)));

        CardsPanel.add(buildCard("Total Value Processed",
                String.format("£%.2f", data.getTotalValueProcessed()),
                "This month",
                new Color(11, 12, 41), new Color(230, 230, 234), new Color(167, 167, 170)));

        CenterPanel.add(CardsPanel, BorderLayout.NORTH);
    }

    private JPanel buildCard(String title, String value, String subtitle,
                             Color bg, Color titleFg, Color subFg) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(bg);
        card.setPreferredSize(new Dimension(400, 200));
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

    private void createOrdersTable(java.util.List<OrderSummary> orders) {
        tableWrapper = new JPanel(new BorderLayout());
        tableWrapper.setBackground(new Color(245, 247, 250));
        tableWrapper.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));

        ordersPanel = new JPanel(new BorderLayout(0, 8));
        ordersPanel.setBackground(Color.WHITE);
        ordersPanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        ordersTitle = new JLabel("Recent Orders");
        ordersTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        ordersTitle.setForeground(new Color(17, 24, 39));

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

        ordersTable = new JTable(new DefaultTableModel(orderData, orderCols) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });

        ordersTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        ordersTable.setRowHeight(30);
        ordersTable.setShowGrid(false);
        ordersTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        ordersTable.getTableHeader().setBackground(new Color(17, 24, 39));
        ordersTable.getTableHeader().setForeground(Color.WHITE);

        // Color coded status column
        ordersTable.getColumnModel().getColumn(3).setCellRenderer(
                new DefaultTableCellRenderer() {
                    @Override
                    public Component getTableCellRendererComponent(JTable table, Object value,
                                                                   boolean isSelected, boolean hasFocus,
                                                                   int row, int column) {
                        JLabel label = new JLabel(value.toString(), SwingConstants.CENTER);
                        label.setOpaque(true);
                        label.setFont(new Font("Segoe UI", Font.BOLD, 11));

                        String status = value.toString();
                        switch (status) {
                            case "Pending":
                                label.setBackground(new Color(255, 243, 205));
                                label.setForeground(new Color(133, 100, 4));
                                break;
                            case "Processing":
                                label.setBackground(new Color(207, 226, 255));
                                label.setForeground(new Color(10, 64, 168));
                                break;
                            case "Completed":
                            case "Delivered":
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

        ordersScroll = new JScrollPane(ordersTable);
        ordersScroll.setBorder(BorderFactory.createEmptyBorder());

        ordersPanel.add(ordersTitle, BorderLayout.NORTH);
        ordersPanel.add(ordersScroll, BorderLayout.CENTER);

        tableWrapper.add(ordersPanel, BorderLayout.CENTER);
        CenterPanel.add(tableWrapper, BorderLayout.CENTER);
    }

    private JButton buildNavButton(String label, boolean active) {
        JButton btn = new JButton(label);
        btn.setFont(new Font("Segoe UI", active ? Font.BOLD : Font.PLAIN, 13));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setBackground(active ? new Color(30, 70, 90) : new Color(14, 37, 48));
        btn.setForeground(active ? Color.WHITE : new Color(160, 190, 210));

        btn.addActionListener(e -> {
            dispose();
            switch (label) {
                case "Overview":
                    new StaffDashboard(fullname, role);
                    break;
                default:
                    JOptionPane.showMessageDialog(this, label + " — coming soon.");
                    break;
            }
        });

        return btn;
    }

    private void handleLogout() {
        dispose();
        new LoginForm();
    }
}
