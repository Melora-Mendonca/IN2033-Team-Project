package IPOS.SA.RPT.Model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TurnoverRptDataTest {

    // --- averageOrderValue calculation ---

    @Test
    void constructor_normalValues_calculatesAverageOrderValue() {
        TurnoverRptData data = new TurnoverRptData("2024-01", 4, 20, 400.0);
        assertEquals(100.0, data.getAverageOrderValue(), 0.001);
    }

    @Test
    void constructor_zeroOrders_averageOrderValueIsZero() {
        TurnoverRptData data = new TurnoverRptData("2024-01", 0, 0, 0.0);
        assertEquals(0.0, data.getAverageOrderValue(), 0.001);
    }

    @Test
    void constructor_singleOrder_averageEqualsRevenue() {
        TurnoverRptData data = new TurnoverRptData("2024-02", 1, 5, 250.0);
        assertEquals(250.0, data.getAverageOrderValue(), 0.001);
    }

    @Test
    void constructor_zeroRevenueWithOrders_averageIsZero() {
        TurnoverRptData data = new TurnoverRptData("2024-03", 3, 10, 0.0);
        assertEquals(0.0, data.getAverageOrderValue(), 0.001);
    }

    // --- getters ---

    @Test
    void constructor_storesAllFields() {
        TurnoverRptData data = new TurnoverRptData("2024-04", 7, 35, 700.0);
        assertEquals("2024-04", data.getPeriod());
        assertEquals(7, data.getTotalOrders());
        assertEquals(35, data.getTotalItemsSold());
        assertEquals(700.0, data.getTotalRevenue(), 0.001);
    }
}
