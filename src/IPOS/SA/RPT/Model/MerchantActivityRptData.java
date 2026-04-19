package IPOS.SA.RPT.Model;

import java.util.Date;
import java.util.List;
/**
 * Data model representing a merchant's full activity for use in reports.
 * Contains the merchant's account details alongside a list of their orders,
 * each containing the items ordered. Used to generate the Merchant Activity
 * Report in ReportForm.
 *
 * Contains two nested static classes:
 * - OrderDetail — represents a single order placed by the merchant
 * - ItemDetail  — represents a single line item within an order
 */
public class MerchantActivityRptData {
    /** The unique identifier of the merchant. */
    private String merchantId;
    /** The merchant's registered company name. */
    private String companyName;
    /** The merchant's contact email address. */
    private String email;
    /** The merchant's contact phone number. */
    private String phone;
    /** The merchant's registered business address. */
    private String address;
    /** The maximum credit allowed for this merchant. */
    private double creditLimit;
    /** The current unpaid balance owed by the merchant. */
    private double outstandingBalance;
    /** The list of orders placed by this merchant in the report period. */
    private List<OrderDetail> orders;
    /** The total combined value of all orders in the report period. */
    private double totalOrderValue;

    /**
     * Represents a single order placed by a merchant.
     * Contains the order's line items, total value, discount applied
     * and the payment status of the corresponding invoice.
     */

    public static class OrderDetail {
        /** The unique order identifier. */
        private String orderId;
        /** The date the order was placed. */
        private Date orderDate;
        /** The list of items included in this order. */
        private List<ItemDetail> items;
        /** The total monetary value of this order after discount. */
        private double orderTotal;
        /** The discount amount applied to this order. */
        private double discountGiven;
        /** The payment status of the invoice for this order. */
        private String paymentStatus;

        // Getters and setters...
        /**
         * Returns the unique order identifier.
         *
         * @return the order ID
         */
        public String getOrderId() { return orderId; }
        /**
         * Sets the unique order identifier.
         *
         * @param orderId the order ID
         */
        public void setOrderId(String orderId) { this.orderId = orderId; }
        /**
         * Returns the date the order was placed.
         *
         * @return the order date
         */
        public Date getOrderDate() { return orderDate; }
        /**
         * Sets the date the order was placed.
         *
         * @param orderDate the order date
         */
        public void setOrderDate(Date orderDate) { this.orderDate = orderDate; }
        /**
         * Returns the list of items in this order.
         *
         * @return the order items
         */
        public List<ItemDetail> getItems() { return items; }
        /**
         * Sets the list of items in this order.
         *
         * @param items the order items
         */
        public void setItems(List<ItemDetail> items) { this.items = items; }
        /**
         * Returns the total value of this order after discount.
         *
         * @return the order total
         */
        public double getOrderTotal() { return orderTotal; }
        /**
         * Sets the total value of this order after discount.
         *
         * @param orderTotal the order total
         */
        public void setOrderTotal(double orderTotal) { this.orderTotal = orderTotal; }
        /**
         * Returns the discount amount applied to this order.
         *
         * @return the discount amount
         */
        public double getDiscountGiven() { return discountGiven; }
        /**
         * Sets the discount amount applied to this order.
         *
         * @param discountGiven the discount amount
         */
        public void setDiscountGiven(double discountGiven) { this.discountGiven = discountGiven; }
        /**
         * Returns the payment status of the invoice for this order.
         *
         * @return the payment status — unpaid, partial, paid or overdue
         */
        public String getPaymentStatus() { return paymentStatus; }
        /**
         * Sets the payment status of the invoice for this order.
         *
         * @param paymentStatus the payment status
         */
        public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    }
    /**
     * Represents a single line item within an order.
     * Contains the catalogue item details, quantity ordered and pricing.
     */
    public static class ItemDetail {
        private String itemId;
        private String description;
        private int quantity;
        private double unitPrice;
        private double totalPrice;

