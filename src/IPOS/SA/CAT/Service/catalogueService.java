package IPOS.SA.CAT.Service;

import IPOS.SA.CAT.Model.CatalogueItem;
import IPOS.SA.DB.DBConnection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class responsible for all catalogue operations in IPOS-SA.
* Provides methods to load, save, update, deactivate and reactivate
* catalogue items, as well as recording stock deliveries and updating
* stock levels when orders are placed.
*/
public class catalogueService {

    private DBConnection db;
    /**
     * Default constructor; initialises the service with a database connection.
     */
    public catalogueService() {
        this.db = new DBConnection();
    }


    /**
     * Loads a single active catalogue item by its ID.
     * Returns null if the item does not exist or has been deactivated.
     *
     * @param itemId the unique item identifier
     * @return the CatalogueItem if found and active, null otherwise
     * @throws Exception if a database error occurs
     */
    public CatalogueItem loadItem(String itemId) throws Exception {
        // Searches the Database to find all items that match the given ID and are active
        ResultSet rs = db.query(
                "SELECT * FROM catalogue WHERE item_id = ? AND is_active = 1",
                itemId
        );

        if (rs != null && rs.next()) {
            return extractItemFromResultSet(rs);
        }
        return null;
    }

    /**
     * Loads a single active catalogue item by its ID.
     * Returns null if the item does not exist or has been deactivated.
     *
     * @param itemId the unique item identifier
     * @return the CatalogueItem if found and active, null otherwise
     * @throws Exception if a database error occurs
     */
    public CatalogueItem loadItemIncludingInactive(String itemId) throws Exception {
        // Searches the Database to find all items that match the given ID
        ResultSet rs = db.query(
                "SELECT * FROM catalogue WHERE item_id = ?",
                itemId
        );

        if (rs != null && rs.next()) {
            return extractItemFromResultSet(rs);
        }
        return null;
    }

    /**
     * Checks whether a catalogue item exists and is currently active.
     *
     * @param itemId the unique item identifier
     * @return true if the item exists and is active
     * @throws Exception if a database error occurs
     */
    public boolean isItemActive(String itemId) throws Exception {
        // Check if an item exists and is active using Item ID
        ResultSet rs = db.query(
                "SELECT is_active FROM catalogue WHERE item_id = ?",
                itemId
        );

        if (rs != null && rs.next()) {
            return rs.getInt("is_active") == 1;
        }
        return false;
    }

    /**
     * Checks whether a catalogue item exists and is either active or inactive.
     *
     * @param itemId the unique item identifier
     * @return true if the item exists and is active/inactive
     * @throws Exception if a database error occurs
     */
    // Check if an item exists (regardless of active status)
    public boolean itemExists(String itemId) throws Exception {
        ResultSet rs = db.query(
                "SELECT item_id FROM catalogue WHERE item_id = ?",
                itemId
        );
        return rs != null && rs.next();
    }

    /**
     * Returns the active status of a catalogue item.
     * Equivalent to isItemActive — provided for readability in some contexts.
     *
     * @param itemId the unique item identifier
     * @return true if the item is active, false otherwise
     * @throws Exception if a database error occurs
     */
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


