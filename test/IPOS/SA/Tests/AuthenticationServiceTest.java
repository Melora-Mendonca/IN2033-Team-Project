package IPOS.SA.Tests;

import IPOS.SA.ACC.Model.User;
import IPOS.SA.ACC.Service.AuthenticationService;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for AuthenticationService — login, role validation, empty inputs.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthenticationServiceTest {

    private static AuthenticationService authService;

    @BeforeAll
    static void setUp() {
        authService = new AuthenticationService();
    }

    // ── Successful logins ────────────────────────────────────────────────────

    @Test @Order(1)
    @DisplayName("Admin login with correct credentials succeeds")
    void testAdminLoginSuccess() {
        User user = authService.authenticate("admin", "admin123", "Administrator");
        assertNotNull(user, "User should not be null on valid login");
        assertEquals("Administrator", user.getRole());
    }

    @Test @Order(2)
    @DisplayName("Manager login with correct credentials succeeds")
    void testManagerLoginSuccess() {
        User user = authService.authenticate("manager", "manager123", "Director of Operations");
        assertNotNull(user);
        assertEquals("Director of Operations", user.getRole());
    }

    @Test @Order(3)
    @DisplayName("Warehouse staff login with correct credentials succeeds")
    void testWarehouseLoginSuccess() {
        User user = authService.authenticate("warehouse1", "staff123", "Warehouse Employee");
        assertNotNull(user);
        assertEquals("Warehouse Employee", user.getRole());
    }

    @Test @Order(4)
    @DisplayName("Senior accountant login with correct credentials succeeds")
    void testAccountantLoginSuccess() {
        User user = authService.authenticate("accountant1", "staff123", "Senior Accountant");
        assertNotNull(user);
        assertEquals("Senior Accountant", user.getRole());
    }

    // ── Failed logins ────────────────────────────────────────────────────────

    @Test @Order(5)
    @DisplayName("Wrong password returns null")
    void testWrongPasswordFails() {
        User user = authService.authenticate("admin", "wrongpassword", "Administrator");
        assertNull(user, "Should return null for wrong password");
    }

    @Test @Order(6)
    @DisplayName("Wrong username returns null")
    void testWrongUsernameFails() {
        User user = authService.authenticate("nobody", "admin123", "Administrator");
        assertNull(user);
    }

    @Test @Order(7)
    @DisplayName("Correct credentials but wrong role returns null")
    void testWrongRoleFails() {
        User user = authService.authenticate("admin", "admin123", "Warehouse Employee");
        assertNull(user, "Should fail when role does not match");
    }

    @Test @Order(8)
    @DisplayName("Empty username returns null")
    void testEmptyUsernameFails() {
        User user = authService.authenticate("", "admin123", "Administrator");
        assertNull(user);
    }

    @Test @Order(9)
    @DisplayName("Empty password returns null")
    void testEmptyPasswordFails() {
        User user = authService.authenticate("admin", "", "Administrator");
        assertNull(user);
    }

    @Test @Order(10)
    @DisplayName("Null inputs return null")
    void testNullInputsFail() {
        User user = authService.authenticate(null, null, null);
        assertNull(user);
    }

    // ── Stock warnings ───────────────────────────────────────────────────────

    @Test @Order(11)
    @DisplayName("Stock warnings returns a list (not null)")
    void testStockWarningsNotNull() {
        var warnings = authService.getStockWarnings();
        assertNotNull(warnings, "Stock warnings list should not be null");
    }

    @Test @Order(12)
    @DisplayName("Low stock items are detected (CAT003, CAT004, CAT006, CAT007 are below min)")
    void testLowStockItemsDetected() {
        var warnings = authService.getStockWarnings();
        assertFalse(warnings.isEmpty(), "Should detect low stock items in sample data");
    }
}
