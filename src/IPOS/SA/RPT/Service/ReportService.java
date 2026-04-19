package IPOS.SA.RPT.Service;

import IPOS.SA.DB.DBConnection;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Service class responsible for generating all reports in IPOS-SA.
 * Provides seven report types covering stock, turnover, merchant activity,
 * invoices and stock deliveries. All reports query the database directly
 * and return results as lists of string arrays for display in ReportForm.
 */
public class ReportService {

    /** Database connection used for all report queries. */
    private DBConnection db;

    /**
     * Default constructor — initialises the service with a database connection.
     */
    public ReportService() {
        this.db = new DBConnection();
    }

    /**
     * 1. Low Stock Report
     * Retrieves all active catalogue items where availability is at or below
     * the minimum stock level. Calculates a recommended order quantity for
     * each item based on the buffer percent stored in the catalogue.
     * Results are ordered by the largest shortfall first.
     *
     * @return list of string arrays — each containing item ID, description,
     *         package type, unit, availability, minimum level and recommended order qty
     * @throws Exception if a database error occurs
     */
    public List<String[]> getLowStockReport() throws Exception {
        List<String[]> data = new ArrayList<>();
        ResultSet rs = db.query(
                "SELECT item_id, description, package_type, unit, " +
                        "availability, minimum_stock_level, buffer_percent " +
                        "FROM catalogue WHERE availability <= minimum_stock_level AND is_active = 1 " +
                        "ORDER BY (minimum_stock_level - availability) DESC"
        );

        while (rs.next()) {

            int availability = rs.getInt("availability");
            int minLevel     = rs.getInt("minimum_stock_level");
            double buffer    = rs.getDouble("buffer_percent"); // from DB
            int recommended  = calculateRecommendedOrder(availability, minLevel, buffer);

            data.add(new String[]{
                    rs.getString("item_id"),
                    rs.getString("description"),
                    rs.getString("package_type"),
                    rs.getString("unit"),
                    String.valueOf(rs.getInt("availability")),
                    String.valueOf(rs.getInt("minimum_stock_level")),
                    String.valueOf(recommended)
            });
        }
        return data;
    }

    /**
     * 2. Turnover Report
     * Retrieves order count, items sold and total revenue grouped by month
     * for the given date range.
     *
     * @param fromDate the start of the reporting period (inclusive)
     * @param toDate   the end of the reporting period (inclusive)
     * @return list of string arrays — each containing period, order count,
     *         items sold and total revenue
     * @throws Exception if a database error occurs
     */
    public List<String[]> getTurnoverReport(Date fromDate, Date toDate) throws Exception {
        List<String[]> data = new ArrayList<>();
        ResultSet rs = db.query(
                "SELECT DATE_FORMAT(order_date, '%Y-%m') as period, " +
                        "COUNT(DISTINCT o.order_id) as orders, " +
                        "COALESCE(SUM(oi.quantity), 0) as items_sold, " +
                        "COALESCE(SUM(o.total_amount), 0) as revenue " +
                        "FROM `order` o " +
                        "LEFT JOIN orderitem oi ON o.order_id = oi.order_id " +
                        "WHERE o.order_date BETWEEN ? AND ? " +
                        "GROUP BY DATE_FORMAT(order_date, '%Y-%m') ORDER BY period",
                new java.sql.Date(fromDate.getTime()), new java.sql.Date(toDate.getTime())
        );

        while (rs.next()) {
            data.add(new String[]{
                    rs.getString("period"),
                    String.valueOf(rs.getInt("orders")),
                    String.valueOf(rs.getInt("items_sold")),
                    String.format("%.2f", rs.getDouble("revenue"))
            });
        }
        return data;
    }

