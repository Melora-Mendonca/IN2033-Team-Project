package IPOS.SA.RPT.Model;

import java.util.Date;
/**
 * Data model representing a single invoice record for use in reports.
 * Holds all invoice details needed to generate invoice summary reports
 * and payment status reports in the ReportForm.
 *
 * The balance due is calculated automatically in the constructor
 * as totalAmount minus amountPaid.
 */
public class InvoiceRptData {
    /** The unique invoice identifier. */
    private String invoiceId;
    /** The unique identifier of the merchant this invoice belongs to. */
    private String merchantId;
    /** The display name of the merchant company. */
    private String merchantName;
    /** The order ID this invoice was generated for. */
    private String orderId;
    /** The date the invoice was issued. */
    private Date invoiceDate;
    /** The date payment is due. */
    private Date dueDate;
    /** The total amount invoiced. */
    private double totalAmount;
    /** The amount already paid by the merchant. */
    private double amountPaid;
    /** The remaining unpaid balance — calculated as totalAmount minus amountPaid. */
    private double balanceDue;
    /** The current payment status — unpaid, partial, paid or overdue. */
    private String status;
    /**
     * Constructor — creates an invoice report record with all required fields.
     * The balance due is calculated automatically from totalAmount and amountPaid.
     *
     * @param invoiceId    the unique invoice identifier
     * @param merchantId   the merchant's unique identifier
     * @param merchantName the merchant's company name
     * @param orderId      the associated order identifier
     * @param invoiceDate  the date the invoice was issued
     * @param dueDate      the payment due date
     * @param totalAmount  the total amount invoiced
     * @param amountPaid   the amount already paid
     * @param status       the current payment status
     */
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
    /**
     * Returns the unique invoice identifier.
     *
     * @return the invoice ID
     */
    public String getInvoiceId() { return invoiceId; }
    /**
     * Returns the merchant's unique identifier.
     *
     * @return the merchant ID
     */
    public String getMerchantId() { return merchantId; }
    /**
     * Returns the merchant's company name.
     *
     * @return the merchant name
     */
    public String getMerchantName() { return merchantName; }
    /**
     * Returns the associated order identifier.
     *
     * @return the order ID
     */
    public String getOrderId() { return orderId; }
    /**
     * Returns the date the invoice was issued.
     *
     * @return the invoice date
     */
    public Date getInvoiceDate() { return invoiceDate; }
    /**
     * Returns the payment due date.
     *
     * @return the due date
     */
    public Date getDueDate() { return dueDate; }
    /**
     * Returns the total amount invoiced.
     *
     * @return the total amount
     */
    public double getTotalAmount() { return totalAmount; }
    /**
     * Returns the amount already paid by the merchant.
     *
     * @return the amount paid
     */
    public double getAmountPaid() { return amountPaid; }
    /**
     * Returns the remaining unpaid balance.
     *
     * @return the balance due
     */
    public double getBalanceDue() { return balanceDue; }
    /**
     * Returns the current payment status.
     *
     * @return the status string — unpaid, partial, paid or overdue
     */
    public String getStatus() { return status; }
}