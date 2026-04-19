package IPOS.SA.RPT.Model;

import java.util.Date;
/**
 * Data model representing a single order record for use in the
 * Merchant Order History report.
 * Contains a summary of an order including its value, dispatch date
 * and the payment status of the corresponding invoice.
 */
public class MerchantOrderRptData {
    /** The unique order identifier. */
    private String orderId;
    /** The date the order was placed. */
    private Date orderDate;
    /** The total monetary value of the order after discount. */
    private double orderValue;
    /** The date the order was dispatched, or null if not yet dispatched. */
    private Date dispatchedDate;
    /** The payment status of the invoice for this order. */
    private String paymentStatus;

    /**
     * Constructor — creates an order report record with all required details.
     *
     * @param orderId       the unique order identifier
     * @param orderDate     the date the order was placed
     * @param orderValue    the total order value after discount
     * @param dispatchedDate the date the order was dispatched, or null if not dispatched
     * @param paymentStatus the payment status of the corresponding invoice
     */
    public MerchantOrderRptData(String orderId, Date orderDate, double orderValue,
                                   Date dispatchedDate, String paymentStatus) {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.orderValue = orderValue;
        this.dispatchedDate = dispatchedDate;
        this.paymentStatus = paymentStatus;
    }

    // Getters
    /**
     * Returns the unique order identifier.
     *
     * @return the order ID
     */
    public String getOrderId() { return orderId; }
    /**
     * Returns the date the order was placed.
     *
     * @return the order date
     */
    public Date getOrderDate() { return orderDate; }
    /**
     * Returns the total value of the order after discount.
     *
     * @return the order value
     */
    public double getOrderValue() { return orderValue; }
    /**
     * Returns the date the order was dispatched.
     * Returns null if the order has not yet been dispatched.
     *
     * @return the dispatch date, or null
     */
    public Date getDispatchedDate() { return dispatchedDate; }
    /**
     * Returns the payment status of the invoice for this order.
     *
     * @return the payment status — unpaid, partial, paid or overdue
     */
    public String getPaymentStatus() { return paymentStatus; }
}