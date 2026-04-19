package IPOS.SA.RPT.Model;
/**
 * Data model representing sales turnover figures for a given time period.
 * Used in the Monthly Turnover report to show order volumes, revenue
 * and average order value per period.
 *
 * The average order value is calculated automatically in the constructor
 * as totalRevenue divided by totalOrders. Returns zero if there are no orders
 * to avoid division by zero.
 */
public class TurnoverRptData {
    /** The label for the reporting period (e.g. "January 2025", "Q1 2025"). */
    private String period;
    /** The total number of orders placed in this period. */
    private int totalOrders;
    /** The total number of individual items sold across all orders in this period. */
    private int totalItemsSold;
    /** The total revenue generated from all orders in this period. */
    private double totalRevenue;
    /** The average value per order in this period — calculated from totalRevenue and totalOrders. */
    private double averageOrderValue;
    /**
     * Constructor — creates a turnover record for a reporting period.
     * The average order value is calculated automatically.
     * Returns zero average if there are no orders to avoid division by zero.
     *
     * @param period         the label for the reporting period
     * @param totalOrders    the total number of orders in the period
     * @param totalItemsSold the total number of items sold in the period
     * @param totalRevenue   the total revenue generated in the period
     */
    public TurnoverRptData(String period, int totalOrders, int totalItemsSold, double totalRevenue) {
        this.period = period;
        this.totalOrders = totalOrders;
        this.totalItemsSold = totalItemsSold;
        this.totalRevenue = totalRevenue;
        this.averageOrderValue = totalOrders > 0 ? totalRevenue / totalOrders : 0;
    }

    // Getters
    /**
     * Returns the label for the reporting period.
     *
     * @return the period label
     */
    public String getPeriod() { return period; }
    /**
     * Returns the total number of orders placed in this period.
     *
     * @return the total order count
     */
    public int getTotalOrders() { return totalOrders; }
    /**
     * Returns the total number of items sold across all orders in this period.
     *
     * @return the total items sold
     */
    public int getTotalItemsSold() { return totalItemsSold; }
    /**
     * Returns the total revenue generated from all orders in this period.
     *
     * @return the total revenue
     */
    public double getTotalRevenue() { return totalRevenue; }
    /**
     * Returns the average monetary value per order in this period.
     * Returns zero if there were no orders in the period.
     *
     * @return the average order value
     */
    public double getAverageOrderValue() { return averageOrderValue; }
}
