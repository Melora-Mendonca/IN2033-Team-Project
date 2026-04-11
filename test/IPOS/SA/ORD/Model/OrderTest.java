package IPOS.SA.ORD.Model;

import IPOS.SA.ORD.OrderStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    // Helper to build a basic order
    private Order buildOrder(List<OrderItem> items) {
        return new Order("ORD-001", "MERCH-01", LocalDate.of(2024, 1, 15), items);
    }

    // --- calculateOrderTotal() ---

    @Test
    void calculateOrderTotal_singleItem_returnsCorrectTotal() {
        OrderItem item = new OrderItem("ITEM001", 2, 50.0);
        Order order = buildOrder(Collections.singletonList(item));
        assertEquals(100.0, order.calculateOrderTotal(), 0.001);
    }

    @Test
    void calculateOrderTotal_multipleItems_returnsSumOfLineTotals() {
        List<OrderItem> items = Arrays.asList(
                new OrderItem("ITEM001", 2, 10.0),  // 20.00
                new OrderItem("ITEM002", 3, 5.0),   // 15.00
                new OrderItem("ITEM003", 1, 100.0)  // 100.00
        );
        Order order = buildOrder(items);
        assertEquals(135.0, order.calculateOrderTotal(), 0.001);
    }

    @Test
    void calculateOrderTotal_emptyItemList_returnsZero() {
        Order order = buildOrder(Collections.emptyList());
        assertEquals(0.0, order.calculateOrderTotal(), 0.001);
    }

    // --- Default status on creation ---

    @Test
    void newOrder_defaultStatus_isAccepted() {
        Order order = buildOrder(Collections.emptyList());
        assertEquals(OrderStatus.ACCEPTED, order.getStatus());
    }

    // --- setStatus() ---

    @Test
    void setStatus_toDispatched_updatesCorrectly() {
        Order order = buildOrder(Collections.emptyList());
        order.setStatus(OrderStatus.DISPATCHED);
        assertEquals(OrderStatus.DISPATCHED, order.getStatus());
    }

    @Test
    void setStatus_toDelivered_updatesCorrectly() {
        Order order = buildOrder(Collections.emptyList());
        order.setStatus(OrderStatus.DELIVERED);
        assertEquals(OrderStatus.DELIVERED, order.getStatus());
    }

    // --- Getters ---

    @Test
    void getOrderId_returnsCorrectId() {
        Order order = buildOrder(Collections.emptyList());
        assertEquals("ORD-001", order.getOrderId());
    }

    @Test
    void getMerchantId_returnsCorrectId() {
        Order order = buildOrder(Collections.emptyList());
        assertEquals("MERCH-01", order.getMerchantId());
    }

    @Test
    void getOrderDate_returnsCorrectDate() {
        Order order = buildOrder(Collections.emptyList());
        assertEquals(LocalDate.of(2024, 1, 15), order.getOrderDate());
    }

    @Test
    void getItems_returnsCorrectItemList() {
        List<OrderItem> items = Collections.singletonList(new OrderItem("ITEM001", 1, 10.0));
        Order order = buildOrder(items);
        assertEquals(1, order.getItems().size());
        assertEquals("ITEM001", order.getItems().get(0).getItemId());
    }

    // --- Discount and final amount setters ---

    @Test
    void setDiscountApplied_andGet_returnsCorrectValue() {
        Order order = buildOrder(Collections.emptyList());
        order.setDiscountApplied(15.0);
        assertEquals(15.0, order.getDiscountApplied(), 0.001);
    }

    @Test
    void setFinalAmount_andGet_returnsCorrectValue() {
        Order order = buildOrder(Collections.emptyList());
        order.setFinalAmount(85.0);
        assertEquals(85.0, order.getFinalAmount(), 0.001);
    }
}
