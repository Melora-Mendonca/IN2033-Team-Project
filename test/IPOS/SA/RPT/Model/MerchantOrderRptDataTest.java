package IPOS.SA.RPT.Model;

import org.junit.jupiter.api.Test;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.*;

class MerchantOrderRptDataTest {

    private static final Date ORDER_DATE      = new Date(1700000000000L);
    private static final Date DISPATCHED_DATE = new Date(1700500000000L);

    // --- getters ---

    @Test
    void constructor_storesAllFields() {
        MerchantOrderRptData order = new MerchantOrderRptData(
                "ORD-001", ORDER_DATE, 350.0, DISPATCHED_DATE, "paid");
        assertEquals("ORD-001", order.getOrderId());
        assertEquals(ORDER_DATE, order.getOrderDate());
        assertEquals(350.0, order.getOrderValue(), 0.001);
        assertEquals(DISPATCHED_DATE, order.getDispatchedDate());
        assertEquals("paid", order.getPaymentStatus());
    }

    @Test
    void constructor_nullDispatchedDate_storedAsNull() {
        MerchantOrderRptData order = new MerchantOrderRptData(
                "ORD-002", ORDER_DATE, 100.0, null, "unpaid");
        assertNull(order.getDispatchedDate());
    }

    @Test
    void constructor_zeroOrderValue_storedCorrectly() {
        MerchantOrderRptData order = new MerchantOrderRptData(
                "ORD-003", ORDER_DATE, 0.0, null, "pending");
        assertEquals(0.0, order.getOrderValue(), 0.001);
    }
}
