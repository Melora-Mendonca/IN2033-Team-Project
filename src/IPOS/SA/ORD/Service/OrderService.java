package IPOS.SA.ORD.Service;

import IPOS.SA.ACC.Model.MerchantAccount;
import IPOS.SA.ACC.Service.AccountService;
import IPOS.SA.Comms.PUClient.PUOrderClient;
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
            String orderSql = "INSERT INTO `order` (order_id, merchant_id, order_date, status, " +
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
                String itemSql = "INSERT INTO orderitem (order_id, catalogue_item_id, quantity, unit_price, total_price) " +
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

            // UPDATE merchant's outstanding balance
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
                "UPDATE catalogue SET availability = availability - ? WHERE item_id = ? AND availability >= ?",
                quantity, itemId, quantity
        );
    }

    /**
     * Update order status with dispatch details
     */
    public boolean updateOrderStatus(String orderId, OrderStatus status, String dispatchedBy,
                                     String courierName, String courierRefNo, LocalDate expectedDelivery) throws Exception {
        String sql = "UPDATE `order` SET status = ?, dispatched_by = ?, dispatched_date = ?, " +
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
                        "FROM `order` o JOIN merchant m ON o.merchant_id = m.merchant_id " +
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
    public List<Object[]> getMerchantFilteredOrders(String merchantId,
                                                    String status,
                                                    String search) throws Exception {
        List<Object[]> orders = new ArrayList<>();

        String sql =
                "SELECT o.order_id, m.company_name, o.order_date, o.status, " +
                        "o.total_amount, o.discount_applied, o.final_amount " +
                        "FROM `order` o JOIN merchant m ON o.merchant_id = m.merchant_id " +
                        "WHERE o.merchant_id = ?";

        if (!status.equals("All"))  sql += " AND o.status = '" + status + "'";
        if (!search.isEmpty())      sql += " AND o.order_id LIKE '%" + search + "%'";
        sql += " ORDER BY o.order_date DESC";

        ResultSet rs = db.query(sql, merchantId);
        while (rs.next()) {
            orders.add(new Object[]{
                    rs.getString("order_id"),
                    rs.getString("company_name"),
                    rs.getDate("order_date"),
                    rs.getString("status"),
                    String.format("%.2f", rs.getDouble("total_amount")),
                    String.format("%.2f", rs.getDouble("discount_applied")),
                    String.format("%.2f", rs.getDouble("final_amount"))
            });
        }
        return orders;
    }

    // Get orders with search and status filter
    public List<Object[]> getFilteredOrders(String status, String search) throws Exception {
        List<Object[]> orders = new ArrayList<>();

        String sql =
                "SELECT o.order_id, m.company_name, o.order_date, o.status, " +
                        "o.total_amount, o.discount_applied, o.final_amount " +
                        "FROM `order` o JOIN merchant m ON o.merchant_id = m.merchant_id " +
                        "WHERE 1=1";

        if (!status.equals("All"))  sql += " AND o.status = '" + status + "'";
        if (!search.isEmpty())      sql += " AND (o.order_id LIKE '%" + search +
                "%' OR m.company_name LIKE '%" + search + "%')";
        sql += " ORDER BY o.order_date DESC";

        ResultSet rs = db.query(sql);
        while (rs.next()) {
            orders.add(new Object[]{
                    rs.getString("order_id"),
                    rs.getString("company_name"),
                    rs.getDate("order_date"),
                    rs.getString("status"),
                    String.format("%.2f", rs.getDouble("total_amount")),
                    String.format("%.2f", rs.getDouble("discount_applied")),
                    String.format("%.2f", rs.getDouble("final_amount"))
            });
        }
        return orders;
    }

    // Get full order details including items and dispatch info
    public String getOrderDetailsText(String orderId) throws Exception {
        StringBuilder sb = new StringBuilder();

        ResultSet orderRs = db.query(
                "SELECT o.*, m.company_name FROM `order` o " +
                        "JOIN merchant m ON o.merchant_id = m.merchant_id " +
                        "WHERE o.order_id = ?", orderId);

        if (!orderRs.next()) return "Order not found.";

        sb.append("ORDER DETAILS\n");
        sb.append("=".repeat(55)).append("\n");
        sb.append("Order ID:   ").append(orderId).append("\n");
        sb.append("Merchant:   ").append(orderRs.getString("company_name")).append("\n");
        sb.append("Date:       ").append(orderRs.getDate("order_date")).append("\n");
        sb.append("Status:     ").append(orderRs.getString("status")).append("\n");
        sb.append("-".repeat(55)).append("\n");
        sb.append("ITEMS:\n\n");

        ResultSet itemRs = db.query(
                "SELECT oi.catalogue_item_id, c.description, oi.quantity, " +
                        "oi.unit_price, oi.total_price " +
                        "FROM orderitem oi " +
                        "JOIN catalogue c ON oi.catalogue_item_id = c.item_id " +
                        "WHERE oi.order_id = ?", orderId);

        while (itemRs.next()) {
            sb.append(String.format("  %-10s %-25s %3d x £%8.2f = £%8.2f\n",
                    itemRs.getString("catalogue_item_id"),
                    itemRs.getString("description"),
                    itemRs.getInt("quantity"),
                    itemRs.getDouble("unit_price"),
                    itemRs.getDouble("total_price")));
        }

        sb.append("-".repeat(55)).append("\n");
        sb.append(String.format("Total:      £%.2f\n", orderRs.getDouble("total_amount")));
        sb.append(String.format("Discount:   £%.2f\n", orderRs.getDouble("discount_applied")));
        sb.append(String.format("Final:      £%.2f\n", orderRs.getDouble("final_amount")));

        if (orderRs.getString("courier_name") != null) {
            sb.append("-".repeat(55)).append("\n");
            sb.append("DISPATCH DETAILS:\n");
            sb.append("  Dispatched by:  ").append(orderRs.getString("dispatched_by")).append("\n");
            sb.append("  Dispatch date:  ").append(orderRs.getDate("dispatched_date")).append("\n");
            sb.append("  Courier:        ").append(orderRs.getString("courier_name")).append("\n");
            sb.append("  Ref No:         ").append(orderRs.getString("courier_ref_no")).append("\n");
            sb.append("  Est. Delivery:  ").append(orderRs.getDate("expected_delivery_date")).append("\n");
        }

        return sb.toString();
    }

    // Accept order and update merchant balance
    public boolean acceptOrder(String orderId) throws Exception {
        // Get order details
        ResultSet rs = db.query(
                "SELECT merchant_id, final_amount FROM `order` WHERE order_id = ?", orderId);
        if (!rs.next()) return false;

        String merchantId  = rs.getString("merchant_id");
        double finalAmount = rs.getDouble("final_amount");

        // Update order status
        db.update("UPDATE `order` SET status = 'accepted' WHERE order_id = ?", orderId);

        // Update merchant outstanding balance
        db.update(
                "UPDATE merchant SET outstanding_balance = outstanding_balance + ? " +
                        "WHERE merchant_id = ?", finalAmount, merchantId);

        // Reduce stock for each item in the order
        ResultSet itemRs = db.query(
                "SELECT catalogue_item_id, quantity FROM orderitem WHERE order_id = ?", orderId);
        while (itemRs.next()) {
            db.update(
                    "UPDATE catalogue SET availability = availability - ? " +
                            "WHERE item_id = ? AND availability >= ?",
                    itemRs.getInt("quantity"),
                    itemRs.getString("catalogue_item_id"),
                    itemRs.getInt("quantity"));
        }

        // Generate invoice automatically
        InvoiceService invoiceService = new InvoiceService();
        invoiceService.generateInvoiceForOrder(orderId);

        PUOrderClient.updateOrderStatus(orderId, "accepted");

        return true;
    }

    public boolean updateStatus(String orderId, String newStatus) throws Exception {
        System.out.println("Updating order " + orderId + " to status: " + newStatus);
        int rows = db.update(
                "UPDATE `order` SET status = ? WHERE order_id = ?", newStatus, orderId);
        if (rows > 0) {
            PUOrderClient.updateOrderStatus(orderId, newStatus);
        }
        return rows > 0;
    }

    public boolean rejectOrder(String orderId) throws Exception {
        int rows = db.update(
                "UPDATE `order` SET status = 'rejected' WHERE order_id = ?", orderId);
        if (rows > 0) {
            PUOrderClient.updateOrderStatus(orderId, "rejected");
        }
        return rows > 0;
    }

    /**
     * Get order details with items
     */
    public Order getOrderDetails(String orderId) throws Exception {
        ResultSet orderRs = db.query(
                "SELECT * FROM `order` WHERE order_id = ?", orderId
        );

        if (orderRs.next()) {
            List<OrderItem> items = new ArrayList<>();
            ResultSet itemRs = db.query(
                    "SELECT oi.catalogue_item_id, c.description, oi.quantity, oi.unit_price " +
                            "FROM orderitem oi JOIN catalogue c ON oi.catalogue_item_id = c.item_id " +
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
}
