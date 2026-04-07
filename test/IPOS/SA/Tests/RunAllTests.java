package IPOS.SA.Tests;

import org.junit.jupiter.api.Test;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Minimal test runner — finds all @Test methods and executes them,
 * printing PASS / FAIL with reasons.
 */
public class RunAllTests {

    private static int passed  = 0;
    private static int failed  = 0;
    private static int skipped = 0;
    private static final List<String> failures = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        Class<?>[] suites = {
                AuthenticationServiceTest.class,
                AccountServiceTest.class,
                MerchantAccountModelTest.class,
                OrderAndInvoiceServiceTest.class,
                PaymentServiceTest.class,
                ReportServiceTest.class,
                CommsClientTest.class
        };

        for (Class<?> suite : suites) {
            System.out.println("\n── " + suite.getSimpleName() + " ──────────────");
            runSuite(suite);
        }

        System.out.println("\n════════════════════════════════════════════════════════");
        System.out.println("  IPOS TEST RESULTS");
        System.out.println("════════════════════════════════════════════════════════");
        System.out.printf("  Tests run : %d%n", passed + failed);
        System.out.printf("  PASSED    : %d%n", passed);
        System.out.printf("  FAILED    : %d%n", failed);
        System.out.println("════════════════════════════════════════════════════════");

        if (!failures.isEmpty()) {
            System.out.println("\n  FAILURES:");
            failures.forEach(f -> System.out.println("  " + f));
        } else {
            System.out.println("\n  ALL TESTS PASSED ✓");
        }

        System.exit(failed > 0 ? 1 : 0);
    }

    private static void runSuite(Class<?> clazz) throws Exception {
        // Run @BeforeAll
        for (Method m : clazz.getDeclaredMethods()) {
            if (m.isAnnotationPresent(org.junit.jupiter.api.BeforeAll.class)) {
                m.setAccessible(true);
                m.invoke(null);
            }
        }

        Object instance = clazz.getDeclaredConstructor().newInstance();

        // Sort test methods by @Order if present
        List<Method> tests = new ArrayList<>();
        for (Method m : clazz.getDeclaredMethods()) {
            if (m.isAnnotationPresent(Test.class)) tests.add(m);
        }
        tests.sort((a, b) -> {
            org.junit.jupiter.api.Order oa = a.getAnnotation(org.junit.jupiter.api.Order.class);
            org.junit.jupiter.api.Order ob = b.getAnnotation(org.junit.jupiter.api.Order.class);
            int va = (oa != null) ? oa.value() : Integer.MAX_VALUE;
            int vb = (ob != null) ? ob.value() : Integer.MAX_VALUE;
            return Integer.compare(va, vb);
        });

        for (Method m : tests) {
            String name = m.isAnnotationPresent(org.junit.jupiter.api.DisplayName.class)
                    ? m.getAnnotation(org.junit.jupiter.api.DisplayName.class).value()
                    : m.getName();
            try {
                m.invoke(instance);
                System.out.printf("  PASS  %s%n", name);
                passed++;
            } catch (java.lang.reflect.InvocationTargetException e) {
                Throwable cause = e.getCause();
                String msg = cause != null ? cause.getMessage() : e.getMessage();
                System.out.printf("  FAIL  %s%n        → %s%n", name, msg);
                failed++;
                failures.add(clazz.getSimpleName() + "::" + m.getName() + " → " + msg);
            }
        }

        // Run @AfterAll
        for (Method m : clazz.getDeclaredMethods()) {
            if (m.isAnnotationPresent(org.junit.jupiter.api.AfterAll.class)) {
                m.setAccessible(true);
                try { m.invoke(null); } catch (Exception ignored) {}
            }
        }
    }
}
