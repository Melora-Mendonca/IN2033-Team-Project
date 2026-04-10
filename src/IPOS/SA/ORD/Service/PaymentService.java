package IPOS.SA.ORD.Service;

import IPOS.SA.DB.DBConnection;
import IPOS.SA.DB.InvoiceDBConnector;

import java.sql.ResultSet;
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
    public void recordPayment(String invoiceId, double amount,
                              String method, String reference) throws Exception {

        // Get invoice details first
        ResultSet invoiceRs = db.query(
                "SELECT i.total_amount, i.amount_paid, o.merchant_id " +
                        "FROM invoice i JOIN `order` o ON i.order_id = o.order_id " +
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
                "INSERT INTO recordpayment (amount, payment_date, payment_method, " +
                        "reference_number, invoice_id, userlogin_user_id) VALUES (?,CURRENT_DATE(),?,?,?,1)",
                amount,
                method,
                reference.isEmpty() ? null : reference,
                invoiceId
        );

        // Update invoice amount_paid and status
        double newAmountPaid = alreadyPaid + amount;
        invoiceDB.updatePayment(invoiceId, newAmountPaid);

        // Update merchant outstanding balance in merchant table
        db.update(
                "UPDATE merchant SET outstanding_balance = outstanding_balance - ? " +
                        "WHERE merchant_id = ?",
                amount, merchantId
        );

        // Check if account should be restored from suspended/in_default
        ResultSet merchantRs = db.query(
                "SELECT outstanding_balance, account_status FROM merchant WHERE merchant_id = ?",
                merchantId
        );
        if (merchantRs.next()) {
            double newBalance = merchantRs.getDouble("outstanding_balance");
            String accountStatus = merchantRs.getString("account_status");
            if (newBalance <= 0 && (accountStatus.equals("suspended") || accountStatus.equals("in_default"))) {
                db.update(
                        "UPDATE merchant SET account_status = 'normal', outstanding_balance = 0 " +
                                "WHERE merchant_id = ?",
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
                        "FROM invoice i " +
                        "JOIN `order` o ON i.order_id = o.order_id " +
                        "JOIN merchant m ON o.merchant_id = m.merchant_id " +
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
                        "FROM recordpayment WHERE invoice_id = ? ORDER BY payment_date",
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
                        "FROM orderitem oi JOIN catalogue c ON oi.catalogue_item_id = c.item_id " +
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
                        "FROM invoice i " +
                        "JOIN `order` o ON i.order_id = o.order_id " +
                        "JOIN merchant m ON o.merchant_id = m.merchant_id " +
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

    /**
     * Returns {merchantId, orderId} for a given invoice, used by the card payment API.
     * Index 0 = merchant_id, index 1 = order_id.
     */
    public String[] getMerchantAndOrderForInvoice(String invoiceId) throws Exception {
        ResultSet rs = db.query(
                "SELECT o.merchant_id, i.order_id " +
                        "FROM invoice i JOIN `order` o ON i.order_id = o.order_id " +
                        "WHERE i.invoice_id = ?", invoiceId
        );
        if (!rs.next()) throw new Exception("Invoice not found: " + invoiceId);
        return new String[]{rs.getString("merchant_id"), rs.getString("order_id")};
    }
}