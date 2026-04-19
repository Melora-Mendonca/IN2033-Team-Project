package IPOS.SA.DB;

import IPOS.SA.ORD.Model.Order;
import IPOS.SA.ORD.Model.OrderItem;
import IPOS.SA.ORD.OrderStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
/**
 * Database connector for order operations in IPOS-SA.
 * Provides methods to save, retrieve and update order records
 * and their associated line items.
 * Used by OrderService for all order-related database interactions.
 */
public class OrderDBConnector {

    /**
     * Saves a new order and all its line items to the database.
     * The order is inserted with status 'pending'.
     * All order items are inserted as a batch for efficiency.
     *
     * @param order  the order to save
     * @param grossTotal the total order value before discount
     * @param discount the discount amount applied to the order
     * @param finalTotal the final order value after discount
     */
    public void saveOrder(Order order, double grossTotal, double discount, double finalTotal) {
        try {
            Connection conn = new DBConnection().getConn();

            String orderSql = "INSERT INTO `order` (order_id, merchant_id, order_date, status, " +
                    "total_amount, discount_applied, final_amount) VALUES (?,?,?,'pending',?,?,?)";
            PreparedStatement orderStmt = conn.prepareStatement(orderSql);
            orderStmt.setString(1, order.getOrderId());
            orderStmt.setString(2, order.getMerchantId());
            orderStmt.setDate(3, Date.valueOf(order.getOrderDate()));
            orderStmt.setDouble(4, grossTotal);
            orderStmt.setDouble(5, discount);
            orderStmt.setDouble(6, finalTotal);
            orderStmt.executeUpdate();

            String itemSql = "INSERT INTO orderitem (order_id, catalogue_item_id, quantity, unit_price, total_price) VALUES (?,?,?,?,?)";
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


    /**
     * Retrieves all orders for display in the order management table.
     * Returns key fields including dispatch and courier details.
     * Results are ordered by order date descending.
     *
     * @return list of order rows, each containing 9 fields for the table display
     */
    public List<Object[]> getOrdersForDisplay() {
        List<Object[]> rows = new ArrayList<>();
        try {
            Connection conn = new DBConnection().getConn();
            String sql =
                    "SELECT o.order_id, o.merchant_id, o.order_date, o.status, " +
                            "o.total_amount, o.discount_applied, o.final_amount, " +
                            "o.dispatched_by, o.courier_name " +
                            "FROM `order` o ORDER BY o.order_date DESC";
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

    /**
     * Retrieves all line items for a specific order.
     * Used by InvoiceDisplayFrame and OrderProcessingFrame to show
     * the pick list and invoice line items.
     *
     * @param orderId the unique order identifier
     * @return list of OrderItem objects for the specified order
     */
    public List<OrderItem> getItemsForOrder(String orderId) {
        List<OrderItem> items = new ArrayList<>();
        try {
            Connection conn = new DBConnection().getConn();
            String sql = "SELECT catalogue_item_id, quantity, unit_price FROM orderitem WHERE order_id = ?";
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

    /**
     * Updates the status of an order.
     * Used to advance the order through the workflow stages.
     *
     * @param orderId the unique order identifier
     * @param status  the new status string to apply
     */
    public void updateOrderStatus(String orderId, String status) {
        try {
            Connection conn = new DBConnection().getConn();
            String sql = "UPDATE `order` SET status = ? WHERE order_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, status);
            stmt.setString(2, orderId);
            stmt.executeUpdate();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates dispatch details for an order and sets its status to dispatched.
     * Records the dispatched by name, dispatch date, courier name,
     * courier reference number and expected delivery date.
     * Called by OrderProcessingFrame when a Delivery Employee dispatches an order.
     *
     * @param orderId          the unique order identifier
     * @param dispatchedBy     the full name of the staff member dispatching the order
     * @param courier          the name of the courier service
     * @param courierRef       the courier tracking reference number
     * @param expectedDelivery the expected delivery date as a string, or empty if unknown
     */
    public void dispatchOrder(String orderId, String dispatchedBy, String courier,
                              String courierRef, String expectedDelivery) {
        try {
            Connection conn = new DBConnection().getConn();
            String sql = "UPDATE `order` SET status='dispatched', dispatched_by=?, " +
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


    /**
     * Reduces the available stock of a catalogue item by the given quantity.
     * Called when an order is accepted to reflect the stock committed to the order.
     *
     * @param itemId   the unique catalogue item identifier
     * @param quantity the number of packs to deduct from availability
     */
    public void reduceStock(String itemId, int quantity) {
        try {
            Connection conn = new DBConnection().getConn();
            String sql = "UPDATE catalogue SET availability = availability - ? WHERE item_id = ?";
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
