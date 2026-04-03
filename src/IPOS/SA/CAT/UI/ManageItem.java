package IPOS.SA.CAT.UI;

import IPOS.SA.CAT.Model.CatalogueItem;
import IPOS.SA.CAT.Service.catalogueService;
import IPOS.SA.UI.BaseFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ManageItem extends BaseFrame {

    private final catalogueService service;
    private final String mode;
    private final int currentUserId;

    private JTextField itemIdField;
    private JTextField descriptionField;
    private JTextField unitField;
    private JTextField costField;
    private JTextField packageField;
    private JTextField units_per_packField;
    private JTextField availabilityField;
    private JTextField stock_limitField;
    private JTextField quantityField;
    private JLabel messageLabel;

    public ManageItem(String fullname, String role, String mode) {
        super(fullname, role, getTitleForMode(mode));
        this.service       = new catalogueService();
        this.mode          = mode;
        this.currentUserId = 1;
        buildContent();
    }

    @Override
    protected String getHeaderTitle() {
        return getTitleForMode(mode != null ? mode : "");
    }

    private static String getTitleForMode(String mode) {
        switch (mode) {
            case "ADD":      return "Add New Catalogue Item";
            case "EDIT":     return "Update Catalogue Item";
            case "DELETE":   return "Deactivate Catalogue Item";
            case "DELIVERY": return "Record Stock Delivery";
            default:         return "Manage Catalogue Item";
        }
    }

    private String getFormTitleForMode() {
        switch (mode) {
            case "ADD":      return "NEW ITEM DETAILS";
            case "EDIT":     return "EDIT ITEM DETAILS";
            case "DELETE":   return "DEACTIVATE ITEM";
            case "DELIVERY": return "RECORD DELIVERY";
            default:         return "ITEM DETAILS";
        }
    }

    private void buildContent() {
        CenterPanel.setLayout(new BorderLayout());
        CenterPanel.setBackground(new Color(245, 247, 250));

        // ── FORM PANEL ───────────────────────────────────────
        JPanel formPanel = new JPanel(new BorderLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(221, 225, 231), 1),
                new EmptyBorder(20, 20, 20, 20)));

        JLabel formTitle = new JLabel(getFormTitleForMode());
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formTitle.setForeground(Color.BLACK);
        formTitle.setBorder(new EmptyBorder(0, 0, 12, 0));

        JPanel grid = new JPanel();
        grid.setLayout(new BoxLayout(grid, BoxLayout.Y_AXIS));
        grid.setBackground(Color.WHITE);

        itemIdField         = createField();
        descriptionField    = createField();
        packageField        = createField();
        unitField           = createField();
        units_per_packField = createField();
        costField           = createField();
        availabilityField   = createField();
        stock_limitField    = createField();
        quantityField       = createField();

        // Item ID always shown
        JPanel row0 = row(1);
        row0.add(fieldWrapper("ITEM ID", itemIdField));
        grid.add(row0);
        grid.add(Box.createVerticalStrut(12));

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

        formPanel.add(formTitle,    BorderLayout.NORTH);
        formPanel.add(grid,         BorderLayout.CENTER);
        formPanel.add(messageLabel, BorderLayout.SOUTH);

        // ── ACTIONS PANEL ─────────────────────────────────────
        JPanel actionsPanel = new JPanel();
        actionsPanel.setLayout(new BoxLayout(actionsPanel, BoxLayout.Y_AXIS));
        actionsPanel.setBackground(Color.WHITE);
        actionsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(221, 225, 231), 1),
                new EmptyBorder(16, 16, 16, 16)));
        actionsPanel.setPreferredSize(new Dimension(210, 0));

        JLabel actionsTitle = new JLabel("ACTIONS");
        actionsTitle.setFont(new Font("Segoe UI", Font.BOLD, 11));
        actionsTitle.setForeground(new Color(107, 114, 128));
        actionsTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        actionsPanel.add(actionsTitle);
        actionsPanel.add(Box.createVerticalStrut(10));

        switch (mode) {
            case "ADD":      addAddButtons(actionsPanel);      break;
            case "EDIT":     addEditButtons(actionsPanel);     break;
            case "DELETE":   addDeleteButtons(actionsPanel);   break;
            case "DELIVERY": addDeliveryButtons(actionsPanel); break;
        }

        actionsPanel.add(Box.createVerticalStrut(8));
        JButton backBtn = actionButton("← Back to Catalogue", new Color(17, 24, 39));
        backBtn.addActionListener(e -> { dispose(); new Catalogue(fullname, role); });
        actionsPanel.add(backBtn);

        CenterPanel.add(formPanel,    BorderLayout.CENTER);
        CenterPanel.add(actionsPanel, BorderLayout.EAST);
    }

    // ── FIELD ROWS ───────────────────────────────────────────
    private void addEditFields(JPanel grid) {
        JPanel row1 = row(2);
        row1.add(fieldWrapper("DESCRIPTION",  descriptionField));
        row1.add(fieldWrapper("PACKAGE TYPE", packageField));

        JPanel row2 = row(2);
        row2.add(fieldWrapper("UNIT",           unitField));
        row2.add(fieldWrapper("UNITS PER PACK", units_per_packField));

        JPanel row3 = row(2);
        row3.add(fieldWrapper("PACKAGE COST (£)", costField));
        row3.add(fieldWrapper("AVAILABILITY",     availabilityField));

        JPanel row4 = row(1);
        row4.add(fieldWrapper("MINIMUM STOCK LIMIT", stock_limitField));

        grid.add(row1); grid.add(Box.createVerticalStrut(12));
        grid.add(row2); grid.add(Box.createVerticalStrut(12));
        grid.add(row3); grid.add(Box.createVerticalStrut(12));
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
        JPanel row1 = row(1);
        row1.add(fieldWrapper("QUANTITY TO ADD (packs)", quantityField));
        grid.add(row1);
    }

    // ── ACTION BUTTONS ────────────────────────────────────────
    private void addAddButtons(JPanel panel) {
        JButton addBtn   = actionButton("Add Item", new Color(30, 70, 90));
        JButton clearBtn = actionButton("Clear",    new Color(107, 114, 128));
        addBtn.addActionListener(e   -> addItem());
        clearBtn.addActionListener(e -> clearForm());
        panel.add(addBtn);   panel.add(Box.createVerticalStrut(8));
        panel.add(clearBtn);
    }

    private void addEditButtons(JPanel panel) {
        JButton loadBtn   = actionButton("Load Item",   new Color(17, 24, 39));
        JButton updateBtn = actionButton("Update Item", new Color(30, 70, 90));
        JButton clearBtn  = actionButton("Clear",       new Color(107, 114, 128));
        loadBtn.addActionListener(e   -> loadItemForEdit());
        updateBtn.addActionListener(e -> updateItem());
        clearBtn.addActionListener(e  -> clearForm());
        panel.add(loadBtn);   panel.add(Box.createVerticalStrut(8));
        panel.add(updateBtn); panel.add(Box.createVerticalStrut(8));
        panel.add(clearBtn);
    }

    private void addDeleteButtons(JPanel panel) {
        JButton deactivateBtn = actionButton("Deactivate Item", new Color(127, 29, 29));
        JButton reactivateBtn = actionButton("Reactivate Item", new Color(20, 83, 45));
        JButton clearBtn      = actionButton("Clear",           new Color(107, 114, 128));
        deactivateBtn.addActionListener(e -> deactivateItem());
        reactivateBtn.addActionListener(e -> reactivateItem());
        clearBtn.addActionListener(e      -> clearForm());
        panel.add(deactivateBtn); panel.add(Box.createVerticalStrut(8));
        panel.add(reactivateBtn); panel.add(Box.createVerticalStrut(8));
        panel.add(clearBtn);
    }

    private void addDeliveryButtons(JPanel panel) {
        JButton deliveryBtn = actionButton("Record Delivery", new Color(20, 83, 45));
        JButton clearBtn    = actionButton("Clear",           new Color(107, 114, 128));
        deliveryBtn.addActionListener(e -> recordDelivery());
        clearBtn.addActionListener(e    -> clearForm());
        panel.add(deliveryBtn); panel.add(Box.createVerticalStrut(8));
        panel.add(clearBtn);
    }

    // ── BUSINESS LOGIC ───────────────────────────────────────
    private void addItem() {
        try {
            if (!validateRequiredFields()) return;
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
        if (id.isEmpty()) { setMessage("Enter an Item ID to load.", false); return; }
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

    private void updateItem() {
        String id = itemIdField.getText().trim();
        if (id.isEmpty()) { setMessage("Load an item first.", false); return; }
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
        if (id.isEmpty()) { setMessage("Enter an Item ID first.", false); return; }
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
        if (id.isEmpty()) { setMessage("Load an item first.", false); return; }
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
        if (id.isEmpty())                         { setMessage("Enter an Item ID.", false); return; }
        if (quantityField.getText().trim().isEmpty()) { setMessage("Enter a quantity.", false); return; }
        try {
            int qty = Integer.parseInt(quantityField.getText().trim());
            if (service.recordDelivery(id, qty, currentUserId)) {
                setMessage("Delivery recorded. Stock updated by " + qty + " packs.", true);
                quantityField.setText("");
                CatalogueItem item = service.loadItem(id);
                if (item != null)
                    availabilityField.setText(String.valueOf(item.getAvailabilityPacks()));
            } else {
                setMessage("Failed to record delivery. Item may be inactive.", false);
            }
        } catch (NumberFormatException ex) {
            setMessage("Please enter a valid quantity.", false);
        } catch (Exception ex) {
            setMessage("Error: " + ex.getMessage(), false);
        }
    }

    // ── HELPERS ──────────────────────────────────────────────
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

    private JTextField createField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(221, 225, 231)),
                BorderFactory.createEmptyBorder(0, 12, 0, 12)));
        return field;
    }

    private JPanel row(int cols) {
        JPanel p = new JPanel(new GridLayout(1, cols, 12, 0));
        p.setBackground(Color.WHITE);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        p.setPreferredSize(new Dimension(0, 60));
        return p;
    }

    private JPanel fieldWrapper(String label, JTextField field) {
        JPanel wrapper = new JPanel(new BorderLayout(0, 4));
        wrapper.setBackground(Color.WHITE);
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lbl.setForeground(new Color(107, 114, 128));
        wrapper.add(lbl,   BorderLayout.NORTH);
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
}