    /**
     * 3. Merchant Orders Report
     * Retrieves all orders placed by a specific merchant within the date range.
     * Includes dispatch date and payment status from the linked invoice.
     * Results are ordered by order date descending.
     *
     * @param merchantId the unique merchant identifier to report on
     * @param fromDate   the start of the reporting period (inclusive)
     * @param toDate     the end of the reporting period (inclusive)
     * @return list of string arrays — each containing order ID, order date,
     *         total amount, dispatch date and payment status
     * @throws Exception if a database error occurs
     */
    public List<String[]> getMerchantOrdersReport(String merchantId,
                                                  Date fromDate,
                                                  Date toDate) throws Exception {
        List<String[]> data = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        ResultSet rs = db.query(
                "SELECT o.order_id, o.order_date, o.total_amount, o.dispatched_date, " +
                        "CASE WHEN i.amount_paid >= i.total_amount THEN 'Paid' ELSE 'Pending' END as payment " +
                        "FROM `order` o LEFT JOIN invoice i ON o.order_id = i.order_id " +
                        "WHERE o.merchant_id = ? AND o.order_date BETWEEN ? AND ? " +
                        "ORDER BY o.order_date DESC",
                merchantId, new java.sql.Date(fromDate.getTime()),
                new java.sql.Date(toDate.getTime())
        );

        while (rs.next()) {
            // Show "Pending" if the order has not yet been dispatched
            String dispatched = rs.getDate("dispatched_date") != null ?
                    sdf.format(rs.getDate("dispatched_date")) : "Pending";
            data.add(new String[]{
                    rs.getString("order_id"),
                    sdf.format(rs.getDate("order_date")),
                    String.format("%.2f", rs.getDouble("total_amount")),
                    dispatched,
                    rs.getString("payment")
            });
        }
        return data;
    }

