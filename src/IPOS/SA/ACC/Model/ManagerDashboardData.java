package IPOS.SA.ACC.Model;

import java.util.List;

/**
 * Data model for the Manager (Director of Operations) Dashboard.
 * Holds summary counts for dashboard stat cards and a list
 * of low stock items for the dashboard table.
 */
public class ManagerDashboardData {
    private int lowStockCount; // Stores the number of catalogue items currently below their minimum levels
    private int totalInvoices; // Stores the total number of invoices recorded this month
    private double totalTurnover; // Stores the total turnover made this month
    private int stockTurnover; // Stores the total deliveries for stock made this month
    private List<LowStockItem> lowStockItems; // Stores a list of all items below their min stock levels

    /**
     * Default constructor — initialises all counts to zero.
     * Used when no database records for stat cards are available.
     */
    public ManagerDashboardData() {
        this.lowStockCount = 0;
        this.totalInvoices = 0;
        this.totalTurnover = 0.0;
        this.stockTurnover = 0;
    }

    // Getters and Setters

    /**
     * Returns the number of items below minimum stock level.
     *
     * @return number of low stock items
     */
    public int getLowStockCount() { return lowStockCount; }

    /**
     * Sets the number of items below minimum stock level.
     *
     * @param lowStockCount number of low stock items
     */
    public void setLowStockCount(int lowStockCount) { this.lowStockCount = lowStockCount; }

    /**
     * Returns the total number of invoices this month.
     *
     * @return total invoice count
     */
    public int getTotalInvoices() { return totalInvoices; }

    /**
     * Sets the total number of invoices this month.
     *
     * @param totalInvoices total invoice count
     */
    public void setTotalInvoices(int totalInvoices) { this.totalInvoices = totalInvoices; }

    /**
     * Returns the total revenue turnover for the current month.
     *
     * @return total turnover amount
     */
    public double getTotalTurnover() { return totalTurnover; }

    /**
     * Sets the total revenue turnover for the current month.
     *
     * @param totalTurnover total turnover amount
     */
    public void setTotalTurnover(double totalTurnover) { this.totalTurnover = totalTurnover; }

    /**
     * Returns the number of stock deliveries recorded this month.
     *
     * @return stock delivery count
     */
    public int getStockTurnover() { return stockTurnover; }

    /**
     * Sets the number of stock deliveries recorded this month.
     *
     * @param stockTurnover stock delivery count
     */
    public void setStockTurnover(int stockTurnover) { this.stockTurnover = stockTurnover; }

    /**
     * Returns the list of catalogue items below minimum stock level.
     *
     * @return list of low stock items
     */
    public List<LowStockItem> getLowStockItems() { return lowStockItems; }

    /**
     * Sets the list of catalogue items below minimum stock level.
     *
     * @param lowStockItems list of low stock items
     */
    public void setLowStockItems(List<LowStockItem> lowStockItems) { this.lowStockItems = lowStockItems; }
}
