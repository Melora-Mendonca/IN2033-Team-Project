package IPOS.SA.ACC;

import java.time.LocalDate;

public class Payment {

    private final String paymentId;
    private final String merchantId;
    private final String orderId;
    private final double amount;
    private final LocalDate paymentDate;
    private final String paymentReference;

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

    public String getPaymentId() {
        return paymentId;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public String getOrderId() {
        return orderId;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public String getPaymentReference() {
        return paymentReference;
    }
}