package IPOS.SA.ACC.UI;

import IPOS.SA.ACC.Model.Staff;
import IPOS.SA.ACC.Service.StaffService;
import IPOS.SA.UI.AppFrame;
import IPOS.SA.UI.BaseFrame;
import IPOS.SA.UI.Refreshable;
import IPOS.SA.UI.ScreenRouter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Screen that displays a searchable table of all staff accounts.
 * Allows administrators to view, search and navigate to staff account management.
 * Inactive staff accounts are highlighted in light red for quick identification.
 */
public class StaffList extends BaseFrame implements Refreshable {

    private final StaffService staffService;

    private JTable staffTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JLabel statusLabel;

    /**
     * Constructor — builds the staff list screen and loads all staff records.
     *
     * @param fullname the full name of the logged-in user
     * @param role the role of the logged-in user
     * @param router he screen router used for navigation
     */
    public StaffList(String fullname, String role, ScreenRouter router) {
        super(fullname, role, "Staff List", router);
        this.staffService = new StaffService();
        buildContent();
        loadStaffData();
    }

    /**
     * Returns the title displayed in the page header.
     *
     * @return the header title string
     */
    @Override
    protected String getHeaderTitle() {
        return "Staff List";
    }

    /**
     * Builds and arranges all UI components for this screen.
     * Includes a search bar, staff table and a role-sensitive button panel.
     * Create Account and Manage Staff buttons are only shown to Administrators.
     */
    private void buildContent() {
        CenterPanel.setLayout(new BorderLayout(0, 0));
        CenterPanel.setBackground(new Color(245, 247, 250));

        // creates a Top bar with search controls
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(17, 24, 39));
        topBar.setBorder(new EmptyBorder(10, 16, 10, 16));

        // Adds a panel for the search button
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchPanel.setBackground(new Color(17, 24, 39));

        // Adds a label to the search bar
        JLabel searchLabel = new JLabel("Search by Staff ID, Name, or Username:");
        searchLabel.setForeground(Color.WHITE);
        searchLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        // textfield stores the search value to use for the search
        searchField = new JTextField(20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        // Adds buttons to perform the search
        JButton searchButton  = new JButton("Search");
        JButton refreshButton = new JButton("Refresh");
        styleBtn(searchButton);
        styleBtn(refreshButton);

        // Adds functionality to the search button to identify the requested record, and display it on it's own
        searchButton.addActionListener(e  -> searchStaff());
        refreshButton.addActionListener(e -> {
            searchField.setText("");
            loadStaffData();
        });

        // All the search buttons and text fields are added to the search panel at the top fo the form
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(refreshButton);
        topBar.add(searchPanel, BorderLayout.WEST);

        // creates a Table setup – cells are not editable
        String[] columns = {
                "Staff ID", "Username", "First Name", "Surname",
                "Email", "Phone", "Role", "Status"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };

        // A new table is created to store all of the staff records from the database
        staffTable = new JTable(tableModel);
        staffTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        staffTable.setRowHeight(30);
        staffTable.setShowGrid(false);
        staffTable.setFillsViewportHeight(true);
        staffTable.getTableHeader().setReorderingAllowed(false);
        staffTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        staffTable.getTableHeader().setBackground(new Color(17, 24, 39));
        staffTable.getTableHeader().setForeground(Color.WHITE);

        // Colour codes inactive staff
        staffTable.setDefaultRenderer(Object.class,
                new javax.swing.table.DefaultTableCellRenderer() {
                    public Component getTableCellRendererComponent(JTable t, Object val,
                                                                   boolean sel, boolean foc, int row, int col) {
                        Component c = super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                        if (!sel) {
                            String status = tableModel.getValueAt(row, 7).toString();
                            c.setBackground(status.equals("Inactive")
                                    ? new Color(255, 240, 240) : Color.WHITE);
                        }
                        return c;
                    }
                }
        );

        // Sroll pane to scroll the table
        JScrollPane scroll = new JScrollPane(staffTable);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        // creates a bottom panel with action buttons and status label
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(17, 24, 39));
        bottomPanel.setBorder(new EmptyBorder(10, 12, 10, 12));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonPanel.setBackground(new Color(17, 24, 39));

