package IPOS.SA.ACC.Service;

import IPOS.SA.ACC.Model.ManagerDashboardData;
import IPOS.SA.ACC.Model.LowStockItem;
import IPOS.SA.DB.DBConnection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ManagerService {
    private DBConnection db;

    public ManagerService() {
        this.db = new DBConnection();
    }

    public ManagerDashboardData getDashboardData() throws Exception {
        ManagerDashboardData data = new ManagerDashboardData();

        // Get low stock count
        data.setLowStockCount(getLowStockCount());

        // Get total invoices for current month
        data.setTotalInvoices(getTotalInvoices());

        // Get total turnover for current month
        data.setTotalTurnover(getTotalTurnover());

        // Get stock turnover count
        data.setStockTurnover(getStockTurnover());

        // Get low stock items list
        data.setLowStockItems(getLowStockItems());

        return data;
    }

    private int getLowStockCount() throws Exception {
        ResultSet rs = db.query(
                "SELECT COUNT(*) as count FROM Catalogue WHERE availability <= minimum_stock_level AND is_active = 1"
        );

        if (rs.next()) {
            return rs.getInt("count");
        }
        return 0;
    }

    private int getTotalInvoices() throws Exception {
        try {
            ResultSet rs = db.query(
                    "SELECT COUNT(*) as count FROM Invoice WHERE MONTH(invoice_date) = MONTH(CURDATE()) " +
                            "AND YEAR(invoice_date) = YEAR(CURDATE())"
            );

            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (Exception e) {
            // Table might not exist yet, return 0
            System.err.println("Invoices table not found: " + e.getMessage());
        }
        return 0;
    }

    private double getTotalTurnover() throws Exception {
        try {
            ResultSet rs = db.query(
                    "SELECT COALESCE(SUM(total_amount), 0) as total FROM Invoice " +
                            "WHERE MONTH(invoice_date) = MONTH(CURDATE()) " +
                            "AND YEAR(invoice_date) = YEAR(CURDATE())"
            );

            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (Exception e) {
            // Table might not exist yet, return 0
            System.err.println("Invoices table not found: " + e.getMessage());
        }
        return 0.0;
    }

    private int getStockTurnover() throws Exception {
        ResultSet rs = db.query(
                "SELECT COUNT(*) as count FROM StockDelivery " +
                        "WHERE MONTH(delivery_date) = MONTH(CURDATE()) " +
                        "AND YEAR(delivery_date) = YEAR(CURDATE())"
        );

        if (rs.next()) {
            return rs.getInt("count");
        }
        return 0;
    }

    private List<LowStockItem> getLowStockItems() throws Exception {
        List<LowStockItem> items = new ArrayList<>();

        ResultSet rs = db.query(
                "SELECT item_id, description, availability, minimum_stock_level as stock_limit " +
                        "FROM Catalogue WHERE availability <= minimum_stock_level AND is_active = 1 " +
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
