package IPOS.SA.ACC.Model;

/**
 * Represents a catalogue item that is currently below its minimum stock level.
 * Used to populate the low stock warning on login and the dashboard stock table.
 */
public class LowStockItem {
    private String itemId; // The unique ID for the catalogue item
    private String itemName; // The name of the item
    private int currentStock; // The current number of stock for the item
    private int minLevel; // The minimum level of stock that can be maintained

    /**
     * Constructor — creates a low stock item with all required details.
     *
     * @param itemId       the unique item identifier
     * @param itemName     the item description
     * @param currentStock the current stock level in packs
     * @param minLevel     the minimum stock level that should be maintained
     */
    public LowStockItem(String itemId, String itemName, int currentStock, int minLevel) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.currentStock = currentStock;
        this.minLevel = minLevel;
    }

    // Getters
    /**
     * Returns the unique item identifier.
     *
     * @return the item ID
     */
    public String getItemId() { return itemId; }

    /**
     * Returns the item description.
     *
     * @return the item name
     */
    public String getItemName() { return itemName; }

    /**
     * Returns the current stock level in packs.
     *
     * @return the current stock level
     */
    public int getCurrentStock() { return currentStock; }

    /**
     * Returns the minimum stock level that should be maintained.
     *
     * @return the minimum stock level
     */
    public int getMinLevel() { return minLevel; }

    // Setters
    /**
     * Sets the unique item identifier.
     *
     * @param itemId the item ID
     */
    public void setItemId(String itemId) { this.itemId = itemId; }

    /**
     * Sets the item description.
     *
     * @param itemName the item name
     */
    public void setItemName(String itemName) { this.itemName = itemName; }

    /**
     * Sets the current stock level in packs.
     *
     * @param currentStock the current stock level
     */
    public void setCurrentStock(int currentStock) { this.currentStock = currentStock; }

    /**
     * Sets the minimum stock level that should be maintained.
     *
     * @param minLevel the minimum stock level
     */
    public void setMinLevel(int minLevel) { this.minLevel = minLevel; }
}