        statusLabel = new JLabel("Ready");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        statusLabel.setForeground(Color.WHITE);

        bottomPanel.add(buttonPanel,  BorderLayout.WEST);
        bottomPanel.add(statusLabel,  BorderLayout.EAST);

        CenterPanel.add(topBar,      BorderLayout.NORTH);
        CenterPanel.add(scroll,      BorderLayout.CENTER);
        CenterPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Only admin has the permission to create and manage new staff accounts
        if (role.equals("Administrator")) {
            JButton createButton = new JButton("Create Account");
            styleBtn(createButton);
            createButton.addActionListener(e -> router.goTo(AppFrame.SCREEN_STAFF_ACCOUNT_CREATE));
            buttonPanel.add(createButton);

            JButton viewDetailsButton = new JButton("Manage Staff");
            styleBtn(viewDetailsButton);

            // Stores the selected staff ID in AppFrame and navigate to manage screen
            viewDetailsButton.addActionListener(e -> {
                int row = staffTable.getSelectedRow();
                if (row >= 0) {
                    String staffId = tableModel.getValueAt(row, 0).toString();
                    AppFrame.getInstance().setSelectedMerchant(staffId);
                    router.goTo(AppFrame.SCREEN_STAFF_ACCOUNT_MANAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Please select a staff member to view details.");
                }
            });

            buttonPanel.add(viewDetailsButton);

        }
    }


    /**
     * Loads all staff records from the database and populates the table.
     * Displays active status as "Active" or "Inactive" in the Status column.
     */
    private void loadStaffData() {
        try {
            List<Staff> staffList = staffService.getStaffList();
            tableModel.setRowCount(0);

            // reads each staff in the staff list and populates the table with details of the staff
            for (Staff s : staffList) {
                tableModel.addRow(new Object[]{
                        s.getStaffId(),
                        s.getUsername(),
                        s.getFirstName(),
                        s.getSurName(),
                        s.getEmail(),
                        s.getPhone(),
                        s.getRole(),
                        s.isActive() ? "Active" : "Inactive"
                });
            }
            statusLabel.setText(staffList.size() + " staff members loaded");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading staff data: " + ex.getMessage());
            statusLabel.setText("Error loading data");
        }
    }

    /**
     * Filters the staff table by the value entered in the search field.
     * Matches against staff ID, username, first name, surname and email.
     * Reloads all data if the search field is empty.
     */
    private void searchStaff() {
        String search = searchField.getText().trim().toLowerCase();
        if (search.isEmpty()) { loadStaffData(); return; }

        try {
            List<Staff> all = staffService.getStaffList();
            List<Staff> filtered = new ArrayList<>();

            // iterates through all the records in the table adding the ones that meet the search critiera to a seperate list
            for (Staff s : all) {
                if (s.getStaffId().toLowerCase().contains(search)   ||
                        s.getUsername().toLowerCase().contains(search)   ||
                        s.getFirstName().toLowerCase().contains(search)  ||
                        s.getSurName().toLowerCase().contains(search)    ||
                        s.getEmail().toLowerCase().contains(search)) {
                    filtered.add(s);
                }
            }

            // the table is rest, and the filered list is iterated, and each record in that list is displayed on the table, creating the appearance of a filtered list
            tableModel.setRowCount(0);
            for (Staff s : filtered) {
                // each row is populated with the staff's's details retrieved from the merchant account
                tableModel.addRow(new Object[]{
                        s.getStaffId(),
                        s.getUsername(),
                        s.getFirstName(),
                        s.getSurName(),
                        s.getEmail(),
                        s.getPhone(),
                        s.getRole(),
                        s.isActive() ? "Active" : "Inactive"
                });
            }
            statusLabel.setText(filtered.size() + " staff member(s) found");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error searching staff: " + ex.getMessage());
        }
    }

    /**
     * Applies a shared visual style to an action button.
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
     * Clears the search field and reloads all merchant data.
     */
    @Override
    public void onShow() {
        searchField.setText("");
        loadStaffData();
    }
}
