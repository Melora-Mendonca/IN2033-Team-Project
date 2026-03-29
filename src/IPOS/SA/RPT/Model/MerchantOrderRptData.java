package IPOS.SA.RPT.Model;

import java.util.Date;

public class MerchantOrderRptData {
    private String orderId;
    private Date orderDate;
    private double orderValue;
    private Date dispatchedDate;
    private String paymentStatus;

    public MerchantOrderRptData(String orderId, Date orderDate, double orderValue,
                                   Date dispatchedDate, String paymentStatus) {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.orderValue = orderValue;
        this.dispatchedDate = dispatchedDate;
        this.paymentStatus = paymentStatus;
    }

    // Getters
    public String getOrderId() { return orderId; }
    public Date getOrderDate() { return orderDate; }
    public double getOrderValue() { return orderValue; }
    public Date getDispatchedDate() { return dispatchedDate; }
    public String getPaymentStatus() { return paymentStatus; }
}