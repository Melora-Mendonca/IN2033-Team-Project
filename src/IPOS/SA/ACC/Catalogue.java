 //Author: Areess Lahmouddi

/*Catalogue GUI
 Week 1 implementation for the IPOS coursework project.
 This class creates a graphical interface that allows users to view the
 electronic catalogue provided in the project brief. The catalgue display sample data (from the biref for now) and allows users to search items.
 Different roles (Admin, Merchant, Manager) see slightly different views as instructed.
  Admin: full access
 Merchant: cannot see stock limit
 Manager: limted access (mainly reports in later weeks)*/

package IPOS.SA.ACC;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

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

     // Constructor that creates the catalogue window and loads the GUI
    public Catalogue() {
        setTitle("IPOS - Catalogue");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1100, 650);
        setLocationRelativeTo(null);

        initialiseSampleData();
        initialiseGui();
        updateTableForSelectedRole();
    }
     // Builds the graphical interface including panels, buttons and table
    private void initialiseGui() {
        Color backgroundColor = new Color(43, 45, 48);
        Color panelColor = new Color(60, 63, 65);
        Color accentColor = new Color(75, 110, 175);
        Color textColor = new Color(230, 230, 230);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(backgroundColor);
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        setContentPane(mainPanel);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(panelColor);
        headerPanel.setBorder(new EmptyBorder(12, 15, 12, 15));

        JLabel titleLabel = new JLabel("Catalogue");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("Week 1 prototype - sample catalogue view");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        subtitleLabel.setForeground(new Color(210, 210, 210));

        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setBackground(panelColor);
        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);

        headerPanel.add(titlePanel, BorderLayout.WEST);

        JPanel topControlsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        topControlsPanel.setBackground(panelColor);

        JLabel roleLabel = new JLabel("Role:");
        roleLabel.setForeground(textColor);

        roleComboBox = new JComboBox<>(new String[]{"Admin", "Merchant", "Manager"});
        roleComboBox.addActionListener(e -> updateTableForSelectedRole());

        topControlsPanel.add(roleLabel);
        topControlsPanel.add(roleComboBox);

        headerPanel.add(topControlsPanel, BorderLayout.EAST);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel centrePanel = new JPanel(new BorderLayout(10, 10));
        centrePanel.setBackground(backgroundColor);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.setBackground(panelColor);

        JLabel searchLabel = new JLabel("Search by Item ID or Keyword:");
        searchLabel.setForeground(textColor);

        searchField = new JTextField(20);

        searchButton = new JButton("Search");
        refreshButton = new JButton("Refresh");

        searchButton.addActionListener(e -> searchCatalogue());
        refreshButton.addActionListener(e -> {
            searchField.setText("");
            updateTableForSelectedRole();
        });

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(refreshButton);

        centrePanel.add(searchPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel();
        catalogueTable = new JTable(tableModel);
        catalogueTable.setRowHeight(24);
        catalogueTable.setFillsViewportHeight(true);
        catalogueTable.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(catalogueTable);
        centrePanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(centrePanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(panelColor);
        bottomPanel.setBorder(new EmptyBorder(10, 12, 10, 12));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonPanel.setBackground(panelColor);

        addButton = new JButton("Add Item");
        updateButton = new JButton("Update Item");
        deleteButton = new JButton("Delete Item");

        addButton.addActionListener(e -> showSimpleMessage("Add item will be completed in Week 2."));
        updateButton.addActionListener(e -> showSimpleMessage("Update item will be completed in Week 2."));
        deleteButton.addActionListener(e -> showSimpleMessage("Delete item will be completed in Week 2."));

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);

        statusLabel = new JLabel("Ready");
        statusLabel.setForeground(textColor);

        bottomPanel.add(buttonPanel, BorderLayout.WEST);
        bottomPanel.add(statusLabel, BorderLayout.EAST);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        styleButtons(accentColor);
    }

    private void styleButtons(Color accentColor) {
        JButton[] buttons = {searchButton, refreshButton, addButton, updateButton, deleteButton};

        for (JButton button : buttons) {
            button.setBackground(accentColor);
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);
        }
    }

     // For Week 1 the catalogue data is added manually.
