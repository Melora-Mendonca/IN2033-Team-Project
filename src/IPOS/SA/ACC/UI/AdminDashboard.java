package IPOS.SA.ACC.UI;

import IPOS.SA.CAT.UI.Catalogue;
import IPOS.SA.ACC.Model.AdminDashboardData;
import IPOS.SA.ACC.Model.OrderSummary;
import IPOS.SA.ACC.Model.LowStockItem;
import IPOS.SA.ACC.Service.AdminService;
import IPOS.SA.RPT.UI.ReportForm;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminDashboard extends JFrame {
    private final AdminService dashboardService;
    private final String fullname;
    private final String role;

    // GUI Components
    private JPanel MainPanel;
    private JPanel ContentPanel;
    private JPanel HeaderPanel;
    private JPanel NavPanel;
    private JPanel CenterPanel;
    private JPanel CardsPanel;
    private JPanel FooterPanel;
    private JPanel tableWrapper;
    private JPanel ordersPanel;
    private JLabel ordersTitle;
    private JTable OrderStatusTable;
    private JScrollPane ordersScroll;
    private JTable LowStockTable;
    private JPanel stockPanel;
    private JLabel stockTitle;
    private JScrollPane stockScroll;
    private JLabel headerLabel;
    private JLabel navIcon;
    private JLabel headerSubTitle;
    private JButton logoutBtn;
    private JSeparator divider;

    public AdminDashboard(String fullname, String role) {
        this.dashboardService = new AdminService();
        this.fullname = fullname;
        this.role = role;

        setTitle("Admin Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(MainPanel);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
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

        headerSubTitle = new JLabel("Administrator");
        headerSubTitle.setForeground(new Color(107, 114, 128));
        headerSubTitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        textPanel.add(headerLabel);
        textPanel.add(headerSubTitle);
        HeaderPanel.add(textPanel);
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

        NavPanel.add(buildNavButton("Overview", true));
        NavPanel.add(Box.createVerticalStrut(4));
        NavPanel.add(buildNavButton("Catalogue", false));
        NavPanel.add(Box.createVerticalStrut(4));
        NavPanel.add(buildNavButton("Orders", false));
        NavPanel.add(Box.createVerticalStrut(4));

        addExpandableNavItem(NavPanel, "Merchants", new String[]{
                "View Merchant Orders", "View Merchant Invoices"
        });

        addExpandableNavItem(NavPanel, "Accounts", new String[]{
                "View All Merchants", "Create Merchant Account", "Manage Merchant Accounts", "Commercial Applications"
        });

        addExpandableNavItem(NavPanel, "Staff", new String[]{
                "View All Staff", "Create Staff Account", "Manage Staff Account",
        });

        NavPanel.add(buildNavButton("Reports", false));
        NavPanel.add(Box.createVerticalStrut(4));
        NavPanel.add(buildNavButton("Settings", false));
        NavPanel.add(Box.createVerticalStrut(4));

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
        // First, set up MainPanel layout
        MainPanel.setLayout(new BorderLayout());

        // Add NavPanel to the left
        MainPanel.add(NavPanel, BorderLayout.WEST);

        // Add HeaderPanel to the top
        MainPanel.add(HeaderPanel, BorderLayout.NORTH);

        // Create CenterPanel for main content
        CenterPanel = new JPanel(new BorderLayout());
        CenterPanel.setBackground(new Color(245, 247, 250));
        CenterPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        ContentPanel.setLayout(new BorderLayout());
        ContentPanel.add(CenterPanel, BorderLayout.CENTER);

        // Add ContentPanel to the center of MainPanel
        MainPanel.add(ContentPanel, BorderLayout.CENTER);
    }

    private void loadDashboardData() {
        try {
            AdminDashboardData data = dashboardService.getDashboardData();

            // Create cards panel
            createCardsPanel(data);

            // Create tables
            createTables(data);

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error loading dashboard data: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void createCardsPanel(AdminDashboardData data) {
        CardsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 16));
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

    private void createTables(AdminDashboardData data) {
        tableWrapper = new JPanel(new GridLayout(1, 2, 16, 0));
        tableWrapper.setBackground(new Color(245, 247, 250));
        tableWrapper.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

          createOrdersTable(data.getRecentOrders());
          createStockTable(data.getLowStockItems());

        CenterPanel.add(tableWrapper, BorderLayout.CENTER);
    }

    private void createOrdersTable(List<OrderSummary> orders) {
        ordersPanel = new JPanel(new BorderLayout(0, 8));
        ordersPanel.setBackground(Color.WHITE);
        ordersPanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        ordersTitle = new JLabel("Recent Orders");
        ordersTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        ordersTitle.setForeground(new Color(17, 24, 39));

        String[] orderCols = {"Order ID", "Merchant", "Date", "Status", "Total"};
        Object[][] orderData = new Object[orders.size()][5];

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

        // If no data, show placeholder
        if (orders.isEmpty()) {
            orderData = new Object[][]{{"No orders found", "", "", "", ""}};
        }

        OrderStatusTable = new JTable(new DefaultTableModel(orderData, orderCols) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });

        OrderStatusTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        OrderStatusTable.setRowHeight(30);
        OrderStatusTable.setShowGrid(false);
        OrderStatusTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        OrderStatusTable.getTableHeader().setBackground(new Color(17, 24, 39));
        OrderStatusTable.getTableHeader().setForeground(Color.WHITE);

        // Color coded status column
        OrderStatusTable.getColumnModel().getColumn(3).setCellRenderer(
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

        ordersScroll = new JScrollPane(OrderStatusTable);
        ordersScroll.setBorder(BorderFactory.createEmptyBorder());

        ordersPanel.add(ordersTitle, BorderLayout.NORTH);
        ordersPanel.add(ordersScroll, BorderLayout.CENTER);
        tableWrapper.add(ordersPanel);
    }

    private void createStockTable(List<LowStockItem> lowStockItems) {
        stockPanel = new JPanel(new BorderLayout(0, 8));
        stockPanel.setBackground(Color.WHITE);
        stockPanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        stockTitle = new JLabel("Low Stock Items");
        stockTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        stockTitle.setForeground(new Color(17, 24, 39));

        String[] stockCols = {"Item ID", "Name", "Current Stock", "Min Level"};
        Object[][] stockData = new Object[lowStockItems.size()][4];

        for (int i = 0; i < lowStockItems.size(); i++) {
            LowStockItem item = lowStockItems.get(i);
            stockData[i] = new Object[]{
                    item.getItemId(),
                    item.getItemName(),
                    item.getCurrentStock(),
                    item.getMinLevel()
            };
        }

        // If no data, show placeholder
        if (lowStockItems.isEmpty()) {
            stockData = new Object[][]{{"No low stock items", "", "", ""}};
        }

        LowStockTable = new JTable(new DefaultTableModel(stockData, stockCols) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });

        LowStockTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        LowStockTable.setRowHeight(30);
        LowStockTable.setShowGrid(false);
        LowStockTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        LowStockTable.getTableHeader().setBackground(new Color(17, 24, 39));
        LowStockTable.getTableHeader().setForeground(Color.WHITE);

        // Highlight low stock rows
        LowStockTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (!isSelected && row < lowStockItems.size()) {
                    LowStockItem item = lowStockItems.get(row);
                    if (item.getCurrentStock() < item.getMinLevel()) {
                        c.setBackground(new Color(255, 240, 240));
                    } else {
                        c.setBackground(Color.WHITE);
                    }
                }
                return c;
            }
        });

        stockScroll = new JScrollPane(LowStockTable);
        stockScroll.setBorder(BorderFactory.createEmptyBorder());

        stockPanel.add(stockTitle, BorderLayout.NORTH);
        stockPanel.add(stockScroll, BorderLayout.CENTER);
        tableWrapper.add(stockPanel);
    }

    private void addExpandableNavItem(JPanel nav, String label, String[] subItems) {
        JButton mainBtn = new JButton(label);
        mainBtn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        mainBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        mainBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainBtn.setHorizontalAlignment(SwingConstants.LEFT);
        mainBtn.setFocusPainted(false);
        mainBtn.setBorderPainted(false);
        mainBtn.setBackground(new Color(14, 37, 48));
        mainBtn.setForeground(new Color(160, 190, 210));

        JPanel subPanel = new JPanel();
        subPanel.setLayout(new BoxLayout(subPanel, BoxLayout.Y_AXIS));
        subPanel.setBackground(new Color(10, 28, 38));
        subPanel.setVisible(false);

        for (String sub : subItems) {
            JButton subBtn = new JButton("    › " + sub);
            subBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            subBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
            subBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
            subBtn.setHorizontalAlignment(SwingConstants.LEFT);
            subBtn.setFocusPainted(false);
            subBtn.setBorderPainted(false);
            subBtn.setBackground(new Color(10, 28, 38));
            subBtn.setForeground(new Color(120, 160, 185));

            subBtn.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    subBtn.setForeground(Color.WHITE);
                    subBtn.setBackground(new Color(20, 50, 65));
                }
                public void mouseExited(java.awt.event.MouseEvent e) {
                    subBtn.setForeground(new Color(120, 160, 185));
                    subBtn.setBackground(new Color(10, 28, 38));
                }
            });

            subBtn.addActionListener(e -> handleSubNavClick(sub));
            subPanel.add(subBtn);
            subPanel.add(Box.createVerticalStrut(2));
        }

        mainBtn.addActionListener(e -> {
            boolean showing = subPanel.isVisible();
            subPanel.setVisible(!showing);
            mainBtn.setForeground(showing ? new Color(160, 190, 210) : Color.WHITE);
            mainBtn.setBackground(showing ? new Color(14, 37, 48) : new Color(20, 45, 60));
            nav.revalidate();
            nav.repaint();
        });

        nav.add(mainBtn);
        nav.add(subPanel);
        nav.add(Box.createVerticalStrut(4));
    }

    private void handleSubNavClick(String label) {
        switch (label) {
            case "View All Merchants":
                dispose();
                new MerchantList(fullname, role);
                break;
            case "Manage Merchant Accounts":
                dispose();
                new AccountManagement(fullname, role, "MANAGE");
                break;
            case "Create Merchant Account":
                dispose();
                new AccountManagement(fullname, role, "CREATE");
                break;
            case "Commercial Applications":
                JOptionPane.showMessageDialog(this, "Commercial Applications — coming soon.");
                break;
            case "View All Staff":
                  dispose();
                  new StaffList(fullname, role);
                  break;
            case "Create Staff Account":
                dispose();
                new StaffAccountManagement(fullname, role, "CREATE");
                break;
            case "Manage Staff Account":
                dispose();
                new StaffAccountManagement(fullname, role, "MANAGE");
                break;
            case "View Merchant Orders":
            case "View Merchant Invoices":
            default:
                JOptionPane.showMessageDialog(this, label + " — coming soon.");
                break;
        }
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
                case "Catalogue":
                    new Catalogue(fullname, role);
                    break;
                case "Overview":
                    new AdminDashboard(fullname, role);
                    break;
                case "Reports":
                    new ReportForm(fullname, role);
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