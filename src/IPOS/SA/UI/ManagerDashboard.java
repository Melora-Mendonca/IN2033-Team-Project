package IPOS.SA.UI;

import IPOS.SA.ACC.Model.LowStockItem;
import IPOS.SA.ACC.Model.ManagerDashboardData;
import IPOS.SA.ACC.Service.ManagerService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
/**
 * Manager Dashboard screen for IPOS-SA.
 * Displayed after a Director of Operations logs in successfully.
 * Shows four summary stat cards and a low stock items table:
 * - Stat cards: low stock count, total invoices, total turnover, stock deliveries
 * - Low Stock Items table: catalogue items below minimum level, highlighted in light red
 *
 * Data is loaded from the database via ManagerService and refreshed
 * automatically each time the screen is shown.
 */
public class ManagerDashboard extends BaseFrame implements Refreshable{

    private final ManagerService dashboardService;

    private JPanel CardsPanel;
    private JPanel tableWrapper;
    private JTable lowStockTable;
    private JLabel headerSubTitle;

    public ManagerDashboard(String fullname, String role, String username, ScreenRouter router) {
        super(fullname, role, username, "Manager Dashboard", router);
        this.dashboardService = new ManagerService();

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
    /**
     * Builds the four summary stat cards at the top of the dashboard.
     * Shows low stock count, total invoices this month,
     * total turnover this month and stock deliveries this month.
     *
     * @param data the dashboard data containing the values for each card
     */
    private void createCardsPanel(ManagerDashboardData data) {
        CardsPanel = new JPanel(new GridLayout(1, 3, 16, 16));
        CardsPanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
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
        //card.setPreferredSize(new Dimension(300, 200));
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
     * Creates the table wrapper and adds the low stock items table to it.
     *
     * @param data the dashboard data containing the low stock items list
     */
    private void createTables(ManagerDashboardData data) {
        tableWrapper = new JPanel(new GridLayout(1, 1, 16, 0));
        tableWrapper.setBackground(new Color(245, 247, 250));
        createStockTable(data.getLowStockItems());
        CenterPanel.add(tableWrapper, BorderLayout.CENTER);
    }
    /**
     * Builds the Low Stock Items table and adds it to the table wrapper.
     * Rows where current stock is below the minimum level are
     * highlighted in light red.
     * Shows a placeholder row if no low stock items exist.
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
            public boolean isCellEditable(int row, int column) { return false; }
        });

        lowStockTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lowStockTable.setRowHeight(30);
        lowStockTable.setShowGrid(false);
        lowStockTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        lowStockTable.getTableHeader().setBackground(new Color(17, 24, 39));
        lowStockTable.getTableHeader().setForeground(Color.WHITE);

        if (lowStockItems != null && !lowStockItems.isEmpty()) {
            lowStockTable.setDefaultRenderer(Object.class,
                    new javax.swing.table.DefaultTableCellRenderer() {
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
                    }
            );
        }

        JScrollPane scroll = new JScrollPane(lowStockTable);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        stockPanel.add(stockTitle, BorderLayout.NORTH);
        stockPanel.add(scroll,     BorderLayout.CENTER);
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
