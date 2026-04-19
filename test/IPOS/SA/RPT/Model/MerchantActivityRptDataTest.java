package IPOS.SA.RPT.Model;

import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class MerchantActivityRptDataTest {

    // --- MerchantActivityRptData setters/getters ---

    @Test
    void settersAndGetters_merchantFields_storedCorrectly() {
        MerchantActivityRptData data = new MerchantActivityRptData();
        data.setMerchantId("M001");
        data.setCompanyName("Acme Ltd");
        data.setEmail("acme@example.com");
        data.setPhone("07700900000");
        data.setAddress("123 High St");
        data.setCreditLimit(5000.0);
        data.setOutstandingBalance(1200.0);
        data.setTotalOrderValue(3500.0);

        assertEquals("M001", data.getMerchantId());
        assertEquals("Acme Ltd", data.getCompanyName());
        assertEquals("acme@example.com", data.getEmail());
        assertEquals("07700900000", data.getPhone());
        assertEquals("123 High St", data.getAddress());
        assertEquals(5000.0, data.getCreditLimit(), 0.001);
        assertEquals(1200.0, data.getOutstandingBalance(), 0.001);
        assertEquals(3500.0, data.getTotalOrderValue(), 0.001);
    }

    @Test
    void setOrders_listStoredAndRetrieved() {
        MerchantActivityRptData data = new MerchantActivityRptData();
        MerchantActivityRptData.OrderDetail od = new MerchantActivityRptData.OrderDetail();
        od.setOrderId("ORD-001");
        data.setOrders(Arrays.asList(od));

        assertEquals(1, data.getOrders().size());
        assertEquals("ORD-001", data.getOrders().get(0).getOrderId());
    }

    // --- OrderDetail setters/getters ---

    @Test
    void orderDetail_settersAndGetters_storedCorrectly() {
        MerchantActivityRptData.OrderDetail od = new MerchantActivityRptData.OrderDetail();
        Date d = new Date(1700000000000L);
        od.setOrderId("ORD-010");
        od.setOrderDate(d);
        od.setOrderTotal(450.0);
        od.setDiscountGiven(50.0);
        od.setPaymentStatus("paid");

        assertEquals("ORD-010", od.getOrderId());
        assertEquals(d, od.getOrderDate());
        assertEquals(450.0, od.getOrderTotal(), 0.001);
        assertEquals(50.0, od.getDiscountGiven(), 0.001);
        assertEquals("paid", od.getPaymentStatus());
    }

    @Test
    void orderDetail_setItems_listStoredAndRetrieved() {
        MerchantActivityRptData.OrderDetail od = new MerchantActivityRptData.OrderDetail();
        MerchantActivityRptData.ItemDetail item = new MerchantActivityRptData.ItemDetail();
        item.setItemId("ITEM-01");
        od.setItems(Arrays.asList(item));

        assertEquals(1, od.getItems().size());
        assertEquals("ITEM-01", od.getItems().get(0).getItemId());
    }

    // --- ItemDetail setters/getters ---

    @Test
    void itemDetail_settersAndGetters_storedCorrectly() {
        MerchantActivityRptData.ItemDetail item = new MerchantActivityRptData.ItemDetail();
        item.setItemId("ITEM-02");
        item.setDescription("Widget A");
        item.setQuantity(5);
        item.setUnitPrice(20.0);
        item.setTotalPrice(100.0);

        assertEquals("ITEM-02", item.getItemId());
        assertEquals("Widget A", item.getDescription());
        assertEquals(5, item.getQuantity());
        assertEquals(20.0, item.getUnitPrice(), 0.001);
        assertEquals(100.0, item.getTotalPrice(), 0.001);
    }
}
