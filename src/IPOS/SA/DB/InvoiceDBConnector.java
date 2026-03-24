package IPOS.SA.DB;

import IPOS.SA.ACC.Invoice;
import IPOS.SA.ORD.OrderItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InvoiceDBConnector {

    public void saveInvoice(Invoice invoice) {
        try {
            Connection conn = new DBConnection().getConn();
            String sql = "INSERT INTO Invoices (invoice_id, order_id, merchant_id, issue_date, gross_total, discount, final_total) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, invoice.getInvoiceId());
            stmt.setString(2, invoice.getOrderId());
            stmt.setString(3, invoice.getMerchantId());
            stmt.setDate(4, Date.valueOf(invoice.getIssueDate()));
            stmt.setDouble(5, invoice.getGrossTotal());
            stmt.setDouble(6, invoice.getDiscountAmount());
            stmt.setDouble(7, invoice.getFinalTotal());
            stmt.executeUpdate();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Object[]> getInvoicesForDisplay() {
        List<Object[]> rows = new ArrayList<>();
        try {
            Connection conn = new DBConnection().getConn();
            String sql = "SELECT invoice_id, order_id, merchant_id, issue_date, gross_total, discount, final_total " +
                         "FROM Invoices ORDER BY issue_date DESC";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                rows.add(new Object[]{
                        rs.getString("invoice_id"),
                        rs.getString("order_id"),
                        rs.getString("merchant_id"),
                        rs.getDate("issue_date").toLocalDate().toString(),
                        String.format("£%.2f", rs.getDouble("gross_total")),
                        String.format("£%.2f", rs.getDouble("discount")),
                        String.format("£%.2f", rs.getDouble("final_total"))
                });
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rows;
    }

    public Invoice getInvoiceById(String invoiceId) {
        try {
            Connection conn = new DBConnection().getConn();
            String sql = "SELECT * FROM Invoices WHERE invoice_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, invoiceId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String orderId = rs.getString("order_id");
                List<OrderItem> items = new OrderDBConnector().getItemsForOrder(orderId);
                Invoice invoice = new Invoice(
                        rs.getString("invoice_id"),
                        orderId,
                        rs.getString("merchant_id"),
                        rs.getDate("issue_date").toLocalDate(),
                        items,
                        rs.getDouble("gross_total"),
                        rs.getDouble("discount"),
                        rs.getDouble("final_total")
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
}
