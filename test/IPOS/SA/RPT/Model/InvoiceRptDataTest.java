package IPOS.SA.RPT.Model;

import org.junit.jupiter.api.Test;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.*;

class InvoiceRptDataTest {

    private static final Date DATE = new Date(1700000000000L);
    private static final Date DUE  = new Date(1702678400000L);

    // --- balanceDue calculation ---

    @Test
    void constructor_partialPayment_calculatesBalanceDue() {
        InvoiceRptData inv = new InvoiceRptData("INV-001", "M001", "Acme Ltd", "ORD-001",
                DATE, DUE, 500.0, 200.0, "partial");
        assertEquals(300.0, inv.getBalanceDue(), 0.001);
    }

    @Test
    void constructor_fullPayment_balanceDueIsZero() {
        InvoiceRptData inv = new InvoiceRptData("INV-002", "M002", "Beta Co", "ORD-002",
                DATE, DUE, 750.0, 750.0, "paid");
        assertEquals(0.0, inv.getBalanceDue(), 0.001);
    }

    @Test
    void constructor_noPayment_balanceDueEqualsTotalAmount() {
        InvoiceRptData inv = new InvoiceRptData("INV-003", "M003", "Gamma Inc", "ORD-003",
                DATE, DUE, 1000.0, 0.0, "unpaid");
        assertEquals(1000.0, inv.getBalanceDue(), 0.001);
    }

    // --- getters ---

    @Test
    void constructor_storesAllFields() {
        InvoiceRptData inv = new InvoiceRptData("INV-004", "M004", "Delta Ltd", "ORD-004",
                DATE, DUE, 250.0, 100.0, "partial");
        assertEquals("INV-004", inv.getInvoiceId());
        assertEquals("M004", inv.getMerchantId());
        assertEquals("Delta Ltd", inv.getMerchantName());
        assertEquals("ORD-004", inv.getOrderId());
        assertEquals(DATE, inv.getInvoiceDate());
        assertEquals(DUE, inv.getDueDate());
        assertEquals(250.0, inv.getTotalAmount(), 0.001);
        assertEquals(100.0, inv.getAmountPaid(), 0.001);
        assertEquals("partial", inv.getStatus());
    }
}
