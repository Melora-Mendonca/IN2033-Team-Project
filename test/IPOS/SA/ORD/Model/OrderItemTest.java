package IPOS.SA.ORD.Model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class OrderItemTest {

    // --- getLineTotal() ---

    @Test
    void getLineTotal_multipleQuantity_returnsCorrectTotal() {
        OrderItem item = new OrderItem("ITEM001", 3, 25.0);
        assertEquals(75.0, item.getLineTotal(), 0.001);
    }

    @Test
    void getLineTotal_singleQuantity_returnsSameAsUnitPrice() {
        OrderItem item = new OrderItem("ITEM002", 1, 50.0);
        assertEquals(50.0, item.getLineTotal(), 0.001);
    }

    @Test
    void getLineTotal_zeroQuantity_returnsZero() {
        OrderItem item = new OrderItem("ITEM003", 0, 20.0);
        assertEquals(0.0, item.getLineTotal(), 0.001);
    }

    @Test
    void getLineTotal_fractionalPrice_returnsCorrectTotal() {
        OrderItem item = new OrderItem("ITEM004", 4, 9.99);
        assertEquals(39.96, item.getLineTotal(), 0.001);
    }

    // --- Getters ---

    @Test
    void getItemId_returnsCorrectId() {
        OrderItem item = new OrderItem("ITEM005", 2, 10.0);
        assertEquals("ITEM005", item.getItemId());
    }

    @Test
    void getQuantity_returnsCorrectQuantity() {
        OrderItem item = new OrderItem("ITEM006", 7, 10.0);
        assertEquals(7, item.getQuantity());
    }

    @Test
    void getUnitPrice_returnsCorrectPrice() {
        OrderItem item = new OrderItem("ITEM007", 2, 14.50);
        assertEquals(14.50, item.getUnitPrice(), 0.001);
    }
}
