package IPOS.SA.CAT.UI;

import IPOS.SA.ACC.UI.AdminDashboard;
import IPOS.SA.ACC.UI.LoginForm;
import IPOS.SA.CAT.Model.CatalogueItem;
import IPOS.SA.CAT.Service.catalogueService;


import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;


public class ManageItem extends JFrame {
    private final catalogueService service;
    private final String fullname;
    private final String role;
    private final String mode;
    private final int currentUserId; // You'll need to pass this from login

    // GUI Components
    private JTextField itemIdField;
    private JTextField descriptionField;
    private JTextField unitField;
    private JTextField costField;
    private JTextField packageField;
    private JTextField units_per_packField;
    private JTextField availabilityField;
    private JTextField stock_limitField;
    private JTextField quantityField;

    private JPanel MainPanel;
    private JPanel NavPanel;
    private JPanel ContentPanel;
    private JPanel HeaderPanel;
    private JPanel CenterPanel;
    private JPanel FormPanel;
    private JPanel FooterPanel;
    private JLabel headerLabel;
    private JLabel navIcon;
    private JButton logoutBtn;
    private JSeparator divider;
    private JLabel messageLabel;

    public ManageItem(String fullname, String role, String mode) {
        this.service = new catalogueService();
        this.fullname = fullname;
        this.role = role;
        this.mode = mode;
        this.currentUserId = 1;

        setTitle(getTitleForMode());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setContentPane(MainPanel);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);

        initializeUI();

