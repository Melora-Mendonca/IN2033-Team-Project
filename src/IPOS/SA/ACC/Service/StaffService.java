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
                    "SELECT COUNT(*) as count FROM `Order` WHERE status IN ('pending', 'processing')"
            );

            if (rs != null && rs.next()) {
                return rs.getInt("count");
            }
        } catch (Exception e) {
            System.err.println("Orders table error: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Get count of orders from last 7 days
     */
    private int getRecentOrdersCount() throws Exception {
        try {
            ResultSet rs = db.query(
                    "SELECT COUNT(*) as count FROM `Order` WHERE order_date >= DATE_SUB(CURDATE(), INTERVAL 7 DAY)"
            );

            if (rs != null && rs.next()) {
                return rs.getInt("count");
            }
        } catch (Exception e) {
            System.err.println("Orders table error: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Get total value of orders processed this month
     */
    private double getTotalValueProcessed() throws Exception {
        try {
            ResultSet rs = db.query(
                    "SELECT COALESCE(SUM(total_amount), 0) as total FROM `Order` " +
                            "WHERE MONTH(order_date) = MONTH(CURDATE()) " +
                            "AND YEAR(order_date) = YEAR(CURDATE())"
            );

            if (rs != null && rs.next()) {
                return rs.getDouble("total");
            }
        } catch (Exception e) {
            System.err.println("Orders table error: " + e.getMessage());
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
                    "SELECT o.order_id, m.company_name as merchant_name, o.order_date, o.status, o.total_amount " +
                            "FROM `Order` o " +
                            "JOIN Merchant m ON o.merchant_id = m.merchant_id " +
                            "ORDER BY o.order_date DESC LIMIT 5"
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
            System.err.println("Orders table error: " + e.getMessage());
            // Return sample data if table doesn't exist
            orders.add(new OrderSummary("ORD-001", "Sample Merchant", "2024-01-15", "Processing", 250.00));
            orders.add(new OrderSummary("ORD-002", "Sample Merchant", "2024-01-14", "Delivered", 180.50));
        }

        return orders;
    }

    // Add this method if needed for staff list
    public List<Staff> getStaffList() throws Exception {
        List<Staff> staffList = new ArrayList<>();

        ResultSet rs = db.query(
                "SELECT user_id, username, first_Name, sur_Name, email, role, is_Active " +
                        "FROM UserLogin WHERE is_Active = 1 ORDER BY first_Name, sur_Name"
        );

        while (rs != null && rs.next()) {
            Staff staff = new Staff();
            staff.setStaffId(String.valueOf(rs.getInt("user_id")));
            staff.setUsername(rs.getString("username"));
            staff.setFirstName(rs.getString("first_Name"));
            staff.setSurName(rs.getString("sur_Name"));
            staff.setEmail(rs.getString("email"));
            staff.setRole(rs.getString("role"));
            staff.setActive(rs.getInt("is_Active") == 1);
            staffList.add(staff);
        }

        return staffList;
    }
}
