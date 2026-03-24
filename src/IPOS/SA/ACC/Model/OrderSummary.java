package IPOS.SA.ACC.Model;

public class OrderSummary {
    private String orderId;
    private String merchantName;
    private String orderDate;
    private String status;
    private double totalAmount;

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
