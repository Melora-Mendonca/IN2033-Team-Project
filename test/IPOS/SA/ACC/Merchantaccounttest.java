package IPOS.SA.ACC.Model;

import IPOS.SA.ACC.AccountStatus;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class MerchantAccountTest {

    // Helper — creates a basic active merchant with normal status
    private MerchantAccount buildMerchant() {
        return new MerchantAccount(
                "M001",
                "Cosymed Ltd",
                "Pharmacy",
                "12345678",
                "info@cosymed.com",
                "0208 778 0124",
                5000.0,
                0.0,
                "normal",
                5.0,
                "M001",
                "password123"
        );
    }


    @Test
    void getMerchantId_returnsCorrectId() {
        MerchantAccount merchant = buildMerchant();
        assertEquals("M001", merchant.getMerchantId());
    }

    @Test
    void getBusinessName_returnsCorrectName() {
        MerchantAccount merchant = buildMerchant();
        assertEquals("Cosymed Ltd", merchant.getBusinessName());
    }

    @Test
    void getBusinessType_returnsCorrectType() {
        MerchantAccount merchant = buildMerchant();
        assertEquals("Pharmacy", merchant.getBusinessType());
    }

    @Test
    void getRegistrationNumber_returnsCorrectNumber() {
        MerchantAccount merchant = buildMerchant();
        assertEquals("12345678", merchant.getRegistrationNumber());
    }

    @Test
    void getEmail_returnsCorrectEmail() {
        MerchantAccount merchant = buildMerchant();
        assertEquals("info@cosymed.com", merchant.getEmail());
    }

    @Test
    void getPhone_returnsCorrectPhone() {
        MerchantAccount merchant = buildMerchant();
        assertEquals("0208 778 0124", merchant.getPhone());
    }

    @Test
    void getCreditLimit_returnsCorrectLimit() {
        MerchantAccount merchant = buildMerchant();
        assertEquals(5000.0, merchant.getCreditLimit(), 0.001);
    }

    @Test
    void getOutstandingBalance_initiallyZero() {
        MerchantAccount merchant = buildMerchant();
        assertEquals(0.0, merchant.getOutstandingBalance(), 0.001);
    }


    @Test
    void setBusinessName_updatesCorrectly() {
        MerchantAccount merchant = buildMerchant();
        merchant.setBusinessName("New Pharmacy Ltd");
        assertEquals("New Pharmacy Ltd", merchant.getBusinessName());
    }

    @Test
    void setEmail_updatesCorrectly() {
        MerchantAccount merchant = buildMerchant();
        merchant.setEmail("new@email.com");
        assertEquals("new@email.com", merchant.getEmail());
    }

    @Test
    void setPhone_updatesCorrectly() {
        MerchantAccount merchant = buildMerchant();
        merchant.setPhone("020 9999 0000");
        assertEquals("020 9999 0000", merchant.getPhone());
    }

    @Test
    void setAddress_updatesCorrectly() {
        MerchantAccount merchant = buildMerchant();
        merchant.setAddress("10 New Street, London");
        assertEquals("10 New Street, London", merchant.getAddress());
    }

    @Test
    void setCreditLimit_updatesCorrectly() {
        MerchantAccount merchant = buildMerchant();
        merchant.setCreditLimit(10000.0);
        assertEquals(10000.0, merchant.getCreditLimit(), 0.001);
    }

    @Test
    void setOutstandingBalance_updatesCorrectly() {
        MerchantAccount merchant = buildMerchant();
        merchant.setOutstandingBalance(500.0);
        assertEquals(500.0, merchant.getOutstandingBalance(), 0.001);
    }

    // --- Status ---

    @Test
    void getStatus_defaultIsNormal() {
        MerchantAccount merchant = buildMerchant();
        assertEquals(AccountStatus.NORMAL, merchant.getStatus());
    }

    @Test
    void setStatus_toSuspended_updatesCorrectly() {
        MerchantAccount merchant = buildMerchant();
        merchant.setStatus(AccountStatus.SUSPENDED);
        assertEquals(AccountStatus.SUSPENDED, merchant.getStatus());
    }

    @Test
    void setStatus_toInDefault_updatesCorrectly() {
        MerchantAccount merchant = buildMerchant();
        merchant.setStatus(AccountStatus.IN_DEFAULT);
        assertEquals(AccountStatus.IN_DEFAULT, merchant.getStatus());
    }

    // --- canPlaceOrder() ---

    @Test
    void canPlaceOrder_withinCreditLimit_returnsTrue() {
        MerchantAccount merchant = buildMerchant();
        assertTrue(merchant.canPlaceOrder(1000.0));
    }

    @Test
    void canPlaceOrder_exceedsCreditLimit_returnsFalse() {
        MerchantAccount merchant = buildMerchant();
        assertFalse(merchant.canPlaceOrder(6000.0));
    }

    @Test
    void canPlaceOrder_exactlyAtCreditLimit_returnsTrue() {
        MerchantAccount merchant = buildMerchant();
        assertTrue(merchant.canPlaceOrder(5000.0));
    }

    @Test
    void canPlaceOrder_withExistingBalance_accountsForBalance() {
        MerchantAccount merchant = buildMerchant();
        merchant.setOutstandingBalance(3000.0);
        assertFalse(merchant.canPlaceOrder(3000.0));
    }

    // --- recordPayment() ---

    @Test
    void recordPayment_reducesOutstandingBalance() {
        MerchantAccount merchant = buildMerchant();
        merchant.setOutstandingBalance(1000.0);
        merchant.recordPayment(400.0);
        assertEquals(600.0, merchant.getOutstandingBalance(), 0.001);
    }

    @Test
    void recordPayment_fullPayment_bringsBalanceToZero() {
        MerchantAccount merchant = buildMerchant();
        merchant.setOutstandingBalance(500.0);
        merchant.recordPayment(500.0);
        assertEquals(0.0, merchant.getOutstandingBalance(), 0.001);
    }

    // --- Discount plan ---

    @Test
    void getDiscountPlan_returnsNonNull() {
        MerchantAccount merchant = buildMerchant();
        assertNotNull(merchant.getDiscountPlan());
    }

    @Test
    void getDiscountPercentage_returnsCorrectRate() {
        MerchantAccount merchant = buildMerchant();
        assertEquals(5.0, merchant.getDiscountPercentage(), 0.001);
    }

    // --- Active flag ---

    @Test
    void setActive_toFalse_updatesCorrectly() {
        MerchantAccount merchant = buildMerchant();
        merchant.setActive(false);
        assertFalse(merchant.isActive());
    }

    @Test
    void setActive_toTrue_updatesCorrectly() {
        MerchantAccount merchant = buildMerchant();
        merchant.setActive(false);
        merchant.setActive(true);
        assertTrue(merchant.isActive());
    }

    // --- Username & Password ---

    @Test
    void setUsername_updatesCorrectly() {
        MerchantAccount merchant = buildMerchant();
        merchant.setUsername("newuser");
        assertEquals("newuser", merchant.getUsername());
    }

    @Test
    void setPassword_updatesCorrectly() {
        MerchantAccount merchant = buildMerchant();
        merchant.setPassword("newpass123");
        assertEquals("newpass123", merchant.getPassword());
    }

    // --- Last payment date ---

    @Test
    void setLastPaymentDate_updatesCorrectly() {
        MerchantAccount merchant = buildMerchant();
        LocalDate payDate = LocalDate.of(2025, 3, 15);
        merchant.setLastPaymentDate(payDate);
        assertNotNull(merchant.getLastPaymentDate());
    }
}