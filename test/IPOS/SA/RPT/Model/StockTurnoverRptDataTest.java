package IPOS.SA.RPT.Model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class StockTurnoverRptDataTest {

    // --- netChange calculation ---

    @Test
    void constructor_receivedMoreThanSold_positiveNetChange() {
        StockTurnoverRptData data = new StockTurnoverRptData("ITEM-01", "Widget", 30, 50, 300.0);
        assertEquals(20, data.getNetChange());
    }

    @Test
    void constructor_soldMoreThanReceived_negativeNetChange() {
        StockTurnoverRptData data = new StockTurnoverRptData("ITEM-02", "Gadget", 50, 20, 500.0);
        assertEquals(-30, data.getNetChange());
    }

    @Test
    void constructor_soldEqualsReceived_zeroNetChange() {
        StockTurnoverRptData data = new StockTurnoverRptData("ITEM-03", "Doohickey", 40, 40, 400.0);
        assertEquals(0, data.getNetChange());
    }

    @Test
    void constructor_allZeros_zeroNetChange() {
        StockTurnoverRptData data = new StockTurnoverRptData("ITEM-04", "Thing", 0, 0, 0.0);
        assertEquals(0, data.getNetChange());
        assertEquals(0.0, data.getRevenueFromSales(), 0.001);
    }

    // --- getters ---

    @Test
    void constructor_storesAllFields() {
        StockTurnoverRptData data = new StockTurnoverRptData("ITEM-05", "Sprocket", 10, 25, 150.0);
        assertEquals("ITEM-05", data.getItemId());
        assertEquals("Sprocket", data.getDescription());
        assertEquals(10, data.getGoodsSold());
        assertEquals(25, data.getGoodsReceived());
        assertEquals(150.0, data.getRevenueFromSales(), 0.001);
    }
}
