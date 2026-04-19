package IPOS.SA.CAT.UI;

import IPOS.SA.CAT.Model.CatalogueItem;
import IPOS.SA.CAT.Service.catalogueService;
import IPOS.SA.UI.AppFrame;
import IPOS.SA.UI.BaseFrame;
import IPOS.SA.UI.ScreenRouter;

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
    /**
     * Form screen for managing catalogue items in IPOS-SA.
     * Operates in four modes, each showing different fields and action buttons:
     * - ADD      — blank form to add a new catalogue item
     * - EDIT     — load an existing item by ID and update its details
     * - DELETE   — deactivate or reactivate a catalogue item
     * - DELIVERY — record a stock delivery to increase availability
     */
    public ManageItem(String fullname, String role, String mode, ScreenRouter router) {
        super(fullname, role, getTitleForMode(mode), router);
        this.service       = new catalogueService();
        this.mode          = mode;
        this.currentUserId = 1;
        buildContent();
    }
    /**
     * Returns the title displayed in the page header based on the current mode.
     *
     * @return the header title string
     */
    @Override
    protected String getHeaderTitle() {
        return getTitleForMode(mode != null ? mode : "");
    }
    /**
     * Returns the window title for a given mode.
     *
     * @param mode the form mode
     * @return the title string for that mode
     */
    private static String getTitleForMode(String mode) {
        switch (mode) {
            case "ADD":      return "Add New Catalogue Item";
            case "EDIT":     return "Update Catalogue Item";
            case "DELETE":   return "Deactivate Catalogue Item";
            case "DELIVERY": return "Record Stock Delivery";
            default:         return "Manage Catalogue Item";
        }
    }
    /**
     * Returns the form section title for the current mode.
     *
     * @return the form title string
     */
    private String getFormTitleForMode() {
        switch (mode) {
            case "ADD":      return "NEW ITEM DETAILS";
            case "EDIT":     return "EDIT ITEM DETAILS";
            case "DELETE":   return "DEACTIVATE ITEM";
            case "DELIVERY": return "RECORD DELIVERY";
            default:         return "ITEM DETAILS";
        }
    }
    /**
     * Builds and arranges all UI components for this screen.
     * The form fields shown and the action buttons available
     * depend on the current mode.
     */
    private void buildContent() {
        CenterPanel.setLayout(new BorderLayout());
        CenterPanel.setBackground(new Color(245, 247, 250));

        // Creates an empty panel for the form to sit in
        JPanel formPanel = new JPanel(new BorderLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(221, 225, 231), 1),
                new EmptyBorder(20, 20, 20, 20)));

        // Adds a title label to the form
        JLabel formTitle = new JLabel(getFormTitleForMode());
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formTitle.setForeground(Color.BLACK);
        formTitle.setBorder(new EmptyBorder(0, 0, 12, 0));

        // Creates a smaller panel inside the frame panel for the text feilds
        JPanel grid = new JPanel();
        grid.setLayout(new BoxLayout(grid, BoxLayout.Y_AXIS));
        grid.setBackground(Color.WHITE);

        // creates the data entry feilds
        itemIdField = createField();
        descriptionField = createField();
        packageField = createField();
        unitField = createField();
        units_per_packField = createField();
        costField = createField();
        availabilityField = createField();
        stock_limitField = createField();
        quantityField = createField();

        // Item ID always shown
        JPanel row0 = row(1);
        row0.add(fieldWrapper("ITEM ID", itemIdField));
        grid.add(row0);
        grid.add(Box.createVerticalStrut(12));

        // Shows the buttons field based on the mode
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

        // message label created to show any errors at the bottom of the form
        messageLabel = new JLabel(" ");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        messageLabel.setBorder(new EmptyBorder(8, 0, 0, 0));

        formPanel.add(formTitle, BorderLayout.NORTH);
        formPanel.add(grid, BorderLayout.CENTER);
        formPanel.add(messageLabel, BorderLayout.SOUTH);

        // Creates a panel to store the action buttons
        JPanel actionsPanel = new JPanel();
        actionsPanel.setLayout(new BoxLayout(actionsPanel, BoxLayout.Y_AXIS));
        actionsPanel.setBackground(Color.WHITE);
        actionsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(221, 225, 231), 1),
                new EmptyBorder(16, 16, 16, 16)));
        actionsPanel.setPreferredSize(new Dimension(210, 0));

        // Creates a label to identify the buttons
        JLabel actionsTitle = new JLabel("ACTIONS");
        actionsTitle.setFont(new Font("Segoe UI", Font.BOLD, 11));
        actionsTitle.setForeground(new Color(107, 114, 128));
        actionsTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        actionsPanel.add(actionsTitle);
        actionsPanel.add(Box.createVerticalStrut(10));

        // Uses the editing mode to decide which buttons to show the user based on how the form is being used
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

        // Adds the action buttons to the right hand side of the form, and a back button to return to the calling form
        actionsPanel.add(Box.createVerticalStrut(8));
        JButton backBtn = actionButton("← Back to Catalogue", new Color(17, 24, 39));
        backBtn.addActionListener(e -> router.goTo(AppFrame.SCREEN_CATALOGUE));
        actionsPanel.add(backBtn);

        CenterPanel.add(formPanel, BorderLayout.CENTER);
        CenterPanel.add(actionsPanel, BorderLayout.EAST);
    }

    /**
     * Adds the full set of item detail fields to the form grid.
     * Used for both ADD and EDIT modes.
     *
     * @param grid the form grid panel to add fields to
     */
    private void addEditFields(JPanel grid) {
        // Adds all the text fields to the grid with equal spacing between them
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

        grid.add(row1);
        grid.add(Box.createVerticalStrut(12));
        grid.add(row2);
        grid.add(Box.createVerticalStrut(12));
        grid.add(row3);
        grid.add(Box.createVerticalStrut(12));
        grid.add(row4);
    }
    /**
     * Adds a red warning label to the form for DELETE mode.
     * Informs the user that the operation will deactivate the item.
     *
     * @param grid the form grid panel to add the warning to
     */
    private void addDeleteWarning(JPanel grid) {
        JLabel warning = new JLabel("Warning! This will deactivate the item from the catalogue.");
        warning.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        warning.setForeground(new Color(180, 30, 30));
        warning.setAlignmentX(Component.LEFT_ALIGNMENT);
        grid.add(warning);
    }
    /**
     * Adds the quantity input field for DELIVERY mode.
     *
     * @param grid the form grid panel to add the field to
     */
    private void addDeliveryFields(JPanel grid) {
        JPanel row1 = row(1);
        row1.add(fieldWrapper("QUANTITY TO ADD (packs)", quantityField));
        grid.add(row1);
    }

    /**
     * Adds the Add Item and Clear action buttons for ADD mode.
     *
     * @param panel the actions panel to add buttons to
     */
    private void addAddButtons(JPanel panel) {

        // creates the buttons, adds functionality to the buttons and adds them to the action panel with spacing
        JButton addBtn = actionButton("Add Item", new Color(30, 70, 90));
        JButton clearBtn = actionButton("Clear", new Color(107, 114, 128));
        addBtn.addActionListener(e   -> addItem());
        clearBtn.addActionListener(e -> clearForm());
        panel.add(addBtn);
        panel.add(Box.createVerticalStrut(8));
        panel.add(clearBtn);
    }
    /**
     * Adds the Load Item, Update Item and Clear action buttons for EDIT mode.
     *
     * @param panel the actions panel to add buttons to
     */
    private void addEditButtons(JPanel panel) {
        // creates the buttons, adds functionality to the buttons and adds them to the action panel with spacing
        JButton loadBtn = actionButton("Load Item", new Color(17, 24, 39));
        JButton updateBtn = actionButton("Update Item", new Color(30, 70, 90));
        JButton clearBtn = actionButton("Clear", new Color(107, 114, 128));
        loadBtn.addActionListener(e   -> loadItemForEdit());
        updateBtn.addActionListener(e -> updateItem());
        clearBtn.addActionListener(e  -> clearForm());
        panel.add(loadBtn);
        panel.add(Box.createVerticalStrut(8));
        panel.add(updateBtn);
        panel.add(Box.createVerticalStrut(8));
        panel.add(clearBtn);
    }
    /**
     * Adds the Deactivate Item, Reactivate Item and Clear action buttons
     * for DELETE mode.
     *
     * @param panel the actions panel to add buttons to
     */
    private void addDeleteButtons(JPanel panel) {
        // creates the buttons, adds functionality to the buttons and adds them to the action panel with spacing
        JButton deactivateBtn = actionButton("Deactivate Item", new Color(127, 29, 29));
        JButton reactivateBtn = actionButton("Reactivate Item", new Color(20, 83, 45));
        JButton clearBtn      = actionButton("Clear",           new Color(107, 114, 128));
        deactivateBtn.addActionListener(e -> deactivateItem());
        reactivateBtn.addActionListener(e -> reactivateItem());
        clearBtn.addActionListener(e      -> clearForm());
        panel.add(deactivateBtn);
        panel.add(Box.createVerticalStrut(8));
        panel.add(reactivateBtn);
        panel.add(Box.createVerticalStrut(8));
        panel.add(clearBtn);
    }
    /**
     * Adds the Record Delivery and Clear action buttons for DELIVERY mode.
     *
     * @param panel the actions panel to add buttons to
     */
    private void addDeliveryButtons(JPanel panel) {
        // creates the buttons, adds functionality to the buttons and adds them to the action panel with spacing
        JButton deliveryBtn = actionButton("Record Delivery", new Color(20, 83, 45));
        JButton clearBtn = actionButton("Clear", new Color(107, 114, 128));
        deliveryBtn.addActionListener(e -> recordDelivery());
        clearBtn.addActionListener(e    -> clearForm());
        panel.add(deliveryBtn);
        panel.add(Box.createVerticalStrut(8));
        panel.add(clearBtn);
    }

    /**
     * Handles the Add Item action.
     * Validates all required fields and saves the new item via the service layer.
     */
    private void addItem() {
        try {
            if (!validateRequiredFields()) return;
            // calls the catalogue item class to create and add a new item to the catalogue and update the database to reflect the addition
            CatalogueItem item = createItemFromForm();
            if (service.saveItem(item)) {
                clearForm(); // clears the data once the item has been recorded
                setMessage("Item added successfully.", true);
            } else {
                setMessage("Item ID already exists.", false);
            }
        } catch (NumberFormatException ex) {
            setMessage("Please enter valid numbers for numeric fields.", false);
        } catch (Exception ex) {
            setMessage("Error: " + ex.getMessage(), false);
        }
    }
    /**
     * Loads an existing catalogue item by its ID and populates the form.
     * Only active items can be loaded for editing.
     */
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
    /**
     * Handles the Update Item action.
     * Validates the form and updates the existing item via the service layer.
     * Prevents updating deactivated items.
     */
    private void updateItem() {
        String id = itemIdField.getText().trim();
        if (id.isEmpty()) {
            setMessage("Load an item first.", false);
            return;
        }
        try {
            // checks if the item selected is currently active or not
            if (!service.isItemActive(id)) {
                setMessage("Cannot update a deactivated item.", false);
                return;
            }
            // creates a new item using the new entered details and passes it to the service class to update in the database
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
    /**
     * Handles the Deactivate Item action.
     * Shows a confirmation dialog before deactivating the item.
     * The item record is retained in the database for historical reference.
     */
    private void deactivateItem() {
        String id = itemIdField.getText().trim();
        if (id.isEmpty()) {
            setMessage("Enter an Item ID first.", false);
            return; }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to deactivate item " + id + "?",
                "Confirm Deactivation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // calls the service class to find the item with the given ID and to deactivate it
                if (service.deactivateItem(id)) {
                    clearForm();
                    setMessage("Item deactivated successfully.", true);
                } else {
                    setMessage("Item not found.", false);
                }
            } catch (Exception ex) {
                setMessage("Error: " + ex.getMessage(), false);
            }
        }
    }
    /**
     * Handles the Reactivate Item action.
     * Shows a confirmation dialog before reactivating a deactivated item.
     */
    private void reactivateItem() {
        String id = itemIdField.getText().trim();
        if (id.isEmpty()) { setMessage("Load an item first.", false); return; }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to reactivate item " + id + "?",
                "Confirm Reactivation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // calls the service class to find the item with the given ID and to reactivate it
                if (service.reactivateItem(id)) {
                    clearForm();
                    setMessage("Item reactivated successfully.", true);
                } else {
                    setMessage("Item not found.", false);
                }
            } catch (Exception ex) {
                setMessage("Error: " + ex.getMessage(), false);
            }
        }
    }
    /**
     * Handles the Record Delivery action.
     * Validates the quantity, records the delivery and updates the
     * displayed availability after the stock is increased.
     */
    private void recordDelivery() {
        String id = itemIdField.getText().trim();
        if (id.isEmpty()) { setMessage("Enter an Item ID.", false); return; }

        String qtyText = quantityField.getText().trim();
        if (qtyText.isEmpty()) { setMessage("Quantity is required.", false); return; }

        try {
            int qty = Integer.parseInt(qtyText);

            // Quantity must be positive
            if (qty <= 0) {
                setMessage("Quantity must be greater than zero.", false);
                return;
            }

            // Quantity reasonable upper limit
            if (qty > 10000) {
                setMessage("Quantity seems too large. Please check.", false);
                return;
            }

            // calls the service class to update the database with the new stock count
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
            setMessage("Quantity must be a whole number.", false);
        } catch (Exception ex) {
            setMessage("Error: " + ex.getMessage(), false);
        }
    }

    /**
     * Builds a CatalogueItem object from the current form field values.
     * Called before saving or updating an item.
     *
     * @return a CatalogueItem populated with values from the form fields
     */
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
    /**
     * Populates all form fields with data from an existing catalogue item.
     *
     * @param item the catalogue item to populate the form with
     */
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
    /**
     * Validates that all required form fields have been filled in.
     * Displays an error message for the first empty field found.
     *
     * @return true if all required fields are filled, false otherwise
     */
    private boolean validateRequiredFields() {
        if (itemIdField.getText().trim().isEmpty()) {
            setMessage("Item ID is required.", false);
            return false;
        }
        if (descriptionField.getText().trim().isEmpty()) {
            setMessage("Description is required.", false);
            return false;
        }
        if (unitField.getText().trim().isEmpty()) {
            setMessage("Unit is required.", false);
            return false;
        }
        if (costField.getText().trim().isEmpty()){
            setMessage("Cost is required.", false);
            return false;
        }
        if (units_per_packField.getText().trim().isEmpty()){
            setMessage("Units per pack is required.", false);
            return false;
        }
        if (availabilityField.getText().trim().isEmpty()){
            setMessage("Stock Availability is required.", false);
            return false;
        }
        if (stock_limitField.getText().trim().isEmpty()){
            setMessage("Stock limit is required.", false);
            return false;
        }
        if (packageField.getText().trim().isEmpty()){
            setMessage("Package is required.", false);
            return false;
        }
        return true;
    }
    /**
     * Clears all form fields and resets the message label.
     */
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
    /**
     * Displays a success or error message below the form.
     *
     * @param text the message to display
     * @param success true for a green success message, false for a red error message
     */
    private void setMessage(String text, boolean success) {
        messageLabel.setText(text);
        messageLabel.setForeground(success ? new Color(0, 97, 0) : new Color(200, 80, 80));
    }
    /**
     * Creates a styled text input field.
     *
     * @return the styled text field
     */
    private JTextField createField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(221, 225, 231)),
                BorderFactory.createEmptyBorder(0, 12, 0, 12)));
        return field;
    }
    /**
     * Creates a row panel with a fixed number of equal-width columns.
     *
     * @param cols the number of columns
     * @return the row panel
     */
    private JPanel row(int cols) {
        JPanel p = new JPanel(new GridLayout(1, cols, 12, 0));
        p.setBackground(Color.WHITE);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        p.setPreferredSize(new Dimension(0, 60));
        return p;
    }
    /**
     * Wraps a text field with a label above it.
     *
     * @param label the field label
     * @param field the text field to wrap
     * @return the wrapped panel
     */
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
    /**
     * Creates a styled action button with the given label and background colour.
     *
     * @param label the button label
     * @param bg    the button background colour
     * @return the styled button
     */
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
