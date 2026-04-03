package IPOS.SA.ACC.Service;

import IPOS.SA.ACC.Model.Payment;
import IPOS.SA.DB.DBConnection;
import IPOS.SA.DB.InvoiceDBConnector;

import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PaymentService {

    private final DBConnection db;
    private final InvoiceDBConnector invoiceDB;

    public PaymentService() {
        this.db        = new DBConnection();
        this.invoiceDB = new InvoiceDBConnector();
    }

    // Records a payment against an invoice
    // Updates RecordPayment table, Invoice amount_paid/status,
    // and CommercialMembership outstanding_balance
    public void recordPayment(String invoiceId, double amount,
                              String method, String reference) throws Exception {

        // Get invoice details first
        ResultSet invoiceRs = db.query(
                "SELECT i.total_amount, i.amount_paid, o.merchant_id " +
                        "FROM Invoice i JOIN `Order` o ON i.order_id = o.order_id " +
                        "WHERE i.invoice_id = ?", invoiceId
        );

        if (!invoiceRs.next()) throw new Exception("Invoice not found: " + invoiceId);

        double totalAmount  = invoiceRs.getDouble("total_amount");
        double alreadyPaid  = invoiceRs.getDouble("amount_paid");
        String merchantId   = invoiceRs.getString("merchant_id");
        double remaining    = totalAmount - alreadyPaid;

        if (amount <= 0)        throw new Exception("Amount must be greater than zero.");
        if (amount > remaining) throw new Exception("Amount exceeds remaining balance of £" +
                String.format("%.2f", remaining));

        // Insert into RecordPayment
        db.update(
                "INSERT INTO RecordPayment (amount, payment_date, payment_method, " +
                        "reference_number, invoice_id, userlogin_user_id) VALUES (?,CURRENT_DATE(),?,?,?,1)",
                amount,
                method,
                reference.isEmpty() ? null : reference,
                invoiceId
        );

        // Update invoice amount_paid and status
        double newAmountPaid = alreadyPaid + amount;
        invoiceDB.updatePayment(invoiceId, newAmountPaid);

        // Update merchant outstanding balance and last payment date
        db.update(
                "UPDATE CommercialMembership SET " +
                        "outstanding_balance = outstanding_balance - ?, " +
                        "last_payment_date = CURRENT_DATE() " +
                        "WHERE merchant_id = ?",
                amount, merchantId
        );

        // Check if account should be restored from suspended/in_default
        // If balance is now 0 and status is suspended or in_default, restore to normal
        ResultSet merchantRs = db.query(
                "SELECT outstanding_balance, account_status FROM CommercialMembership WHERE merchant_id = ?",
                merchantId
        );
        if (merchantRs.next()) {
            double newBalance = merchantRs.getDouble("outstanding_balance");
            String status     = merchantRs.getString("account_status");
            if (newBalance <= 0 && (status.equals("suspended") || status.equals("in_default"))) {
                db.update(
                        "UPDATE CommercialMembership SET account_status = 'normal', " +
                                "outstanding_balance = 0 WHERE merchant_id = ?",
                        merchantId
                );
            }
        }
    }

    // Gets all invoices for display with merchant name
    public List<Object[]> getAllInvoices(String statusFilter, String search) throws Exception {
        List<Object[]> rows = new ArrayList<>();

        String sql =
                "SELECT i.invoice_id, i.order_id, m.company_name, i.invoice_date, i.due_date, " +
                        "i.total_amount, i.amount_paid, i.status, i.days_overdue " +
                        "FROM Invoice i " +
                        "JOIN `Order` o ON i.order_id = o.order_id " +
                        "JOIN CommercialMembership m ON o.merchant_id = m.merchant_id " +
                        "WHERE 1=1";

        if (!statusFilter.equals("All")) sql += " AND i.status = '" + statusFilter + "'";
        if (!search.isEmpty())           sql += " AND (i.invoice_id LIKE '%" + search +
                "%' OR m.company_name LIKE '%" + search + "%')";
        sql += " ORDER BY i.invoice_date DESC";

        ResultSet rs = db.query(sql);
        while (rs.next()) {
            rows.add(new Object[]{
                    rs.getString("invoice_id"),
                    rs.getString("order_id"),
                    rs.getString("company_name"),
                    rs.getString("invoice_date"),
                    rs.getString("due_date"),
                    String.format("%.2f", rs.getDouble("total_amount")),
                    String.format("%.2f", rs.getDouble("amount_paid")),
                    rs.getString("status"),
                    rs.getInt("days_overdue")
            });
        }
        return rows;
    }

    // Gets payment history for a specific invoice
    public List<Object[]> getPaymentHistory(String invoiceId) throws Exception {
        List<Object[]> rows = new ArrayList<>();
        ResultSet rs = db.query(
                "SELECT amount, payment_date, payment_method, reference_number " +
                        "FROM RecordPayment WHERE invoice_id = ? ORDER BY payment_date",
                invoiceId
        );
        while (rs.next()) {
            rows.add(new Object[]{
                    rs.getString("payment_date"),
                    String.format("%.2f", rs.getDouble("amount")),
                    rs.getString("payment_method"),
                    rs.getString("reference_number") != null ? rs.getString("reference_number") : "—"
            });
        }
        return rows;
    }

    // Gets order items for invoice details view
    public List<Object[]> getOrderItems(String orderId) throws Exception {
        List<Object[]> rows = new ArrayList<>();
        ResultSet rs = db.query(
                "SELECT c.description, oi.quantity, oi.unit_price, oi.total_price " +
                        "FROM OrderItem oi JOIN Catalogue c ON oi.catalogue_item_id = c.item_id " +
                        "WHERE oi.order_id = ?", orderId
        );
        while (rs.next()) {
            rows.add(new Object[]{
                    rs.getString("description"),
                    rs.getInt("quantity"),
                    String.format("%.2f", rs.getDouble("unit_price")),
                    String.format("%.2f", rs.getDouble("total_price"))
            });
        }
        return rows;
    }

    // Gets debtors — merchants with overdue invoices for reminders screen
    public List<Object[]> getDebtors() throws Exception {
        List<Object[]> rows = new ArrayList<>();
        ResultSet rs = db.query(
                "SELECT m.merchant_id, m.company_name, m.email, " +
                        "SUM(i.total_amount - i.amount_paid) as outstanding, " +
                        "MAX(i.days_overdue) as max_overdue, " +
                        "m.account_status " +
                        "FROM Invoice i " +
                        "JOIN `Order` o ON i.order_id = o.order_id " +
                        "JOIN CommercialMembership m ON o.merchant_id = m.merchant_id " +
                        "WHERE i.status IN ('unpaid', 'partial', 'overdue') " +
                        "GROUP BY m.merchant_id, m.company_name, m.email, m.account_status " +
                        "ORDER BY max_overdue DESC"
        );
        while (rs.next()) {
            rows.add(new Object[]{
                    rs.getString("merchant_id"),
                    rs.getString("company_name"),
                    rs.getString("email"),
                    String.format("%.2f", rs.getDouble("outstanding")),
                    rs.getInt("max_overdue"),
                    rs.getString("account_status")
            });
        }
        return rows;
    }
}