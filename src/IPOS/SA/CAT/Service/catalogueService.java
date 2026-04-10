package IPOS.SA.CAT.Service;

import IPOS.SA.CAT.Model.CatalogueItem;
import IPOS.SA.DB.DBConnection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class catalogueService {

    private DBConnection db;

    public catalogueService() {
        this.db = new DBConnection();
    }

    // Load a single item by ID (only active items)
    public CatalogueItem loadItem(String itemId) throws Exception {
        ResultSet rs = db.query(
                "SELECT * FROM catalogue WHERE item_id = ? AND is_active = 1",
                itemId
        );

        if (rs != null && rs.next()) {
            return extractItemFromResultSet(rs);
        }
        return null;
    }

    // Load an item including inactive ones (for admin purposes)
    public CatalogueItem loadItemIncludingInactive(String itemId) throws Exception {
        ResultSet rs = db.query(
                "SELECT * FROM catalogue WHERE item_id = ?",
                itemId
        );

        if (rs != null && rs.next()) {
            return extractItemFromResultSet(rs);
        }
        return null;
    }

    // Check if an item exists and is active
    public boolean isItemActive(String itemId) throws Exception {
        ResultSet rs = db.query(
                "SELECT is_active FROM catalogue WHERE item_id = ?",
                itemId
        );

        if (rs != null && rs.next()) {
            return rs.getInt("is_active") == 1;
        }
        return false;
    }

    // Check if an item exists (regardless of active status)
    public boolean itemExists(String itemId) throws Exception {
        ResultSet rs = db.query(
                "SELECT item_id FROM catalogue WHERE item_id = ?",
                itemId
        );
        return rs != null && rs.next();
    }

    // Get the active status of an item
    public boolean getItemActiveStatus(String itemId) throws Exception {
        ResultSet rs = db.query(
                "SELECT is_active FROM catalogue WHERE item_id = ?",
                itemId
        );

        if (rs != null && rs.next()) {
            return rs.getInt("is_active") == 1;
        }
        return false;
    }

    // Save a new item
    public boolean saveItem(CatalogueItem item) throws Exception {
        // Check if item already exists
        if (itemExists(item.getItemId())) {
            return false;
        }

        int rowsAffected = db.update(
                "INSERT INTO catalogue (item_id, description, package_type, unit, " +
                        "unit_per_pack, package_cost, availability, minimum_stock_level, is_active) " +
                        "VALUES (?,?,?,?,?,?,?,?,1)",
                item.getItemId(),
                item.getDescription(),
                item.getPackageType(),
                item.getUnit(),
                item.getUnitsInPack(),
                item.getPackageCost(),
                item.getAvailabilityPacks(),
                item.getStockLimitPacks()  // This maps to minimum_stock_level
        );
        return rowsAffected > 0;
    }

    // Update an existing item (only if active)
    public boolean updateItem(CatalogueItem item) throws Exception {
        // Check if item exists and is active
        if (!isItemActive(item.getItemId())) {
            return false;
        }

        int rowsAffected = db.update(
                "UPDATE catalogue SET description=?, package_type=?, unit=?, " +
                        "unit_per_pack=?, package_cost=?, availability=?, minimum_stock_level=? " +
                        "WHERE item_id=? AND is_active=1",
                item.getDescription(),
                item.getPackageType(),
                item.getUnit(),
                item.getUnitsInPack(),
                item.getPackageCost(),
                item.getAvailabilityPacks(),
                item.getStockLimitPacks(),
                item.getItemId()
        );

        return rowsAffected > 0;
    }

    // Deactivate an item (soft delete)
    public boolean deactivateItem(String itemId) throws Exception {
        if (!itemExists(itemId)) {
            return false;
        }

        int rowsAffected = db.update(
                "UPDATE catalogue SET is_active = 0 WHERE item_id = ?",
                itemId
        );

        return rowsAffected > 0;
    }

    // Reactivate an item
    public boolean reactivateItem(String itemId) throws Exception {
        if (!itemExists(itemId)) {
            return false;
        }

        int rowsAffected = db.update(
                "UPDATE catalogue SET is_active = 1 WHERE item_id = ?",
                itemId
        );

        return rowsAffected > 0;
    }

    // Update Stock after an order
    public boolean UpdateCatalogue(String itemId, int quantity) throws Exception {
        if (quantity <= 0) {
            return false;
        }

        // Check if item exists and is active
        if (!isItemActive(itemId)) {
            return false;
        }

        try {
            // Update stock in Catalogue table
            db.update(
                    "UPDATE catalogue SET availability = availability - ? " +
                            "WHERE item_id = ? AND is_active = 1",
                    quantity, itemId
            );

            return true;
        } catch (Exception e) {
            throw new Exception("Failed to update stock: " + e.getMessage());
        }

    }
    // Record a stock delivery - UPDATED to match your StockDelivery table
    public boolean recordDelivery(String itemId, int quantity, int enteredBy) throws Exception {
        if (quantity <= 0) {
            return false;
        }

        // Check if item exists and is active
        if (!isItemActive(itemId)) {
            return false;
        }

        try {
            // Insert delivery record into StockDelivery table
            db.update(
                    "INSERT INTO stockdelivery (catalogue_item_id, quantity, delivery_date, status, supplier_name, userlogin_user_id) " +
                            "VALUES (?, ?, CURDATE(), 'completed', 'Manual Entry', ?)",
                    itemId, quantity, enteredBy
            );

            // Update stock in Catalogue table
            db.update(
                    "UPDATE catalogue SET availability = availability + ? " +
                            "WHERE item_id = ? AND is_active = 1",
                    quantity, itemId
            );

            return true;
        } catch (Exception e) {
            throw new Exception("Failed to record delivery: " + e.getMessage());
        }
    }

    // Get all active items
    public List<CatalogueItem> getAllActiveItems() throws Exception {
        List<CatalogueItem> items = new ArrayList<>();
        ResultSet rs = db.query(
                "SELECT * FROM catalogue WHERE is_active = 1 ORDER BY item_id"
        );

        while (rs != null && rs.next()) {
            items.add(extractItemFromResultSet(rs));
        }

        return items;
    }

    // Get all items (including inactive)
    public List<CatalogueItem> getAllItems() throws Exception {
        List<CatalogueItem> items = new ArrayList<>();
        ResultSet rs = db.query(
                "SELECT * FROM catalogue ORDER BY item_id"
        );

        while (rs != null && rs.next()) {
            items.add(extractItemFromResultSet(rs));
        }

        return items;
    }

    // Helper method to extract CatalogueItem from ResultSet
    private CatalogueItem extractItemFromResultSet(ResultSet rs) throws Exception {
        return new CatalogueItem(
                rs.getString("item_id"),
                rs.getString("description"),
                rs.getString("package_type"),
                rs.getString("unit"),
                rs.getInt("unit_per_pack"),
                rs.getDouble("package_cost"),
                rs.getInt("availability"),
                rs.getInt("minimum_stock_level")
        );
    }
}