// In a full system this could be loaded from a database or file.

    private void initialiseSampleData() {
        Items.add(new CatalogueItem("100 00001", "Paracetamol", "box", "Caps", 20, 0.10, 10345, 300));
        Items.add(new CatalogueItem("100 00002", "Aspirin", "box", "Caps", 20, 0.50, 12453, 500));
        Items.add(new CatalogueItem("100 00003", "Analgin", "box", "Caps", 10, 1.20, 4235, 200));
        Items.add(new CatalogueItem("100 00004", "Celebrex, caps 100 mg", "box", "Caps", 10, 10.00, 3420, 200));
        Items.add(new CatalogueItem("100 00005", "Celebrex, caps 200 mg", "box", "Caps", 10, 18.50, 1450, 150));
        Items.add(new CatalogueItem("100 00006", "Retin-A Tretin, 30 g", "box", "Caps", 20, 25.00, 2013, 200));
        Items.add(new CatalogueItem("100 00007", "Lipitor TB, 20 mg", "box", "Caps", 30, 15.50, 1562, 200));
        Items.add(new CatalogueItem("100 00008", "Claritin CR, 60g", "box", "Caps", 20, 19.50, 2540, 200));
        Items.add(new CatalogueItem("200 00004", "Iodine tincture", "bottle", "ml", 100, 0.30, 22134, 200));
        Items.add(new CatalogueItem("200 00005", "Rhynol", "bottle", "ml", 200, 2.50, 1908, 300));
        Items.add(new CatalogueItem("300 00001", "Ospen", "box", "Caps", 20, 10.50, 809, 200));
        Items.add(new CatalogueItem("300 00002", "Amopen", "box", "Caps", 30, 15.00, 1340, 300));
        Items.add(new CatalogueItem("400 00001", "Vitamin C", "box", "Caps", 30, 1.20, 3258, 300));
        Items.add(new CatalogueItem("400 00002", "Vitamin B12", "box", "Caps", 30, 1.30, 2673, 300));
    }

     // Updates the catalogue table depending on which role is selected
    private void updateTableForSelectedRole() {
        String role = roleComboBox.getSelectedItem().toString();

        if (role.equals("Admin")) {
            setAdminView();
        } else if (role.equals("Merchant")) {
            setMerchantView();
        } else {
            setManagerView();
        }
    }
     // Admin users can see the full catalogue including stock limits
    private void setAdminView() {
        String[] columns = {
                "Item ID", "Description", "Package Type", "Unit",
                "Units in a pack", "Package Cost (£)", "Availability (packs)", "Stock Limit (packs)"
        };

        buildTable(columns, true, true, true, Items);
        statusLabel.setText("Admin view: full catalogue access");
    }
     // Merchants can see catalogue items but stock limit is hidden
    private void setMerchantView() {
        String[] columns = {
                "Item ID", "Description", "Package Type", "Unit",
                "Units in a pack", "Package Cost (£)", "Availability (packs)"
        };

        buildTable(columns, false, false, false, Items);
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

        statusLabel.setText("Manager view: no catalogue maintenance access");
    }

    private void buildTable(String[] columns, boolean canAdd, boolean canUpdate, boolean canDelete, List<CatalogueItem> items) {
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
// TODO: implement add/update/delete catalogue items in later weeks
        addButton.setEnabled(canAdd);
        updateButton.setEnabled(canUpdate);
        deleteButton.setEnabled(canDelete);
    }
     // Searches catalogue items by item ID or desciption keyword
    private void searchCatalogue() {
        String role = roleComboBox.getSelectedItem().toString();
        String searchText = searchField.getText().trim().toLowerCase();

        if (role.equals("Manager")) {
            showSimpleMessage("Manager role does not search catalogue in this prototype.");
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
            buildTable(columns, true, true, true, filteredItems);
        } else {
            String[] columns = {
                    "Item ID", "Description", "Package Type", "Unit",
                    "Units in a pack", "Package Cost (£)", "Availability (packs)"
            };
            buildTable(columns, false, false, false, filteredItems);
        }

        statusLabel.setText(filteredItems.size() + " item(s) found");
    }

    private void showSimpleMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }
     // Main method obviously used to launch the catalogue window for testing
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Catalogue catalogue = new Catalogue();
            catalogue.setVisible(true);
        });
    }
}