        setVisible(true);
    }

    private String getTitleForMode() {
        switch (mode) {
            case "ADD":
                return "Add New Catalogue Item";
            case "EDIT":
                return "Update Catalogue Item";
            case "DELETE":
                return "Deactivate Catalogue Item";
            case "DELIVERY":
                return "Record Stock Delivery";
            default:
                return "Manage Catalogue Item";
        }
    }

    private String getFormTitleForMode() {
        switch (mode) {
            case "ADD":
                return "NEW ITEM DETAILS";
            case "EDIT":
                return "EDIT ITEM DETAILS";
            case "DELETE":
                return "DEACTIVATE ITEM";
            case "DELIVERY":
                return "RECORD DELIVERY";
            default:
                return "ITEM DETAILS";
        }
    }

    private void initializeUI() {
        createHeaderPanel();
        createNavPanel();
        createCenterPanel();
        createActionsPanel();
    }

    private void createHeaderPanel() {
        HeaderPanel.setLayout(new BoxLayout(HeaderPanel, BoxLayout.X_AXIS));
        HeaderPanel.setPreferredSize(new Dimension(1000, 54));
        HeaderPanel.setBackground(new Color(240, 252, 255));
        HeaderPanel.setBorder(BorderFactory.createEmptyBorder(0, 24, 0, 24));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        headerLabel = new JLabel(getTitleForMode());
        headerLabel.setForeground(Color.BLACK);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));

        textPanel.add(headerLabel);
        HeaderPanel.add(textPanel);
    }

    private void createNavPanel() {
        NavPanel.setLayout(new BoxLayout(NavPanel, BoxLayout.Y_AXIS));
        NavPanel.setBackground(new Color(14, 37, 48));
        NavPanel.setBorder(BorderFactory.createEmptyBorder(20, 16, 20, 16));

        ImageIcon Icon = new ImageIcon(new ImageIcon("data/Logo.png")
                .getImage().getScaledInstance(80, 60, Image.SCALE_SMOOTH));
        navIcon = new JLabel(Icon);
        NavPanel.add(navIcon);
        NavPanel.add(Box.createVerticalStrut(16));

        NavPanel.add(buildNavButton("Overview", false));
        NavPanel.add(Box.createVerticalStrut(4));
        NavPanel.add(buildNavButton("Catalogue", true));
        NavPanel.add(Box.createVerticalStrut(4));
        NavPanel.add(buildNavButton("Orders", false));
        NavPanel.add(Box.createVerticalStrut(4));

        addExpandableNavItem(NavPanel, "Merchants", new String[]{"View Merchant Orders", "View Merchant Invoices"});
        addExpandableNavItem(NavPanel, "Accounts", new String[]{"Create Merchant Account", "Manage Merchant Accounts", "Commercial Applications"});
        addExpandableNavItem(NavPanel, "Staff", new String[]{"View All Staff", "Create Staff Account", "Manage Staff Account"});

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
        CenterPanel.setLayout(new BorderLayout(0, 0));
        CenterPanel.setBackground(new Color(245, 247, 250));

        FormPanel.setLayout(new BorderLayout(0, 0));
        FormPanel.setBackground(Color.WHITE);
        FormPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(221, 225, 231), 1),
                new EmptyBorder(20, 20, 20, 20)));

        JLabel formTitle = new JLabel(getFormTitleForMode());
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formTitle.setForeground(Color.BLACK);
        formTitle.setBorder(new EmptyBorder(0, 0, 12, 0));

        JPanel grid = new JPanel();
        grid.setLayout(new BoxLayout(grid, BoxLayout.Y_AXIS));
        grid.setBackground(Color.WHITE);

        // Create all fields
        itemIdField = createField();
        descriptionField = createField();
        packageField = createField();
        unitField = createField();
        units_per_packField = createField();
        costField = createField();
        availabilityField = createField();
        stock_limitField = createField();
        quantityField = createField();

        // Row 1 - Item ID
        JPanel row0 = new JPanel(new GridLayout(1, 1));
        row0.setBackground(Color.WHITE);
        row0.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        row0.add(fieldWrapper("ITEM ID", itemIdField));
        grid.add(row0);
        grid.add(Box.createVerticalStrut(12));

        // Show different fields based on mode
        switch (mode) {
            case "ADD":
            case "EDIT":
                addEditFields(grid);
                break;
            case "DELETE":
                addDeleteWarning(grid);
                break;
            case "DELIVERY":
                addDeliveryFields(grid);
                break;
        }

        messageLabel = new JLabel(" ");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        messageLabel.setBorder(new EmptyBorder(8, 0, 0, 0));

        FormPanel.add(formTitle, BorderLayout.NORTH);
        FormPanel.add(grid, BorderLayout.CENTER);
        FormPanel.add(messageLabel, BorderLayout.SOUTH);

        JPanel northWrapper = new JPanel(new BorderLayout());
        northWrapper.setBackground(new Color(245, 247, 250));
        northWrapper.add(FormPanel, BorderLayout.CENTER);

        CenterPanel.add(northWrapper, BorderLayout.CENTER);
    }

    private void addEditFields(JPanel grid) {
        JPanel row1 = new JPanel(new GridLayout(1, 2, 12, 0));
        row1.setBackground(Color.WHITE);
        row1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        row1.setPreferredSize(new Dimension(0, 60));
        row1.add(fieldWrapper("DESCRIPTION", descriptionField));
        row1.add(fieldWrapper("PACKAGE TYPE", packageField));

        JPanel row2 = new JPanel(new GridLayout(1, 2, 12, 0));
        row2.setBackground(Color.WHITE);
        row2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        row2.setPreferredSize(new Dimension(0, 60));
        row2.add(fieldWrapper("UNIT", unitField));
        row2.add(fieldWrapper("UNITS PER PACK", units_per_packField));

        JPanel row3 = new JPanel(new GridLayout(1, 2, 12, 0));
        row3.setBackground(Color.WHITE);
        row3.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        row3.setPreferredSize(new Dimension(0, 60));
        row3.add(fieldWrapper("PACKAGE COST (£)", costField));
        row3.add(fieldWrapper("AVAILABILITY", availabilityField));

        JPanel row4 = new JPanel(new GridLayout(1, 1));
        row4.setBackground(Color.WHITE);
        row4.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        row4.setPreferredSize(new Dimension(0, 60));
        row4.add(fieldWrapper("MINIMUM STOCK LIMIT", stock_limitField));

        grid.add(row1);
        grid.add(Box.createVerticalStrut(12));
        grid.add(row2);
        grid.add(Box.createVerticalStrut(12));
        grid.add(row3);
        grid.add(Box.createVerticalStrut(12));
        grid.add(row4);
    }

    private void addDeleteWarning(JPanel grid) {
        JLabel warning = new JLabel("Warning! This will deactivate the item from the catalogue.");
        warning.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        warning.setForeground(new Color(180, 30, 30));
        warning.setAlignmentX(Component.LEFT_ALIGNMENT);
        grid.add(warning);
    }

    private void addDeliveryFields(JPanel grid) {
        JPanel row1 = new JPanel(new GridLayout(1, 1));
        row1.setBackground(Color.WHITE);
        row1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        row1.add(fieldWrapper("QUANTITY TO ADD (packs)", quantityField));
        grid.add(row1);
    }

    private void createActionsPanel() {
        JPanel actionsPanel = new JPanel();
        actionsPanel.setLayout(new BoxLayout(actionsPanel, BoxLayout.Y_AXIS));
        actionsPanel.setBackground(new Color(245, 247, 250));
        actionsPanel.setPreferredSize(new Dimension(210, 0));
        actionsPanel.setBorder(BorderFactory.createEmptyBorder(20, 16, 20, 16));

        JLabel actionsTitle = new JLabel("ACTIONS");
        actionsTitle.setFont(new Font("Segoe UI", Font.BOLD, 11));
        actionsTitle.setForeground(new Color(107, 114, 128));
        actionsTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        actionsPanel.add(actionsTitle);
        actionsPanel.add(Box.createVerticalStrut(10));

        switch (mode) {
            case "ADD":
                addAddButtons(actionsPanel);
                break;
            case "EDIT":
                addEditButtons(actionsPanel);
                break;
            case "DELETE":
                addDeleteButtons(actionsPanel);
                break;
            case "DELIVERY":
                addDeliveryButtons(actionsPanel);
                break;
        }

        actionsPanel.add(Box.createVerticalStrut(8));
        JButton backBtn = actionButton("← Back to Catalogue", new Color(17, 24, 39));
        backBtn.addActionListener(e -> {
            dispose();
            new Catalogue(fullname, role);
        });
        actionsPanel.add(backBtn);

        ContentPanel.setLayout(new BorderLayout());
        ContentPanel.add(CenterPanel, BorderLayout.CENTER);
        ContentPanel.add(actionsPanel, BorderLayout.EAST);
    }

    private void addAddButtons(JPanel panel) {
        JButton addBtn = actionButton("Add Item", new Color(30, 70, 90));
        JButton clearBtn = actionButton("Clear", new Color(107, 114, 128));

        addBtn.addActionListener(e -> addItem());
        clearBtn.addActionListener(e -> clearForm());

        panel.add(addBtn);
        panel.add(Box.createVerticalStrut(8));
        panel.add(clearBtn);
    }

    private void addEditButtons(JPanel panel) {
        JButton loadBtn = actionButton("Load Item", new Color(17, 24, 39));
        JButton updateBtn = actionButton("Update Item", new Color(30, 70, 90));
        JButton clearBtn = actionButton("Clear", new Color(107, 114, 128));

        loadBtn.addActionListener(e -> loadItemForEdit());
        updateBtn.addActionListener(e -> updateItem());
        clearBtn.addActionListener(e -> clearForm());

        panel.add(loadBtn);
        panel.add(Box.createVerticalStrut(8));
        panel.add(updateBtn);
        panel.add(Box.createVerticalStrut(8));
        panel.add(clearBtn);
    }

    private void addDeleteButtons(JPanel panel) {
        JButton deactivateBtn = actionButton("Deactivate Item", new Color(127, 29, 29));
        JButton reactivateBtn = actionButton("Reactivate Item", new Color(20, 83, 45));
        JButton clearBtn = actionButton("Clear", new Color(107, 114, 128));

        deactivateBtn.addActionListener(e -> deactivateItem());
        reactivateBtn.addActionListener(e -> reactivateItem());
        clearBtn.addActionListener(e -> clearForm());

        panel.add(deactivateBtn);
        panel.add(Box.createVerticalStrut(8));
        panel.add(reactivateBtn);
        panel.add(Box.createVerticalStrut(8));
        panel.add(clearBtn);
    }

    private void addDeliveryButtons(JPanel panel) {
        JButton deliveryBtn = actionButton("Record Delivery", new Color(20, 83, 45));
        JButton clearBtn = actionButton("Clear", new Color(107, 114, 128));

        deliveryBtn.addActionListener(e -> recordDelivery());
        clearBtn.addActionListener(e -> clearForm());

        panel.add(deliveryBtn);
        panel.add(Box.createVerticalStrut(8));
        panel.add(clearBtn);
    }

    // Business Logic Methods (using the service layer)
    private void addItem() {
        try {
            if (!validateRequiredFields()) {
                return;
            }

            CatalogueItem item = createItemFromForm();

            if (service.saveItem(item)) {
                setMessage("Item added successfully.", true);
                clearForm();
            } else {
                setMessage("Item ID already exists.", false);
            }
        } catch (NumberFormatException ex) {
            setMessage("Please enter valid numbers for numeric fields.", false);
        } catch (Exception ex) {
            setMessage("Error: " + ex.getMessage(), false);
        }
    }

    private void loadItemForEdit() {
        String id = itemIdField.getText().trim();
        if (id.isEmpty()) {
            setMessage("Enter an Item ID to load.", false);
            return;
        }

        try {
            CatalogueItem item = service.loadItem(id);
            if (item != null) {
                populateForm(item);
                setMessage("Item loaded successfully.", true);
            } else {
                setMessage("Item ID not found or has been deactivated.", false);
            }
        } catch (Exception ex) {
            setMessage("Error: " + ex.getMessage(), false);
        }
    }

    private void loadItemForDelete() {
        String id = itemIdField.getText().trim();
        if (id.isEmpty()) {
            setMessage("Enter an Item ID to load.", false);
            return;
        }

        try {
            CatalogueItem item = service.loadItemIncludingInactive(id);
            if (item != null) {
                populateForm(item);
                boolean isActive = service.getItemActiveStatus(id);
                if (isActive) {
                    setMessage("Item loaded. Ready to deactivate.", true);
                } else {
                    setMessage("Item is already deactivated. You can reactivate it.", true);
                }
            } else {
                setMessage("Item ID not found.", false);
            }
        } catch (Exception ex) {
            setMessage("Error: " + ex.getMessage(), false);
        }
    }

    private void updateItem() {
        String id = itemIdField.getText().trim();
        if (id.isEmpty()) {
            setMessage("Load an item first.", false);
            return;
        }

        try {
            if (!service.isItemActive(id)) {
                setMessage("Cannot update a deactivated item.", false);
                return;
            }

            CatalogueItem item = createItemFromForm();

            if (service.updateItem(item)) {
                setMessage("Item updated successfully.", true);
            } else {
                setMessage("Failed to update item.", false);
            }
        } catch (NumberFormatException ex) {
            setMessage("Please enter valid numbers for numeric fields.", false);
        } catch (Exception ex) {
            setMessage("Error: " + ex.getMessage(), false);
        }
    }

    private void deactivateItem() {
        String id = itemIdField.getText().trim();

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to deactivate item " + id + "?",
                "Confirm Deactivation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (service.deactivateItem(id)) {
                    setMessage("Item deactivated successfully.", true);
                    clearForm();
                } else {
                    setMessage("Item not found.", false);
                }
            } catch (Exception ex) {
                setMessage("Error: " + ex.getMessage(), false);
            }
        }
    }

    private void reactivateItem() {
        String id = itemIdField.getText().trim();
        if (id.isEmpty()) {
            setMessage("Load an item first.", false);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to reactivate item " + id + "?",
                "Confirm Reactivation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (service.reactivateItem(id)) {
                    setMessage("Item reactivated successfully.", true);
                    clearForm();
                } else {
                    setMessage("Item not found.", false);
                }
            } catch (Exception ex) {
                setMessage("Error: " + ex.getMessage(), false);
            }
        }
    }

    private void recordDelivery() {
        String id = itemIdField.getText().trim();
        if (id.isEmpty()) {
            setMessage("Load an item first.", false);
            return;
        }

        if (quantityField.getText().trim().isEmpty()) {
            setMessage("Enter a quantity.", false);
            return;
        }

        try {
            int qty = Integer.parseInt(quantityField.getText().trim());

            if (service.recordDelivery(id, qty, currentUserId)) {
                setMessage("Delivery recorded. Stock updated by " + qty + " packs.", true);
                quantityField.setText("");

                // Reload to show updated stock
                CatalogueItem item = service.loadItem(id);
                if (item != null) {
                    availabilityField.setText(String.valueOf(item.getAvailabilityPacks()));
                }
            } else {
                setMessage("Failed to record delivery. Item may be inactive.", false);
            }
        } catch (NumberFormatException ex) {
            setMessage("Please enter a valid quantity.", false);
        } catch (Exception ex) {
            setMessage("Error: " + ex.getMessage(), false);
        }
    }

    // Helper Methods
    private CatalogueItem createItemFromForm() {
        return new CatalogueItem(
                itemIdField.getText().trim(),
                descriptionField.getText().trim(),
                packageField.getText().trim(),
                unitField.getText().trim(),
                Integer.parseInt(units_per_packField.getText().trim()),
                Double.parseDouble(costField.getText().trim()),
                Integer.parseInt(availabilityField.getText().trim()),
                Integer.parseInt(stock_limitField.getText().trim())
        );
    }

    private void populateForm(CatalogueItem item) {
        itemIdField.setText(item.getItemId());
        descriptionField.setText(item.getDescription());
        packageField.setText(item.getPackageType());
        unitField.setText(item.getUnit());
        units_per_packField.setText(String.valueOf(item.getUnitsInPack()));
        costField.setText(String.valueOf(item.getPackageCost()));
        availabilityField.setText(String.valueOf(item.getAvailabilityPacks()));
        stock_limitField.setText(String.valueOf(item.getStockLimitPacks()));
    }

    private boolean validateRequiredFields() {
        if (itemIdField.getText().trim().isEmpty()) {
            setMessage("Item ID is required.", false);
            return false;
        }
        if (descriptionField.getText().trim().isEmpty()) {
            setMessage("Description is required.", false);
            return false;
        }
        return true;
    }

    private void clearForm() {
        itemIdField.setText("");
        descriptionField.setText("");
        packageField.setText("");
        unitField.setText("");
        units_per_packField.setText("");
        costField.setText("");
        availabilityField.setText("");
        stock_limitField.setText("");
        quantityField.setText("");
        setMessage("", true);
    }

    private void setMessage(String text, boolean success) {
        messageLabel.setText(text);
        messageLabel.setForeground(success ? new Color(0, 97, 0) : new Color(200, 80, 80));
    }

    // UI Helper Methods
    private JTextField createField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(221, 225, 231)),
                BorderFactory.createEmptyBorder(0, 12, 0, 12)));
        return field;
    }

    private JPanel fieldWrapper(String label, JTextField field) {
        JPanel wrapper = new JPanel(new BorderLayout(0, 4));
        wrapper.setBackground(Color.WHITE);
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lbl.setForeground(new Color(107, 114, 128));
        wrapper.add(lbl, BorderLayout.NORTH);
        wrapper.add(field, BorderLayout.CENTER);
        return wrapper;
    }

    private JButton actionButton(String label, Color bg) {
        JButton btn = new JButton(label);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        return btn;
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
            switch (label) {
                case "Overview":
                    dispose();
                    new AdminDashboard(fullname, role);
                    break;
                case "Catalogue":
                    dispose();
                    new Catalogue(fullname, role);
                    break;
                default:
                    JOptionPane.showMessageDialog(this, label + " — coming soon.");
                    break;
            }
        });
        return btn;
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
            subBtn.addActionListener(e -> JOptionPane.showMessageDialog(this, sub + " — coming soon."));
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

    private void handleLogout() {
        dispose();
        new LoginForm();
    }
}
