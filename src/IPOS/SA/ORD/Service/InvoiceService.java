package IPOS.SA.ORD.Service;

import IPOS.SA.DB.InvoiceDBConnector;
import IPOS.SA.ORD.Model.Invoice;
import IPOS.SA.ACC.Model.MerchantAccount;
import IPOS.SA.DB.DBConnection;
import IPOS.SA.ORD.Model.Order;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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
                order.getOrderId(),
                order.getMerchantId(),
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

    public Invoice getInvoiceById(String invoiceId) throws Exception {
        InvoiceDBConnector db = new InvoiceDBConnector();
        return db.getInvoiceById(invoiceId);
    }

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

    public void updateOverdueDays() {
        new InvoiceDBConnector().updateOverdueDays();
    }

    public void generateInvoiceForOrder(String orderId) throws Exception {
        DBConnection db = new DBConnection();

        // Check order exists
        ResultSet rs = db.query(
                "SELECT final_amount FROM `order` WHERE order_id = ?", orderId);

        if (!rs.next()) throw new Exception("Order not found: " + orderId);

        double finalAmount = rs.getDouble("final_amount");
        String invoiceId   = "INV-" + java.util.UUID.randomUUID()
                .toString().substring(0, 8).toUpperCase();

        db.update(
                "INSERT INTO invoice (invoice_id, order_id, invoice_date, due_date, " +
                        "total_amount, amount_paid, status, days_overdue) " +
                        "VALUES (?, ?, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 30 DAY), ?, 0.00, 'unpaid', 0)",
                invoiceId, orderId, finalAmount);
    }

}