package IPOS.SA.RPT.Model;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class CommercialApplicationTest {

    // --- full constructor ---

    @Test
    void constructor_fullParams_defaultStatusIsPending() {
        CommercialApplication app = new CommercialApplication(
                "Acme Ltd", "REG-001", "Retail", "John Doe",
                "john@acme.com", "07700900000", "01234567890", "1 Main St");
        assertEquals("pending", app.getStatus());
    }

    @Test
    void constructor_fullParams_applicationDateIsToday() {
        CommercialApplication app = new CommercialApplication(
                "Beta Co", "REG-002", "Wholesale", "Jane Smith",
                "jane@beta.com", "07700900001", "09876543210", "2 Side St");
        assertEquals(LocalDate.now(), app.getApplicationDate());
    }

    @Test
    void constructor_fullParams_storesAllFields() {
        CommercialApplication app = new CommercialApplication(
                "Gamma Inc", "REG-003", "Distribution", "Bob Jones",
                "bob@gamma.com", "07700900002", "01111222233", "3 Park Ave");
        assertEquals("Gamma Inc", app.getCompanyName());
        assertEquals("REG-003", app.getRegistrationNo());
        assertEquals("Distribution", app.getBusinessType());
        assertEquals("Bob Jones", app.getDirectorName());
        assertEquals("bob@gamma.com", app.getEmail());
        assertEquals("07700900002", app.getPhone());
        assertEquals("01111222233", app.getFax());
        assertEquals("3 Park Ave", app.getAddress());
    }

    @Test
    void constructor_fullParams_reviewFieldsAreNull() {
        CommercialApplication app = new CommercialApplication(
                "Delta Ltd", "REG-004", "Retail", "Alice Brown",
                "alice@delta.com", "07700900003", "02222333344", "4 Oak Rd");
        assertNull(app.getReviewedBy());
        assertNull(app.getReviewDate());
        assertNull(app.getReviewNotes());
    }

    // --- default constructor + setters ---

    @Test
    void defaultConstructor_settersAndGetters_storedCorrectly() {
        CommercialApplication app = new CommercialApplication();
        app.setApplicationId(42);
        app.setCompanyName("Epsilon Co");
        app.setStatus("approved");
        app.setReviewedBy(1);
        app.setReviewDate(LocalDate.of(2024, 6, 15));
        app.setReviewNotes("All checks passed");

        assertEquals(42, app.getApplicationId());
        assertEquals("Epsilon Co", app.getCompanyName());
        assertEquals("approved", app.getStatus());
        assertEquals(1, app.getReviewedBy());
        assertEquals(LocalDate.of(2024, 6, 15), app.getReviewDate());
        assertEquals("All checks passed", app.getReviewNotes());
    }

    @Test
    void setStatus_changesStatusFromPending() {
        CommercialApplication app = new CommercialApplication(
                "Zeta Ltd", "REG-005", "Retail", "Eve White",
                "eve@zeta.com", "07700900004", "03333444455", "5 Elm St");
        app.setStatus("rejected");
        assertEquals("rejected", app.getStatus());
    }
}
