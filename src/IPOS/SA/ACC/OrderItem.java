package IPOS.SA.ACC;

public class OrderItem {

    private final String itemId;
    private final int quantity;
    private final double unitPrice;

    public OrderItem(String itemId, int quantity, double unitPrice) {
        this.itemId = itemId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }
    public String getItemId() {
        return itemId;
    }
    public int getQuantity() {
        return quantity;
    }
    public double getUnitPrice() {
        return unitPrice;
    }

    public double getLineTotal() {
        return quantity * unitPrice;
    }
}
