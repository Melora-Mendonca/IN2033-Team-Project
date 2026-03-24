// CatalogueItem represents a single item (medicine/product) in the catalogue.
// Each item contains details such as ID, description, package type, cost and stock information

package IPOS.SA.CAT.Model;

// Fields that store information about each catalogue item
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

    // Constructor used to create a new catalogue item with all required information
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
    public String getItemId() {
        return itemId;
    }

    public String getDescription() {
        return description;
    }

    public String getPackageType() {
        return packageType;
    }

    public String getUnit() {
        return unit;
    }

    public int getUnitsInPack() {
        return unitsInPack;
    }

    public double getPackageCost() {
        return packageCost;
    }

    public int getAvailabilityPacks() {
        return availabilityPacks;
    }

    public int getStockLimitPacks() {
        return stockLimitPacks;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPackageType(String packageType) {
        this.packageType = packageType;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setUnitsInPack(int unitsInPack) {
        this.unitsInPack = unitsInPack;
    }

    public void setPackageCost(double packageCost) {
        this.packageCost = packageCost;
    }

    public void setAvailabilityPacks(int availabilityPacks) {
        this.availabilityPacks = availabilityPacks;
    }

    public void setStockLimitPacks(int stockLimitPacks) {
        this.stockLimitPacks = stockLimitPacks;
    }
}