package IPOS.SA.ACC;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AccountStatusTest {

    @Test
    void normal_valueOf_isCorrect() {
        assertEquals(AccountStatus.NORMAL, AccountStatus.valueOf("NORMAL"));
    }

    @Test
    void suspended_valueOf_isCorrect() {
        assertEquals(AccountStatus.SUSPENDED, AccountStatus.valueOf("SUSPENDED"));
    }

    @Test
    void inDefault_valueOf_isCorrect() {
        assertEquals(AccountStatus.IN_DEFAULT, AccountStatus.valueOf("IN_DEFAULT"));
    }

    @Test
    void allStatuses_arePresent() {
        assertEquals(3, AccountStatus.values().length);
    }

    @Test
    void valueOf_invalidName_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> AccountStatus.valueOf("INVALID"));
    }

    @Test
    void values_containsNormal() {
        boolean found = false;
        for (AccountStatus s : AccountStatus.values()) {
            if (s == AccountStatus.NORMAL) { found = true; break; }
        }
        assertTrue(found);
    }

    @Test
    void values_containsSuspended() {
        boolean found = false;
        for (AccountStatus s : AccountStatus.values()) {
            if (s == AccountStatus.SUSPENDED) { found = true; break; }
        }
        assertTrue(found);
    }

    @Test
    void values_containsInDefault() {
        boolean found = false;
        for (AccountStatus s : AccountStatus.values()) {
            if (s == AccountStatus.IN_DEFAULT) { found = true; break; }
        }
        assertTrue(found);
    }
}