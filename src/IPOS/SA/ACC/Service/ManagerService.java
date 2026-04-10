package IPOS.SA.ACC.Service;

import IPOS.SA.ACC.Model.ManagerDashboardData;
import IPOS.SA.ACC.Model.LowStockItem;
import IPOS.SA.DB.DBConnection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Service responsible for retrieving data required
 * for the Manager dashboard.
 */
public class ManagerService {
    private DBConnection db;

    /**
     * Initialises the service with a database connection.
     */
    public ManagerService() {
        this.db = new DBConnection();
    }

    /**
     * Retrieves all data for the dashboard cards and tables.
     *
     * @return populated ManagerDashboardData object
     * @throws Exception if a database error occurs
     */
    public ManagerDashboardData getDashboardData() throws Exception {
        ManagerDashboardData data = new ManagerDashboardData();

        // Gets low stock count
        data.setLowStockCount(getLowStockCount());

        // Gets total invoices for current month
        data.setTotalInvoices(getTotalInvoices());

        // Gets total turnover for current month
        data.setTotalTurnover(getTotalTurnover());

        // Gets stock turnover count
        data.setStockTurnover(getStockTurnover());

        // Gets low stock items list
        data.setLowStockItems(getLowStockItems());

        return data;
    }

    /**
     * Gets the count of low stock items
     * @return count of items below minimum stock level
     */
    private int getLowStockCount() throws Exception {
        ResultSet rs = db.query(
                "SELECT COUNT(*) as count FROM catalogue WHERE availability <= minimum_stock_level AND is_active = 1"
        );

        if (rs.next()) {
            return rs.getInt("count");
        }
        return 0;
    }

    /**
     * Gets the total invoices that have been generated for that month
     * @return number of invoices created in the current month
     */
    private int getTotalInvoices() throws Exception {
        try {
            ResultSet rs = db.query(
                    "SELECT COUNT(*) as count FROM invoice WHERE MONTH(invoice_date) = MONTH(CURDATE()) " +
                            "AND YEAR(invoice_date) = YEAR(CURDATE())"
            );

            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (Exception e) {
            // If table does not exist yet, return 0
            System.err.println("Invoices table not found: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Gets the total turnover made for that month based on the invoices generated.
     * @return total invoice value for the current month
     */
    private double getTotalTurnover() throws Exception {
        try {
            ResultSet rs = db.query(
                    "SELECT COALESCE(SUM(total_amount), 0) as total FROM invoice " +
                            "WHERE MONTH(invoice_date) = MONTH(CURDATE()) " +
                            "AND YEAR(invoice_date) = YEAR(CURDATE())"
            );

            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (Exception e) {
            // If table does not exist yet, return 0
            System.err.println("Invoices table not found: " + e.getMessage());
        }
        return 0.0;
    }

    /**
     * Gets tht total stock turnover for that month based on the number of stock deliveries made
     * @return number of stock deliveries in the current month
     */
    private int getStockTurnover() throws Exception {
        ResultSet rs = db.query(
                "SELECT COUNT(*) as count FROM stockdelivery " +
                        "WHERE MONTH(delivery_date) = MONTH(CURDATE()) " +
                        "AND YEAR(delivery_date) = YEAR(CURDATE())"
        );

        if (rs.next()) {
            return rs.getInt("count");
        }
        return 0;
    }

    /**
     * Gets the list of low stock items
     * @return list of low stock items ordered by severity
     */
    private List<LowStockItem> getLowStockItems() throws Exception {
        List<LowStockItem> items = new ArrayList<>();

        ResultSet rs = db.query(
                "SELECT item_id, description, availability, minimum_stock_level as stock_limit " +
                        "FROM catalogue WHERE availability <= minimum_stock_level AND is_active = 1 " +
                        "ORDER BY (minimum_stock_level - availability) DESC LIMIT 10"
        );

        while (rs.next()) {
            items.add(new LowStockItem(
                    rs.getString("item_id"),
                    rs.getString("description"),
                    rs.getInt("availability"),
                    rs.getInt("stock_limit")
            ));
        }

        return items;
    }
}
