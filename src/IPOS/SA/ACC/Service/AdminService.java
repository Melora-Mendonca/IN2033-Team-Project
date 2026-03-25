package IPOS.SA.ACC.Service;

import IPOS.SA.ACC.Model.*;
import IPOS.SA.DB.DBConnection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class AdminService {
    private DBConnection db;

    public AdminService() {
        this.db = new DBConnection();
    }

    public AdminDashboardData getDashboardData() throws Exception {
        AdminDashboardData data = new AdminDashboardData();

        // Get low stock count
        data.setLowStockCount(getLowStockCount());

        // Get stock deliveries count for current month
        data.setStockDeliveriesCount(getStockDeliveriesCount());

        // Get overdue payments count
        data.setOverduePaymentsCount(getOverduePaymentsCount());

        // Get recent orders
        //data.setRecentOrders(getRecentOrders());

        // Get low stock items
        data.setLowStockItems(getLowStockItems());

        return data;
    }

    private int getLowStockCount() throws Exception {
        ResultSet rs = db.query(
                "SELECT COUNT(*) as count FROM Catalogue WHERE availability <= stock_limit AND is_active = 1"
        );
        if (rs.next()) {
            return rs.getInt("count");
        }
        return 0;
    }

    private int getStockDeliveriesCount() throws Exception {
        ResultSet rs = db.query(
                "SELECT COUNT(*) as count FROM Stock_Deliveries " +
                        "WHERE MONTH(delivery_date) = MONTH(CURRENT_DATE()) " +
                        "AND YEAR(delivery_date) = YEAR(CURRENT_DATE())"
        );
        if (rs.next()) {
            return rs.getInt("count");
        }
        return 0;
    }

    private int getOverduePaymentsCount() throws Exception {
//        // This is a placeholder - implement based on your payment schema
//        ResultSet rs = db.query(
//                "SELECT COUNT(*) as count FROM Merchant_Details WHERE current_balance > credit_limit"
//        );
//        if (rs.next()) {
//            return rs.getInt("count");
//        }
        return 0;
    }

//    private List<OrderSummary> getRecentOrders() throws Exception {
//        List<OrderSummary> orders = new ArrayList<>();
//
//        // This query assumes you have an Orders table - adjust based on your schema
//        ResultSet rs = db.query(
//                "SELECT order_id, merchant_name, order_date, status, total_amount " +
//                        "FROM Orders ORDER BY order_date DESC LIMIT 5"
//        );
//
//        while (rs.next()) {
//            orders.add(new OrderSummary(
//                    rs.getString("order_id"),
//                    rs.getString("merchant_name"),
//                    rs.getString("order_date"),
//                    rs.getString("status"),
//                    rs.getDouble("total_amount")
//            ));
//        }
//
//        return orders;
//    }

    private List<LowStockItem> getLowStockItems() throws Exception {
        List<LowStockItem> items = new ArrayList<>();

        ResultSet rs = db.query(
                "SELECT item_id, description, availability, stock_limit " +
                        "FROM Catalogue WHERE availability <= stock_limit AND is_active = 1 " +
                        "ORDER BY (stock_limit - availability) DESC LIMIT 10"
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

    public List<MerchantAccount> getAllMerchants() throws Exception {
        List<MerchantAccount> merchants = new ArrayList<>();

        ResultSet rs = db.query(
                "SELECT ipos_account_no, company_name, email, phone, address, credit_limit, " +
                        "current_balance, account_status, fixed_rate, DATE_FORMAT(registration_date, '%Y-%m-%d') as registration_date " +
                        "FROM Merchant_Details ORDER BY company_name"
        );

        while (rs.next()) {
            MerchantAccount merchant = new MerchantAccount(
                    rs.getString("ipos_account_no"),
                    rs.getString("company_name"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getString("address"),
                    rs.getDouble("credit_limit"),
                    rs.getDouble("current_balance"),
                    rs.getString("account_status"),
                    rs.getDouble("fixed_rate")
            );
            merchants.add(merchant);
        }

        return merchants;
    }


}
