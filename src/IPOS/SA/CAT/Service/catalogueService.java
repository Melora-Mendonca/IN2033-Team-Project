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
                "SELECT * FROM Catalogue WHERE item_id = ? AND is_active = 1",
                itemId
        );

        if (rs.next()) {
            return extractItemFromResultSet(rs);
        }
        return null;
    }

    // Load an item including inactive ones (for admin purposes)
    public CatalogueItem loadItemIncludingInactive(String itemId) throws Exception {
        ResultSet rs = db.query(
                "SELECT * FROM Catalogue WHERE item_id = ?",
                itemId
        );

        if (rs.next()) {
            return extractItemFromResultSet(rs);
        }
        return null;
    }

    // Check if an item exists and is active
    public boolean isItemActive(String itemId) throws Exception {
        ResultSet rs = db.query(
                "SELECT is_active FROM Catalogue WHERE item_id = ?",
                itemId
        );

        if (rs.next()) {
            return rs.getInt("is_active") == 1;
        }
        return false;
    }

    // Check if an item exists (regardless of active status)
    public boolean itemExists(String itemId) throws Exception {
        ResultSet rs = db.query(
                "SELECT item_id FROM Catalogue WHERE item_id = ?",
                itemId
        );
        return rs.next();
    }

    // Get the active status of an item
    public boolean getItemActiveStatus(String itemId) throws Exception {
        ResultSet rs = db.query(
                "SELECT is_active FROM Catalogue WHERE item_id = ?",
                itemId
        );

        if (rs.next()) {
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

        db.update(
                "INSERT INTO Catalogue (item_id, description, package_type, unit, " +
                        "units_per_pack, package_cost, availability, stock_limit, is_active) " +
                        "VALUES (?,?,?,?,?,?,?,?,1)",
                item.getItemId(),
                item.getDescription(),
                item.getPackageType(),
                item.getUnit(),
                item.getUnitsInPack(),
                item.getPackageCost(),
                item.getAvailabilityPacks(),
                item.getStockLimitPacks()
        );
        return true;
    }

    // Update an existing item (only if active)
    public boolean updateItem(CatalogueItem item) throws Exception {
        // Check if item exists and is active
        if (!isItemActive(item.getItemId())) {
            return false;
        }

        int rowsAffected = db.update(
                "UPDATE Catalogue SET description=?, package_type=?, unit=?, " +
                        "units_per_pack=?, package_cost=?, availability=?, stock_limit=? " +
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
        // Check if item exists
        if (!itemExists(itemId)) {
            return false;
        }

        int rowsAffected = db.update(
                "UPDATE Catalogue SET is_active = 0 WHERE item_id = ?",
                itemId
        );

        return rowsAffected > 0;
    }

    // Reactivate an item
    public boolean reactivateItem(String itemId) throws Exception {
        // Check if item exists
        if (!itemExists(itemId)) {
            return false;
        }

        int rowsAffected = db.update(
                "UPDATE Catalogue SET is_active = 1 WHERE item_id = ?",
                itemId
        );

        return rowsAffected > 0;
    }

    // Record a stock delivery
    public boolean recordDelivery(String itemId, int quantity, int enteredBy) throws Exception {
        if (quantity <= 0) {
            return false;
        }

        // Check if item exists and is active
        if (!isItemActive(itemId)) {
            return false;
        }

        try {
            // Insert delivery record
            db.update(
                    "INSERT INTO Stock_Deliveries (item_id, quantity_added, entered_by, notes) " +
                            "VALUES (?,?,?,'Manual delivery')",
                    itemId, quantity, enteredBy
            );

            // Update stock
            db.update(
                    "UPDATE Catalogue SET availability = availability + ? " +
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
                "SELECT * FROM Catalogue WHERE is_active = 1 ORDER BY item_id"
        );

        while (rs.next()) {
            items.add(extractItemFromResultSet(rs));
        }

        return items;
    }

    // Get all items (including inactive)
    public List<CatalogueItem> getAllItems() throws Exception {
        List<CatalogueItem> items = new ArrayList<>();
        ResultSet rs = db.query(
                "SELECT * FROM Catalogue ORDER BY item_id"
        );

        while (rs.next()) {
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
                rs.getInt("units_per_pack"),
                rs.getDouble("package_cost"),
                rs.getInt("availability"),
                rs.getInt("stock_limit")
        );
    }
}