        // Getters and setters...
        /**
         * Returns the unique catalogue item identifier.
         *
         * @return the item ID
         */
        public String getItemId() { return itemId; }
        /**
         * Sets the unique catalogue item identifier.
         *
         * @param itemId the item ID
         */
        public void setItemId(String itemId) { this.itemId = itemId; }
        /**
         * Returns the description of the catalogue item.
         *
         * @return the item description
         */
        public String getDescription() { return description; }
        /**
         * Sets the description of the catalogue item.
         *
         * @param description the item description
         */
        public void setDescription(String description) { this.description = description; }
        /**
         * Returns the quantity of packs ordered.
         *
         * @return the quantity
         */
        public int getQuantity() { return quantity; }
        /**
         * Sets the quantity of packs ordered.
         *
         * @param quantity the quantity
         */
        public void setQuantity(int quantity) { this.quantity = quantity; }
        /**
         * Returns the price per pack at the time of ordering.
         *
         * @return the unit price
         */
        public double getUnitPrice() { return unitPrice; }
        /**
         * Returns the price per pack at the time of ordering.
         *
         * @return the unit price
         */
        public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }
        /**
         * Returns the total price for this line item.
         *
         * @return the total price
         */
        public double getTotalPrice() { return totalPrice; }
        /**
         * Sets the total price for this line item.
         *
         * @param totalPrice the total price
         */
        public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
    }

    // Getters and setters...
    public String getMerchantId() { return merchantId; }
    /**
     * Returns the merchant's unique identifier.
     *
     * @return the merchant ID
     */
    public void setMerchantId(String merchantId) { this.merchantId = merchantId; }
    /**
     * Returns the merchant's registered company name.
     *
     * @return the company name
     */
    public String getCompanyName() { return companyName; }
    /**
     * Returns the merchant's registered company name.
     *
     * @return the company name
     */
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    /**
     * Returns the merchant's contact email address.
     *
     * @return the email address
     */
    public String getEmail() { return email; }
    /**
     * Sets the merchant's contact email address.
     *
     * @param email the email address
     */
    public void setEmail(String email) { this.email = email; }
    /**
     * Returns the merchant's contact phone number.
     *
     * @return the phone number
     */
    public String getPhone() { return phone; }
    /**
     * Sets the merchant's contact phone number.
     *
     * @param phone the phone number
     */
    public void setPhone(String phone) { this.phone = phone; }
    /**
     * Returns the merchant's registered business address.
     *
     * @return the address
     */
    public String getAddress() { return address; }
    /**
     * Sets the merchant's registered business address.
     *
     * @param address the address
     */
    public void setAddress(String address) { this.address = address; }
    /**
     * Returns the maximum credit allowed for this merchant.
     *
     * @return the credit limit
     */
    public double getCreditLimit() { return creditLimit; }
    /**
     * Sets the maximum credit allowed for this merchant.
     *
     * @param creditLimit the credit limit
     */
    public void setCreditLimit(double creditLimit) { this.creditLimit = creditLimit; }
    /**
     * Returns the current unpaid balance owed by the merchant.
     *
     * @return the outstanding balance
     */
    public double getOutstandingBalance() { return outstandingBalance; }
    /**
     * Sets the current unpaid balance owed by the merchant.
     *
     * @param outstandingBalance the outstanding balance
     */
    public void setOutstandingBalance(double outstandingBalance) { this.outstandingBalance = outstandingBalance; }
    /**
     * Returns the list of orders placed by this merchant in the report period.
     *
     * @return the list of order details
     */
    public List<OrderDetail> getOrders() { return orders; }
    /**
     * Sets the list of orders placed by this merchant in the report period.
     *
     * @param orders the list of order details
     */
    public void setOrders(List<OrderDetail> orders) { this.orders = orders; }
    /**
     * Returns the total combined value of all orders in the report period.
     *
     * @return the total order value
     */
    public double getTotalOrderValue() { return totalOrderValue; }
    /**
     * Sets the total combined value of all orders in the report period.
     *
     * @param totalOrderValue the total order value
     */
    public void setTotalOrderValue(double totalOrderValue) { this.totalOrderValue = totalOrderValue; }
}