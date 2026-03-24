//Author: Areess Lahmouddi

/*Catalogue GUI
 Week 1 implementation for the IPOS coursework project.
 This class creates a graphical interface that allows users to view the
 electronic catalogue provided in the project brief. The catalgue display sample data (from the biref for now) and allows users to search items.
 Different roles (Admin, Merchant, Manager) see slightly different views as instructed.
  Admin: full access
 Merchant: cannot see stock limit
 Manager: limted access (mainly reports in later weeks)*/


package IPOS.SA.CAT.UI;

import IPOS.SA.ACC.AccountManagement;
import IPOS.SA.ACC.UI.AdminDashboard;
import IPOS.SA.ACC.UI.LoginForm;
import IPOS.SA.CAT.Model.CatalogueItem;
import IPOS.SA.DB.DBConnection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;

// Main window that displays the catalogue GUI
public class Catalogue extends JFrame {

    // List used to store all catalogue items
    //// These items will appear in the catalogue table
    private final List<CatalogueItem> Items = new ArrayList<>();

    //// GUI components used in the catalogue window

    // table used to display catalogue data
    private JTable catalogueTable;

    // model that controls the table data
    private DefaultTableModel tableModel;

    // search field for searching item ID or keyword
    private JTextField searchField;

    // dropdown used to simulate different user roles
    private JComboBox<String> roleComboBox;
    private JLabel statusLabel;
    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton refreshButton;
    private JButton searchButton;
    private JButton deliveryButton;
    private JPanel MainPanel;
    private JPanel NavPanel;
    private JPanel ContentPanel;
    private JPanel FooterPanel;
    private JPanel HeaderPanel;
    private JPanel CenterPanel;
    private JLabel headerLabel;
    private JLabel navIcon;
    private JButton logoutBtn;
    private JSeparator divider;
    private String fullname;
    private String role;


    // Constructor that creates the catalogue window and loads the GUI
    public Catalogue(String fullname, String role) {
        this.fullname = fullname;
        this.role = role;
        setTitle("Catalogue");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(MainPanel);
        setSize(1100, 650);
        // Sets the form size to the size of the display
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        // Sets the frame location to the center of the screen
        setLocationRelativeTo(null);

        //initialiseSampleData();
        createHeaderPanel();
        createNavPanel();
        createContentPanel();
        loadData();
        //initialiseGui();
        updateTableForSelectedRole();

        // Sets the frame to be visible when running
        setVisible(true);
    }

