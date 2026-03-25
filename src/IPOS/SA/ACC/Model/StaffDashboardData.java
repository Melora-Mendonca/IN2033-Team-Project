package IPOS.SA.ACC.Model;

import java.util.List;

public class StaffDashboardData {
    private int pendingOrders;
    private int recentOrders;
    private double totalValueProcessed;
    private List<OrderSummary> recentOrderList;

    public StaffDashboardData() {
        this.pendingOrders = 0;
        this.recentOrders = 0;
        this.totalValueProcessed = 0.0;
    }

    public int getPendingOrders() { return pendingOrders; }
    public void setPendingOrders(int pendingOrders) { this.pendingOrders = pendingOrders; }

    public int getRecentOrders() { return recentOrders; }
    public void setRecentOrders(int recentOrders) { this.recentOrders = recentOrders; }

    public double getTotalValueProcessed() { return totalValueProcessed; }
    public void setTotalValueProcessed(double totalValueProcessed) { this.totalValueProcessed = totalValueProcessed; }

    public List<OrderSummary> getRecentOrderList() { return recentOrderList; }
    public void setRecentOrderList(List<OrderSummary> recentOrderList) { this.recentOrderList = recentOrderList; }
}
