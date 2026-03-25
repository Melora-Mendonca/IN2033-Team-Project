package IPOS.SA.ACC.UI;

import IPOS.SA.ACC.Model.ManagerDashboardData;
import IPOS.SA.ACC.Model.LowStockItem;
import IPOS.SA.ACC.Service.ManagerService;
import IPOS.SA.CAT.UI.Catalogue;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ManagerDashboard extends JFrame {
    private final ManagerService dashboardService;
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
    private JTable lowStockTable;
    private JPanel stockPanel;
    private JLabel stockTitle;
    private JScrollPane stockScroll;

    public ManagerDashboard(String fullname, String role) {
        this.dashboardService = new ManagerService();
        this.fullname = fullname;
        this.role = role;

        setTitle("Manager Dashboard");
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

        headerSubTitle = new JLabel("Manager");
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

        String[] navItems = {"Overview", "Catalogue", "Merchants", "Invoices", "Reports", "Settings"};
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
            ManagerDashboardData data = dashboardService.getDashboardData();
            createCardsPanel(data);
            createTables(data);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error loading dashboard data: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void createCardsPanel(ManagerDashboardData data) {
        CardsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 16));
        CardsPanel.setBackground(new Color(245, 247, 250));

        CardsPanel.add(buildCard("Low Stock Items",
                String.valueOf(data.getLowStockCount()),
                "Below minimum level",
                new Color(81, 116, 136), new Color(11, 12, 41), new Color(11, 11, 10)));

        CardsPanel.add(buildCard("Total Invoices",
                String.valueOf(data.getTotalInvoices()),
                "Recorded this month",
                new Color(17, 54, 74), new Color(245, 247, 248), new Color(200, 202, 207)));

        CardsPanel.add(buildCard("Total Turnover",
                String.format("£%.2f", data.getTotalTurnover()),
                "For this month",
                new Color(11, 12, 41), new Color(230, 230, 234), new Color(167, 167, 170)));

        CardsPanel.add(buildCard("Stock Turnover",
                String.valueOf(data.getStockTurnover()),
                "Deliveries this month",
                new Color(7, 9, 92), new Color(185, 185, 188), new Color(167, 167, 170)));

        CenterPanel.add(CardsPanel, BorderLayout.NORTH);
    }

    private JPanel buildCard(String title, String value, String subtitle,
                             Color bg, Color titleFg, Color subFg) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(bg);
        card.setPreferredSize(new Dimension(300, 200));
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

    private void createTables(ManagerDashboardData data) {
        tableWrapper = new JPanel(new GridLayout(1, 1, 16, 0));
        tableWrapper.setBackground(new Color(245, 247, 250));
        tableWrapper.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        createStockTable(data.getLowStockItems());

        CenterPanel.add(tableWrapper, BorderLayout.CENTER);
    }

    private void createStockTable(java.util.List<LowStockItem> lowStockItems) {
        stockPanel = new JPanel(new BorderLayout(0, 8));
        stockPanel.setBackground(Color.WHITE);
        stockPanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        stockTitle = new JLabel("Low Stock Items");
        stockTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        stockTitle.setForeground(new Color(17, 24, 39));

        String[] stockCols = {"Item ID", "Name", "Current Stock", "Min Level"};
        Object[][] stockData;

        if (lowStockItems == null || lowStockItems.isEmpty()) {
            stockData = new Object[][]{{"No low stock items", "—", "—", "—"}};
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

        lowStockTable = new JTable(new DefaultTableModel(stockData, stockCols) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });

        lowStockTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lowStockTable.setRowHeight(30);
        lowStockTable.setShowGrid(false);
        lowStockTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        lowStockTable.getTableHeader().setBackground(new Color(17, 24, 39));
        lowStockTable.getTableHeader().setForeground(Color.WHITE);

        // Highlight low stock rows
        lowStockTable.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (!isSelected && lowStockItems != null && row < lowStockItems.size()) {
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

        stockScroll = new JScrollPane(lowStockTable);
        stockScroll.setBorder(BorderFactory.createEmptyBorder());

        stockPanel.add(stockTitle, BorderLayout.NORTH);
        stockPanel.add(stockScroll, BorderLayout.CENTER);

        tableWrapper.add(stockPanel);
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
