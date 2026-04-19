package IPOS.SA.ACC.Model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class PaymentTest {

    // Helper — creates a basic payment
    private Payment buildPayment() {
        return new Payment(
                "PAY-001",
                "M001",
                "ORD-001",
                427.50,
                LocalDate.of(2025, 3, 15),
                "BT-2025-001"
        );
    }

    // --- Getters ---

    @Test
    void getPaymentId_returnsCorrectId() {
        Payment payment = buildPayment();
        assertEquals("PAY-001", payment.getPaymentId());
    }

    @Test
    void getMerchantId_returnsCorrectId() {
        Payment payment = buildPayment();
        assertEquals("M001", payment.getMerchantId());
    }

    @Test
    void getOrderId_returnsCorrectId() {
        Payment payment = buildPayment();
        assertEquals("ORD-001", payment.getOrderId());
    }

    @Test
    void getAmount_returnsCorrectAmount() {
        Payment payment = buildPayment();
        assertEquals(427.50, payment.getAmount(), 0.001);
    }

    @Test
    void getPaymentDate_returnsCorrectDate() {
        Payment payment = buildPayment();
        assertEquals(LocalDate.of(2025, 3, 15), payment.getPaymentDate());
    }

    @Test
    void getPaymentReference_returnsCorrectReference() {
        Payment payment = buildPayment();
        assertEquals("BT-2025-001", payment.getPaymentReference());
    }

    // --- Edge cases ---

    @Test
    void getAmount_zeroAmount_returnsZero() {
        Payment payment = new Payment("PAY-002", "M002", "ORD-002", 0.0, LocalDate.now(), "REF-ZERO");
        assertEquals(0.0, payment.getAmount(), 0.001);
    }

    @Test
    void getAmount_largeAmount_returnsCorrectValue() {
        Payment payment = new Payment("PAY-003", "M003", "ORD-003", 99999.99, LocalDate.now(), "REF-LARGE");
        assertEquals(99999.99, payment.getAmount(), 0.001);
    }
}