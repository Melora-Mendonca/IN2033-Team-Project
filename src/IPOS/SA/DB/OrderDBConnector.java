package IPOS.SA.DB;

import IPOS.SA.ORD.Model.Order;
import IPOS.SA.ORD.Model.OrderItem;
import IPOS.SA.ORD.OrderStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDBConnector {

    // Saves a new order and its items to the database
    public void saveOrder(Order order, double grossTotal, double discount, double finalTotal) {
        try {
            Connection conn = new DBConnection().getConn();

            String orderSql = "INSERT INTO `Order` (order_id, merchant_id, order_date, status, " +
                    "total_amount, discount_applied, final_amount) VALUES (?,?,?,'pending',?,?,?)";
            PreparedStatement orderStmt = conn.prepareStatement(orderSql);
            orderStmt.setString(1, order.getOrderId());
            orderStmt.setString(2, order.getMerchantId());
            orderStmt.setDate(3, Date.valueOf(order.getOrderDate()));
            orderStmt.setDouble(4, grossTotal);
            orderStmt.setDouble(5, discount);
            orderStmt.setDouble(6, finalTotal);
            orderStmt.executeUpdate();

            String itemSql = "INSERT INTO OrderItem (order_id, catalogue_item_id, quantity, unit_price, total_price) VALUES (?,?,?,?,?)";
            PreparedStatement itemStmt = conn.prepareStatement(itemSql);
            for (OrderItem item : order.getItems()) {
                itemStmt.setString(1, order.getOrderId());
                itemStmt.setString(2, item.getItemId());
                itemStmt.setInt(3, item.getQuantity());
                itemStmt.setDouble(4, item.getUnitPrice());
                itemStmt.setDouble(5, item.getLineTotal());
                itemStmt.addBatch();
            }
            itemStmt.executeBatch();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Gets all orders for display in the order management table
    public List<Object[]> getOrdersForDisplay() {
        List<Object[]> rows = new ArrayList<>();
        try {
            Connection conn = new DBConnection().getConn();
            String sql =
                    "SELECT o.order_id, o.merchant_id, o.order_date, o.status, " +
                            "o.total_amount, o.discount_applied, o.final_amount, " +
                            "o.dispatched_by, o.courier_name " +
                            "FROM `Order` o ORDER BY o.order_date DESC";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                rows.add(new Object[]{
                        rs.getString("order_id"),
                        rs.getString("merchant_id"),
                        rs.getString("order_date"),
                        rs.getString("status"),
                        String.format("%.2f", rs.getDouble("total_amount")),
                        String.format("%.2f", rs.getDouble("discount_applied")),
                        String.format("%.2f", rs.getDouble("final_amount")),
                        rs.getString("dispatched_by") != null ? rs.getString("dispatched_by") : "—",
                        rs.getString("courier_name")  != null ? rs.getString("courier_name")  : "—"
                });
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rows;
    }

    // Gets order items for a specific order
    public List<OrderItem> getItemsForOrder(String orderId) {
        List<OrderItem> items = new ArrayList<>();
        try {
            Connection conn = new DBConnection().getConn();
            String sql = "SELECT catalogue_item_id, quantity, unit_price FROM OrderItem WHERE order_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, orderId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                items.add(new OrderItem(
                        rs.getString("catalogue_item_id"),
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

    // Updates order status
    public void updateOrderStatus(String orderId, String status) {
        try {
            Connection conn = new DBConnection().getConn();
            String sql = "UPDATE `Order` SET status = ? WHERE order_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, status);
            stmt.setString(2, orderId);
            stmt.executeUpdate();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Updates dispatch details and sets status to dispatched
    public void dispatchOrder(String orderId, String dispatchedBy, String courier,
                              String courierRef, String expectedDelivery) {
        try {
            Connection conn = new DBConnection().getConn();
            String sql = "UPDATE `Order` SET status='dispatched', dispatched_by=?, " +
                    "dispatched_date=CURRENT_DATE(), courier_name=?, courier_ref_no=?, " +
                    "expected_delivery_date=? WHERE order_id=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, dispatchedBy);
            stmt.setString(2, courier);
            stmt.setString(3, courierRef);
            stmt.setString(4, expectedDelivery.isEmpty() ? null : expectedDelivery);
            stmt.setString(5, orderId);
            stmt.executeUpdate();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Reduces stock when order is placed
    public void reduceStock(String itemId, int quantity) {
        try {
            Connection conn = new DBConnection().getConn();
            String sql = "UPDATE Catalogue SET availability = availability - ? WHERE item_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, quantity);
            stmt.setString(2, itemId);
            stmt.executeUpdate();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
