package IPOS.SA.DB;

import IPOS.SA.ORD.Model.Invoice;
import IPOS.SA.ORD.Model.OrderItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Low-level JDBC operations for the invoice table
public class InvoiceDBConnector {

    // Saves a new invoice to the database
    public void saveInvoice(Invoice invoice) {
        try {
            Connection conn = new DBConnection().getConn();
            String sql = "INSERT INTO invoice (invoice_id, order_id, invoice_date, due_date, " +
                    "total_amount, amount_paid, status, days_overdue) VALUES (?,?,?,?,?,?,?,?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, invoice.getInvoiceId());
            stmt.setString(2, invoice.getOrderId());
            stmt.setDate(3, Date.valueOf(invoice.getInvoiceDate()));
            stmt.setDate(4, Date.valueOf(invoice.getDueDate()));
            stmt.setDouble(5, invoice.getTotalAmount());
            stmt.setDouble(6, invoice.getAmountPaid());
            stmt.setString(7, invoice.getStatus());
            stmt.setInt(8, invoice.getDaysOverdue());
            stmt.executeUpdate();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Gets all invoices for display
    public List<Object[]> getInvoicesForDisplay() {
        List<Object[]> rows = new ArrayList<>();
        try {
            Connection conn = new DBConnection().getConn();
            String sql =
                    "SELECT i.invoice_id, i.order_id, m.company_name, i.invoice_date, i.due_date, " +
                            "i.total_amount, i.amount_paid, i.status, i.days_overdue " +
                            "FROM invoice i " +
                            "JOIN `order` o ON i.order_id = o.order_id " +
                            "JOIN merchant m ON o.merchant_id = m.merchant_id " +
                            "ORDER BY i.invoice_date DESC";
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rows;
    }

    // Gets invoices for a specific merchant
    public List<Object[]> getInvoicesByMerchant(String merchantId) {
        List<Object[]> rows = new ArrayList<>();
        try {
            Connection conn = new DBConnection().getConn();
            String sql =
                    "SELECT i.invoice_id, i.order_id, i.invoice_date, i.due_date, " +
                            "i.total_amount, i.amount_paid, i.status, i.days_overdue " +
                            "FROM invoice i JOIN `order` o ON i.order_id = o.order_id " +
                            "WHERE o.merchant_id = ? ORDER BY i.invoice_date DESC";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, merchantId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                rows.add(new Object[]{
                        rs.getString("invoice_id"),
                        rs.getString("order_id"),
                        rs.getString("invoice_date"),
                        rs.getString("due_date"),
                        String.format("%.2f", rs.getDouble("total_amount")),
                        String.format("%.2f", rs.getDouble("amount_paid")),
                        rs.getString("status"),
                        rs.getInt("days_overdue")
                });
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rows;
    }

    // Gets a single invoice by ID
    public Invoice getInvoiceById(String invoiceId) {
        try {
            Connection conn = new DBConnection().getConn();
            String sql = "SELECT * FROM invoice WHERE invoice_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, invoiceId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String orderId = rs.getString("order_id");
                List<OrderItem> items = new OrderDBConnector().getItemsForOrder(orderId);

                Invoice invoice = new Invoice(
                        rs.getString("invoice_id"),
                        orderId,
                        null,                                          // merchantId; get from Order if needed
                        rs.getDate("invoice_date").toLocalDate(),
                        rs.getDate("due_date").toLocalDate(),
                        rs.getDouble("total_amount"),
                        rs.getDouble("amount_paid"),
                        rs.getString("status"),
                        items                                          // full constructor with items
                );
                conn.close();
                return invoice;
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Updates amount_paid and derives status (paid / partial / unpaid)
    public void updatePayment(String invoiceId, double newAmountPaid) {
        try {
            Connection conn = new DBConnection().getConn();

            ResultSet rs = conn.createStatement().executeQuery(
                    "SELECT total_amount FROM invoice WHERE invoice_id = '" + invoiceId + "'");
            double totalAmount = rs.next() ? rs.getDouble("total_amount") : 0;

            String status;
            if (newAmountPaid >= totalAmount)    status = "paid";
            else if (newAmountPaid > 0)          status = "partial";
            else                                 status = "unpaid";

            String sql = "UPDATE invoice SET amount_paid = ?, status = ? WHERE invoice_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setDouble(1, newAmountPaid);
            stmt.setString(2, status);
            stmt.setString(3, invoiceId);
            stmt.executeUpdate();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Updates overdue days and status for all unpaid invoices
    public void updateOverdueDays() {
        try {
            Connection conn = new DBConnection().getConn();
            String sql =
                    "UPDATE invoice SET " +
                            "days_overdue = DATEDIFF(CURDATE(), due_date), " +
                            "status = CASE " +
                            "WHEN amount_paid >= total_amount THEN 'paid' " +
                            "WHEN CURDATE() > due_date AND amount_paid < total_amount THEN 'overdue' " +
                            "WHEN amount_paid > 0 AND amount_paid < total_amount THEN 'partial' " +
                            "ELSE 'unpaid' END " +
                            "WHERE amount_paid < total_amount";
            conn.createStatement().executeUpdate(sql);
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}