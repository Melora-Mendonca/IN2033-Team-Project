package IPOS.SA.RPT.Model;

public class StockTurnoverRptData {
    private String itemId;
    private String description;
    private int goodsSold;
    private int goodsReceived;
    private int netChange;
    private double revenueFromSales;

    public StockTurnoverRptData(String itemId, String description, int goodsSold,
                             int goodsReceived, double revenueFromSales) {
        this.itemId = itemId;
        this.description = description;
        this.goodsSold = goodsSold;
        this.goodsReceived = goodsReceived;
        this.netChange = goodsReceived - goodsSold;
        this.revenueFromSales = revenueFromSales;
    }

    // Getters
    public String getItemId() { return itemId; }
    public String getDescription() { return description; }
    public int getGoodsSold() { return goodsSold; }
    public int getGoodsReceived() { return goodsReceived; }
    public int getNetChange() { return netChange; }
    public double getRevenueFromSales() { return revenueFromSales; }
}