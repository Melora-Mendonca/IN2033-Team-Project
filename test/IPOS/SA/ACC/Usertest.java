package IPOS.SA.ACC.Model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    // Helper — creates a basic user
    private User buildUser() {
        return new User("admin", "John Smith", "Administrator", "admin@ipos.com");
    }

    // --- Getters ---

    @Test
    void getUsername_returnsCorrectUsername() {
        User user = buildUser();
        assertEquals("admin", user.getUsername());
    }

    @Test
    void getFullName_returnsCorrectName() {
        User user = buildUser();
        assertEquals("John Smith", user.getFullName());
    }

    @Test
    void getRole_returnsCorrectRole() {
        User user = buildUser();
        assertEquals("Administrator", user.getRole());
    }

    @Test
    void getEmail_returnsCorrectEmail() {
        User user = buildUser();
        assertEquals("admin@ipos.com", user.getEmail());
    }

    // --- Setters ---

    @Test
    void setUsername_updatesCorrectly() {
        User user = buildUser();
        user.setUsername("director");
        assertEquals("director", user.getUsername());
    }

    @Test
    void setFullName_updatesCorrectly() {
        User user = buildUser();
        user.setFullName("Sarah Johnson");
        assertEquals("Sarah Johnson", user.getFullName());
    }

    @Test
    void setRole_updatesCorrectly() {
        User user = buildUser();
        user.setRole("Accountant");
        assertEquals("Accountant", user.getRole());
    }

    @Test
    void setEmail_updatesCorrectly() {
        User user = buildUser();
        user.setEmail("new@ipos.com");
        assertEquals("new@ipos.com", user.getEmail());
    }

    // --- toString ---

    @Test
    void toString_returnsNonNullString() {
        User user = buildUser();
        assertNotNull(user.toString());
    }

    @Test
    void toString_containsUsername() {
        User user = buildUser();
        assertTrue(user.toString().contains("admin"));
    }
}