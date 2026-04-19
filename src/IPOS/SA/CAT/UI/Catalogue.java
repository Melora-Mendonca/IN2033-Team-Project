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

import IPOS.SA.CAT.Model.CatalogueItem;
import IPOS.SA.DB.DBConnection;
import IPOS.SA.UI.AppFrame;
import IPOS.SA.UI.BaseFrame;
import IPOS.SA.UI.Refreshable;
import IPOS.SA.UI.ScreenRouter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Screen that displays the pharmaceutical product catalogue.
 * Shows all active catalogue items in a searchable table.
 *
 * The view adapts based on the logged-in user's role:
 * - Administrator — full table with stock limit, all action buttons enabled
 * - Director of Operations — full table with stock limit, read-only
 * - All other roles (Merchant view) — stock limit column hidden, read-only
 *
 * Administrators can also use a "View As" dropdown to preview
 * how the catalogue appears to different roles.
 */
public class Catalogue extends BaseFrame implements Refreshable {

    private final List<CatalogueItem> Items = new ArrayList<>();

    private JTable catalogueTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> roleComboBox;
    private JLabel statusLabel;
    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton refreshButton;
    private JButton searchButton;
    private JButton deliveryButton;

    /**
     * Constructor — builds the catalogue screen and loads all active items.
     *
     * @param fullname the full name of the logged-in user
     * @param role the role of the logged-in user
     * @param router the screen router used for navigation
     */
    public Catalogue(String fullname, String role, ScreenRouter router) {
        super(fullname, role, "Catalogue", router);
        buildContent();
        loadData();
        updateTableForSelectedRole();
    }

    /**
     * Returns the title displayed in the page header.
     *
     * @return the header title string
     */
    @Override
    protected String getHeaderTitle() {
        return "Catalogue";
    }

