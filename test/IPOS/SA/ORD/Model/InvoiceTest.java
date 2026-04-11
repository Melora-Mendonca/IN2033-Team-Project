package IPOS.SA.ORD.Model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class InvoiceTest {

    // Helper — creates a basic unpaid invoice with a future due date
    private Invoice buildInvoice(double totalAmount, double amountPaid, String status, LocalDate dueDate) {
        return new Invoice(
                "INV-0001",
                "ORD-001",
                "MERCH-01",
                LocalDate.now(),
                dueDate,
                totalAmount,
                amountPaid,
                status
        );
    }

    // --- getOutstandingBalance() ---

    @Test
    void getOutstandingBalance_nothingPaid_returnsFullAmount() {
        Invoice invoice = buildInvoice(200.0, 0.0, "unpaid", LocalDate.now().plusDays(30));
        assertEquals(200.0, invoice.getOutstandingBalance(), 0.001);
    }

    @Test
    void getOutstandingBalance_partialPayment_returnsRemainder() {
        Invoice invoice = buildInvoice(200.0, 75.0, "partial", LocalDate.now().plusDays(30));
        assertEquals(125.0, invoice.getOutstandingBalance(), 0.001);
    }

    @Test
    void getOutstandingBalance_fullPayment_returnsZero() {
        Invoice invoice = buildInvoice(200.0, 200.0, "paid", LocalDate.now().plusDays(30));
        assertEquals(0.0, invoice.getOutstandingBalance(), 0.001);
    }

    // --- isPaid() ---

    @Test
    void isPaid_amountPaidEqualsTotal_returnsTrue() {
        Invoice invoice = buildInvoice(150.0, 150.0, "paid", LocalDate.now().plusDays(30));
        assertTrue(invoice.isPaid());
    }

    @Test
    void isPaid_amountPaidExceedsTotal_returnsTrue() {
        Invoice invoice = buildInvoice(150.0, 200.0, "paid", LocalDate.now().plusDays(30));
        assertTrue(invoice.isPaid());
    }

    @Test
    void isPaid_partialPayment_returnsFalse() {
        Invoice invoice = buildInvoice(150.0, 50.0, "partial", LocalDate.now().plusDays(30));
        assertFalse(invoice.isPaid());
    }

    @Test
    void isPaid_nothingPaid_returnsFalse() {
        Invoice invoice = buildInvoice(150.0, 0.0, "unpaid", LocalDate.now().plusDays(30));
        assertFalse(invoice.isPaid());
    }

    // --- isOverdue() ---

    @Test
    void isOverdue_pastDueDateAndUnpaid_returnsTrue() {
        Invoice invoice = buildInvoice(100.0, 0.0, "unpaid", LocalDate.now().minusDays(5));
        assertTrue(invoice.isOverdue());
    }

    @Test
    void isOverdue_pastDueDateButPaid_returnsFalse() {
        Invoice invoice = buildInvoice(100.0, 100.0, "paid", LocalDate.now().minusDays(5));
        assertFalse(invoice.isOverdue());
    }

    @Test
    void isOverdue_futureDueDate_returnsFalse() {
        Invoice invoice = buildInvoice(100.0, 0.0, "unpaid", LocalDate.now().plusDays(10));
        assertFalse(invoice.isOverdue());
    }

    // --- recordPayment() ---

    @Test
    void recordPayment_partialAmount_updatesAmountPaidAndStatus() {
        Invoice invoice = buildInvoice(200.0, 0.0, "unpaid", LocalDate.now().plusDays(30));
        invoice.recordPayment(100.0);
        assertEquals(100.0, invoice.getAmountPaid(), 0.001);
        assertEquals("partial", invoice.getStatus());
    }

    @Test
    void recordPayment_fullAmount_marksAsPaid() {
        Invoice invoice = buildInvoice(200.0, 0.0, "unpaid", LocalDate.now().plusDays(30));
        invoice.recordPayment(200.0);
        assertEquals(200.0, invoice.getAmountPaid(), 0.001);
        assertEquals("paid", invoice.getStatus());
    }

    @Test
    void recordPayment_twoInstalmentsAddUp_marksAsPaid() {
        Invoice invoice = buildInvoice(200.0, 0.0, "unpaid", LocalDate.now().plusDays(30));
        invoice.recordPayment(100.0);
        invoice.recordPayment(100.0);
        assertEquals(200.0, invoice.getAmountPaid(), 0.001);
        assertEquals("paid", invoice.getStatus());
    }

    @Test
    void recordPayment_zeroAmount_doesNotChangeAmountPaid() {
        Invoice invoice = buildInvoice(200.0, 0.0, "unpaid", LocalDate.now().plusDays(30));
        invoice.recordPayment(0.0);
        assertEquals(0.0, invoice.getAmountPaid(), 0.001);
    }

    // --- setAmountPaid() status logic ---

    @Test
    void setAmountPaid_fullAmount_setsStatusToPaid() {
        Invoice invoice = buildInvoice(100.0, 0.0, "unpaid", LocalDate.now().plusDays(30));
        invoice.setAmountPaid(100.0);
        assertEquals("paid", invoice.getStatus());
    }

    @Test
    void setAmountPaid_partialAmount_setsStatusToPartial() {
        Invoice invoice = buildInvoice(100.0, 0.0, "unpaid", LocalDate.now().plusDays(30));
        invoice.setAmountPaid(40.0);
        assertEquals("partial", invoice.getStatus());
    }

    // --- Getters ---

    @Test
    void getInvoiceId_returnsCorrectId() {
        Invoice invoice = buildInvoice(100.0, 0.0, "unpaid", LocalDate.now().plusDays(30));
        assertEquals("INV-0001", invoice.getInvoiceId());
    }

    @Test
    void getOrderId_returnsCorrectId() {
        Invoice invoice = buildInvoice(100.0, 0.0, "unpaid", LocalDate.now().plusDays(30));
        assertEquals("ORD-001", invoice.getOrderId());
    }

    @Test
    void getMerchantId_returnsCorrectId() {
        Invoice invoice = buildInvoice(100.0, 0.0, "unpaid", LocalDate.now().plusDays(30));
        assertEquals("MERCH-01", invoice.getMerchantId());
    }

    @Test
    void getTotalAmount_returnsCorrectAmount() {
        Invoice invoice = buildInvoice(350.0, 0.0, "unpaid", LocalDate.now().plusDays(30));
        assertEquals(350.0, invoice.getTotalAmount(), 0.001);
    }
}