    private void createHeaderPanel() {
        HeaderPanel.setLayout(new BoxLayout(HeaderPanel, BoxLayout.X_AXIS));
        HeaderPanel.setPreferredSize(new Dimension(1000, 54));
        HeaderPanel.setBackground(new Color(240, 252, 255));
        HeaderPanel.setBorder(BorderFactory.createEmptyBorder(0, 24, 0, 24));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        headerLabel = new JLabel("Catalogue");
        headerLabel.setForeground(Color.BLACK);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));

        textPanel.add(headerLabel);
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

        // Adds the logo to the navigation panel
        NavPanel.add(navIcon);
        NavPanel.add(Box.createVerticalStrut(16));

        // Adds nav buttons to the navigation panel
        NavPanel.add(buildNavButton("Overview",  false));
        NavPanel.add(Box.createVerticalStrut(4));
        NavPanel.add(buildNavButton("Catalogue", false));
        NavPanel.add(Box.createVerticalStrut(4));
        NavPanel.add(buildNavButton("Orders",    false));
        NavPanel.add(Box.createVerticalStrut(4));

        // Expandable sections for certain navigation options
        addExpandableNavItem(NavPanel, "Merchants", new String[]{
                "View Merchant Orders",
                "View Merchant Invoices"
        });

        addExpandableNavItem(NavPanel, "Accounts", new String[]{
                "Create Merchant Account",
                "Manage Merchant Accounts",
                "Commercial Applications"
        });

        addExpandableNavItem(NavPanel, "Staff", new String[]{
                "View All Staff",
                "Create Staff Account",
                "Manage Staff Account",
        });

        // Adds remaining option to the navigation panel
        NavPanel.add(buildNavButton("Reports",  false));  NavPanel.add(Box.createVerticalStrut(4));
        NavPanel.add(buildNavButton("Settings", false));  NavPanel.add(Box.createVerticalStrut(4));

        // Creates a divider to separate and format the navigation options
        divider = new JSeparator();
        divider.setForeground(Color.WHITE);
        divider.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        NavPanel.add(divider);
        NavPanel.add(Box.createVerticalGlue());

        // creates a log Out button at the base of the navigation panel
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
        // Adds the button to the panel
        NavPanel.add(logoutBtn);
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

        // Sub-items panel — hidden by default
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

        // Toggle sub-panel on click
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
            case "Manage Merchant Accounts":
                dispose();
                new AccountManagement(fullname, role, "MANAGE");
                break;
            case "Create Merchant Account":
                dispose();
                new AccountManagement(fullname, role,  "CREATE");
                break;
            case "Commercial Applications":
                JOptionPane.showMessageDialog(this, "Commercial Applications — coming soon.");
                break;
            case "View All Staff":
            case "Create Staff Account":
                dispose();
                //new StaffManagement(fullname, role);
            case "Manage Staff Account":
                dispose();
                //new StaffManagement(fullname, role);

                break;
            case "View Merchant Orders":
            case "View Merchant Invoices":
            default:
                JOptionPane.showMessageDialog(this, label + " — coming soon.");
                break;
        }
    }

    // Creates the button functionality for the items in the navigation panel
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
                    dispose();
                    break;
                case "Overview":
                    new AdminDashboard(fullname, role);
                    dispose();
                    break;
            }
        });

        return btn;
    }

    // Manages the logout functionality for the logout button
    private void handleLogout() {
        dispose();
        new LoginForm();
    }

    private void createContentPanel() {
        ContentPanel.setLayout(new BorderLayout(0, 0));
        ContentPanel.setBackground(new Color(245, 247, 250));

        // Top Panel — search and role selector
        JPanel topControlPanel = new JPanel(new BorderLayout());
        topControlPanel.setBackground(new Color(17, 24, 39));
        topControlPanel.setBorder(new EmptyBorder(10, 16, 10, 16));

        JPanel rolePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rolePanel.setBackground(new Color(17, 24, 39));

        JLabel roleLabel = new JLabel("Role:");
        roleLabel.setForeground(Color.WHITE);
        roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        // Only show role selector for admin
        if (role.equals("administrator")) {
            roleComboBox = new JComboBox<>(new String[]{"Admin", "Merchant", "Manager"});
            roleComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            roleComboBox.addActionListener(e -> updateTableForSelectedRole());
            rolePanel.add(roleLabel);
            rolePanel.add(roleComboBox);
        } else {
            // Non-admins don't see the dropdown at all
            roleComboBox = new JComboBox<>(new String[]{role});
        }

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchPanel.setBackground(new Color(17, 24, 39));

        JLabel searchLabel = new JLabel("Search by Item ID or Keyword:");
        searchLabel.setForeground(Color.WHITE);
        searchLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        topControlPanel.add(searchPanel, BorderLayout.WEST);
        topControlPanel.add(rolePanel, BorderLayout.EAST);


        searchField = new JTextField(20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        searchButton  = new JButton("Search");
        refreshButton = new JButton("Refresh");
        styleButton(searchButton);
        styleButton(refreshButton);

        searchButton.addActionListener(e -> searchCatalogue());
        refreshButton.addActionListener(e -> {
            searchField.setText("");
            updateTableForSelectedRole();
        });

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(refreshButton);
        rolePanel.add(roleLabel);
        rolePanel.add(roleComboBox);

        // Table
        tableModel = new DefaultTableModel();
        catalogueTable = new JTable(tableModel);
        catalogueTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        catalogueTable.setRowHeight(30);
        catalogueTable.setShowGrid(false);
        catalogueTable.setFillsViewportHeight(true);
        catalogueTable.getTableHeader().setReorderingAllowed(false);
        catalogueTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        catalogueTable.getTableHeader().setBackground(new Color(17, 24, 39));
        catalogueTable.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(catalogueTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        // Bottom buttons
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(17, 24, 39));
        bottomPanel.setBorder(new EmptyBorder(10, 12, 10, 12));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonPanel.setBackground(new Color(17, 24, 39));

        addButton = new JButton("Add Item");
        updateButton = new JButton("Update Item");
        deleteButton = new JButton("Delete Item");
        deliveryButton = new JButton("Record Delivery");

        styleButton(addButton);
        styleButton(updateButton);
        styleButton(deleteButton);
        styleButton(deliveryButton);

        buttonPanel.add(addButton);
        addButton.addActionListener(e    -> {
                    dispose();
                    new ManageItem(fullname, role, "ADD");
                });

        buttonPanel.add(updateButton);
        updateButton.addActionListener(e    -> {
            dispose();
            new ManageItem(fullname, role, "EDIT");
        });

        buttonPanel.add(deleteButton);
        deleteButton.addActionListener(e    -> {
            dispose();
            new ManageItem(fullname, role, "DELETE");
        });

        buttonPanel.add(deliveryButton);
        deliveryButton.addActionListener(e    -> {
            dispose();
            new ManageItem(fullname, role, "DELIVERY");
        });

        statusLabel = new JLabel("Ready");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        statusLabel.setForeground(Color.WHITE);

        bottomPanel.add(buttonPanel, BorderLayout.WEST);
        bottomPanel.add(statusLabel, BorderLayout.EAST);

        ContentPanel.add(topControlPanel, BorderLayout.NORTH);
        ContentPanel.add(scrollPane, BorderLayout.CENTER);
        ContentPanel.add(bottomPanel, BorderLayout.SOUTH);

        updateTableForSelectedRole();
    }

    private void styleButton(JButton btn) {
        btn.setBackground(new Color(30, 70, 90));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    }

    private void loadData() {
        Items.clear();
        try {
            DBConnection db = new DBConnection();
            ResultSet rs = db.query("SELECT * FROM Catalogue WHERE is_active = 1 ORDER BY item_id");
            while (rs.next()) {
                Items.add(new CatalogueItem(
                        rs.getString("item_id"),
                        rs.getString("description"),
                        rs.getString("package_type"),
                        rs.getString("unit"),
                        rs.getInt("units_per_pack"),
                        rs.getDouble("package_cost"),
                        rs.getInt("availability"),
                        rs.getInt("stock_limit")
                ));
            }
            statusLabel.setText(Items.size() + " items loaded");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading catalogue: " + e.getMessage());
        }
    }

    // Updates the catalogue table depending on which role is selected
    private void updateTableForSelectedRole() {
        String selectedRole;

        if (role.equals("administrator")) {
            selectedRole = roleComboBox.getSelectedItem().toString().toLowerCase();
        } else {
            selectedRole = role;
        }

        switch (selectedRole) {
            case "admin":
            case "administrator":
                setAdminView();
                break;
            case "director_of_operations":
                setManagerView();
                break;
            default:
                setMerchantView();
                break;
        }
    }

    // Admin users can see the full catalogue including stock limits
    private void setAdminView() {
        String[] columns = {
                "Item ID", "Description", "Package Type", "Unit",
                "Units in a pack", "Package Cost (£)", "Availability (packs)", "Stock Limit (packs)"
        };

        buildTable(columns, true, true, true, true, Items);
        statusLabel.setText("Admin view: full catalogue access");
    }
    // Merchants can see catalogue items but stock limit is hidden
    private void setMerchantView() {
        String[] columns = {
                "Item ID", "Description", "Package Type", "Unit",
                "Units in a pack", "Package Cost (£)", "Availability (packs)"
        };

        buildTable(columns, false, false, false, false, Items);
        statusLabel.setText("Merchant view: stock limit hidden");
    }
    // Manager role does not maintain catalogue in this prototype
    private void setManagerView() {
        String[] columns = {
                "Message"
        };

        tableModel.setDataVector(new Object[][]{
                {"Manager does not use catalogue maintenance in the final system. This role mainly focuses on reports and account changes."}
        }, columns);

        addButton.setEnabled(false);
        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);
        deliveryButton.setEnabled(false);

        statusLabel.setText("Manager view: no catalogue maintenance access");
    }

    private void buildTable(String[] columns, boolean canAdd, boolean canUpdate, boolean canDelete, boolean canRecord, List<CatalogueItem> items) {
        tableModel.setRowCount(0);
        tableModel.setColumnCount(0);

        for (String column : columns) {
            tableModel.addColumn(column);
        }

        boolean showStockLimit = columns.length == 8;

        for (CatalogueItem item : items) {
            if (showStockLimit) {
                tableModel.addRow(new Object[]{
                        item.getItemId(),
                        item.getDescription(),
                        item.getPackageType(),
                        item.getUnit(),
                        item.getUnitsInPack(),
                        String.format("%.2f", item.getPackageCost()),
                        item.getAvailabilityPacks(),
                        item.getStockLimitPacks()
                });
            } else {
                tableModel.addRow(new Object[]{
                        item.getItemId(),
                        item.getDescription(),
                        item.getPackageType(),
                        item.getUnit(),
                        item.getUnitsInPack(),
                        String.format("%.2f", item.getPackageCost()),
                        item.getAvailabilityPacks()
                });
            }
        }

        addButton.setEnabled(canAdd);
        updateButton.setEnabled(canUpdate);
        deleteButton.setEnabled(canDelete);
        deliveryButton.setEnabled(canRecord);
    }
    // Searches catalogue items by item ID or desciption keyword
    private void searchCatalogue() {
        String role = roleComboBox.getSelectedItem().toString();
        String searchText = searchField.getText().trim().toLowerCase();

        if (role.equals("Manager")) {
            System.out.println("Manager role does not search catalogue in this prototype.");
            return;
        }

        if (searchText.isEmpty()) {
            updateTableForSelectedRole();
            return;
        }

        List<CatalogueItem> filteredItems = new ArrayList<>();

        for (CatalogueItem item : Items) {
            if (item.getItemId().toLowerCase().contains(searchText)
                    || item.getDescription().toLowerCase().contains(searchText)) {
                filteredItems.add(item);
            }
        }

        if (role.equals("Admin")) {
            String[] columns = {
                    "Item ID", "Description", "Package Type", "Unit",
                    "Units in a pack", "Package Cost (£)", "Availability (packs)", "Stock Limit (packs)"
            };
            buildTable(columns, true, true, true, true, filteredItems);
        } else {
            String[] columns = {
                    "Item ID", "Description", "Package Type", "Unit",
                    "Units in a pack", "Package Cost (£)", "Availability (packs)"
            };
            buildTable(columns, false, false, false, false, filteredItems);
        }

        statusLabel.setText(filteredItems.size() + " item(s) found");
    }


}