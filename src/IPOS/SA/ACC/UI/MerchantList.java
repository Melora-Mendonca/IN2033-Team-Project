package IPOS.SA.ACC.UI;

import IPOS.SA.ACC.Model.MerchantAccount;
import IPOS.SA.ACC.Service.AccountService;
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
 * UI screen that displays a searchable table of all merchant accounts.
 * Supports three caller contexts: DEFAULT (account management), ORDERS, and INVOICES,
 * each of which adjusts the available action buttons and screen title accordingly.
 */
public class MerchantList extends BaseFrame implements Refreshable {

    private final AccountService accountService;
    private JTable merchantTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JLabel statusLabel;
    private final String callerContext;

    /**
     * Constructs a MerchantList screen with the DEFAULT caller context.
     *
     * @param fullname the display name of the logged-in user
     * @param role the role of the logged-in user
     * @param router the screen router used for navigation
     */
    public MerchantList(String fullname, String role, ScreenRouter router) {
        this(fullname, role, "DEFAULT", router);
    }

    /**
     * Constructs a MerchantList screen with a specific caller context.
     *
     * @param fullname the display name of the logged-in user
     * @param role the role of the logged-in user
     * @param callerContext the context that opened this screen: "DEFAULT", "ORDERS", or "INVOICES"
     * @param router the screen router used for navigation
     */
    public MerchantList(String fullname, String role, String callerContext, ScreenRouter router) {
        super(fullname, role, getTitleForContext(callerContext), router);
        this.accountService = new AccountService();
        this.callerContext = callerContext; // stores the context, of which class called the list and for what purpose
        buildContent(); // creates the main form for the GUI
        loadMerchantData(); // Loads the merchant records from the database
    }

    /**
     * Returns the screen title based on the caller context.
     *
     * @param callerContext the context that opened this screen
     * @return a title string appropriate for the context
     */
    private static String getTitleForContext(String callerContext) {
        switch (callerContext) {
            case "ORDERS": return "Select Merchant - View Orders";
            case "INVOICES": return "Select Merchant - View Invoices";
            default: return "Merchant List";
        }
    }

    /**
     * Returns the header title displayed at the top of the screen.
     *
     * @return the header title string
     */
    @Override
    protected String getHeaderTitle() {
        return "Merchant List";
    }

