package IPOS.SA.RPT.Model;

import java.util.Date;

public class InvoiceRptData {
    private String invoiceId;
    private String merchantId;
    private String merchantName;
    private String orderId;
    private Date invoiceDate;
    private Date dueDate;
    private double totalAmount;
    private double amountPaid;
    private double balanceDue;
    private String status;

    public InvoiceRptData(String invoiceId, String merchantId, String merchantName, String orderId,
                       Date invoiceDate, Date dueDate, double totalAmount, double amountPaid, String status) {
        this.invoiceId = invoiceId;
        this.merchantId = merchantId;
        this.merchantName = merchantName;
        this.orderId = orderId;
        this.invoiceDate = invoiceDate;
        this.dueDate = dueDate;
        this.totalAmount = totalAmount;
        this.amountPaid = amountPaid;
        this.balanceDue = totalAmount - amountPaid;
        this.status = status;
    }

    // Getters
    public String getInvoiceId() { return invoiceId; }
    public String getMerchantId() { return merchantId; }
    public String getMerchantName() { return merchantName; }
    public String getOrderId() { return orderId; }
    public Date getInvoiceDate() { return invoiceDate; }
    public Date getDueDate() { return dueDate; }
    public double getTotalAmount() { return totalAmount; }
    public double getAmountPaid() { return amountPaid; }
    public double getBalanceDue() { return balanceDue; }
    public String getStatus() { return status; }
}