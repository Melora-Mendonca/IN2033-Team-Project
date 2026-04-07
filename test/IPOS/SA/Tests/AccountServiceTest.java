package IPOS.SA.Tests;

import IPOS.SA.ACC.Model.FixedDiscountPlan;
import IPOS.SA.ACC.Model.MerchantAccount;
import IPOS.SA.ACC.Service.AccountService;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for AccountService — CRUD, status updates, balance retrieval.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AccountServiceTest {

    private static AccountService service;
    private static final String TEST_MERCHANT_ID = "TST-001";

    @BeforeAll
    static void setUp() {
        service = new AccountService();
    }

    @AfterAll
    static void tearDown() throws Exception {
        // Clean up test merchant after all tests
        try { service.deleteAccount(TEST_MERCHANT_ID); } catch (Exception ignored) {}
    }

    // ── Retrieve existing merchants ──────────────────────────────────────────

    @Test @Order(1)
    @DisplayName("Get all accounts returns 3 sample merchants")
    void testGetAllAccounts() throws Exception {
        var accounts = service.getAllAccounts();
        assertNotNull(accounts);
        assertTrue(accounts.size() >= 3, "Should have at least 3 merchants");
    }

    @Test @Order(2)
    @DisplayName("Get account M001 returns Tech Supplies Ltd")
    void testGetAccountById() throws Exception {
        MerchantAccount acc = service.getAccount("M001");
        assertNotNull(acc, "M001 should exist");
        assertEquals("Tech Supplies Ltd", acc.getBusinessName());
    }

    @Test @Order(3)
    @DisplayName("Get account M003 is suspended")
    void testGetSuspendedAccount() throws Exception {
        MerchantAccount acc = service.getAccount("M003");
        assertNotNull(acc);
        assertEquals("suspended", acc.getAccountStatus().toLowerCase());
    }

    @Test @Order(4)
    @DisplayName("Get non-existent account returns null")
    void testGetNonExistentAccount() throws Exception {
        MerchantAccount acc = service.getAccount("DOESNOTEXIST");
        assertNull(acc);
    }

    // ── Balance retrieval ────────────────────────────────────────────────────

    @Test @Order(5)
    @DisplayName("M001 outstanding balance is 0")
    void testGetBalance_M001() throws Exception {
        double balance = service.getAccountBalance("M001");
        assertEquals(0.0, balance, 0.01);
    }

    @Test @Order(6)
    @DisplayName("M002 outstanding balance is 1200")
    void testGetBalance_M002() throws Exception {
        double balance = service.getAccountBalance("M002");
        assertEquals(1200.0, balance, 0.01);
    }

    @Test @Order(7)
    @DisplayName("Non-existent merchant balance returns 0")
    void testGetBalance_nonExistent() throws Exception {
        double balance = service.getAccountBalance("MISSING");
        assertEquals(0.0, balance, 0.01);
    }

    // ── Create account ───────────────────────────────────────────────────────

    @Test @Order(8)
    @DisplayName("Create new merchant account succeeds")
    void testAddAccount() throws Exception {
        MerchantAccount acc = new MerchantAccount(
                TEST_MERCHANT_ID, "Test Company", "Retailer", "REG-TEST",
                "test@test.com", "07700000000", "N/A", "1 Test St",
                5000.0, new FixedDiscountPlan("Fixed", 5.0)
        );
        boolean result = service.addAccount(acc);
        assertTrue(result, "New account should be created successfully");
    }

    @Test @Order(9)
    @DisplayName("Creating duplicate merchant account returns false")
    void testAddDuplicateAccount() throws Exception {
        MerchantAccount acc = new MerchantAccount(
                TEST_MERCHANT_ID, "Duplicate Co", "Retailer", "REG-DUP",
                "dup@test.com", "07700000001", "N/A", "2 Test St",
                5000.0, new FixedDiscountPlan("Fixed", 5.0)
        );
        boolean result = service.addAccount(acc);
        assertFalse(result, "Duplicate account should not be created");
    }

    @Test @Order(10)
    @DisplayName("Created account is retrievable from DB")
    void testCreatedAccountRetrievable() throws Exception {
        MerchantAccount acc = service.getAccount(TEST_MERCHANT_ID);
        assertNotNull(acc);
        assertEquals("Test Company", acc.getBusinessName());
    }

    // ── Status updates ───────────────────────────────────────────────────────

    @Test @Order(11)
    @DisplayName("Suspend merchant account succeeds")
    void testSuspendAccount() throws Exception {
        boolean result = service.updateAccountStatus("M001", "suspended");
        assertTrue(result);
        MerchantAccount acc = service.getAccount("M001");
        assertEquals("suspended", acc.getAccountStatus().toLowerCase());
    }

    @Test @Order(12)
    @DisplayName("Restore merchant account to normal succeeds")
    void testRestoreAccount() throws Exception {
        boolean result = service.updateAccountStatus("M001", "normal");
        assertTrue(result);
        MerchantAccount acc = service.getAccount("M001");
        assertEquals("normal", acc.getAccountStatus().toLowerCase());
    }

    // ── Discount plan ────────────────────────────────────────────────────────

    @Test @Order(13)
    @DisplayName("Delete discount plan resets to 0%")
    void testDeleteDiscountPlan() throws Exception {
        boolean result = service.deleteDiscountPlan("M001");
        assertTrue(result);
        MerchantAccount acc = service.getAccount("M001");
        assertEquals(0.0, acc.getFixedDiscountRate(), 0.01);
    }

    // ── Delete ───────────────────────────────────────────────────────────────

    @Test @Order(14)
    @DisplayName("Delete test merchant succeeds")
    void testDeleteAccount() throws Exception {
        boolean result = service.deleteAccount(TEST_MERCHANT_ID);
        assertTrue(result);
        assertNull(service.getAccount(TEST_MERCHANT_ID));
    }
}
