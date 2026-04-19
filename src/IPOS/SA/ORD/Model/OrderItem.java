package IPOS.SA.ORD.Model;

/**
 * Represents a single line item within an order.
 * Contains the catalogue item ID, quantity ordered and unit price.
 * The line total is calculated automatically as quantity multiplied by unit price.
 */
public class OrderItem {

    private final String itemId;
    private final int quantity;
    private final double unitPrice;

    /**
     * Constructor — creates an order item with all required details.
     *
     * @param itemId the unique catalogue item identifier
     * @param quantity the number of packs ordered
     * @param unitPrice the price per pack at time of ordering
     */
    public OrderItem(String itemId, int quantity, double unitPrice) {
        this.itemId = itemId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    /**
     * Returns the unique catalogue item identifier
     * @return the item ID
     */
    public String getItemId() {
        return itemId;
    }

    /**
     * Returns the number of packs ordered.
     *
     * @return the quantity
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Returns the price per pack at the time the order was placed.
     *
     * @return the unit price
     */
    public double getUnitPrice() {
        return unitPrice;
    }

    /**
     * Returns the total price for this line item.
     * Calculated as quantity multiplied by unit price.
     *
     * @return the line total
     */
    public double getLineTotal() {
        return quantity * unitPrice;
    }
}
