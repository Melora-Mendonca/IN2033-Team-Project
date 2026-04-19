package IPOS.SA.ORD.Service;

import IPOS.SA.DB.DBConnection;
import IPOS.SA.DB.InvoiceDBConnector;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class responsible for payment recording and related invoice queries in IPOS-SA.
 * Handles recording payments against invoices, updating merchant outstanding balances,
 * auto-restoring merchant account status when a balance is cleared, and providing
 * data for the debtors list and payment history views in PaymentRecording.
 */
public class PaymentService {

    /** Database connection used for all queries and updates. */
    private final DBConnection db;

    /** Invoice database connector used to update invoice payment amounts and status. */
    private final InvoiceDBConnector invoiceDB;

    /**
     * Default constructor — initialises the service with a database connection
     * and invoice database connector.
     */
    public PaymentService() {
        this.db        = new DBConnection();
        this.invoiceDB = new InvoiceDBConnector();
    }

    /**
     * Records a payment against an invoice, updates the invoice status and
     * reduces the merchant's outstanding balance.
     * After recording, checks if the merchant's balance has been cleared and
     * auto-restores the account status from suspended or in_default to normal.
     *
     * @param invoiceId the unique invoice identifier to record payment against
     * @param amount    the payment amount — must be greater than zero and not exceed the remaining balance
     * @param method    the payment method — bank_transfer, cheque, cash or card
     * @param reference the payment reference number — cheque number, bank reference etc.
     * @throws Exception if the invoice is not found, the amount is invalid or a database error occurs
     */
    // Records a payment, updates invoice status, and reduces merchant outstanding balance
    public void recordPayment(String invoiceId, double amount,
                              String method, String reference) throws Exception {

        // Get invoice details first
        ResultSet invoiceRs = db.query(
                "SELECT i.total_amount, i.amount_paid, o.merchant_id " +
                        "FROM invoice i JOIN `order` o ON i.order_id = o.order_id " +
                        "WHERE i.invoice_id = ?", invoiceId
        );

        if (!invoiceRs.next()) throw new Exception("Invoice not found: " + invoiceId);

        double totalAmount = invoiceRs.getDouble("total_amount");
        double alreadyPaid = invoiceRs.getDouble("amount_paid");
        String merchantId  = invoiceRs.getString("merchant_id");
        double remaining   = totalAmount - alreadyPaid;

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

        // auto-restore account if balance cleared
        ResultSet merchantRs = db.query(
                "SELECT outstanding_balance, account_status FROM merchant WHERE merchant_id = ?",
                merchantId
        );
        if (merchantRs.next()) {
            double newBalance    = merchantRs.getDouble("outstanding_balance");
            String accountStatus = merchantRs.getString("account_status");

            // Restore to normal if balance is now zero and account was suspended or in default
            if (newBalance <= 0 && (accountStatus.equals("suspended") ||
                    accountStatus.equals("in_default"))) {
                db.update(
                        "UPDATE merchant SET account_status = 'normal', outstanding_balance = 0 " +
                                "WHERE merchant_id = ?",
                        merchantId
                );
            }
        }
    }

    /**
     * Retrieves all invoices for display with optional status and search filtering.
     * Joins with the order and merchant tables to include the merchant's company name.
     * Results are ordered by invoice date descending.
     *
     * @param statusFilter the status to filter by — "All" returns all invoices
     * @param search       the search text to match against invoice ID or company name
     * @return list of invoice rows, each containing 9 fields for the table display
     * @throws Exception if a database error occurs
     */
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

    /**
     * Retrieves the full payment history for a specific invoice.
     * Results are ordered chronologically by payment date.
     *
     * @param invoiceId the unique invoice identifier
     * @return list of payment rows, each containing payment date, amount,
     *         payment method and reference number
     * @throws Exception if a database error occurs
     */
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
                    rs.getString("reference_number") != null
                            ? rs.getString("reference_number") : "—"
            });
        }
        return rows;
    }

    /**
     * Retrieves the line items for a specific order for display in the invoice details view.
     * Joins with the catalogue table to include item descriptions.
     *
     * @param orderId the unique order identifier
     * @return list of item rows, each containing description, quantity,
     *         unit price and total price
     * @throws Exception if a database error occurs
     */
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

    /**
     * Retrieves all merchants with outstanding unpaid, partial or overdue invoices.
     * Groups by merchant and returns the total outstanding balance and the maximum
     * days overdue across all their unpaid invoices.
     * Results are ordered by the most overdue merchants first.
     * Used to populate the debtors dialog in PaymentRecording.
     *
     * @return list of debtor rows, each containing merchant ID, company name, email,
     *         total outstanding balance, maximum days overdue and account status
     * @throws Exception if a database error occurs
     */
    // Gets debtors; merchants with overdue invoices for reminders screen
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
     *
     * @param invoiceId the unique invoice identifier
     * @return a two-element string array containing the merchant ID and order ID
     * @throws Exception if the invoice is not found or a database error occurs
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