package IPOS.SA.DB;

import IPOS.SA.ORD.Order;
import IPOS.SA.ORD.OrderItem;
import IPOS.SA.ORD.OrderStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDBConnector {

    public void saveOrder(Order order, double grossTotal, double discount, double finalTotal) {
        try {
            Connection conn = new DBConnection().getConn();

            String orderSql = "INSERT INTO Orders (order_id, merchant_id, order_date, status, gross_total, discount, final_total) " +
                              "VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement orderStmt = conn.prepareStatement(orderSql);
            orderStmt.setString(1, order.getOrderId());
            orderStmt.setString(2, order.getMerchantId());
            orderStmt.setDate(3, Date.valueOf(order.getOrderDate()));
            orderStmt.setString(4, order.getStatus().name());
            orderStmt.setDouble(5, grossTotal);
            orderStmt.setDouble(6, discount);
            orderStmt.setDouble(7, finalTotal);
            orderStmt.executeUpdate();

            String itemSql = "INSERT INTO Order_Items (order_id, item_id, quantity, unit_price) VALUES (?, ?, ?, ?)";
            PreparedStatement itemStmt = conn.prepareStatement(itemSql);
            for (OrderItem item : order.getItems()) {
                itemStmt.setString(1, order.getOrderId());
                itemStmt.setString(2, item.getItemId());
                itemStmt.setInt(3, item.getQuantity());
                itemStmt.setDouble(4, item.getUnitPrice());
                itemStmt.addBatch();
            }
            itemStmt.executeBatch();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Object[]> getOrdersForDisplay() {
        List<Object[]> rows = new ArrayList<>();
        try {
            Connection conn = new DBConnection().getConn();
            String sql = "SELECT order_id, merchant_id, order_date, status, final_total FROM Orders ORDER BY order_date DESC";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                rows.add(new Object[]{
                        rs.getString("order_id"),
                        rs.getString("merchant_id"),
                        rs.getDate("order_date").toLocalDate().toString(),
                        rs.getString("status"),
                        String.format("£%.2f", rs.getDouble("final_total"))
                });
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rows;
    }

    public List<OrderItem> getItemsForOrder(String orderId) {
        List<OrderItem> items = new ArrayList<>();
        try {
            Connection conn = new DBConnection().getConn();
            String sql = "SELECT item_id, quantity, unit_price FROM Order_Items WHERE order_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, orderId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                items.add(new OrderItem(
                        rs.getString("item_id"),
                        rs.getInt("quantity"),
                        rs.getDouble("unit_price")
                ));
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    public void updateOrderStatus(String orderId, OrderStatus status) {
        try {
            Connection conn = new DBConnection().getConn();
            String sql = "UPDATE Orders SET status = ? WHERE order_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, status.name());
            stmt.setString(2, orderId);
            stmt.executeUpdate();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void reduceStock(String itemId, int quantity) {
        try {
            Connection conn = new DBConnection().getConn();
            String sql = "UPDATE Catalogue_Items SET availability_packs = availability_packs - ? WHERE item_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, quantity);
            stmt.setString(2, itemId);
            stmt.executeUpdate();
            conn.close();
        } catch (SQLException e) {
            System.out.println("Note: Could not reduce stock for item " + itemId + " — " + e.getMessage());
        }
    }
}
