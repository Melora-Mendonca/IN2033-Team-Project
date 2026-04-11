package IPOS.SA.ORD;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class OrderStatusTest {

    @Test
    void accepted_displayName_isCorrect() {
        assertEquals("accepted", OrderStatus.ACCEPTED.getDisplayName());
    }

    @Test
    void pending_displayName_isCorrect() {
        assertEquals("pending", OrderStatus.PENDING.getDisplayName());
    }

    @Test
    void processing_displayName_isCorrect() {
        assertEquals("processing", OrderStatus.PROCESSING.getDisplayName());
    }

    @Test
    void dispatched_displayName_isCorrect() {
        assertEquals("dispatched", OrderStatus.DISPATCHED.getDisplayName());
    }

    @Test
    void delivered_displayName_isCorrect() {
        assertEquals("delivered", OrderStatus.DELIVERED.getDisplayName());
    }

    @Test
    void toString_returnsDisplayName() {
        assertEquals("dispatched", OrderStatus.DISPATCHED.toString());
    }

    @Test
    void allStatuses_arePresent() {
        assertEquals(5, OrderStatus.values().length);
    }
}