    /**
     * 4. Merchant Activity Report
     * Generates a detailed formatted text report of all orders placed by a specific
     * merchant within the date range. Includes merchant account details, all orders
     * with their line items, discounts applied and payment status.
     * Orders are grouped in the output with a running item total per order.
     *
     * @param merchantId the unique merchant identifier to report on
     * @param fromDate   the start of the reporting period (inclusive)
     * @param toDate     the end of the reporting period (inclusive)
     * @return the full report as a formatted multi-line string
     * @throws Exception if a database error occurs
     */
    public String getMerchantActivityReport(String merchantId,
                                            Date fromDate,
                                            Date toDate) throws Exception {
        StringBuilder text = new StringBuilder();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        // Get merchant details
        ResultSet merchantRs = db.query(
                "SELECT company_name, email, phone, address, credit_limit, outstanding_balance " +
                        "FROM merchant WHERE merchant_id = ?", merchantId);

        if (merchantRs.next()) {
            text.append("MERCHANT ACTIVITY REPORT\n");
            text.append("=".repeat(60)).append("\n\n");
            text.append("Merchant Details:\n");
            text.append("  ID: ").append(merchantId).append("\n");
            text.append("  Company: ").append(merchantRs.getString("company_name")).append("\n");
            text.append("  Email: ").append(merchantRs.getString("email")).append("\n");
            text.append("  Phone: ").append(merchantRs.getString("phone")).append("\n");
            text.append("  Address: ").append(merchantRs.getString("address")).append("\n");
            text.append("  Credit Limit: £").append(
                    String.format("%.2f", merchantRs.getDouble("credit_limit"))).append("\n");
            text.append("  Outstanding Balance: £").append(
                    String.format("%.2f", merchantRs.getDouble("outstanding_balance"))).append("\n\n");
        }

        text.append("Period: ").append(sdf.format(fromDate))
                .append(" to ").append(sdf.format(toDate)).append("\n");
        text.append("-".repeat(60)).append("\n\n");

        // Get orders with items — joined to invoice for payment status
        ResultSet rs = db.query(
                "SELECT o.order_id, o.order_date, o.total_amount, o.discount_applied, " +
                        "CASE WHEN i.amount_paid >= i.total_amount THEN 'Paid' ELSE 'Pending' END as payment_status, " +
                        "oi.catalogue_item_id, c.description, oi.quantity, oi.unit_price " +
                        "FROM `Order` o " +
                        "LEFT JOIN invoice i ON o.order_id = i.order_id " +
                        "LEFT JOIN orderitem oi ON o.order_id = oi.order_id " +
                        "LEFT JOIN catalogue c ON oi.catalogue_item_id = c.item_id " +
                        "WHERE o.merchant_id = ? AND o.order_date BETWEEN ? AND ? " +
                        "ORDER BY o.order_date DESC, o.order_id",
                merchantId, new java.sql.Date(fromDate.getTime()),
                new java.sql.Date(toDate.getTime())
        );

        String currentOrder = "";
        double orderTotal   = 0;

        while (rs.next()) {
            String orderId = rs.getString("order_id");

            // When the order ID changes, print the running total for the previous order
            if (!orderId.equals(currentOrder)) {
                if (!currentOrder.isEmpty()) {
                    text.append(String.format("Order Total: £%.2f\n", orderTotal));
                    text.append("-".repeat(40)).append("\n");
                    orderTotal = 0;
                }
                currentOrder = orderId;
                text.append("\nOrder ID: ").append(orderId).append("\n");
                text.append("  Date: ").append(sdf.format(rs.getDate("order_date"))).append("\n");
                text.append("  Status: ").append(rs.getString("payment_status")).append("\n");
                text.append("  Discount: £").append(
                        String.format("%.2f", rs.getDouble("discount_applied"))).append("\n");
                text.append("  Items:\n");
            }

            // Append each line item with quantity, unit price and line total
            if (rs.getString("catalogue_item_id") != null) {
                double itemTotal = rs.getInt("quantity") * rs.getDouble("unit_price");
                orderTotal += itemTotal;
                text.append(String.format("    - %s: %d x £%.2f = £%.2f\n",
                        rs.getString("description"),
                        rs.getInt("quantity"),
                        rs.getDouble("unit_price"),
                        itemTotal));
            }
        }

        // Print final order total after the loop ends
        if (!currentOrder.isEmpty()) {
            text.append(String.format("Order Total: £%.2f\n", orderTotal));
        }

        if (text.toString().equals("")) {
            text.append("No activity found for this merchant in the selected period.");
        }

        return text.toString();
    }

    /**
     * 5. Invoices by Merchant
     * Retrieves all invoices for a specific merchant within the date range.
     * Results are ordered by invoice date descending.
     *
     * @param merchantId the unique merchant identifier to filter by
     * @param fromDate   the start of the reporting period (inclusive)
     * @param toDate     the end of the reporting period (inclusive)
     * @return list of string arrays — each containing invoice ID, invoice date,
     *         due date, order ID, total amount, amount paid and status
     * @throws Exception if a database error occurs
     */
    public List<String[]> getInvoicesByMerchant(String merchantId,
                                                Date fromDate,
                                                Date toDate) throws Exception {
        List<String[]> data = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        ResultSet rs = db.query(
                "SELECT i.invoice_id, i.invoice_date, i.due_date, i.order_id, " +
                        "i.total_amount, i.amount_paid, i.status " +
                        "FROM invoice i JOIN `order` o ON i.order_id = o.order_id " +
                        "WHERE o.merchant_id = ? AND i.invoice_date BETWEEN ? AND ? " +
                        "ORDER BY i.invoice_date DESC",
                merchantId, new java.sql.Date(fromDate.getTime()),
                new java.sql.Date(toDate.getTime())
        );

        while (rs.next()) {
            data.add(new String[]{
                    rs.getString("invoice_id"),
                    sdf.format(rs.getDate("invoice_date")),
                    sdf.format(rs.getDate("due_date")),
                    rs.getString("order_id"),
                    String.format("%.2f", rs.getDouble("total_amount")),
                    String.format("%.2f", rs.getDouble("amount_paid")),
                    rs.getString("status")
            });
        }
        return data;
    }

