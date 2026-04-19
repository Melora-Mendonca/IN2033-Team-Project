// CatalogueItem represents a single item (medicine/product) in the catalogue.
// Each item contains details such as ID, description, package type, cost and stock information

package IPOS.SA.CAT.Model;

/**
 * Represents a single pharmaceutical product in the IPOS-SA catalogue.
 * Stores all details about a catalogue item including packaging information,
 * pricing and current stock availability.
 * Used by the catalogue screen, order processing and stock management.
 */
public class CatalogueItem {

    // unique ID for the item
    private final String itemId;

    // name/description of the medicine
    private String description;

    // how the item is packaged (box, bottle etc)
    private String packageType;

    // unit type (caps, ml etc)
    private String unit;

    // number of units inside one pack
    private int unitsInPack;

    // price of one package
    private double packageCost;

    // How many packages are currentky available in stock
    private int availabilityPacks;

    // stock limit used internally to order new stock
    private int stockLimitPacks;

    /**
     * Constructor — creates a catalogue item with all required details.
     *
     * @param itemId the unique item identifier
     * @param description the item name or description
     * @param packageType the packaging type
     * @param unit the unit type for individual doses
     * @param unitsInPack the number of units per pack
     * @param packageCost the cost per package in pounds
     * @param availabilityPacks the current stock level in packs
     * @param stockLimitPacks the minimum stock level before a warning is triggered
     */
    public CatalogueItem(String itemId, String description, String packageType, String unit,
                         int unitsInPack, double packageCost, int availabilityPacks, int stockLimitPacks) {
        this.itemId = itemId;
        this.description = description;
        this.packageType = packageType;
        this.unit = unit;
        this.unitsInPack = unitsInPack;
        this.packageCost = packageCost;
        this.availabilityPacks = availabilityPacks;
        this.stockLimitPacks = stockLimitPacks;
    }


    // Getter methods used to retrieve information about the catalogue item
    /**
     * Returns the unique item identifier.
     *
     * @return the item ID
     */
    public String getItemId() {
        return itemId;
    }

    /**
     * Returns the item name or description.
     *
     * @return the item description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the packaging type.
     *
     * @return the package type
     */
    public String getPackageType() {
        return packageType;
    }

    /**
     * Returns the unit type for individual doses.
     *
     * @return the unit type
     */
    public String getUnit() {
        return unit;
    }

    /**
     * Returns the number of individual units per pack.
     *
     * @return units per pack
     */
    public int getUnitsInPack() {
        return unitsInPack;
    }

    /**
     * Returns the cost per package in pounds.
     *
     * @return the package cost
     */
    public double getPackageCost() {
        return packageCost;
    }

    /**
     * Returns the current number of packs available in stock.
     *
     * @return current stock level in packs
     */
    public int getAvailabilityPacks() {
        return availabilityPacks;
    }

    /**
     * Returns the minimum stock level.
     * A low stock warning is triggered when availability falls below this value.
     *
     * @return the minimum stock limit in packs
     */
    public int getStockLimitPacks() {
        return stockLimitPacks;
    }

    /**
     * Sets the item description.
     *
     * @param description the new item description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Sets the packaging type.
     *
     * @param packageType the new package type
     */
    public void setPackageType(String packageType) {
        this.packageType = packageType;
    }

    /**
     * Sets the unit type for individual doses.
     *
     * @param unit the new unit type
     */
    public void setUnit(String unit) {
        this.unit = unit;
    }

    /**
     * Sets the number of individual units per pack.
     *
     * @param unitsInPack the new units per pack value
     */
    public void setUnitsInPack(int unitsInPack) {
        this.unitsInPack = unitsInPack;
    }

    /**
     * Sets the cost per package in pounds.
     *
     * @param packageCost the new package cost
     */
    public void setPackageCost(double packageCost) {
        this.packageCost = packageCost;
    }

    /**
     * Sets the current stock level in packs.
     *
     * @param availabilityPacks the new availability count
     */
    public void setAvailabilityPacks(int availabilityPacks) {
        this.availabilityPacks = availabilityPacks;
    }

    /**
     * Sets the minimum stock level.
     *
     * @param stockLimitPacks the new minimum stock limit
     */
    public void setStockLimitPacks(int stockLimitPacks) {
        this.stockLimitPacks = stockLimitPacks;
    }
}