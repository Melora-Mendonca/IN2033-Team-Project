package IPOS.SA.ORD.Model;

import IPOS.SA.ORD.OrderStatus;

import java.time.LocalDate;
import java.util.List;

/**
 * Represents an order placed by a merchant in IPOS-SA.
 * Stores the order details, line items, financial totals
 * and dispatch information once the order has been sent.
 *
 * Orders progress through the following workflow:
 * pending → accepted → processing → dispatched → delivered
 */
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
    private double totalAmount;

    /**
     * Constructor; creates a new order with the given details.
     * Status defaults to ACCEPTED on creation.
     *
     * @param orderId the unique order identifier
     * @param merchantId the ID of the merchant who placed the order
     * @param orderDate the date the order was placed
     * @param items the list of items ordered
     */
    public Order(String orderId, String merchantId, LocalDate orderDate, List<OrderItem> items) {
        this.orderId = orderId;
        this.merchantId = merchantId;
        this.orderDate = orderDate;
        this.items = items;
        this.status = OrderStatus.ACCEPTED;
    }

    /**
     * Returns the unique order identifier.
     *
     * @return the order ID
     */
    public String getOrderId() {
        return orderId;
    }

    /**
     * Returns the ID of the merchant who placed the order.
     *
     * @return the merchant ID
     */
    public String getMerchantId() {
        return merchantId;
    }

    /**
     * Returns the date the order was placed.
     *
     * @return the order date
     */
    public LocalDate getOrderDate() {
        return orderDate;
    }

    /**
     * Returns the current status of the order.
     *
     * @return the order status enum value
     */
    public OrderStatus getStatus() {
        return status;
    }

    /**
     * Returns the list of items included in this order.
     *
     * @return the order items
     */
    public List<OrderItem> getItems() {
        return items;
    }

    /**
     * Sets the current status of the order.
     * Used to advance the order through the workflow stages.
     *
     * @param status the new order status
     */
    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    /**
     * Calculates the gross total of the order by summing all line item totals.
     *
     * @return the sum of all line totals before discount
     */
    public double calculateOrderTotal() {
        double total = 0;
        for (OrderItem item : items) {
            total += item.getLineTotal();
        }
        return total;
    }

    /**
     * Returns the gross order total before any discount is applied.
     *
     * @return the gross total amount
     */
    public double getTotalAmount() {
        return totalAmount;
    }

    /**
     * Sets the gross order total before any discount is applied.
     *
     * @param totalAmount the gross total amount
     */
    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    /**
     * Returns the discount amount applied to this order.
     *
     * @return the discount amount
     */
    public double getDiscountApplied() {
        return discountApplied;
    }

    /**
     * Sets the discount amount applied to this order.
     *
     * @param discountApplied the discount amount
     */
    public void setDiscountApplied(double discountApplied) {
        this.discountApplied = discountApplied;
    }

    /**
     * Returns the final order value after the discount has been applied.
     *
     * @return the final amount
     */
    public double getFinalAmount() {
        return finalAmount;
    }

    /**
     * Sets the final order value after the discount has been applied.
     *
     * @param finalAmount the final amount after discount
     */
    public void setFinalAmount(double finalAmount) {
        this.finalAmount = finalAmount;
    }

    /**
     * Returns the full name of the staff member who dispatched the order.
     *
     * @return the name of the dispatching staff member, or null if not yet dispatched
     */
    public String getDispatchedBy() {
        return dispatchedBy;
    }

    /**
     * Sets the full name of the staff member who dispatched the order.
     *
     * @param dispatchedBy the name of the dispatching staff member
     */
    public void setDispatchedBy(String dispatchedBy) {
        this.dispatchedBy = dispatchedBy;
    }

    /**
     * Returns the date the order was dispatched.
     *
     * @return the dispatch date, or null if not yet dispatched
     */
    public LocalDate getDispatchedDate() {
        return dispatchedDate;
    }

    /**
     * Sets the date the order was dispatched.
     *
     * @param dispatchedDate the date the order was dispatched
     */
    public void setDispatchedDate(LocalDate dispatchedDate) {
        this.dispatchedDate = dispatchedDate;
    }

    /**
     * Returns the name of the courier service used for delivery.
     *
     * @return the courier name, or null if not yet dispatched
     */
    public String getCourierName() {
        return courierName;
    }

    /**
     * Sets the name of the courier service used for delivery.
     *
     * @param courierName the courier service name
     */
    public void setCourierName(String courierName) {
        this.courierName = courierName;
    }

    /**
     * Returns the courier tracking reference number.
     *
     * @return the courier reference number, or null if not yet dispatched
     */
    public String getCourierRefNo() {
        return courierRefNo;
    }

    /**
     * Sets the courier tracking reference number.
     *
     * @param courierRefNo the courier tracking reference number
     */
    public void setCourierRefNo(String courierRefNo) {
        this.courierRefNo = courierRefNo;
    }

    /**
     * Returns the expected delivery date provided by the courier.
     *
     * @return the expected delivery date, or null if not yet dispatched
     */
    public LocalDate getExpectedDeliveryDate() {
        return expectedDeliveryDate;
    }

    /**
     * Sets the expected delivery date provided by the courier.
     *
     * @param expectedDeliveryDate the expected delivery date
     */
    public void setExpectedDeliveryDate(LocalDate expectedDeliveryDate) {
        this.expectedDeliveryDate = expectedDeliveryDate;
    }
}