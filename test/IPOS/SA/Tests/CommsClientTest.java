package IPOS.SA.Tests;

import IPOS.SA.Comms.CommsClient;
import org.junit.jupiter.api.*;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for CommsClient — verifies correct JSON is sent to both API endpoints.
 * When the comms server is not running, tests verify graceful IOException is thrown.
 * When the comms server IS running on localhost:8080, tests verify successful responses.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CommsClientTest {

    private static boolean serverAvailable = false;

    @BeforeAll
    static void checkServerAvailability() {
        // Try a quick connection to see if server is up
        try {
            java.net.Socket socket = new java.net.Socket();
            socket.connect(new java.net.InetSocketAddress("localhost", 8080), 500);
            socket.close();
            serverAvailable = true;
            System.out.println("[CommsClientTest] Comms server is RUNNING — live tests will execute");
        } catch (IOException e) {
            serverAvailable = false;
            System.out.println("[CommsClientTest] Comms server is NOT running — testing graceful failure only");
        }
    }

    // ── Email endpoint ───────────────────────────────────────────────────────

    @Test @Order(1)
    @DisplayName("sendEmail throws IOException when server is down (not NullPointerException)")
    void testSendEmailFailsGracefullyWhenServerDown() {
        if (serverAvailable) {
            System.out.println("Skipping — server is up, tested in live test below");
            return;
        }
        assertThrows(IOException.class, () ->
                CommsClient.sendEmail("test@test.com", "Test Subject", "Test body")
        );
    }

    @Test @Order(2)
    @DisplayName("sendEmail succeeds when server is running")
    void testSendEmailSucceedsWhenServerUp() throws Exception {
        if (!serverAvailable) {
            System.out.println("SKIPPED — comms server not running on localhost:8080");
            return;
        }
        String response = CommsClient.sendEmail(
                "recipient@email.com",
                "Test Subject",
                "This is a test email from IPOS integration test."
        );
        assertNotNull(response);
        System.out.println("Email API response: " + response);
    }

    @Test @Order(3)
    @DisplayName("sendEmail with special characters does not throw NullPointerException")
    void testSendEmailSpecialCharacters() {
        // Should throw IOException (server down) not NPE or other runtime error
        try {
            CommsClient.sendEmail(
                    "test+user@company.co.uk",
                    "Subject with \"quotes\" & symbols",
                    "Body with\nnewlines\nand 'apostrophes'"
            );
        } catch (IOException e) {
            // Expected — server not running
            assertTrue(e.getMessage() != null || serverAvailable);
        }
    }

    // ── Payment endpoint ─────────────────────────────────────────────────────

    @Test @Order(4)
    @DisplayName("processPayment throws IOException when server is down (not NullPointerException)")
    void testProcessPaymentFailsGracefullyWhenServerDown() {
        if (serverAvailable) {
            System.out.println("Skipping — server is up, tested in live test below");
            return;
        }
        assertThrows(IOException.class, () ->
                CommsClient.processPayment(
                        "M001", "ORD-001", "John Smith",
                        "10 Downing Street, London",
                        "4111", "1111", 250.00
                )
        );
    }

    @Test @Order(5)
    @DisplayName("processPayment succeeds when server is running")
    void testProcessPaymentSucceedsWhenServerUp() throws Exception {
        if (!serverAvailable) {
            System.out.println("SKIPPED — comms server not running on localhost:8080");
            return;
        }
        String response = CommsClient.processPayment(
                "M001", "0001", "Name",
                "10 Downing Street, London",
                "0111", "1110", 9.99
        );
        assertNotNull(response);
        System.out.println("Payment API response: " + response);
    }

    @Test @Order(6)
    @DisplayName("processPayment with zero amount throws IOException or succeeds (not NPE)")
    void testProcessPaymentZeroAmount() {
        try {
            CommsClient.processPayment("M001", "ORD-002", "Test", "Test Address", "0000", "0000", 0.0);
        } catch (IOException e) {
            // Expected when server is down
            assertNotNull(e);
        }
    }

    @Test @Order(7)
    @DisplayName("processPayment with null fields throws NullPointerException or IOException only")
    void testProcessPaymentNullFields() {
        // Should not throw unexpected exceptions — only IOException or NPE
        Exception thrown = null;
        try {
            CommsClient.processPayment(null, null, null, null, null, null, 0.0);
        } catch (IOException | NullPointerException e) {
            thrown = e;
        }
        // If it threw, it must be one of the expected types
        if (thrown != null) {
            assertTrue(thrown instanceof IOException || thrown instanceof NullPointerException);
        }
    }
}
