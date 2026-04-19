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

/**
 * Service class responsible for all order operations in IPOS-SA.
 * Handles order placement, status transitions, dispatch recording,
 * stock reduction, invoice generation and synchronisation with IPOS-PU.
 *
 * Works in conjunction with AccountService for balance and credit limit checks
 * and InvoiceService for automatic invoice generation on order acceptance.
 */
public class OrderService {

    /** Database connection used for all queries and updates. */
    private final DBConnection db;

    /** Service used for merchant balance updates and credit limit checks. */
    private final AccountService accountService;

    /** Service used to generate invoices when orders are accepted. */
    private final InvoiceService invoiceService;

    /**
     * Constructor — initialises the service with the required dependencies.
     *
     * @param accountService the account service for merchant balance operations
     * @param invoiceService the invoice service for automatic invoice generation
     */
    public OrderService(AccountService accountService, InvoiceService invoiceService) {
        this.db             = new DBConnection();
        this.accountService = accountService;
        this.invoiceService = invoiceService;
    }

    /**
     * Calculate discount based on merchant discount type
     * For fixed discount — returns the merchant's fixed discount rate directly.
     * For flexible discount — returns the full rate for orders over £2000,
     * half rate for orders over £1000, and zero for orders below £1000.
     *
     * @param account the merchant account to calculate the discount for
     * @param orderTotal the gross order total before discount
     * @return the discount percentage to apply
     */
    private double calculateDiscount(MerchantAccount account, double orderTotal) {
        if (account == null) return 0.0;
        String discountType = account.getDiscountType();
        if (discountType == null) return 0.0;

        if ("fixed".equalsIgnoreCase(discountType)) {
            return account.getFixedDiscountRate();
        } else {
            // Variable discount based on order total
            double flexRate = account.getFlexibleDiscountRate(); // max rate e.g. 2%
            if (orderTotal >= 2000) return flexRate;             // 2%
            if (orderTotal >= 1000) return flexRate / 2;         // 1%
            return 0.0;                                           // < £1000 = 0%
        }
    }

