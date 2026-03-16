package IPOS.SA.ACC;

import javax.swing.*;
import java.awt.*;

public class ManagerDashboard extends JFrame {
    private String fullname;
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
    private JTable LowStockTable;
    private JPanel stockPanel;
    private JLabel stockTitle;
    private JScrollPane stockScroll ;

    public ManagerDashboard(String fullname) {
        this.fullname = fullname;
        setTitle("Manager Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(MainPanel);
        // Sets the form size to the size of the display
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        // Sets the frame location to the center of the screen
        setLocationRelativeTo(null);
        // Sets the frame to be visible when running
        setVisible(true);
        // HEADER:
        createHeaderPanel();
        createNavPanel();
        createCardsPanel();
        createTableWrapper();
        CreateStockStatsTable();
    }

    private void createHeaderPanel() {
        HeaderPanel.setLayout(new BoxLayout(HeaderPanel, BoxLayout.X_AXIS));
        HeaderPanel.setPreferredSize(new Dimension(1000, 54));
        HeaderPanel.setBackground(new Color(240, 252, 255));
        HeaderPanel.setBorder(BorderFactory.createEmptyBorder(0, 24, 0, 24));

        // Nested panel stacks title and subtitle vertically
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        // Creates a Label beside the logo, with the user's name
        headerLabel = new JLabel("Welcome back, " + fullname);
        headerLabel.setForeground(Color.BLACK);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));

        // Adds a smaller subtitle below the username to show the role
        headerSubTitle = new JLabel("Manager");
        headerSubTitle.setForeground(new Color(107, 114, 128));
        headerSubTitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        // Adds both labels to the inner panel, one under the other.
        textPanel.add(headerLabel);
        textPanel.add(headerSubTitle);

        // Adds the text panel to the header
        HeaderPanel.add(textPanel);
    }

    // Creates the Navigation Panel
    private void createNavPanel() {
        NavPanel.setLayout(new BoxLayout(NavPanel, BoxLayout.Y_AXIS));
        NavPanel.setBackground(new Color(14, 37, 48));
        NavPanel.setBorder(BorderFactory.createEmptyBorder(20, 16, 20, 16));

        // Logo icon
        ImageIcon Icon = new ImageIcon(new ImageIcon("data/Logo.png")
                .getImage().getScaledInstance(80, 60, Image.SCALE_SMOOTH));
        navIcon = new JLabel(Icon);

        NavPanel.add(navIcon);

        // generates Navigation buttons — Overview is active by default
        String[] navItems = {"Overview", "Catalogue", "Merchants", "Invoices", "Reports", "Settings"};
        for (String item : navItems) {
            NavPanel.add(buildNavButton(item, item.equals("Overview")));
            NavPanel.add(Box.createVerticalStrut(4));
        }

        // Creates Divider line separating the logo and label from the list of features.
        divider = new JSeparator();
        divider.setForeground(Color.WHITE); // Sets a colour for the divider, with a size for the divider thickness.
        divider.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

        NavPanel.add(divider);
        // Pushes logout to the bottom
        NavPanel.add(Box.createVerticalGlue());

        // Logout button
        logoutBtn = new JButton("[]→ Log out");
        logoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        logoutBtn.setForeground(new Color(200, 80, 80));
        logoutBtn.setBackground(new Color(14, 37, 48));
        logoutBtn.setBorderPainted(false);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        logoutBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        logoutBtn.setHorizontalAlignment(SwingConstants.LEFT);
        logoutBtn.addActionListener(e -> handleLogout());

        // Adds logout button to the navigation panel
        NavPanel.add(logoutBtn);
    }

    // Creates the button funtionality for the items in the navigation panel
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
        return btn;
    }

    // Manages the logout funtionality for the logout button
    private void handleLogout() {
        dispose();
        new LoginForm();
    }

    // Creates cards for key stats in the main content panel
    private void createCardsPanel() {
        CenterPanel = new JPanel(new BorderLayout());
        CenterPanel.setBackground(new Color(245, 247, 250));

        CardsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 16));
        CardsPanel.setBackground(new Color(245, 247, 250));

        // TO BE UPDATED WITH REAL DATA!!!!! //
        CardsPanel.add(buildCard("Low Stock Items", "0", "Below minimum level",
                new Color(81, 116, 136), new Color(11, 12, 41), new Color(11, 11, 10)));
        CardsPanel.add(buildCard("Total Invoices", "0", "Recorded this month",
                new Color(17, 54, 74), new Color(245, 247, 248), new Color(200, 202, 207)));
        CardsPanel.add(buildCard("Total Turnover", "0", "For this month",
                new Color(11, 12, 41), new Color(230, 230, 234), new Color(167, 167, 170)));
        CardsPanel.add(buildCard("Stock Turnover", "0", "For this month",
                new Color(7, 9, 92), new Color(185, 185, 188), new Color(167, 167, 170)));

        CenterPanel.add(CardsPanel, BorderLayout.NORTH);
        ContentPanel.add(CenterPanel, BorderLayout.CENTER);
    }

    // Builds the structure and colour of each of the cards, with the font styles, size and alignments.
    private JPanel buildCard(String title, String value, String subtitle,
                             Color bg, Color titleFg, Color subFg) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(bg);
        card.setPreferredSize(new Dimension(320, 200));
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

    private void createTableWrapper() {
        tableWrapper = new JPanel(new GridLayout(1, 2, 16, 0));
        tableWrapper.setBackground(new Color(245, 247, 250));
        tableWrapper.setBorder(BorderFactory.createEmptyBorder(0, 16, 16, 16));
    }

    // Creates scrollable table for Low stock warnings
    private void CreateStockStatsTable(){
        stockPanel = new JPanel(new BorderLayout(0, 8));
        stockPanel.setBackground(Color.WHITE);
        stockPanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        stockTitle = new JLabel("Low Stock Items");
        stockTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        stockTitle.setForeground(new Color(17, 24, 39));

        // TO BE UPDATED WITH REAL DATA!!!!! //
        String[] StockCols = {"Item ID", "Name", "Current Stock", "Min Level"};
        Object[][] stockData = {
                {"100 00001", "Paracetamol",     "250", "300"},
                {"100 00002", "Aspirin",         "320", "500"},
                {"100 00003", "Analgin",         "102", "200"},
                {"200 00004", "Iodine tincture", "87",  "200"},
                {"200 00005", "Rhynol",          "201", "300"},
        };

        LowStockTable = new JTable(new javax.swing.table.DefaultTableModel(stockData, StockCols) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        });

        LowStockTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        LowStockTable.setRowHeight(30);
        LowStockTable.setShowGrid(false);
        LowStockTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        LowStockTable.getTableHeader().setBackground(new Color(17, 24, 39));
        LowStockTable.getTableHeader().setForeground(Color.WHITE);

        stockScroll = new JScrollPane(LowStockTable);
        stockScroll.setBorder(BorderFactory.createEmptyBorder());

        stockPanel.add(stockTitle, BorderLayout.NORTH);
        stockPanel.add(stockScroll, BorderLayout.CENTER);

        tableWrapper.add(stockPanel);
        CenterPanel.add(tableWrapper, BorderLayout.CENTER);
    }

}