    /**
     * Builds and arranges all UI components for this screen.
     * Includes a search bar, merchant table, and a context-sensitive button panel.
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
        JLabel searchLabel = new JLabel("Search by Merchant ID or Business Name:");
        searchLabel.setForeground(Color.WHITE);
        searchLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        // textfield stores the search value to use for the search
        searchField = new JTextField(20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        // Adds buttons to perform the search
        JButton searchButton = new JButton("Search");
        JButton refreshButton = new JButton("Refresh");
        styleBtn(searchButton);
        styleBtn(refreshButton);

        // Adds functionality to the search button to identify the requested record, and display it on it's own
        searchButton.addActionListener(e -> searchMerchants());
        refreshButton.addActionListener(e -> {
            searchField.setText("");
            loadMerchantData();
        });

        // All the search buttons and text fields are added to the search panel at the top fo the form
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(refreshButton);
        topBar.add(searchPanel, BorderLayout.WEST);

        // creates a Table setup – cells are not editable
        String[] columns = {"Merchant ID", "Business Name", "Email", "Phone", "Credit Limit", "Balance", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };

        // A new table is created to store all of the merchant records from the database
        merchantTable = new JTable(tableModel);
        merchantTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        merchantTable.setRowHeight(30);
        merchantTable.setShowGrid(false);
        merchantTable.setFillsViewportHeight(true);
        merchantTable.getTableHeader().setReorderingAllowed(false);
        merchantTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        merchantTable.getTableHeader().setBackground(new Color(17, 24, 39));
        merchantTable.getTableHeader().setForeground(Color.WHITE);

        // reads the Status column for each row from the database and colours the whole row
        // light red for suspended accounts and darker red for in default accounts. Normal accounts stay white.
        merchantTable.setDefaultRenderer(Object.class,
                new javax.swing.table.DefaultTableCellRenderer() {
                    public Component getTableCellRendererComponent(JTable t, Object val,
                                                                   boolean sel, boolean foc, int row, int col) {
                        Component c = super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                        if (!sel) {
                            String status = tableModel.getValueAt(row, 6).toString();
                            if (status.equalsIgnoreCase("suspended")) {
                                c.setBackground(new Color(255, 240, 240));
                            } else if (status.equalsIgnoreCase("in_default")) {
                                c.setBackground(new Color(255, 220, 220));
                            } else {
                                c.setBackground(Color.WHITE);
                            }
                        }
                        return c;
                    }
                });

        // Sroll pane to scroll the table
        JScrollPane scroll = new JScrollPane(merchantTable);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        // creates a bottom panel with action buttons and status label
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(17, 24, 39));
        bottomPanel.setBorder(new EmptyBorder(10, 12, 10, 12));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonPanel.setBackground(new Color(17, 24, 39));

        // the buttons vary depending on which screen opened this list
        if ("DEFAULT".equals(callerContext)) {
            // View Details, View Orders, View Invoices, Create Account buttons
            JButton viewDetailsButton = new JButton("View Details");
            JButton viewOrdersButton = new JButton("View Orders");
            JButton viewInvoicesButton = new JButton("View Invoices");

            styleBtn(viewDetailsButton);
            styleBtn(viewOrdersButton);
            styleBtn(viewInvoicesButton);

            // Button opens up the account management page with pre-loaded merchant data
            viewDetailsButton.addActionListener(e -> {
                int row = merchantTable.getSelectedRow();
                if (row >= 0) {
                    String merchantId = tableModel.getValueAt(row, 0).toString();
                    AppFrame.getInstance().setSelectedMerchant(merchantId);
                    router.goTo(AppFrame.SCREEN_ACCOUNT_MANAGEMENT_MANAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Please select a merchant.");
                }
            });

            // Button opens up the order management page pre-filtered to the requesting merchant's orders
            viewOrdersButton.addActionListener(e -> {
                int row = merchantTable.getSelectedRow();
                if (row >= 0) {
                    String merchantId = tableModel.getValueAt(row, 0).toString();
                    AppFrame.getInstance().setSelectedMerchant(merchantId);
                    router.goTo(AppFrame.SCREEN_ORDER_MANAGEMENT);
                } else {
                    JOptionPane.showMessageDialog(this, "Please select a merchant.");
                }
            });

            // Button opens up the invoice list pre-filtered by merchant ID for the merchant's invoices
            viewInvoicesButton.addActionListener(e -> {
                int row = merchantTable.getSelectedRow();
                if (row >= 0) {
                    String merchantId = tableModel.getValueAt(row, 0).toString();
                    AppFrame.getInstance().setSelectedMerchant(merchantId);
                    router.goTo(AppFrame.SCREEN_INVOICE_LIST);
                } else {
                    JOptionPane.showMessageDialog(this, "Please select a merchant.");
                }
            });

            buttonPanel.add(viewDetailsButton);
            buttonPanel.add(viewOrdersButton);
            buttonPanel.add(viewInvoicesButton);

            // Create Account button is restricted to Administrators
            if (role.equals("Administrator")) {
                JButton createButton = new JButton("Create Account");
                styleBtn(createButton);
                createButton.addActionListener(e -> router.goTo(AppFrame.SCREEN_ACCOUNT_MANAGEMENT_CREATE));
                buttonPanel.add(createButton);
            }

        } else if ("ORDERS".equals(callerContext)) {
            // View Orders button + Back button + instruction label
            JButton selectButton = new JButton("View Orders");
            JButton backButton = new JButton("← Back");

            styleBtn(selectButton);
            styleBtn(backButton);

            // Button opens up the order management page pre-filtered to the merchant's orders
            selectButton.addActionListener(e -> {
                int row = merchantTable.getSelectedRow();
                if (row >= 0) {
                    String merchantId = tableModel.getValueAt(row, 0).toString();
                    AppFrame.getInstance().setSelectedMerchant(merchantId);
                    router.goTo(AppFrame.SCREEN_ORDER_MANAGEMENT);
                }
            });

            // Button returns to the dashboard
            backButton.addActionListener(e -> router.goTo(AppFrame.SCREEN_ADMIN_DASHBOARD));

            buttonPanel.add(selectButton);
            buttonPanel.add(backButton);

            // Creates a small instruction label in the button panel to instruct the user
            JLabel instructionLabel = new JLabel("Select a merchant to view their orders");
            instructionLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
            instructionLabel.setForeground(new Color(200, 200, 200));
            bottomPanel.add(instructionLabel, BorderLayout.CENTER);

        } else if ("INVOICES".equals(callerContext)) {
            // View Invoices button + Back button + instruction label
            JButton selectButton = new JButton("View Invoices");
            JButton backButton = new JButton("← Back");

            styleBtn(selectButton);
            styleBtn(backButton);

            // Button opens up the invoice list, pre-filtered to the merchant's invoices
            selectButton.addActionListener(e -> {
                int row = merchantTable.getSelectedRow();
                if (row >= 0) {
                    String merchantId = tableModel.getValueAt(row, 0).toString();
                    AppFrame.getInstance().setSelectedMerchant(merchantId);
                    router.goTo(AppFrame.SCREEN_INVOICE_LIST);
                } else {
                    JOptionPane.showMessageDialog(this, "Please select a merchant.");
                }
            });
            // button returns the user back to the dashboard page
            backButton.addActionListener(e -> router.goTo(AppFrame.SCREEN_ADMIN_DASHBOARD));
            buttonPanel.add(selectButton);
            buttonPanel.add(backButton);

            // Creates a small instruction label in the button panel to instruct the user
            JLabel instructionLabel = new JLabel("Select a merchant to view their invoices");
            instructionLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
            instructionLabel.setForeground(new Color(200, 200, 200));
            bottomPanel.add(instructionLabel, BorderLayout.CENTER);
        }

        statusLabel = new JLabel("Ready");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        statusLabel.setForeground(Color.WHITE);

        bottomPanel.add(buttonPanel, BorderLayout.WEST);
        bottomPanel.add(statusLabel, BorderLayout.EAST);

        CenterPanel.add(topBar, BorderLayout.NORTH);
        CenterPanel.add(scroll, BorderLayout.CENTER);
        CenterPanel.add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * Loads all merchant accounts from the database and populates the table.
     * Marks a merchant as OVERDUE if their balance exceeds their credit limit.
     */
    private void loadMerchantData() {
        try {
            List<MerchantAccount> merchants = accountService.getAllAccounts();
            tableModel.setRowCount(0);

            // each account in the list of merchants is iterated through and statuses and balances are updated
            for (MerchantAccount m : merchants) {
                String status = m.getStatus().toString();
                if (m.getOutstandingBalance() > m.getCreditLimit()) status = "OVERDUE";

                // each row is populated with the merchant's details retrieved from the merchant account
                tableModel.addRow(new Object[]{
                        m.getMerchantId(),
                        m.getBusinessName(),
                        m.getEmail(),
                        m.getPhone(),
                        String.format("£%.2f", m.getCreditLimit()),
                        String.format("£%.2f", m.getOutstandingBalance()),
                        status
                });
            }
            statusLabel.setText(merchants.size() + " merchants loaded");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading merchant data: " + ex.getMessage());
            statusLabel.setText("Error loading data");
        }
    }

