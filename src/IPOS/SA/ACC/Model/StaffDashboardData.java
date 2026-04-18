package IPOS.SA.ACC.Model;

import java.util.List;

/**
 * Data model for the Staff Dashboard.
 * Holds summary counts for dashboard stat cards and a list
 * of recent orders for the dashboard orders table.
 * Used for warehouse and delivery employee dashboards.
 */
public class StaffDashboardData {
    private int pendingOrders; // the number of orders that are currently pending
    private int recentOrders; // the number of orders recently processed
    private double totalValueProcessed; // the total monetary value of the processed orders
    private List<OrderSummary> recentOrderList; // the list of recently placed orders

    /**
     * Default constructor — initialises all counts to zero.
     * Used when no database records are available.
     */
    public StaffDashboardData() {
        this.pendingOrders = 0;
        this.recentOrders = 0;
        this.totalValueProcessed = 0.0;
    }

    // Getter and setter methods for data values

    /**
     * Gets all the orders that are currently in pending status
     * @return number of orders in pending status
     */
    public int getPendingOrders() { return pendingOrders; }

    /**
     * sets all the number of orders that are currently in pending status
     * @param pendingOrders the number of pending order
     */
    public void setPendingOrders(int pendingOrders) { this.pendingOrders = pendingOrders; }

    /**
     * Gets all the orders that were placed recently
     * @return number of orders in placed recently
     */
    public int getRecentOrders() { return recentOrders; }

    /**
     * sets all the number of orders that were placed
     * @param recentOrders the number of recently processed orders
     */
    public void setRecentOrders(int recentOrders) { this.recentOrders = recentOrders; }

    /**
     * Gets the total monetary value fo the orders processed
     * @return total monetary value
     */
    public double getTotalValueProcessed() { return totalValueProcessed; }

    /**
     * sets the monetary value of the total orders
     * @param totalValueProcessed the total value of orders processed
     */
    public void setTotalValueProcessed(double totalValueProcessed) { this.totalValueProcessed = totalValueProcessed; }

    /**
     * Gets the list of recent orders to display in the dashboard
     * @return the list of recent orders for the dashboard table
     */
    public List<OrderSummary> getRecentOrderList() { return recentOrderList; }

    /**
     * Sets the list of orders to the list passed in
     * @param recentOrderList the list of recent orders */
    public void setRecentOrderList(List<OrderSummary> recentOrderList) { this.recentOrderList = recentOrderList; }
}
