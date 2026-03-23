package IPOS.SA.CAT;

import IPOS.SA.ACC.AdminDashboard;
import IPOS.SA.ACC.LoginForm;
import IPOS.SA.DB.DBConnection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.ResultSet;

public class ManageItem extends JFrame {
    private final String fullname;
    private final String role;
    private final String mode;

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
        this.fullname = fullname;
        this.role = role;
        this.mode = mode;

        setTitle(getTitleForMode());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setContentPane(MainPanel);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);

        createHeaderPanel();
        createNavPanel();
        createCenterPanel();
        createActionsPanel();

        setVisible(true);
    }

    // Returns the appropriate title based on mode
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

    // Returns the form section title based on mode
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

        NavPanel.add(buildNavButton("Overview",  false));
        NavPanel.add(Box.createVerticalStrut(4));
        NavPanel.add(buildNavButton("Catalogue", true));
        NavPanel.add(Box.createVerticalStrut(4));
        NavPanel.add(buildNavButton("Orders",    false));
        NavPanel.add(Box.createVerticalStrut(4));

        addExpandableNavItem(NavPanel, "Merchants", new String[]{"View Merchant Orders", "View Merchant Invoices"});
        addExpandableNavItem(NavPanel, "Accounts",  new String[]{"Create Merchant Account", "Manage Merchant Accounts", "Commercial Applications"});
        addExpandableNavItem(NavPanel, "Staff",     new String[]{"View All Staff", "Create Staff Account", "Manage Staff Account"});

        NavPanel.add(buildNavButton("Reports",  false));
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

        // Item ID is always shown
        itemIdField        = createField();
        descriptionField   = createField();
        packageField       = createField();
        unitField          = createField();
        units_per_packField = createField();
        costField          = createField();
        availabilityField  = createField();
        stock_limitField   = createField();
        quantityField      = createField();

        // Row 1 — Item ID always visible
        JPanel row0 = new JPanel(new GridLayout(1, 1));
        row0.setBackground(Color.WHITE);
        row0.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        row0.add(fieldWrapper("ITEM ID", itemIdField));
        grid.add(row0);
        grid.add(Box.createVerticalStrut(12));

        // Show different fields based on mode
        switch (mode) {
            case "ADD":
            case "UPDATE": {
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
                row4.add(fieldWrapper("STOCK LIMIT", stock_limitField));

                grid.add(row1); grid.add(Box.createVerticalStrut(12));
                grid.add(row2); grid.add(Box.createVerticalStrut(12));
                grid.add(row3); grid.add(Box.createVerticalStrut(12));
                grid.add(row4);
                break;
            }
            case "DELETE": {
                // Only item ID needed — show a warning label
                JLabel warning = new JLabel("Warning! This will deactivate the item from the catalogue.");
                warning.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                warning.setForeground(new Color(180, 30, 30));
                warning.setAlignmentX(Component.LEFT_ALIGNMENT);
                grid.add(warning);
                break;
            }
            case "DELIVERY": {
                JPanel row1 = new JPanel(new GridLayout(1, 1));
                row1.setBackground(Color.WHITE);
                row1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
                row1.add(fieldWrapper("QUANTITY TO ADD (packs)", quantityField));
                grid.add(row1);
                break;
            }
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

        System.out.println("Mode is: " + mode); // ← add this

        switch (mode) {
            case "ADD": {
                JButton addBtn   = actionButton("Add Item",  new Color(30, 70, 90));
                JButton clearBtn = actionButton("Clear", new Color(107, 114, 128));
                addBtn.addActionListener(e   -> saveItem());
                clearBtn.addActionListener(e -> clearForm());
                actionsPanel.add(addBtn);
                actionsPanel.add(Box.createVerticalStrut(8));
                actionsPanel.add(clearBtn);
                break;
            }
            case "UPDATE": {
                JButton loadBtn   = actionButton("Load Item", new Color(17, 24, 39));
                JButton updateBtn = actionButton("Update Item", new Color(30, 70, 90));
                JButton clearBtn  = actionButton("Clear", new Color(107, 114, 128));
                loadBtn.addActionListener(e   -> loadItem());
                updateBtn.addActionListener(e -> updateItem());
                clearBtn.addActionListener(e  -> clearForm());
                actionsPanel.add(loadBtn);
                actionsPanel.add(Box.createVerticalStrut(8));
                actionsPanel.add(updateBtn);
                actionsPanel.add(Box.createVerticalStrut(8));
                actionsPanel.add(clearBtn);
                break;
            }
            case "DELETE": {
                JButton loadBtn = actionButton("Load Item", new Color(17, 24, 39));
                JButton deactivateBtn = actionButton("Deactivate Item", new Color(127, 29, 29));
                JButton clearBtn = actionButton("Clear", new Color(107, 114, 128));
                loadBtn.addActionListener(e       -> loadItem());
                deactivateBtn.addActionListener(e -> deactivateItem());
                clearBtn.addActionListener(e      -> clearForm());
                actionsPanel.add(loadBtn);
                actionsPanel.add(Box.createVerticalStrut(8));
                actionsPanel.add(deactivateBtn);
                actionsPanel.add(Box.createVerticalStrut(8));
                actionsPanel.add(clearBtn);
                break;
            }
            case "DELIVERY": {
                JButton deliveryBtn = actionButton("Record Delivery", new Color(20, 83, 45));
                JButton clearBtn    = actionButton("Clear",           new Color(107, 114, 128));
                deliveryBtn.addActionListener(e -> recordDelivery());
                clearBtn.addActionListener(e    -> clearForm());
                actionsPanel.add(deliveryBtn); actionsPanel.add(Box.createVerticalStrut(8));
                actionsPanel.add(clearBtn);
                break;
            }
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

    private void loadItem() {
        String id = itemIdField.getText().trim();
        if (id.isEmpty()) {
            setMessage("Enter an Item ID to load.", false);
            return;
        }
        try {
            DBConnection db = new DBConnection();
            ResultSet rs = db.query("SELECT * FROM Catalogue WHERE item_id = ?", id);
            if (rs.next()) {
                descriptionField.setText(rs.getString("description"));
                packageField.setText(rs.getString("package_type"));
                unitField.setText(rs.getString("unit"));
                units_per_packField.setText(String.valueOf(rs.getInt("units_per_pack")));
                costField.setText(String.valueOf(rs.getDouble("package_cost")));
                availabilityField.setText(String.valueOf(rs.getInt("availability")));
                stock_limitField.setText(String.valueOf(rs.getInt("stock_limit")));
                setMessage("Item loaded successfully.", true);
            } else {
                setMessage("Item ID not found.", false);
            }
        } catch (Exception ex) {
            setMessage("Error: " + ex.getMessage(), false);
        }
    }

    private void saveItem() {
        try {
            if (itemIdField.getText().trim().isEmpty() || descriptionField.getText().trim().isEmpty()) {
                setMessage("Item ID and Description are required.", false);
                return;
            }
            DBConnection db = new DBConnection();
            db.update(
                    "INSERT INTO Catalogue (item_id, description, package_type, unit, units_per_pack, package_cost, availability, stock_limit, is_active) VALUES (?,?,?,?,?,?,?,?,1)",
                    itemIdField.getText().trim(),
                    descriptionField.getText().trim(),
                    packageField.getText().trim(),
                    unitField.getText().trim(),
                    Integer.parseInt(units_per_packField.getText().trim()),
                    Double.parseDouble(costField.getText().trim()),
                    Integer.parseInt(availabilityField.getText().trim()),
                    Integer.parseInt(stock_limitField.getText().trim())
            );
            setMessage("Item added successfully.", true);
            clearForm();
        } catch (NumberFormatException ex) {
            setMessage("Please enter valid numbers for numeric fields.", false);
        } catch (Exception ex) {
            setMessage("Error: " + ex.getMessage(), false);
        }
    }

    private void updateItem() {
        String id = itemIdField.getText().trim();
        if (id.isEmpty()) { setMessage("Load an item first.", false); return; }
        try {
            DBConnection db = new DBConnection();
            db.update(
                    "UPDATE Catalogue SET description=?, package_type=?, unit=?, units_per_pack=?, package_cost=?, availability=?, stock_limit=? WHERE item_id=?",
                    descriptionField.getText().trim(),
                    packageField.getText().trim(),
                    unitField.getText().trim(),
                    Integer.parseInt(units_per_packField.getText().trim()),
                    Double.parseDouble(costField.getText().trim()),
                    Integer.parseInt(availabilityField.getText().trim()),
                    Integer.parseInt(stock_limitField.getText().trim()),
                    id
            );
            setMessage("Item updated successfully.", true);
        } catch (NumberFormatException ex) {
            setMessage("Please enter valid numbers for numeric fields.", false);
        } catch (Exception ex) {
            setMessage("Error: " + ex.getMessage(), false);
        }
    }

    private void deactivateItem() {
        String id = itemIdField.getText().trim();
        if (id.isEmpty()) { setMessage("Load an item first.", false); return; }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to deactivate item " + id + "?",
                "Confirm Deactivation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                DBConnection db = new DBConnection();
                db.update("UPDATE Catalogue SET is_active = 0 WHERE item_id = ?", id);
                setMessage("Item deactivated successfully.", true);
                clearForm();
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
            if (qty <= 0) {
                setMessage("Quantity must be greater than zero.", false);
                return;
            }
            DBConnection db = new DBConnection();

            // Check item exists first
            ResultSet rs = db.query("SELECT item_id FROM Catalogue WHERE item_id = ? AND is_active = 1", id);
            if (!rs.next()) {
                setMessage("Item ID not found in catalogue.", false);
                return;
            }

            db.update("INSERT INTO Stock_Deliveries (item_id, quantity_added, entered_by, notes) VALUES (?,?,1,'Manual delivery')", id, qty);
            db.update("UPDATE Catalogue SET availability = availability + ? WHERE item_id = ?", qty, id);
            setMessage("Delivery recorded. Stock updated by " + qty + " packs.", true);
            quantityField.setText("");

        } catch (NumberFormatException ex) {
            setMessage("Please enter a valid quantity.", false);
        } catch (Exception ex) {
            setMessage("Error: " + ex.getMessage(), false);
        }
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
        messageLabel.setText(" ");
    }

    private void setMessage(String text, boolean success) {
        messageLabel.setText(text);
        messageLabel.setForeground(success ? new Color(0, 97, 0) : new Color(200, 80, 80));
    }

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
