package IPOS.SA.ORD;

import java.time.LocalDate;
import java.util.List;

/**
 * Represents a merchant order.
 */
public class Order {

    private final String orderId;
    private final String merchantId;
    private final LocalDate orderDate;
    private OrderStatus status;
    private final List<OrderItem> items;

    public Order(String orderId, String merchantId, LocalDate orderDate, List<OrderItem> items) {
        this.orderId = orderId;
        this.merchantId = merchantId;
        this.orderDate = orderDate;
        this.items = items;
        this.status = OrderStatus.ACCEPTED;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public double calculateOrderTotal() {
        double total = 0;
        for (OrderItem item : items) {
            total += item.getLineTotal();
        }
        return total;
    }
}