    /**
     * 6. All Invoices Report
     * Retrieves all invoices across all merchants within the date range.
     * Joins with the merchant table to include the company name.
     * Results are ordered by invoice date descending.
     *
     * @param fromDate the start of the reporting period (inclusive)
     * @param toDate   the end of the reporting period (inclusive)
     * @return list of string arrays — each containing invoice ID, company name,
     *         invoice date, due date, order ID, total amount, amount paid and status
     * @throws Exception if a database error occurs
     */
    public List<String[]> getAllInvoicesReport(Date fromDate, Date toDate) throws Exception {
        List<String[]> data = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        ResultSet rs = db.query(
                "SELECT i.invoice_id, m.company_name, i.invoice_date, i.due_date, " +
                        "i.order_id, i.total_amount, i.amount_paid, i.status " +
                        "FROM invoice i JOIN `order` o ON i.order_id = o.order_id " +
                        "JOIN merchant m ON o.merchant_id = m.merchant_id " +
                        "WHERE i.invoice_date BETWEEN ? AND ? ORDER BY i.invoice_date DESC",
                new java.sql.Date(fromDate.getTime()), new java.sql.Date(toDate.getTime())
        );

        while (rs.next()) {
            data.add(new String[]{
                    rs.getString("invoice_id"),
                    rs.getString("company_name"),
                    sdf.format(rs.getDate("invoice_date")),
                    sdf.format(rs.getDate("due_date")),
                    rs.getString("order_id"),
                    String.format("%.2f", rs.getDouble("total_amount")),
                    String.format("%.2f", rs.getDouble("amount_paid")),
                    rs.getString("status")
            });
        }
        return data;
    }

    /**
     * 7. Stock Turnover Report
     * Calculates stock movement per catalogue item for the given date range.
     * Queries goods sold from order items and goods received from stock deliveries
     * separately, then combines them to produce a net change per item.
     * Results are sorted alphabetically by item description.
     *
     * @param fromDate the start of the reporting period (inclusive)
     * @param toDate   the end of the reporting period (inclusive)
     * @return list of string arrays — each containing item ID, description,
     *         goods sold, goods received, net change and revenue from sales
     * @throws Exception if a database error occurs
     */
    public List<String[]> getStockTurnoverReport(Date fromDate, Date toDate) throws Exception {
        Map<String, SoldInfo> soldMap     = new HashMap<>();
        Map<String, Integer>  receivedMap = new HashMap<>();

        // Get goods sold within the date range
        ResultSet soldRs = db.query(
                "SELECT oi.catalogue_item_id, c.description, COALESCE(SUM(oi.quantity), 0) as sold, " +
                        "COALESCE(SUM(oi.total_price), 0) as revenue " +
                        "FROM orderitem oi JOIN `order` o ON oi.order_id = o.order_id " +
                        "JOIN catalogue c ON oi.catalogue_item_id = c.item_id " +
                        "WHERE o.order_date BETWEEN ? AND ? GROUP BY oi.catalogue_item_id, c.description",
                new java.sql.Date(fromDate.getTime()), new java.sql.Date(toDate.getTime())
        );

        while (soldRs.next()) {
            SoldInfo info       = new SoldInfo();
            info.description    = soldRs.getString("description");
            info.sold           = soldRs.getInt("sold");
            info.revenue        = soldRs.getDouble("revenue");
            soldMap.put(soldRs.getString("catalogue_item_id"), info);
        }

        // Get goods received via stock deliveries within the date range
        ResultSet receivedRs = db.query(
                "SELECT catalogue_item_id, COALESCE(SUM(quantity), 0) as received " +
                        "FROM stockdelivery WHERE delivery_date BETWEEN ? AND ? GROUP BY catalogue_item_id",
                new java.sql.Date(fromDate.getTime()), new java.sql.Date(toDate.getTime())
        );

        while (receivedRs.next()) {
            receivedMap.put(receivedRs.getString("catalogue_item_id"),
                    receivedRs.getInt("received"));
        }

        // Combine sold and received data — include items that only appear in one map
        List<String[]> data = new ArrayList<>();
        Set<String> allItems = new HashSet<>();
        allItems.addAll(soldMap.keySet());
        allItems.addAll(receivedMap.keySet());

        for (String itemId : allItems) {
            SoldInfo sold = soldMap.getOrDefault(itemId, new SoldInfo());
            int received  = receivedMap.getOrDefault(itemId, 0);
            data.add(new String[]{
                    itemId,
                    sold.description != null ? sold.description : itemId,
                    String.valueOf(sold.sold),
                    String.valueOf(received),
                    String.valueOf(received - sold.sold), // net change — positive means stock increased
                    String.format("%.2f", sold.revenue)
            });
        }

        // Sort alphabetically by item description
        data.sort((a, b) -> a[1].compareTo(b[1]));
        return data;
    }

