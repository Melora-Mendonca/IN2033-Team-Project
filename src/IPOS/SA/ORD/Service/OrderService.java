package IPOS.SA.ORD.Service;

import IPOS.SA.ACC.Model.MerchantAccount;
import IPOS.SA.ACC.Service.AccountService;
import IPOS.SA.ACC.Service.InvoiceService;
import IPOS.SA.DB.DBConnection;
import IPOS.SA.ORD.Model.Order;
import IPOS.SA.ORD.Model.OrderItem;
import IPOS.SA.ORD.OrderStatus;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OrderService {
    private final DBConnection db;
    private final AccountService accountService;
    private final InvoiceService invoiceService;

    public OrderService(AccountService accountService, InvoiceService invoiceService) {
        this.db = new DBConnection();
        this.accountService = accountService;
        this.invoiceService = invoiceService;
    }

    /**
     * Place a new order
     */
    public boolean placeOrder(Order order, MerchantAccount account, double discountPercentage) throws Exception {
        double grossTotal = order.calculateOrderTotal();

        // Check if merchant can place order (credit limit check)
        if (!accountService.canMerchantPlaceOrder(account, grossTotal)) {
            return false;
        }

        // Calculate discount
        double discountAmount = grossTotal * (discountPercentage / 100.0);
        double finalTotal = grossTotal - discountAmount;

        // Start transaction
        try {
            // Insert order - using your actual Order table columns
            String orderSql = "INSERT INTO `Order` (order_id, merchant_id, order_date, status, " +
                    "total_amount, discount_applied, final_amount) " +
                    "VALUES (?, ?, ?, 'pending', ?, ?, ?)";
            db.update(orderSql,
                    order.getOrderId(),
                    order.getMerchantId(),
                    java.sql.Date.valueOf(order.getOrderDate()),
                    grossTotal,
                    discountAmount,
                    finalTotal
            );

            // Insert order items - using your actual OrderItem table columns
            for (OrderItem item : order.getItems()) {
                String itemSql = "INSERT INTO OrderItem (order_id, catalogue_item_id, quantity, unit_price, total_price) " +
                        "VALUES (?, ?, ?, ?, ?)";
                db.update(itemSql,
                        order.getOrderId(),
                        item.getItemId(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getLineTotal()
                );

                // Reduce stock - using your actual Catalogue table
                reduceStock(item.getItemId(), item.getQuantity());
            }

            // Update merchant's outstanding balance
            accountService.addToBalance(order.getMerchantId(), finalTotal);

            // Generate invoice
            invoiceService.generateInvoice(order, account, finalTotal);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Reduce stock when order is placed
     */
    private void reduceStock(String itemId, int quantity) throws Exception {
        db.update(
                "UPDATE Catalogue SET availability = availability - ? WHERE item_id = ? AND availability >= ?",
                quantity, itemId, quantity
        );
    }

    /**
     * Update order status with dispatch details
     */
    public boolean updateOrderStatus(String orderId, OrderStatus status, String dispatchedBy,
                                     String courierName, String courierRefNo, LocalDate expectedDelivery) throws Exception {
        String sql = "UPDATE `Order` SET status = ?, dispatched_by = ?, dispatched_date = ?, " +
                "courier_name = ?, courier_ref_no = ?, expected_delivery_date = ? " +
                "WHERE order_id = ?";

        int rows = db.update(sql,
                status.toString().toLowerCase(),
                dispatchedBy,
                java.sql.Date.valueOf(LocalDate.now()),
                courierName,
                courierRefNo,
                expectedDelivery != null ? java.sql.Date.valueOf(expectedDelivery) : null,
                orderId
        );

        return rows > 0;
    }

    /**
     * Get all orders with merchant names (for admin/manager)
     */
    public List<Object[]> getAllOrders() throws Exception {
        List<Object[]> orders = new ArrayList<>();
        ResultSet rs = db.query(
                "SELECT o.order_id, m.company_name, o.order_date, o.status, o.final_amount, " +
                        "o.dispatched_date, o.courier_name, o.courier_ref_no, o.expected_delivery_date " +
                        "FROM `Order` o JOIN Merchant m ON o.merchant_id = m.merchant_id " +
                        "ORDER BY o.order_date DESC"
        );

        while (rs.next()) {
            orders.add(new Object[]{
                    rs.getString("order_id"),
                    rs.getString("company_name"),
                    rs.getDate("order_date"),
                    rs.getString("status"),
                    String.format("£%.2f", rs.getDouble("final_amount")),
                    rs.getDate("dispatched_date") != null ? rs.getDate("dispatched_date") : "—",
                    rs.getString("courier_name") != null ? rs.getString("courier_name") : "—",
                    rs.getString("courier_ref_no") != null ? rs.getString("courier_ref_no") : "—",
                    rs.getDate("expected_delivery_date") != null ? rs.getDate("expected_delivery_date") : "—"
            });
        }
        return orders;
    }

    /**
     * Get orders for a specific merchant (for merchant view)
     */
    public List<Object[]> getMerchantOrders(String merchantId) throws Exception {
        List<Object[]> orders = new ArrayList<>();
        ResultSet rs = db.query(
                "SELECT order_id, order_date, status, total_amount, discount_applied, final_amount, " +
                        "dispatched_date, courier_name, courier_ref_no, expected_delivery_date " +
                        "FROM `Order` WHERE merchant_id = ? ORDER BY order_date DESC",
                merchantId
        );

        while (rs.next()) {
            orders.add(new Object[]{
                    rs.getString("order_id"),
                    rs.getDate("order_date"),
                    rs.getString("status"),
                    String.format("£%.2f", rs.getDouble("final_amount")),
                    rs.getDate("dispatched_date") != null ? rs.getDate("dispatched_date") : "Pending",
                    rs.getString("courier_name") != null ? rs.getString("courier_name") : "—",
                    rs.getString("courier_ref_no") != null ? rs.getString("courier_ref_no") : "—",
                    rs.getDate("expected_delivery_date") != null ? rs.getDate("expected_delivery_date") : "—"
            });
        }
        return orders;
    }

    /**
     * Get order details with items
     */
    public Order getOrderDetails(String orderId) throws Exception {
        ResultSet orderRs = db.query(
                "SELECT * FROM `Order` WHERE order_id = ?", orderId
        );

        if (orderRs.next()) {
            List<OrderItem> items = new ArrayList<>();
            ResultSet itemRs = db.query(
                    "SELECT oi.catalogue_item_id, c.description, oi.quantity, oi.unit_price " +
                            "FROM OrderItem oi JOIN Catalogue c ON oi.catalogue_item_id = c.item_id " +
                            "WHERE oi.order_id = ?", orderId
            );

            while (itemRs.next()) {
                items.add(new OrderItem(
                        itemRs.getString("catalogue_item_id"),
                        itemRs.getInt("quantity"),
                        itemRs.getDouble("unit_price")
                ));
            }

            return new Order(
                    orderRs.getString("order_id"),
                    orderRs.getString("merchant_id"),
                    orderRs.getDate("order_date").toLocalDate(),
                    items
            );
        }
        return null;
    }

    /**
     * Get pending orders count for dashboard
     */
    public int getPendingOrdersCount() throws Exception {
        ResultSet rs = db.query(
                "SELECT COUNT(*) as count FROM `Order` WHERE status IN ('pending', 'accepted', 'processing')"
        );
        if (rs.next()) {
            return rs.getInt("count");
        }
        return 0;
    }
}