    /**
     * Place a new order
     * Validates the merchant's credit limit, calculates the discount,
     * inserts the order and all line items, updates the merchant's outstanding
     * balance and generates an invoice.
     * Note: stock is NOT reduced here — stock is reduced when warehouse staff confirm picking.
     *
     * @param order the order to place
     * @param account the merchant account placing the order
     * @param discountPercentage the discount percentage to apply — if 0, calculated automatically
     * @return true if the order was placed successfully
     * @throws Exception if a database error occurs or the credit limit check fails
     */
    public boolean placeOrder(Order order, MerchantAccount account,
                              double discountPercentage) throws Exception {
        double grossTotal = order.calculateOrderTotal();

        // Check if merchant can place order (credit limit check)
        if (!accountService.canMerchantPlaceOrder(account, grossTotal)) {
            return false;
        }

        // Use calculateDiscount if discountPercentage not explicitly passed
        if (discountPercentage == 0 && account != null) {
            discountPercentage = calculateDiscount(account, grossTotal);
        }

        // Calculate discount
        double discountAmount = grossTotal * (discountPercentage / 100.0);
        double finalTotal     = grossTotal - discountAmount;

        try {
            String orderSql =
                    "INSERT INTO `order` (order_id, merchant_id, order_date, status, " +
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

            for (OrderItem item : order.getItems()) {
                String itemSql =
                        "INSERT INTO orderitem " +
                                "(order_id, catalogue_item_id, quantity, unit_price, total_price) " +
                                "VALUES (?, ?, ?, ?, ?)";
                db.update(itemSql,
                        order.getOrderId(),
                        item.getItemId(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getLineTotal()
                );
                // Stock is reduced when warehouse staff confirm picking
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
     * Reduce stock when warehouse picks order
     * Decrements the catalogue availability for the given item.
     * Only reduces stock if sufficient availability exists to satisfy the quantity.
     *
     * @param itemId   the catalogue item ID to reduce stock for
     * @param quantity the number of packs to deduct from availability
     * @throws Exception if a database error occurs
     */
    private void reduceStock(String itemId, int quantity) throws Exception {
        db.update(
                "UPDATE catalogue SET availability = availability - ? " +
                        "WHERE item_id = ? AND availability >= ?",
                quantity, itemId, quantity
        );
    }

    /**
     * Update order status with dispatch details
     * Updates the order status and records all dispatch details including
     * the dispatching staff member, courier name, reference number and
     * expected delivery date. Sets dispatched date to today.
     *
     * @param orderId the unique order identifier
     * @param status the new order status
     * @param dispatchedBy the full name of the staff member dispatching the order
     * @param courierName the name of the courier service
     * @param courierRefNo the courier tracking reference number
     * @param expectedDelivery the expected delivery date, or null if not known
     * @return true if the update was successful
     * @throws Exception if a database error occurs
     */
    public boolean updateOrderStatus(String orderId, OrderStatus status,
                                     String dispatchedBy, String courierName,
                                     String courierRefNo,
                                     LocalDate expectedDelivery) throws Exception {
        String sql =
                "UPDATE `order` SET status = ?, dispatched_by = ?, dispatched_date = ?, " +
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
     * Get all orders with merchant names
     * Retrieves all orders including dispatch and courier details.
     * Joins with the merchant table to include company name.
     * Results are ordered by order date descending.
     *
     * @return list of order rows containing 9 fields for the tracking table display
     * @throws Exception if a database error occurs
     */
    public List<Object[]> getAllOrders() throws Exception {
        List<Object[]> orders  = new ArrayList<>();
        DBConnection freshDb   = new DBConnection();
        ResultSet rs = freshDb.query(
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
                    rs.getDate("dispatched_date")        != null ? rs.getDate("dispatched_date")        : "—",
                    rs.getString("courier_name")         != null ? rs.getString("courier_name")         : "—",
                    rs.getString("courier_ref_no")       != null ? rs.getString("courier_ref_no")       : "—",
                    rs.getDate("expected_delivery_date") != null ? rs.getDate("expected_delivery_date") : "—"
            });
        }
        return orders;
    }

    /**
     * Get orders for a specific merchant
     * Retrieves orders for the given merchant with optional status and search filtering.
     * Search matches against order ID only.
     * Results are ordered by order date descending.
     *
     * @param merchantId the unique merchant identifier to filter by
     * @param status     the status to filter by — "All" returns all statuses
     * @param search     the search text to match against order ID
     * @return list of order rows containing 7 fields for the order management table
     * @throws Exception if a database error occurs
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

    /**
     * Get orders with search and status filter
     * Retrieves all orders with optional status and search filtering.
     * Search matches against order ID or merchant company name.
     * Results are ordered by order date descending.
     *
     * @param status the status to filter by — "All" returns all statuses
     * @param search the search text to match against order ID or company name
     * @return list of order rows containing 7 fields for the order management table
     * @throws Exception if a database error occurs
     */
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

    /**
     * Get full order details including items and dispatch info
     * Builds a formatted multi-line text summary of an order.
     * Includes all order metadata, line items with descriptions and totals,
     * and dispatch details if the order has been dispatched.
     *
     * @param orderId the unique order identifier
     * @return a formatted order details string, or "Order not found." if not found
     * @throws Exception if a database error occurs
     */
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

        // Only show dispatch section if courier details have been recorded
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

    /**
     * Accept order and update merchant balance
     * Updates the order status to accepted, increases the merchant's outstanding
     * balance by the final order amount, auto-generates an invoice and notifies
     * IPOS-PU of the status change.
     * Note: stock is NOT reduced here — reduced when warehouse staff confirm picking.
     *
     * @param orderId the unique order identifier to accept
     * @return true if the order was accepted successfully
     * @throws Exception if the order is not found or a database error occurs
     */
    public boolean acceptOrder(String orderId) throws Exception {
        ResultSet rs = db.query(
                "SELECT merchant_id, final_amount FROM `order` WHERE order_id = ?", orderId);
        if (!rs.next()) return false;

        String merchantId  = rs.getString("merchant_id");
        double finalAmount = rs.getDouble("final_amount");

        db.update("UPDATE `order` SET status = 'accepted' WHERE order_id = ?", orderId);

        db.update(
                "UPDATE merchant SET outstanding_balance = outstanding_balance + ? " +
                        "WHERE merchant_id = ?", finalAmount, merchantId);

        // NOTE: Stock NOT reduced here; reduced when warehouse picks
        InvoiceService invoiceService = new InvoiceService();
        invoiceService.generateInvoiceForOrder(orderId);

        PUOrderClient.updateOrderStatus(orderId, "accepted");

        return true;
    }

    /**
     * Updates the status of an order and notifies IPOS-PU of the change.
     *
     * @param orderId   the unique order identifier
     * @param newStatus the new status string to apply
     * @return true if the update was successful
     * @throws Exception if a database error occurs
     */
    public boolean updateStatus(String orderId, String newStatus) throws Exception {
        System.out.println("Updating order " + orderId + " to status: " + newStatus);
        int rows = db.update(
                "UPDATE `order` SET status = ? WHERE order_id = ?", newStatus, orderId);
        if (rows > 0) {
            PUOrderClient.updateOrderStatus(orderId, newStatus);
        }
        return rows > 0;
    }

    /**
     * Rejects a pending order and notifies IPOS-PU of the rejection.
     *
     * @param orderId the unique order identifier to reject
     * @return true if the rejection was successful
     * @throws Exception if a database error occurs
     */
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
     * Retrieves a full Order object including all line items.
     * Used by OrderProcessingFrame to load the pick list.
     *
     * @param orderId the unique order identifier
     * @return the fully populated Order object, or null if not found
     * @throws Exception if a database error occurs
     */
    public Order getOrderDetails(String orderId) throws Exception {
        ResultSet orderRs = db.query(
                "SELECT * FROM `order` WHERE order_id = ?", orderId);

        if (orderRs.next()) {
            List<OrderItem> items = new ArrayList<>();
            ResultSet itemRs = db.query(
                    "SELECT oi.catalogue_item_id, c.description, oi.quantity, oi.unit_price " +
                            "FROM orderitem oi JOIN catalogue c ON oi.catalogue_item_id = c.item_id " +
                            "WHERE oi.order_id = ?", orderId);

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