package IPOS.SA.RPT.Model;
/**
 * Data model representing a single catalogue item's stock movement
 * for use in the Stock Turnover report.
 * Shows how much stock was sold and received in the report period,
 * the net change in stock level and the revenue generated from sales.
 *
 * The net change is calculated automatically as goodsReceived minus goodsSold.
 * A positive net change means stock increased. A negative net change means
 * more stock was sold than received.
 */
public class StockTurnoverRptData {
    /** The unique catalogue item identifier. */
    private String itemId;
    /** The description of the catalogue item. */
    private String description;
    /** The total number of packs sold in the report period. */
    private int goodsSold;
    /** The total number of packs received via stock deliveries in the report period. */
    private int goodsReceived;
    /** The net change in stock level — goodsReceived minus goodsSold. */
    private int netChange;
    /** The total revenue generated from sales of this item in the report period. */
    private double revenueFromSales;


    /**
     * Constructor — creates a stock turnover record for a catalogue item.
     * The net change is calculated automatically.
     *
     * @param itemId           the unique catalogue item identifier
     * @param description      the item description
     * @param goodsSold        the number of packs sold in the report period
     * @param goodsReceived    the number of packs received in the report period
     * @param revenueFromSales the total revenue from sales of this item
     */
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
    /**
     * Returns the unique catalogue item identifier.
     *
     * @return the item ID
     */
    public String getItemId() { return itemId; }
    /**
     * Returns the description of the catalogue item.
     *
     * @return the item description
     */
    public String getDescription() { return description; }
    /**
     * Returns the total number of packs sold in the report period.
     *
     * @return the number of packs sold
     */
    public int getGoodsSold() { return goodsSold; }
    /**
     * Returns the total number of packs sold in the report period.
     *
     * @return the number of packs sold
     */
    public int getGoodsReceived() { return goodsReceived; }
    /**
     * Returns the net change in stock level for the report period.
     * Positive means stock increased, negative means more was sold than received.
     *
     * @return the net stock change
     */
    public int getNetChange() { return netChange; }
    /**
     * Returns the total revenue generated from sales of this item.
     *
     * @return the revenue from sales
     */
    public double getRevenueFromSales() { return revenueFromSales; }
}