    /**
     * Filters the merchant table by the value in the search field.
     * Matches against merchant ID, business name, and email.
     * Reloads all data if the search field is empty.
     */
    private void searchMerchants() {
        String search = searchField.getText().trim().toLowerCase();
        if (search.isEmpty()) { loadMerchantData(); return; }

        try {
            List<MerchantAccount> all = accountService.getAllAccounts();
            List<MerchantAccount> filtered = new ArrayList<>();

            // iterates through all the records in the table adding the ones that meet the search critiera to a seperate list
            for (MerchantAccount m : all) {
                if (m.getMerchantId().toLowerCase().contains(search) ||
                        m.getBusinessName().toLowerCase().contains(search) ||
                        m.getEmail().toLowerCase().contains(search)) {
                    filtered.add(m);
                }
            }

            // the table is rest, and the filered list is iterated, and each record in that list is displayed on the table, creating the appearance of a filtered list
            tableModel.setRowCount(0);
            for (MerchantAccount m : filtered) {
                String status = m.getStatus().toString();
                if (m.getOutstandingBalance() > m.getCreditLimit()) status = "OVERDUE";

                // each row is populated with the merchant's details retrieved from the merchant account
                tableModel.addRow(new Object[]{
                        m.getMerchantId(),
                        m.getBusinessName(),
                        m.getEmail(),
                        m.getPhone(),
                        String.format("£%.2f", m.getCreditLimit()),
                        String.format("£%.2f", m.getOutstandingBalance()),
                        status
                });
            }
            statusLabel.setText(filtered.size() + " merchant(s) found");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error searching: " + ex.getMessage());
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
        loadMerchantData();
    }
}