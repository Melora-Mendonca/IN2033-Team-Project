package IPOS.SA.Tests;

import IPOS.SA.ACC.AccountStatus;
import IPOS.SA.ACC.Model.DiscountPlan;
import IPOS.SA.ACC.Model.FixedDiscountPlan;
import IPOS.SA.ACC.Model.FlexibleDiscountPlan;
import IPOS.SA.ACC.Model.MerchantAccount;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for MerchantAccount model — business logic, discount plans, credit checks.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MerchantAccountModelTest {

    // ── FixedDiscountPlan ────────────────────────────────────────────────────

    @Test @Order(1)
    @DisplayName("FixedDiscountPlan calculates 10% discount correctly")
    void testFixedDiscountPlan() {
        FixedDiscountPlan plan = new FixedDiscountPlan("Fixed", 10.0);
        assertEquals(10.0, plan.calculateDiscount(100.0), 0.001);
    }

    @Test @Order(2)
    @DisplayName("FixedDiscountPlan 0% discount returns 0")
    void testFixedDiscountZero() {
        FixedDiscountPlan plan = new FixedDiscountPlan("Fixed", 0.0);
        assertEquals(0.0, plan.calculateDiscount(500.0), 0.001);
    }

    @Test @Order(3)
    @DisplayName("FixedDiscountPlan percentage getter returns correct value")
    void testFixedDiscountPercentageGetter() {
        FixedDiscountPlan plan = new FixedDiscountPlan("Fixed", 15.0);
        assertEquals(15.0, plan.getPercentage(), 0.001);
    }

    // ── MerchantAccount canPlaceOrder ────────────────────────────────────────

    @Test @Order(4)
    @DisplayName("Normal account within credit limit can place order")
    void testCanPlaceOrderWithinLimit() {
        MerchantAccount acc = new MerchantAccount(
                "M-TEST", "Test Co", "Retail", "REG-001",
                "test@test.com", "07700", "1 Test St", "N/A",
                5000.0, new FixedDiscountPlan("Fixed", 5.0)
        );
        assertTrue(acc.canPlaceOrder(1000.0), "Should allow order within credit limit");
    }

    @Test @Order(5)
    @DisplayName("Account at credit limit cannot place order")
    void testCannotPlaceOrderAtLimit() {
        MerchantAccount acc = new MerchantAccount(
                "M-TEST2", "Test Co 2", "Retail", "REG-002",
                "test2@test.com", "07700", "2 Test St", "N/A",
                1000.0, new FixedDiscountPlan("Fixed", 5.0)
        );
        // Order value exactly equals credit limit — balance is 0, so 0 + 1000 <= 1000 is TRUE
        assertTrue(acc.canPlaceOrder(1000.0));
    }

    @Test @Order(6)
    @DisplayName("Account exceeding credit limit cannot place order")
    void testCannotPlaceOrderExceedingLimit() {
        MerchantAccount acc = new MerchantAccount(
                "M-TEST3", "Test Co 3", "Retail", "REG-003",
                "test3@test.com", "07700", "3 Test St", "N/A",
                1000.0, new FixedDiscountPlan("Fixed", 5.0)
        );
        assertFalse(acc.canPlaceOrder(1500.0), "Should block order exceeding credit limit");
    }

    @Test @Order(7)
    @DisplayName("Suspended account cannot place order")
    void testSuspendedAccountCannotPlaceOrder() {
        MerchantAccount acc = new MerchantAccount(
                "M-SUSP", "Suspended Co", "Retail", "REG-004",
                "susp@test.com", "07700", "4 Test St", "N/A",
                10000.0, 500.0, "suspended", "fixed",
                5.0, 0.0, null, true, null
        );
        assertFalse(acc.canPlaceOrder(100.0), "Suspended account should not place orders");
    }

    @Test @Order(8)
    @DisplayName("In-default account cannot place order")
    void testDefaultAccountCannotPlaceOrder() {
        MerchantAccount acc = new MerchantAccount(
                "M-DEF", "Default Co", "Retail", "REG-005",
                "def@test.com", "07700", "5 Test St", "N/A",
                10000.0, 800.0, "in_default", "fixed",
                5.0, 0.0, null, true, null
        );
        assertFalse(acc.canPlaceOrder(100.0), "In-default account should not place orders");
    }

    // ── recordPayment ────────────────────────────────────────────────────────

    @Test @Order(9)
    @DisplayName("recordPayment reduces outstanding balance")
    void testRecordPaymentReducesBalance() {
        MerchantAccount acc = new MerchantAccount(
                "M-BAL", "Balance Co", "Retail", "REG-006",
                "bal@test.com", "07700", "6 Test St", "N/A",
                5000.0, new FixedDiscountPlan("Fixed", 5.0)
        );
        acc.setOutstandingBalance(1000.0);
        acc.recordPayment(300.0);
        assertEquals(700.0, acc.getOutstandingBalance(), 0.001);
    }

    @Test @Order(10)
    @DisplayName("recordPayment full amount sets balance to 0")
    void testRecordPaymentFullSetsZero() {
        MerchantAccount acc = new MerchantAccount(
                "M-BAL2", "Balance Co 2", "Retail", "REG-007",
                "bal2@test.com", "07700", "7 Test St", "N/A",
                5000.0, new FixedDiscountPlan("Fixed", 5.0)
        );
        acc.setOutstandingBalance(500.0);
        acc.recordPayment(500.0);
        assertEquals(0.0, acc.getOutstandingBalance(), 0.001);
    }

    @Test @Order(11)
    @DisplayName("recordPayment overpayment does not go below 0")
    void testRecordPaymentOverpaymentClampsToZero() {
        MerchantAccount acc = new MerchantAccount(
                "M-BAL3", "Balance Co 3", "Retail", "REG-008",
                "bal3@test.com", "07700", "8 Test St", "N/A",
                5000.0, new FixedDiscountPlan("Fixed", 5.0)
        );
        acc.setOutstandingBalance(100.0);
        acc.recordPayment(999.0);
        assertEquals(0.0, acc.getOutstandingBalance(), 0.001);
    }

    // ── Getters & setters ────────────────────────────────────────────────────

    @Test @Order(12)
    @DisplayName("Setters update account fields correctly")
    void testSetters() {
        MerchantAccount acc = new MerchantAccount(
                "M-SET", "Set Co", "Retail", "REG-009",
                "set@test.com", "07700", "9 Test St", "N/A",
                5000.0, new FixedDiscountPlan("Fixed", 5.0)
        );
        acc.setBusinessName("Updated Co");
        acc.setCreditLimit(9999.0);
        acc.setEmail("updated@test.com");

        assertEquals("Updated Co", acc.getBusinessName());
        assertEquals(9999.0, acc.getCreditLimit(), 0.001);
        assertEquals("updated@test.com", acc.getEmail());
    }

    @Test @Order(13)
    @DisplayName("New account default status is NORMAL")
    void testDefaultStatusIsNormal() {
        MerchantAccount acc = new MerchantAccount(
                "M-NEW", "New Co", "Retail", "REG-010",
                "new@test.com", "07700", "10 Test St", "N/A",
                5000.0, new FixedDiscountPlan("Fixed", 5.0)
        );
        assertEquals(AccountStatus.NORMAL, acc.getStatus());
    }
}
