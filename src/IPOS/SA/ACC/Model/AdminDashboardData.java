package IPOS.SA.ACC.Model;

import java.util.List;

public class AdminDashboardData {

    private int lowStockCount;
    private int stockDeliveriesCount;
    private int overduePaymentsCount;
    private List<OrderSummary> recentOrders;
    private List<LowStockItem> lowStockItems;

    public AdminDashboardData() {
        this.lowStockCount = 0;
        this.stockDeliveriesCount = 0;
        this.overduePaymentsCount = 0;
    }

    // Getters and Setters
    public int getLowStockCount() { return lowStockCount; }
    public void setLowStockCount(int lowStockCount) { this.lowStockCount = lowStockCount; }

    public int getStockDeliveriesCount() { return stockDeliveriesCount; }
    public void setStockDeliveriesCount(int stockDeliveriesCount) { this.stockDeliveriesCount = stockDeliveriesCount; }

    public int getOverduePaymentsCount() { return overduePaymentsCount; }
    public void setOverduePaymentsCount(int overduePaymentsCount) { this.overduePaymentsCount = overduePaymentsCount; }

    public List<OrderSummary> getRecentOrders() { return recentOrders; }
    public void setRecentOrders(List<OrderSummary> recentOrders) { this.recentOrders = recentOrders; }

    public List<LowStockItem> getLowStockItems() { return lowStockItems; }
    public void setLowStockItems(List<LowStockItem> lowStockItems) { this.lowStockItems = lowStockItems; }
}
