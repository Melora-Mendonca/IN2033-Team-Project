package IPOS.SA.ORD.Model;

import java.time.LocalDate;
import java.util.List;

/**
 * Represents an invoice generated for an accepted order in IPOS-SA.
 * An invoice is created automatically when an order is accepted
 * and tracks the total amount, amount paid and payment status.
 * Days overdue is calculated dynamically from the due date.
 */
public class Invoice {

    private final String invoiceId;
    private final String orderId;
    private final String merchantId;
    private final LocalDate invoiceDate;
    private final LocalDate dueDate;
    private final double totalAmount;
    private double amountPaid;
    private String status;
    private int daysOverdue;
    private List<OrderItem> items;

    /**
     * Full constructor; creates an invoice with all details including line items.
     * Used when displaying the full invoice in InvoiceDisplayFrame.
     *
     * @param invoiceId the unique invoice identifier
     * @param orderId the associated order identifier
     * @param merchantId the merchant this invoice belongs to
     * @param invoiceDate the date the invoice was issued
     * @param dueDate the payment due date
     * @param totalAmount the total amount invoiced
     * @param amountPaid the amount already paid
     * @param status  the current payment status
     * @param items the line items included in this invoice
     */
    public Invoice(String invoiceId,
                   String orderId,
                   String merchantId,
                   LocalDate invoiceDate,
                   LocalDate dueDate,
                   double totalAmount,
                   double amountPaid,
                   String status,
                   List<OrderItem> items) {
        this.invoiceId = invoiceId;
        this.orderId = orderId;
        this.merchantId = merchantId;
        this.invoiceDate = invoiceDate;
        this.dueDate = dueDate;
        this.totalAmount = totalAmount;
        this.amountPaid = amountPaid;
        this.status = status;
        this.daysOverdue = calculateDaysOverdue(dueDate);
        this.items = items;
    }

    /**
     * Simple constructor; creates an invoice without line items.
     * Used for list view display where item details are not needed.
     *
     * @param invoiceId the unique invoice identifier
     * @param orderId the associated order identifier
     * @param merchantId the merchant this invoice belongs to
     * @param invoiceDate the date the invoice was issued
     * @param dueDate the payment due date
     * @param totalAmount the total amount invoiced
     * @param amountPaid the amount already paid
     * @param status the current payment status
     */
    public Invoice(String invoiceId,
                   String orderId,
                   String merchantId,
                   LocalDate invoiceDate,
                   LocalDate dueDate,
                   double totalAmount,
                   double amountPaid,
                   String status) {
        this(invoiceId, orderId, merchantId, invoiceDate, dueDate, totalAmount, amountPaid, status, null);
    }

    /**
     * Calculates the number of days this invoice is overdue.
     * Returns zero if the due date has not yet passed.
     *
     * @param dueDate the payment due date
     * @return the number of days overdue, or 0 if not yet overdue
     */
    private int calculateDaysOverdue(LocalDate dueDate) {
        if (dueDate == null) return 0;
        LocalDate today = LocalDate.now();
        if (today.isAfter(dueDate)) {
            return (int) java.time.temporal.ChronoUnit.DAYS.between(dueDate, today);
        }
        return 0;
    }

    /**
     * Gets the Invoice ID of the order
     * @return invoiceId the id of the invoice
     */
    public String getInvoiceId() {
        return invoiceId;
    }

    /**
     * Gets the order ID of the order
     * @return orderId the id of the order
     */
    public String getOrderId() {
        return orderId;
    }

    /**
     * Gets the merchant ID of the order
     * @return merchantId the id of the merchant
     */
    public String getMerchantId() {
        return merchantId;
    }

    /**
     * Gets the date the invoice was created
     * @return invoiceDate the date the invoice was created
     */
    public LocalDate getInvoiceDate() {
        return invoiceDate;
    }

    /**
     * Gets the date the invoice is due by
     * @return dueDate the date the invoice is due
     */
    public LocalDate getDueDate() {
        return dueDate;
    }

    /**
     * Gets the total amount of the order invoice
     * @return totalAmount the total cost of the order
     */
    public double getTotalAmount() {
        return totalAmount;
    }

    /**
     * Gets the total amount of the order invoice that has been paid
     * @return amountPaid the total payment of the order
     */
    public double getAmountPaid() {
        return amountPaid;
    }

    /**
     * Returns the remaining unpaid balance.
     * @return totalAmount minus amountPaid
     */
    public double getOutstandingBalance() {
        return totalAmount - amountPaid;
    }

    /**
     * Returns the current payment status for the order
     * @return the current payment status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Returns the number of days this invoice is overdue.
     * Calculated dynamically each time to reflect the current date.
     *
     * @return days overdue, or 0 if not yet overdue
     */
    public int getDaysOverdue() {
        return calculateDaysOverdue(dueDate);
    }

    /**
     * returns the list of items in the invoice
     * @return the invoice items, or null if not loaded
     */
    public List<OrderItem> getItems() {
        return items;
    }

    /**
     * Sets the amount paid and automatically updates the payment status.
     * Status is set to paid, partial, overdue or unpaid based on the new amount.
     *
     * @param amountPaid the new total amount paid
     */
    public void setAmountPaid(double amountPaid) {
        this.amountPaid = amountPaid;
        // Update status based on payment
        if (this.amountPaid >= totalAmount) {
            this.status = "paid";
        } else if (this.amountPaid > 0) {
            this.status = "partial";
        } else if (getDaysOverdue() > 0) {
            this.status = "overdue";
        } else {
            this.status = "unpaid";
        }
    }

    /**
     * sets the Status of the payment for the invoice
     *
     * @param status the new payment status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Returns whether this invoice has been fully paid.
     *
     * @return true if amountPaid is greater than or equal to totalAmount
     */
    public boolean isPaid() {
        return amountPaid >= totalAmount;
    }

    /**
     * Returns whether this invoice is currently overdue.
     * An invoice is overdue if today is after the due date and it is not fully paid.
     *
     * @return true if the invoice is past its due date and unpaid
     */
    public boolean isOverdue() {
        return LocalDate.now().isAfter(dueDate) && !isPaid();
    }

    /**
     * Records a payment against this invoice.
     * Increases the amount paid and updates the status to partial or paid.
     * Does nothing if the payment amount is zero or negative.
     *
     * @param paymentAmount the amount being paid
     */
    public void recordPayment(double paymentAmount) {
        if (paymentAmount > 0) {
            this.amountPaid += paymentAmount;
            if (this.amountPaid >= totalAmount) {
                this.status = "paid";
            } else {
                this.status = "partial";
            }
        }
    }

    /**
     * Returns a string representation of the invoice showing key fields.
     *
     * @return formatted invoice summary string
     */
    @Override
    public String toString() {
        return "Invoice{" +
                "invoiceId='" + invoiceId + '\'' +
                ", orderId='" + orderId + '\'' +
                ", merchantId='" + merchantId + '\'' +
                ", invoiceDate=" + invoiceDate +
                ", dueDate=" + dueDate +
                ", totalAmount=" + totalAmount +
                ", amountPaid=" + amountPaid +
                ", status='" + status + '\'' +
                ", daysOverdue=" + getDaysOverdue() +
                '}';
    }

}