package IPOS.SA.ORD.Service;

import IPOS.SA.Comms.PUClient.IPOSPUEmailClient;
import IPOS.SA.DB.InvoiceDBConnector;
import IPOS.SA.ORD.Model.Invoice;
import IPOS.SA.ACC.Model.MerchantAccount;
import IPOS.SA.DB.DBConnection;
import IPOS.SA.ORD.Model.Order;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service class responsible for all invoice operations in IPOS-SA.
 * Handles invoice generation, retrieval, filtering and email notifications
 * to merchants when invoices are created.
 */
public class InvoiceService {

    /** Database connection used for all queries and updates. */
    private DBConnection db;

    /**
     * Default constructor — initialises the service with a database connection.
     */
    public InvoiceService() {
        this.db = new DBConnection();
    }

    /**
     * Generates a new invoice for an accepted order and saves it to the database.
     * The invoice date is set to today and the due date is 30 days from today.
     * The invoice is created with status unpaid and amount paid of zero.
     *
     * @param order       the order to generate an invoice for
     * @param account     the merchant account the order belongs to
     * @param finalAmount the final order value after discount
     * @return the newly created Invoice object
     * @throws Exception if a database error occurs
     */
    // Creates invoice record and persists to DB; due date is 30 days from today
    public Invoice generateInvoice(Order order, MerchantAccount account,
                                   double finalAmount) throws Exception {
        String invoiceId  = "INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        LocalDate invoiceDate = LocalDate.now();
        LocalDate dueDate     = invoiceDate.plusDays(30);

        Invoice invoice = new Invoice(
                invoiceId,
                order.getOrderId(),
                order.getMerchantId(),
                invoiceDate,
                dueDate,
                finalAmount,
                0.0,
                "unpaid"
        );

        // Save to database - using your actual Invoice table columns
        String sql = "INSERT INTO invoice (invoice_id, order_id, invoice_date, due_date, " +
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

    /**
     * Retrieves all invoices from the database with optional status and search filtering.
     * Joins with the order and merchant tables to include the merchant's company name.
     * Results are ordered by invoice date descending.
     *
     * @param status the status to filter by — "All" returns all invoices
     * @param search the search text to match against invoice ID or merchant name
     * @return list of invoice rows, each containing 9 fields for the table display
     * @throws Exception if a database error occurs
     */
    public List<Object[]> getAllInvoices(String status, String search) throws Exception {
        List<Object[]> rows = new ArrayList<>();
        Connection conn = new DBConnection().getConn();

        String sql =
                "SELECT i.invoice_id, i.order_id, m.company_name, i.invoice_date, " +
                        "i.due_date, i.total_amount, i.amount_paid, i.status, i.days_overdue " +
                        "FROM invoice i " +
                        "JOIN `order` o ON i.order_id = o.order_id " +
                        "JOIN merchant m ON o.merchant_id = m.merchant_id " +
                        "WHERE 1=1";

        if (!status.equals("All"))  sql += " AND i.status = '" + status + "'";
        if (!search.isEmpty())      sql += " AND (i.invoice_id LIKE '%" + search +
                "%' OR m.company_name LIKE '%" + search + "%')";
        sql += " ORDER BY i.invoice_date DESC";

        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
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
        conn.close();
        return rows;
    }

    /**
     * Retrieves a single invoice by its ID including all line items.
     * Delegates to InvoiceDBConnector which joins with the order table
     * to retrieve the merchant ID and loads order items via OrderDBConnector.
     *
     * @param invoiceId the unique invoice identifier
     * @return the fully populated Invoice object, or null if not found
     * @throws Exception if a database error occurs
     */
    public Invoice getInvoiceById(String invoiceId) throws Exception {
        InvoiceDBConnector db = new InvoiceDBConnector();
        return db.getInvoiceById(invoiceId);
    }

    /**
     * Retrieves invoices for a specific merchant with optional status and search filtering.
     * Joins with the order and merchant tables to filter by merchant ID.
     * Results are ordered by invoice date descending.
     *
     * @param merchantId the unique merchant identifier to filter by
     * @param status     the status to filter by — "All" returns all statuses
     * @param search     the search text to match against invoice ID
     * @return list of invoice rows for the specified merchant
     * @throws Exception if a database error occurs
     */
    public List<Object[]> getMerchantInvoices(String merchantId,
                                              String status,
                                              String search) throws Exception {
        List<Object[]> rows = new ArrayList<>();
        DBConnection db = new DBConnection();

        String sql =
                "SELECT i.invoice_id, i.order_id, m.company_name, i.invoice_date, " +
                        "i.due_date, i.total_amount, i.amount_paid, i.status, i.days_overdue " +
                        "FROM invoice i " +
                        "JOIN `order` o ON i.order_id = o.order_id " +
                        "JOIN merchant m ON o.merchant_id = m.merchant_id " +
                        "WHERE o.merchant_id = ?";

        if (!status.equals("All"))  sql += " AND i.status = '" + status + "'";
        if (!search.isEmpty())      sql += " AND i.invoice_id LIKE '%" + search + "%'";
        sql += " ORDER BY i.invoice_date DESC";

        ResultSet rs = db.query(sql, merchantId);
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
     * Updates the days overdue count and payment status for all unpaid invoices.
     * Delegates to InvoiceDBConnector which runs a single SQL UPDATE using DATEDIFF.
     * Called on Refresh in InvoiceListFrame to keep invoice statuses current.
     */
    public void updateOverdueDays() {
        new InvoiceDBConnector().updateOverdueDays();
    }

    /**
     * Generates an invoice for an order and sends a notification email to the merchant.
     * Looks up the order and merchant details, inserts the invoice record and
     * sends a formatted invoice email via the IPOS-PU email service.
     *
     * @param orderId the unique order identifier to generate an invoice for
     * @throws Exception if the order is not found or a database error occurs
     */
    // Used by the order API to auto-generate an invoice and email the merchant
    public void generateInvoiceForOrder(String orderId) throws Exception {
        DBConnection db = new DBConnection();

        // Check order exists and get merchant details for email
        ResultSet rs = db.query(
                "SELECT o.final_amount, o.merchant_id, o.order_date, m.email, m.company_name " +
                        "FROM `order` o " +
                        "JOIN merchant m ON o.merchant_id = m.merchant_id " +
                        "WHERE o.order_id = ?", orderId);

        if (!rs.next()) throw new Exception("Order not found: " + orderId);

        double finalAmount   = rs.getDouble("final_amount");
        String merchantEmail = rs.getString("email");
        String companyName   = rs.getString("company_name");
        String merchantId    = rs.getString("merchant_id");
        java.sql.Date orderDate = rs.getDate("order_date");

        // Generate unique invoice ID
        String invoiceId = "INV-" + java.util.UUID.randomUUID()
                .toString().substring(0, 8).toUpperCase();

        int rowsAffected = db.update(
                "INSERT INTO invoice (invoice_id, order_id, invoice_date, due_date, " +
                        "total_amount, amount_paid, status, days_overdue) " +
                        "VALUES (?, ?, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 30 DAY), ?, 0.00, 'unpaid', 0)",
                invoiceId, orderId, finalAmount);

        // Calls the emailService API to send an invoice email to the merchant
        if (rowsAffected > 0) {
            sendInvoiceEmail(merchantEmail, companyName, merchantId,
                    invoiceId, orderId, finalAmount, orderDate);
        }
    }

    /**
     * Sends an invoice notification email to a merchant via the IPOS-PU email service.
     *
     * @param merchantEmail the merchant's email address
     * @param companyName   the merchant's company name
     * @param merchantId    the merchant's unique identifier
     * @param invoiceId     the unique invoice identifier
     * @param orderId       the associated order identifier
     * @param amount        the total invoice amount
     * @param orderDate     the date the order was placed
     */
    // Add this method to send invoice email
    private void sendInvoiceEmail(String merchantEmail, String companyName,
                                  String merchantId, String invoiceId,
                                  String orderId, double amount,
                                  java.sql.Date orderDate) {
        String emailContent = buildInvoiceEmailContent(
                companyName, merchantId, invoiceId, orderId, amount, orderDate);

        try {
            // Using IPOS-PU email API
            boolean emailSent = IPOSPUEmailClient.produceEmail(
                    merchantEmail,   // recipient
                    emailContent,    // email body
                    invoiceId,       // reference (invoice ID)
                    "IPOS-SA",       // sender
                    "Invoicing"      // subsystem
            );

            if (emailSent) {
                System.out.println("Invoice " + invoiceId + " sent to " + merchantEmail);
            } else {
                System.err.println("Failed to send invoice email to " + merchantEmail);
            }
        } catch (IOException e) {
            System.err.println("Email service error for invoice " +
                    invoiceId + ": " + e.getMessage());
        }
    }

    /**
     * Builds the email body for an invoice notification email.
     * Includes order details, invoice details and payment instructions.
     *
     * @param companyName the merchant's company name
     * @param merchantId  the merchant's unique identifier
     * @param invoiceId   the unique invoice identifier
     * @param orderId     the associated order identifier
     * @param amount      the total invoice amount
     * @param orderDate   the date the order was placed
     * @return the formatted email body as a string
     */
    // Add this method to build the invoice email content
    private String buildInvoiceEmailContent(String companyName, String merchantId,
                                            String invoiceId, String orderId,
                                            double amount, java.sql.Date orderDate) {
        // Calculate due date (30 days from now)
        java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd");
        java.util.Calendar cal = java.util.Calendar.getInstance();
        String invoiceDate = dateFormat.format(cal.getTime());
        cal.add(java.util.Calendar.DAY_OF_MONTH, 30);
        String dueDate = dateFormat.format(cal.getTime());

        return "Dear " + companyName + ",\n\n"
                + "An invoice has been generated for your order.\n\n"
                + "Order Details:\n"
                + "Order ID: " + orderId + "\n"
                + "Order Date: " + orderDate + "\n"
                + "Merchant ID: " + merchantId + "\n\n"
                + "Invoice Details:\n"
                + "Invoice ID: " + invoiceId + "\n"
                + "Invoice Date: " + invoiceDate + "\n"
                + "Due Date: " + dueDate + "\n"
                + "Total Amount: £" + String.format("%.2f", amount) + "\n"
                + "Amount Paid: £0.00\n"
                + "Status: Unpaid\n\n"
                + "Please make payment by the due date to avoid late fees.\n\n"
                + "If you have already made payment, please disregard this notice.\n\n"
                + "Regards,\nIPOS System Administrator";
    }

    /**
     * Returns a brief invoice summary string for a given order ID.
     * Used by the REST API to return invoice details to IPOS-CA.
     *
     * @param orderId the order ID to look up the invoice for
     * @return a formatted summary string, or an error/not found message
     */
    public String getInvoiceAsString(String orderId) {
        try {
            ResultSet rs = db.query(
                    "SELECT * FROM invoice WHERE order_id = ?", orderId);
            if (rs.next()) {
                return "Invoice ID: " + rs.getString("invoice_id") +
                        ", Total: £" + rs.getDouble("total_amount") +
                        ", Paid: £" + rs.getDouble("amount_paid") +
                        ", Status: " + rs.getString("status");
            }
            return "Invoice not found for order: " + orderId;
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}