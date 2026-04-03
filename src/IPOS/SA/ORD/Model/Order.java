package IPOS.SA.ORD.Model;

import IPOS.SA.ORD.OrderStatus;

import java.time.LocalDate;
import java.util.List;

public class Order {

    private final String orderId;
    private final String merchantId;
    private final LocalDate orderDate;
    private OrderStatus status;
    private final List<OrderItem> items;
    private double discountApplied;
    private double finalAmount;
    private String dispatchedBy;
    private LocalDate dispatchedDate;
    private String courierName;
    private String courierRefNo;
    private LocalDate expectedDeliveryDate;

    public Order(String orderId, String merchantId, LocalDate orderDate, List<OrderItem> items) {
        this.orderId = orderId;
        this.merchantId = merchantId;
        this.orderDate = orderDate;
        this.items = items;
        this.status = OrderStatus.ACCEPTED;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public double calculateOrderTotal() {
        double total = 0;
        for (OrderItem item : items) {
            total += item.getLineTotal();
        }
        return total;
    }
    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    private double totalAmount;

    public double getDiscountApplied() {
        return discountApplied;
    }

    public void setDiscountApplied(double discountApplied) {
        this.discountApplied = discountApplied;
    }

    public double getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(double finalAmount) {
        this.finalAmount = finalAmount;
    }

    public String getDispatchedBy() {
        return dispatchedBy;
    }

    public void setDispatchedBy(String dispatchedBy) {
        this.dispatchedBy = dispatchedBy;
    }

    public LocalDate getDispatchedDate() {
        return dispatchedDate;
    }

    public void setDispatchedDate(LocalDate dispatchedDate) {
        this.dispatchedDate = dispatchedDate;
    }

    public String getCourierName() {
        return courierName;
    }

    public void setCourierName(String courierName) {
        this.courierName = courierName;
    }

    public String getCourierRefNo() {
        return courierRefNo;
    }

    public void setCourierRefNo(String courierRefNo) {
        this.courierRefNo = courierRefNo;
    }

    public LocalDate getExpectedDeliveryDate() {
        return expectedDeliveryDate;
    }

    public void setExpectedDeliveryDate(LocalDate expectedDeliveryDate) {
        this.expectedDeliveryDate = expectedDeliveryDate;
    }
}