    /**
     * Builds and arranges all UI components for this screen.
     * Includes a search bar, optional role picker, catalogue table
     * and role-sensitive action buttons.
     */
    private void buildContent() {
        CenterPanel.setLayout(new BorderLayout(0, 0));
        CenterPanel.setBackground(new Color(245, 247, 250));

        // creates the top bar with the search panel and search buttons
        JPanel topControlPanel = new JPanel(new BorderLayout());
        topControlPanel.setBackground(new Color(17, 24, 39));
        topControlPanel.setBorder(new EmptyBorder(10, 16, 10, 16));

        // Adds a panel for the search button
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchPanel.setBackground(new Color(17, 24, 39));

        // Adds a label to the search bar
        JLabel searchLabel = new JLabel("Search by Item ID or Keyword:");
        searchLabel.setForeground(Color.WHITE);
        searchLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        // textfield stores the search value to use for the search
        searchField   = new JTextField(20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        // Adds buttons to perform the search
        searchButton  = new JButton("Search");
        refreshButton = new JButton("Refresh");
        styleBtn(searchButton);
        styleBtn(refreshButton);

        // Adds functionality to the search button to identify the requested record, and display it on it's own
        searchButton.addActionListener(e  -> searchCatalogue());
        refreshButton.addActionListener(e -> {
            searchField.setText("");
            updateTableForSelectedRole();
        });

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(refreshButton);

        // Role dropdown — admin only
        JPanel rolePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rolePanel.setBackground(new Color(17, 24, 39));

        // Allows the admin user to be able to view the catalogue as different users
        if (role.equals("Administrator")) {
            JLabel roleLabel = new JLabel("View As:");
            roleLabel.setForeground(Color.WHITE);
            roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            roleComboBox = new JComboBox<>(new String[]{"Admin", "Director", "Merchant"});
            roleComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            roleComboBox.addActionListener(e -> updateTableForSelectedRole());
            rolePanel.add(roleLabel);
            rolePanel.add(roleComboBox);
        } else {
            roleComboBox = new JComboBox<>(new String[]{role});
        }

        topControlPanel.add(searchPanel, BorderLayout.WEST);
        topControlPanel.add(rolePanel,   BorderLayout.EAST);


        tableModel     = new DefaultTableModel();

        // A new table is created to store all of the catalogue records from the database
        catalogueTable = new JTable(tableModel);
        catalogueTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        catalogueTable.setRowHeight(30);
        catalogueTable.setShowGrid(false);
        catalogueTable.setFillsViewportHeight(true);
        catalogueTable.getTableHeader().setReorderingAllowed(false);
        catalogueTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        catalogueTable.getTableHeader().setBackground(new Color(17, 24, 39));
        catalogueTable.getTableHeader().setForeground(Color.WHITE);

        // Sroll pane to scroll the table
        JScrollPane scrollPane = new JScrollPane(catalogueTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        // creates a bottom panel with action buttons and status label
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(17, 24, 39));
        bottomPanel.setBorder(new EmptyBorder(10, 12, 10, 12));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonPanel.setBackground(new Color(17, 24, 39));

        // Adds action buttons to modify the catalogue at the base of the catalogue
        addButton      = new JButton("Add Item");
        updateButton   = new JButton("Update Item");
        deleteButton   = new JButton("Delete Item");
        deliveryButton = new JButton("Record Delivery");

        // calls the style button method to style the buttons
        styleBtn(addButton);
        styleBtn(updateButton);
        styleBtn(deleteButton);
        styleBtn(deliveryButton);

        // adds functionality to the buttons so that they perform tasks or redirect to differnt pages when clicked
        addButton.addActionListener(e -> router.goTo(AppFrame.SCREEN_MANAGE_ITEM_ADD));
        updateButton.addActionListener(e -> router.goTo(AppFrame.SCREEN_MANAGE_ITEM_EDIT));
        deleteButton.addActionListener(e -> router.goTo(AppFrame.SCREEN_MANAGE_ITEM_DELETE));
        deliveryButton.addActionListener(e -> router.goTo(AppFrame.SCREEN_MANAGE_ITEM_DELIVERY));

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(deliveryButton);

        statusLabel = new JLabel("Ready");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        statusLabel.setForeground(Color.WHITE);

        bottomPanel.add(buttonPanel, BorderLayout.WEST);
        bottomPanel.add(statusLabel, BorderLayout.EAST);

        CenterPanel.add(topControlPanel, BorderLayout.NORTH);
        CenterPanel.add(scrollPane,      BorderLayout.CENTER);
        CenterPanel.add(bottomPanel,     BorderLayout.SOUTH);
    }


    /**
     * Loads all active catalogue items from the database into the Items list.
     * Only items where is_active = 1 are loaded.
     */
    private void loadData() {
        Items.clear();
        try {
            DBConnection db = new DBConnection();
            ResultSet rs = db.query(
                    "SELECT * FROM catalogue WHERE is_active = 1 ORDER BY item_id");
            while (rs.next()) {
                Items.add(new CatalogueItem(
                        rs.getString("item_id"),
                        rs.getString("description"),
                        rs.getString("package_type"),
                        rs.getString("unit"),
                        rs.getInt("unit_per_pack"),
                        rs.getDouble("package_cost"),
                        rs.getInt("availability"),
                        rs.getInt("minimum_stock_level")
                ));
            }
            statusLabel.setText(Items.size() + " items loaded");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error loading catalogue: " + e.getMessage());
        }
    }

    /**
     * Updates the table columns and button availability based on the
     * currently selected role in the dropdown (or the logged-in role
     * for non-admin users).
     */
    private void updateTableForSelectedRole() {
        String selectedRole;

        if (role.equals("Administrator")) {
            selectedRole = roleComboBox.getSelectedItem().toString();
        } else {
            selectedRole = role;
        }

        switch (selectedRole) {
            case "Admin":
            case "Administrator":
                setAdminView();
                break;
            case "Director":
            case "Director of Operations":
                setManagerView();
                break;
            default:
                setMerchantView();
                break;
        }
    }

    /**
     * Configures the table for Administrator view.
     * Shows all 8 columns including stock limit.
     * All action buttons are enabled.
     */
    private void setAdminView() {
        String[] columns = {
                "Item ID", "Description", "Package Type", "Unit",
                "Units in a pack", "Package Cost (£)", "Availability (packs)", "Stock Limit (packs)"
        };
        buildTable(columns, true, true, true, true, Items);
        statusLabel.setText("Admin view — " + Items.size() + " items");
    }

    /**
     * Configures the table for Director of Operations view.
     * Shows all 8 columns including stock limit.
     * All action buttons are disabled — read-only.
     */
    private void setManagerView() {
        String[] columns = {
                "Item ID", "Description", "Package Type", "Unit",
                "Units in a pack", "Package Cost (£)", "Availability (packs)", "Stock Limit (packs)"
        };
        buildTable(columns, false, false, false, false, Items);
        statusLabel.setText("Manager view — read only");
    }

    /**
     * Configures the table for Merchant view.
     * Shows 7 columns — the stock limit column is hidden.
     * All action buttons are disabled — read-only.
     */
    private void setMerchantView() {
        String[] columns = {
                "Item ID", "Description", "Package Type", "Unit",
                "Units in a pack", "Package Cost (£)", "Availability (packs)"
        };
        buildTable(columns, false, false, false, false, Items);
        statusLabel.setText("Merchant view — stock limit hidden");
    }

    /**
     * Builds the table with the given columns and item list.
     * Enables or disables action buttons based on the role permissions.
     * Shows the stock limit column only when 8 columns are specified.
     *
     * @param columns the column headers to display
     * @param canAdd whether the Add Item button should be enabled
     * @param canUpdate  whether the Update Item button should be enabled
     * @param canDelete  whether the Delete Item button should be enabled
     * @param canRecord  whether the Record Delivery button should be enabled
     * @param items the list of catalogue items to display
     */
    private void buildTable(String[] columns, boolean canAdd, boolean canUpdate,
                            boolean canDelete, boolean canRecord,
                            List<CatalogueItem> items) {
        tableModel.setRowCount(0);
        tableModel.setColumnCount(0);

        for (String col : columns) tableModel.addColumn(col);

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

    /**
     * Filters the catalogue table based on the search field text.
     * Matches against item ID and description.
     * Restores the full table if the search field is empty.
     * Applies the correct columns for the currently selected role view.
     */
    private void searchCatalogue() {
        String searchText = searchField.getText().trim().toLowerCase();

        if (searchText.isEmpty()) {
            updateTableForSelectedRole();
            return;
        }

        // Filters items matching the search text
        List<CatalogueItem> filtered = new ArrayList<>();
        for (CatalogueItem item : Items) {
            if (item.getItemId().toLowerCase().contains(searchText) ||
                    item.getDescription().toLowerCase().contains(searchText)) {
                filtered.add(item);
            }
        }

        // Applies the correct column set for the selected role view
        String selectedRole = roleComboBox.getSelectedItem().toString();

        if (selectedRole.equals("Admin") || selectedRole.equals("Administrator")) {
            String[] columns = {
                    "Item ID", "Description", "Package Type", "Unit",
                    "Units in a pack", "Package Cost (£)", "Availability (packs)", "Stock Limit (packs)"
            };
            buildTable(columns, true, true, true, true, filtered);
        } else if (selectedRole.equals("director") ||
                selectedRole.equals("director_of_operations")) {
            String[] columns = {
                    "Item ID", "Description", "Package Type", "Unit",
                    "Units in a pack", "Package Cost (£)", "Availability (packs)", "Stock Limit (packs)"
            };
            buildTable(columns, false, false, false, false, filtered);
        } else {
            String[] columns = {
                    "Item ID", "Description", "Package Type", "Unit",
                    "Units in a pack", "Package Cost (£)", "Availability (packs)"
            };
            buildTable(columns, false, false, false, false, filtered);
        }

        statusLabel.setText(filtered.size() + " item(s) found");
    }

    /**
     * Applies a consistent visual style to an action button.
     *
     * @param btn the button to style
     */
    private void styleBtn(JButton btn) {
        btn.setBackground(new Color(30, 70, 90));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    }

    /**
     * Called by the screen router when this screen becomes visible.
     * Clears the search field, reloads data from the database
     * and refreshes the table for the current role view.
     */
    @Override
    public void onShow() {
        searchField.setText("");
        loadData();
        updateTableForSelectedRole();
    }
}