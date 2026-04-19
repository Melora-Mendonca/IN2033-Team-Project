package IPOS.SA.ACC.Model;

/**
 * Represents a summary of an order for display in the Admin Dashboard.
 * Contains key order details shown in the recent orders table.
 */
public class OrderSummary {
    private String orderId; // The unique ID of the order
    private String merchantName; // the name of the ordering merchant
    private String orderDate; // the date the order was placed
    private String status; // the current status of the order
    private double totalAmount; // the total cost of the order

    /**
     * Constructor — creates an order summary with all required fields.
     *
     * @param orderId      the unique order identifier
     * @param merchantName the name of the merchant
     * @param orderDate    the date the order was placed
     * @param status       the current order status
     * @param totalAmount  the total order value
     */
    public OrderSummary(String orderId, String merchantName, String orderDate,
                        String status, double totalAmount) {
        this.orderId = orderId;
        this.merchantName = merchantName;
        this.orderDate = orderDate;
        this.status = status;
        this.totalAmount = totalAmount;
    }

    // Getters
    public String getOrderId() { return orderId; }
    public String getMerchantName() { return merchantName; }
    public String getOrderDate() { return orderDate; }
    public String getStatus() { return status; }
    public double getTotalAmount() { return totalAmount; }

    // Setters
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public void setMerchantName(String merchantName) { this.merchantName = merchantName; }
    public void setOrderDate(String orderDate) { this.orderDate = orderDate; }
    public void setStatus(String status) { this.status = status; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
}