    /**
     * Saves a new catalogue item to the database.
     * If the item ID previously existed but was deactivated, it is
     * reactivated and updated with the new details instead of inserting a duplicate.
     * Returns false if the item ID already exists and is active.
     *
     * @param item the catalogue item to save
     * @return true if saved or reactivated successfully, false if item already exists
     * @throws Exception if a database error occurs
     */
    public boolean saveItem(CatalogueItem item) throws Exception {
        CatalogueItem inactive = loadInactiveItem(item.getItemId());
        if (inactive != null) {
            // Reactivates inactive item and update with new details
            db.update("UPDATE catalogue SET is_active = 1, description = ?, " +
                            "package_type = ?, unit = ?, unit_per_pack = ?, package_cost = ?, " +
                            "availability = ?, minimum_stock_level = ? WHERE item_id = ?",
                    item.getDescription(),
                    item.getPackageType(),
                    item.getUnit(),
                    item.getUnitsInPack(),
                    item.getPackageCost(),
                    item.getAvailabilityPacks(),
                    item.getStockLimitPacks(),
                    item.getItemId());
            return true;
        } // Checks if item already exists
        if (itemExists(item.getItemId())) {
            return false;
        }

        /**
         * Updates an existing active catalogue item.
         * Returns false if the item does not exist or has been deactivated.
         *
         * @param item the catalogue item with updated details
         * @return true if updated successfully
         * @throws Exception if a database error occurs
         */

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

    public boolean updateItem(CatalogueItem item) throws Exception {
        // Checks if item exists and is active
        if (!isItemActive(item.getItemId())) {
            return false;
        }

        // update stored details with given details
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

    /**
     * Deactivates a catalogue item by setting is_active to 0.
     * The item record is retained in the database for historical reference.
     * Returns false if the item does not exist.
     *
     * @param itemId the unique item identifier
     * @return true if deactivated successfully
     * @throws Exception if a database error occurs
     */
    public boolean deactivateItem(String itemId) throws Exception {
        // Cehcks if item exists first
        if (!itemExists(itemId)) {
            return false;
        }

        // does soft delete by setting the item to inactive and removing it from the displayed catalogue
        int rowsAffected = db.update(
                "UPDATE catalogue SET is_active = 0 WHERE item_id = ?",
                itemId
        );

        return rowsAffected > 0;
    }

    /**
     * Reactivates a previously deactivated catalogue item.
     * Returns false if the item does not exist.
     *
     * @param itemId the unique item identifier
     * @return true if reactivated successfully
     * @throws Exception if a database error occurs
     */
    public boolean reactivateItem(String itemId) throws Exception {
        // Checks if the item exists
        if (!itemExists(itemId)) {
            return false;
        }

        // sts a inactive item to active again
        int rowsAffected = db.update(
                "UPDATE catalogue SET is_active = 1 WHERE item_id = ?",
                itemId
        );

        return rowsAffected > 0;
    }

    /**
     * Deducts a quantity from a catalogue item's stock after an order is placed.
     * Returns false if the quantity is invalid, the item does not exist
     * or the item is inactive.
     *
     * @param itemId   the unique item identifier
     * @param quantity the number of packs to deduct from stock
     * @return true if the stock was successfully updated
     * @throws Exception if a database error occurs
     */
    public boolean UpdateCatalogue(String itemId, int quantity) throws Exception {
        // checks if the quantity given is a valid positive number
        if (quantity <= 0) {
            return false;
        }

        // Checks if item exists and is active
        if (!isItemActive(itemId)) {
            return false;
        }

        try {
            // Updates stock in Catalogue table
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

    /**
     * Records a stock delivery and updates the catalogue availability.
     * Inserts a record into the stockdelivery table and increases
     * the item's availability by the delivered quantity.
     * Returns false if the quantity is invalid or the item is inactive.
     *
     * @param itemId the unique item identifier
     * @param quantity the number of packs delivered
     * @param enteredBy the user ID of the staff member recording the delivery
     * @return true if the delivery was recorded successfully
     * @throws Exception if a database error occurs
     */
    public boolean recordDelivery(String itemId, int quantity, int enteredBy) throws Exception {
        // checks to see if the quantity entered is a valid positive number
        if (quantity <= 0) {
            return false;
        }

        // Checks if item exists and is active
        if (!isItemActive(itemId)) {
            return false;
        }

        try {
            // Inserts delivery record into StockDelivery table
            db.update(
                    "INSERT INTO stockdelivery (catalogue_item_id, quantity, delivery_date, status, supplier_name, userlogin_user_id) " +
                            "VALUES (?, ?, CURDATE(), 'completed', 'Manual Entry', ?)",
                    itemId, quantity, enteredBy
            );

            // Updates stock in Catalogue table
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

    /**
     * Returns a list of all active catalogue items ordered by item ID.
     * Used by the catalogue screen and the REST API endpoint for IPOS-PU.
     *
     * @return list of all active catalogue items
     * @throws Exception if a database error occurs
     */
    public List<CatalogueItem> getAllActiveItems() throws Exception {
        // queries the database for all the active items in the catalogue table, and adds them all to a list of catalogue items
        List<CatalogueItem> items = new ArrayList<>();
        ResultSet rs = db.query(
                "SELECT * FROM catalogue WHERE is_active = 1 ORDER BY item_id"
        );

        while (rs != null && rs.next()) {
            items.add(extractItemFromResultSet(rs));
        }

        return items;
    }


    /**
     * Returns a list of all catalogue items including deactivated ones.
     * Used for admin reporting and audit purposes.
     *
     * @return list of all catalogue items
     * @throws Exception if a database error occurs
     */
    public List<CatalogueItem> getAllItems() throws Exception {
        // queries the database for all the items in the catalogue table, and adds them all to a list of catalogue items
        List<CatalogueItem> items = new ArrayList<>();
        ResultSet rs = db.query(
                "SELECT * FROM catalogue ORDER BY item_id"
        );

        while (rs != null && rs.next()) {
            items.add(extractItemFromResultSet(rs));
        }

        return items;
    }

    /**
     * Returns a list of all catalogue items including deactivated ones.
     * Used for admin reporting and audit purposes.
     *
     * @return list of all catalogue items
     * @throws Exception if a database error occurs
     */
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
    /**
     * Returns the package cost for a catalogue item.
     * Used when calculating order totals in the REST API.
     *
     * @param itemId the unique item identifier
     * @return the package cost, or 0.0 if the item is not found
     */
    public double getItemPrice(String itemId) {
        try {
            DBConnection db = new DBConnection();
            ResultSet rs = db.query("SELECT package_cost FROM catalogue WHERE item_id = ? AND is_active = 1", itemId);
            if (rs.next()) return rs.getDouble("package_cost");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0.0;
    }
    /**
     * Loads a deactivated catalogue item by its ID.
     * Used by saveItem to check if an item can be reactivated
     * instead of inserting a duplicate.
     *
     * @param id the unique item identifier
     * @return the deactivated CatalogueItem if found, null otherwise
     */
    public CatalogueItem loadInactiveItem(String id) {
        try {
            ResultSet rs = db.query(
                    "SELECT * FROM catalogue WHERE item_id = ? AND is_active = 0", id);
            if (rs.next()) {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
