package IPOS.SA.ACC.Model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class OrderSummaryTest {

    // Helper — creates a basic order summary
    private OrderSummary buildOrderSummary() {
        return new OrderSummary("ORD-001", "Cosymed Ltd", "2025-03-01", "delivered", 427.50);
    }

    // --- Getters ---

    @Test
    void getOrderId_returnsCorrectId() {
        OrderSummary summary = buildOrderSummary();
        assertEquals("ORD-001", summary.getOrderId());
    }

    @Test
    void getMerchantName_returnsCorrectName() {
        OrderSummary summary = buildOrderSummary();
        assertEquals("Cosymed Ltd", summary.getMerchantName());
    }

    @Test
    void getOrderDate_returnsCorrectDate() {
        OrderSummary summary = buildOrderSummary();
        assertEquals("2025-03-01", summary.getOrderDate());
    }

    @Test
    void getStatus_returnsCorrectStatus() {
        OrderSummary summary = buildOrderSummary();
        assertEquals("delivered", summary.getStatus());
    }

    @Test
    void getTotalAmount_returnsCorrectAmount() {
        OrderSummary summary = buildOrderSummary();
        assertEquals(427.50, summary.getTotalAmount(), 0.001);
    }

    // --- Setters ---

    @Test
    void setOrderId_updatesCorrectly() {
        OrderSummary summary = buildOrderSummary();
        summary.setOrderId("ORD-002");
        assertEquals("ORD-002", summary.getOrderId());
    }

    @Test
    void setMerchantName_updatesCorrectly() {
        OrderSummary summary = buildOrderSummary();
        summary.setMerchantName("HealthPlus Pharmacy");
        assertEquals("HealthPlus Pharmacy", summary.getMerchantName());
    }

    @Test
    void setOrderDate_updatesCorrectly() {
        OrderSummary summary = buildOrderSummary();
        summary.setOrderDate("2025-04-01");
        assertEquals("2025-04-01", summary.getOrderDate());
    }

    @Test
    void setStatus_updatesCorrectly() {
        OrderSummary summary = buildOrderSummary();
        summary.setStatus("pending");
        assertEquals("pending", summary.getStatus());
    }

    @Test
    void setTotalAmount_updatesCorrectly() {
        OrderSummary summary = buildOrderSummary();
        summary.setTotalAmount(999.99);
        assertEquals(999.99, summary.getTotalAmount(), 0.001);
    }

    @Test
    void setTotalAmount_toZero_updatesCorrectly() {
        OrderSummary summary = buildOrderSummary();
        summary.setTotalAmount(0.0);
        assertEquals(0.0, summary.getTotalAmount(), 0.001);
    }
}