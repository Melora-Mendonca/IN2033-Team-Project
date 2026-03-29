package IPOS.SA.RPT.Model;

public class TurnoverRptData {
    private String period;
    private int totalOrders;
    private int totalItemsSold;
    private double totalRevenue;
    private double averageOrderValue;

    public TurnoverRptData(String period, int totalOrders, int totalItemsSold, double totalRevenue) {
        this.period = period;
        this.totalOrders = totalOrders;
        this.totalItemsSold = totalItemsSold;
        this.totalRevenue = totalRevenue;
        this.averageOrderValue = totalOrders > 0 ? totalRevenue / totalOrders : 0;
    }

    // Getters
    public String getPeriod() { return period; }
    public int getTotalOrders() { return totalOrders; }
    public int getTotalItemsSold() { return totalItemsSold; }
    public double getTotalRevenue() { return totalRevenue; }
    public double getAverageOrderValue() { return averageOrderValue; }
}
