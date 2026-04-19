package IPOS.SA.ORD.Model;

import java.time.LocalDate;
import java.util.List;

// Represents an invoice tied to an order; status updates automatically on payment
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
    private List<OrderItem> items;  // Optional - for detailed invoice view

    // Constructor for full invoice (with items)
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

    // Constructor for simple invoice (without items - for list views)
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

    // Helper method to calculate overdue days
    private int calculateDaysOverdue(LocalDate dueDate) {
        if (dueDate == null) return 0;
        LocalDate today = LocalDate.now();
        if (today.isAfter(dueDate)) {
            return (int) java.time.temporal.ChronoUnit.DAYS.between(dueDate, today);
        }
        return 0;
    }

    // Getters
    public String getInvoiceId() {
        return invoiceId;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public LocalDate getInvoiceDate() {
        return invoiceDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public double getAmountPaid() {
        return amountPaid;
    }

    public double getOutstandingBalance() {
        return totalAmount - amountPaid;
    }

    public String getStatus() {
        return status;
    }

    public int getDaysOverdue() {
        return calculateDaysOverdue(dueDate);
    }

    public List<OrderItem> getItems() {
        return items;
    }

    // Setters (for fields that can change)
    public void setAmountPaid(double amountPaid) {
        this.amountPaid = amountPaid;
        // derive status from payment amount
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

    public void setStatus(String status) {
        this.status = status;
    }

    // Business methods
    public boolean isPaid() {
        return amountPaid >= totalAmount;
    }

    public boolean isOverdue() {
        return LocalDate.now().isAfter(dueDate) && !isPaid();
    }

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

    public void setDaysOverdue(int daysOverdue) {

    }
}