package IPOS.SA.ACC.Model;

import java.time.LocalDate;

/**
 * Represents a payment made by a merchant against an invoice.
 * Payments are recorded by accounting staff when a merchant
 * settles their outstanding balance via bank transfer, card or cheque.
 */
public class Payment {

    private final String paymentId; // the unique id of the payment
    private final String merchantId; // the merchant that made the payment
    private final String orderId; // the order id of the order the payment was for
    private final double amount; // the amount being paid
    private final LocalDate paymentDate; // the date of the payment
    private final String paymentReference; // the reference for the payment

    /**
     * Constructor — creates a payment record with all required details.
     *
     * @param paymentId unique payment identifier
     * @param merchantId the merchant who made the payment
     * @param orderId the order this payment is for
     * @param amount the amount paid
     * @param paymentDate the date payment was received
     * @param paymentReference the payment reference number
     */
    public Payment(String paymentId,
                   String merchantId,
                   String orderId,
                   double amount,
                   LocalDate paymentDate,
                   String paymentReference) {
        this.paymentId = paymentId;
        this.merchantId = merchantId;
        this.orderId = orderId;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.paymentReference = paymentReference;
    }

    /**
     * Gets the payment ID for the payment
     * @return the unique payment identifier
     */
    public String getPaymentId() {
        return paymentId;
    }

    /**
     * Gets the merchant ID for the payment
     * @return the ID of the merchant who made the payment
     */
    public String getMerchantId() {
        return merchantId;
    }

    /**
     * Gets the order ID for the payment
     * @return the ID of the order this payment is for
     */
    public String getOrderId() {
        return orderId;
    }

    /**
     * Gets the amount due for the payment
     * @return the amount paid
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Gets the payment ID for the payment
     * @return the date the payment was received
     */
    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    /**
     * Gets the payment reference for the payment
     * @return the payment reference number
     */
    public String getPaymentReference() {
        return paymentReference;
    }
}