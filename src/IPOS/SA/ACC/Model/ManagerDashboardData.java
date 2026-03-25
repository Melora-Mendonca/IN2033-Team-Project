package IPOS.SA.ACC.Model;

import java.util.List;

public class ManagerDashboardData {
    private int lowStockCount;
    private int totalInvoices;
    private double totalTurnover;
    private int stockTurnover;
    private List<LowStockItem> lowStockItems;

    public ManagerDashboardData() {
        this.lowStockCount = 0;
        this.totalInvoices = 0;
        this.totalTurnover = 0.0;
        this.stockTurnover = 0;
    }

    // Getters and Setters
    public int getLowStockCount() { return lowStockCount; }
    public void setLowStockCount(int lowStockCount) { this.lowStockCount = lowStockCount; }

    public int getTotalInvoices() { return totalInvoices; }
    public void setTotalInvoices(int totalInvoices) { this.totalInvoices = totalInvoices; }

    public double getTotalTurnover() { return totalTurnover; }
    public void setTotalTurnover(double totalTurnover) { this.totalTurnover = totalTurnover; }

    public int getStockTurnover() { return stockTurnover; }
    public void setStockTurnover(int stockTurnover) { this.stockTurnover = stockTurnover; }

    public List<LowStockItem> getLowStockItems() { return lowStockItems; }
    public void setLowStockItems(List<LowStockItem> lowStockItems) { this.lowStockItems = lowStockItems; }
}
