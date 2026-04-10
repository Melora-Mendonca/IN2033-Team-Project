package IPOS.SA.ACC.Service;

import IPOS.SA.ACC.Model.*;
import IPOS.SA.DB.DBConnection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class responsible for retrieving and aggregating
 * data required for the Admin Dashboard.
 */
public class AdminService {
    private DBConnection db;

    /**
     * Default constructor initializes the service with a database connection.
     */
    public AdminService() {
        this.db = new DBConnection();
    }

    /**
     * Retrieves all dashboard data and aggregates it into a single object.
     *
     * @return AdminDashboardData containing counts and lists for the dashboard
     * @throws Exception if an error occurs during database querying.
     */
    public AdminDashboardData getDashboardData() throws Exception {
        AdminDashboardData data = new AdminDashboardData();

        // Get low stock count
        data.setLowStockCount(getLowStockCount());

        // Get stock deliveries count for current month
        data.setStockDeliveriesCount(getStockDeliveriesCount());

        // Get overdue payments count
        data.setOverduePaymentsCount(getOverduePaymentsCount());

        // Get recent orders list
        data.setRecentOrders(getRecentOrders());

        // Get low stock items list
        data.setLowStockItems(getLowStockItems());

        return data;
    }

    /**
     * Retrieves the count of items that are below their minimum stock level.
     *
     * @return number of low stock items
     * @throws Exception if a database error occurs
     */
    private int getLowStockCount() throws Exception {
        try {
            ResultSet rs = db.query(
                    "SELECT COUNT(*) as count FROM catalogue WHERE availability <= minimum_stock_level AND is_active = 1"
            );
            if (rs != null && rs.next()) {
                return rs.getInt("count");
            }
        } catch (Exception e) {
            System.err.println("Query error in getLowStockCount: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Retrieves the number of stock deliveries made in the current month.
     *
     * @return number of deliveries for the current month
     * @throws Exception if a database error occurs
     */
    private int getStockDeliveriesCount() throws Exception {
        try {
            ResultSet rs = db.query(
                    "SELECT COUNT(*) as count FROM stockdelivery " +
                            "WHERE MONTH(delivery_date) = MONTH(CURRENT_DATE) " +
                            "AND YEAR(delivery_date) = YEAR(CURRENT_DATE)"
            );
            if (rs != null && rs.next()) {
                return rs.getInt("count");
            }
        } catch (Exception e) {
            System.err.println("Query error in getStockDeliveriesCount: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Retrieves the number of invoices that are overdue.
     * An invoice is considered overdue if its due date has passed, and it is not marked as fully paid.
     *
     * @return number of overdue invoices
     * @throws Exception if a database error occurs
     */
    private int getOverduePaymentsCount() throws Exception {
        try {
            // Query invoices that are overdue (due date passed and not fully paid)
            ResultSet rs = db.query(
                    "SELECT COUNT(*) as count FROM invoice " +
                            "WHERE due_date < CURDATE() AND status != 'paid'"
            );
            if (rs != null && rs.next()) {
                return rs.getInt("count");
            }
        } catch (Exception e) {
            System.err.println("Query error in getOverduePaymentsCount: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Retrieves the most recent orders along with merchant details.
     *
     * @return list of recent order summaries
     * @throws Exception if a database error occurs
     */
    private List<OrderSummary> getRecentOrders() throws Exception {
        List<OrderSummary> orders = new ArrayList<>();

        try {
            // Query the Order table with merchant details
            ResultSet rs = db.query(
                    "SELECT o.order_id, o.order_date, o.status, o.total_amount, m.company_name as merchant_name " +
                            "FROM `order` o " +
                            "JOIN merchant m ON o.merchant_id = m.merchant_id " +
                            "ORDER BY o.order_date DESC"
            );

            while (rs != null && rs.next()) {
                orders.add(new OrderSummary(
                        rs.getString("order_id"),
                        rs.getString("merchant_name"),
                        rs.getString("order_date"),
                        rs.getString("status"),
                        rs.getDouble("total_amount")
                ));
            }
        } catch (Exception e) {
            System.err.println("Query error in getRecentOrders: " + e.getMessage());
        }

        return orders;
    }

    /**
     * Retrieves a list of items that are low in stock,
     * ordered by how critical the shortage is.
     *
     * @return list of low stock items
     * @throws Exception if a database error occurs
     */
    private List<LowStockItem> getLowStockItems() throws Exception {
        List<LowStockItem> items = new ArrayList<>();

        try {
            ResultSet rs = db.query(
                    "SELECT item_id, description, availability, minimum_stock_level " +
                            "FROM catalogue WHERE availability <= minimum_stock_level AND is_active = 1 " +
                            "ORDER BY (minimum_stock_level - availability) DESC"
            );

            if (rs != null) {
                while (rs.next()) {
                    items.add(new LowStockItem(
                            rs.getString("item_id"),
                            rs.getString("description"),
                            rs.getInt("availability"),
                            rs.getInt("minimum_stock_level")  // Changed from stock_limit
                    ));
                }
            }
        } catch (Exception e) {
            System.err.println("Error getting low stock items: " + e.getMessage());
        }

        return items;
    }
}
