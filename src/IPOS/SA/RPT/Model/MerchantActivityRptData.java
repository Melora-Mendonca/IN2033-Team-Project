package IPOS.SA.RPT.Model;

import java.util.Date;
import java.util.List;

public class MerchantActivityRptData {
    private String merchantId;
    private String companyName;
    private String email;
    private String phone;
    private String address;
    private double creditLimit;
    private double outstandingBalance;
    private List<OrderDetail> orders;
    private double totalOrderValue;

    public static class OrderDetail {
        private String orderId;
        private Date orderDate;
        private List<ItemDetail> items;
        private double orderTotal;
        private double discountGiven;
        private String paymentStatus;

        // Getters and setters...
        public String getOrderId() { return orderId; }
        public void setOrderId(String orderId) { this.orderId = orderId; }
        public Date getOrderDate() { return orderDate; }
        public void setOrderDate(Date orderDate) { this.orderDate = orderDate; }
        public List<ItemDetail> getItems() { return items; }
        public void setItems(List<ItemDetail> items) { this.items = items; }
        public double getOrderTotal() { return orderTotal; }
        public void setOrderTotal(double orderTotal) { this.orderTotal = orderTotal; }
        public double getDiscountGiven() { return discountGiven; }
        public void setDiscountGiven(double discountGiven) { this.discountGiven = discountGiven; }
        public String getPaymentStatus() { return paymentStatus; }
        public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    }

    public static class ItemDetail {
        private String itemId;
        private String description;
        private int quantity;
        private double unitPrice;
        private double totalPrice;

        // Getters and setters...
        public String getItemId() { return itemId; }
        public void setItemId(String itemId) { this.itemId = itemId; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
        public double getUnitPrice() { return unitPrice; }
        public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }
        public double getTotalPrice() { return totalPrice; }
        public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
    }

    // Getters and setters...
    public String getMerchantId() { return merchantId; }
    public void setMerchantId(String merchantId) { this.merchantId = merchantId; }
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public double getCreditLimit() { return creditLimit; }
    public void setCreditLimit(double creditLimit) { this.creditLimit = creditLimit; }
    public double getOutstandingBalance() { return outstandingBalance; }
    public void setOutstandingBalance(double outstandingBalance) { this.outstandingBalance = outstandingBalance; }
    public List<OrderDetail> getOrders() { return orders; }
    public void setOrders(List<OrderDetail> orders) { this.orders = orders; }
    public double getTotalOrderValue() { return totalOrderValue; }
    public void setTotalOrderValue(double totalOrderValue) { this.totalOrderValue = totalOrderValue; }
}