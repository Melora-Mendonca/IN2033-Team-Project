package IPOS.SA.ACC.Service;

import IPOS.SA.ORD.Model.Invoice;
import IPOS.SA.ACC.Model.MerchantAccount;
import IPOS.SA.DB.DBConnection;
import IPOS.SA.ORD.Model.Order;
import java.time.LocalDate;
import java.util.UUID;

public class InvoiceService {
    private DBConnection db;

    public InvoiceService() {
        this.db = new DBConnection();
    }

    public Invoice generateInvoice(Order order, MerchantAccount account, double finalAmount) throws Exception {
        String invoiceId = "INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        LocalDate invoiceDate = LocalDate.now();
        LocalDate dueDate = invoiceDate.plusDays(30);

        Invoice invoice = new Invoice(
                invoiceId,
                order.getMerchantId(),
                order.getOrderId(),
                invoiceDate,
                dueDate,
                finalAmount,
                0.0,
                "unpaid"
        );

        // Save to database - using your actual Invoice table columns
        String sql = "INSERT INTO Invoice (invoice_id, order_id, invoice_date, due_date, " +
                "total_amount, amount_paid, status, days_overdue) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, 0)";
        db.update(sql,
                invoice.getInvoiceId(),
                invoice.getOrderId(),
                java.sql.Date.valueOf(invoice.getInvoiceDate()),
                java.sql.Date.valueOf(invoice.getDueDate()),
                invoice.getTotalAmount(),
                invoice.getAmountPaid(),
                invoice.getStatus()
        );

        return invoice;
    }
}