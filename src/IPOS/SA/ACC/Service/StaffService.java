package IPOS.SA.ACC.Service;

import IPOS.SA.ACC.Model.Staff;
import IPOS.SA.ACC.Model.StaffDashboardData;
import IPOS.SA.ACC.Model.OrderSummary;
import IPOS.SA.DB.DBConnection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class StaffService {
    private DBConnection db;

    public StaffService() {
        this.db = new DBConnection();
    }

    /**
     * Get all dashboard data for staff
     */
    public StaffDashboardData getDashboardData() throws Exception {
        StaffDashboardData data = new StaffDashboardData();

        // Get pending orders count
        data.setPendingOrders(getPendingOrders());

        // Get recent orders count (last 7 days)
        data.setRecentOrders(getRecentOrdersCount());

        // Get total value processed this month
        data.setTotalValueProcessed(getTotalValueProcessed());

        // Get recent orders list
        data.setRecentOrderList(getRecentOrdersList());

        return data;
    }

    /**
     * Get count of pending orders
     */
    private int getPendingOrders() throws Exception {
        try {
            ResultSet rs = db.query(
                    "SELECT COUNT(*) as count FROM Orders WHERE status IN ('Pending', 'Processing')"
            );

            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (Exception e) {
            // Orders table might not exist yet
            System.err.println("Orders table not found: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Get count of orders from last 7 days
     */
    private int getRecentOrdersCount() throws Exception {
        try {
            ResultSet rs = db.query(
                    "SELECT COUNT(*) as count FROM Orders WHERE order_date >= DATE_SUB(CURRENT_DATE(), INTERVAL 7 DAY)"
            );

            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (Exception e) {
            System.err.println("Orders table not found: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Get total value of orders processed this month
     */
    private double getTotalValueProcessed() throws Exception {
        try {
            ResultSet rs = db.query(
                    "SELECT COALESCE(SUM(total_amount), 0) as total FROM Orders " +
                            "WHERE MONTH(order_date) = MONTH(CURRENT_DATE()) " +
                            "AND YEAR(order_date) = YEAR(CURRENT_DATE())"
            );

            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (Exception e) {
            System.err.println("Orders table not found: " + e.getMessage());
        }
        return 0.0;
    }

    /**
     * Get list of recent orders (last 5)
     */
    private List<OrderSummary> getRecentOrdersList() throws Exception {
        List<OrderSummary> orders = new ArrayList<>();

        try {
            ResultSet rs = db.query(
                    "SELECT order_id, merchant_name, order_date, status, total_amount " +
                            "FROM Orders ORDER BY order_date DESC LIMIT 5"
            );

            while (rs.next()) {
                orders.add(new OrderSummary(
                        rs.getString("order_id"),
                        rs.getString("merchant_name"),
                        rs.getString("order_date"),
                        rs.getString("status"),
                        rs.getDouble("total_amount")
                ));
            }
        } catch (Exception e) {
            System.err.println("Orders table not found: " + e.getMessage());
            // Return sample data if table doesn't exist
            orders.add(new OrderSummary("ORD-001", "Sample Merchant", "2024-01-15", "Processing", 250.00));
            orders.add(new OrderSummary("ORD-002", "Sample Merchant", "2024-01-14", "Delivered", 180.50));
        }

        return orders;
    }

    public List<Staff> getStaffList() throws Exception {
        List<Staff> staffList = new ArrayList<>();

        ResultSet rs = db.query(
                "SELECT user_id, username, full_name, email, role, is_active " +
                        "FROM User_Login WHERE is_active = 1 ORDER BY full_name"
        );

        while (rs.next()) {
            Staff staff = new Staff();
            staff.setStaffId(rs.getString("user_id"));
            staff.setUsername(rs.getString("username"));
            staff.setFirstName(rs.getString("full_name"));
            staff.setEmail(rs.getString("email"));
            staff.setRole(rs.getString("role"));
            staff.setActive(rs.getInt("is_active") == 1);
            staffList.add(staff);
        }

        return staffList;
    }
}
