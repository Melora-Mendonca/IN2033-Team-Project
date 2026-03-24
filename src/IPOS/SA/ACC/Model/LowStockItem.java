package IPOS.SA.ACC.Model;

public class LowStockItem {
    private String itemId;
    private String itemName;
    private int currentStock;
    private int minLevel;

    public LowStockItem(String itemId, String itemName, int currentStock, int minLevel) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.currentStock = currentStock;
        this.minLevel = minLevel;
    }

    // Getters
    public String getItemId() { return itemId; }
    public String getItemName() { return itemName; }
    public int getCurrentStock() { return currentStock; }
    public int getMinLevel() { return minLevel; }

    // Setters
    public void setItemId(String itemId) { this.itemId = itemId; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    public void setCurrentStock(int currentStock) { this.currentStock = currentStock; }
    public void setMinLevel(int minLevel) { this.minLevel = minLevel; }
}
