package IPOS.SA.ACC.Model;

import java.util.List;

/**
 * Dataloader class for admin Dashboard
 * Represents the data required for the Admin Dashboard.
 * Holds summary counts and lists used to populate dashboard components.
 */
public class AdminDashboardData {

    private int lowStockCount;
    private int stockDeliveriesCount;
    private int overduePaymentsCount;
    private List<OrderSummary> recentOrders; // Stores a list of all the recent orders that have been made, with their status
    private List<LowStockItem> lowStockItems; // Stores a list of all the items that are below their minimum stock levels

    /**
     * Default constructor intialises initial count for the dashboard cards to 0, if no records are available to reference
      */
    public AdminDashboardData() {
        this.lowStockCount = 0;
        this.stockDeliveriesCount = 0;
        this.overduePaymentsCount = 0;
    }

    /**
     * Gets the Current count of items that are low in stock
     * @return lowStockCount the value of items below minimum stock levels
     */
    public int getLowStockCount() { return lowStockCount; }

    /**
     * @param lowStockCount the number of items below minimum stock level
     */
    public void setLowStockCount(int lowStockCount) { this.lowStockCount = lowStockCount; }

    /**
     * Gets the number of stock deliveries made in that month
     * @return the number of stock deliveries
     */
    public int getStockDeliveriesCount() { return stockDeliveriesCount; }

    /**
     * @param stockDeliveriesCount the number of stock deliveries
     */
    public void setStockDeliveriesCount(int stockDeliveriesCount) { this.stockDeliveriesCount = stockDeliveriesCount; }

    /**
     * Gets the number of overdue payments FROM merchants
     * @return the number of overdue payments
     */
    public int getOverduePaymentsCount() { return overduePaymentsCount; }

    /**
     * @param overduePaymentsCount the number of overdue payments
     */
    public void setOverduePaymentsCount(int overduePaymentsCount) { this.overduePaymentsCount = overduePaymentsCount; }

    /**
     * Gets the list of recent orders from the database
     * @return a list of recent orders
     */
    public List<OrderSummary> getRecentOrders() { return recentOrders; }

    /**
     * @param recentOrders list of recent order summaries
     */
    public void setRecentOrders(List<OrderSummary> recentOrders) { this.recentOrders = recentOrders; }

    /**
     * Gets a list of low stock items from the catalogue
     * @return a list of items that are low in stock
     */
    public List<LowStockItem> getLowStockItems() { return lowStockItems; }

    /**
     * @param lowStockItems list of low stock items
     */
    public void setLowStockItems(List<LowStockItem> lowStockItems) { this.lowStockItems = lowStockItems; }
}