    /**
     * Get merchant list for dropdown
     * Retrieves all active merchants for populating the merchant selector
     * in the report form. Returns ID and company name only.
     *
     * @return list of string arrays — each containing merchant ID and company name
     * @throws Exception if a database error occurs
     */
    public List<String[]> getMerchantList() throws Exception {
        List<String[]> merchants = new ArrayList<>();
        ResultSet rs = db.query(
                "SELECT merchant_id, company_name FROM merchant " +
                        "WHERE is_Active = 1 ORDER BY company_name"
        );

        while (rs.next()) {
            merchants.add(new String[]{
                    rs.getString("merchant_id"),
                    rs.getString("company_name")
            });
        }
        return merchants;
    }

    /**
     * Internal helper class for aggregating sold quantity and revenue per item
     * when building the stock turnover report.
     */
    private static class SoldInfo {
        String description = "";
        int    sold        = 0;
        double revenue     = 0;
    }

    /**
     * Calculates the recommended order quantity for a low stock item.
     * The target stock level is the minimum stock level increased by the buffer percent.
     * The recommended quantity is the difference between the target and current availability,
     * rounded up to the nearest whole unit. Returns zero if stock already meets the target.
     *
     * @param availability   the current stock availability
     * @param minStockLevel  the minimum stock level
     * @param bufferPercent  the buffer percentage above the minimum level to target
     * @return the recommended order quantity, or 0 if no reorder is needed
     */
    private int calculateRecommendedOrder(int availability, int minStockLevel,
                                          double bufferPercent) {
        double targetStock = minStockLevel * (1 + bufferPercent / 100.0);
        int recommended    = (int) Math.ceil(targetStock - availability);
        return Math.max(0, recommended); // never negative
    }

    /**
     * Retrieves full account details for a specific merchant.
     * Used by the report form to display merchant information alongside reports.
     *
     * @param merchantId the unique merchant identifier
     * @return string array containing merchant ID, company name, business type,
     *         registration number, email, phone, address, credit limit,
     *         outstanding balance and account status, or null if not found
     */
    public String[] getMerchantDetails(String merchantId) {
        try {
            DBConnection db = new DBConnection();
            ResultSet rs = db.query(
                    "SELECT merchant_id, company_name, business_type, registration_number, " +
                            "email, phone, address, credit_limit, outstanding_balance, account_status " +
                            "FROM merchant WHERE merchant_id = ?", merchantId);

            if (rs.next()) {
                return new String[]{
                        rs.getString("merchant_id"),
                        rs.getString("company_name"),
                        rs.getString("business_type"),
                        rs.getString("registration_number"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("address"),
                        String.format("£%.2f", rs.getDouble("credit_limit")),
                        String.format("£%.2f", rs.getDouble("outstanding_balance")),
                        rs.getString("account_status")
                };
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}