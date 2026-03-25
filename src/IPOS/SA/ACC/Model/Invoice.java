package IPOS.SA.ACC.Model;

import IPOS.SA.ORD.OrderItem;

import java.time.LocalDate;
import java.util.List;

public class Invoice {

    private final String invoiceId;
    private final String orderId;
    private final String merchantId;
    private final LocalDate issueDate;
    private final List<OrderItem> items;
    private final double grossTotal;
    private final double discountAmount;
    private final double finalTotal;

    public Invoice(String invoiceId,
                   String orderId,
                   String merchantId,
                   LocalDate issueDate,
                   List<OrderItem> items,
                   double grossTotal,
                   double discountAmount,
                   double finalTotal) {
        this.invoiceId = invoiceId;
        this.orderId = orderId;
        this.merchantId = merchantId;
        this.issueDate = issueDate;
        this.items = items;
        this.grossTotal = grossTotal;
        this.discountAmount = discountAmount;
        this.finalTotal = finalTotal;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public double getGrossTotal() {
        return grossTotal;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public double getFinalTotal() {
        return finalTotal;
    }
}