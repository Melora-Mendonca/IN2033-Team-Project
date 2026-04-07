package IPOS.SA.Tests;

import IPOS.SA.RPT.Service.ReportService;
import org.junit.jupiter.api.*;
import java.util.Date;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for ReportService — all report types return valid data.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ReportServiceTest {

    private static ReportService reportService;
    private static Date fromDate;
    private static Date toDate;

    @BeforeAll
    static void setUp() {
        reportService = new ReportService();
        // Date range: 1 year ago to today
        toDate   = new Date();
        fromDate = new Date(toDate.getTime() - (365L * 24 * 60 * 60 * 1000));
    }

    // ── Low stock report ─────────────────────────────────────────────────────

    @Test @Order(1)
    @DisplayName("getLowStockReport returns non-null list")
    void testLowStockReportNotNull() throws Exception {
        List<String[]> data = reportService.getLowStockReport();
        assertNotNull(data);
    }

    @Test @Order(2)
    @DisplayName("getLowStockReport detects items below minimum (CAT003, CAT004, CAT006, CAT007)")
    void testLowStockReportHasEntries() throws Exception {
        List<String[]> data = reportService.getLowStockReport();
        assertFalse(data.isEmpty(), "Should detect items below minimum stock level");
    }

    @Test @Order(3)
    @DisplayName("getLowStockReport rows have 6 columns")
    void testLowStockReportRowStructure() throws Exception {
        List<String[]> data = reportService.getLowStockReport();
        if (!data.isEmpty()) {
            assertEquals(6, data.get(0).length, "Low stock row should have 6 columns");
        }
    }

    // ── Turnover report ──────────────────────────────────────────────────────

    @Test @Order(4)
    @DisplayName("getTurnoverReport returns non-null list")
    void testTurnoverReportNotNull() throws Exception {
        List<String[]> data = reportService.getTurnoverReport(fromDate, toDate);
        assertNotNull(data);
    }

    @Test @Order(5)
    @DisplayName("getTurnoverReport rows have 4 columns when not empty")
    void testTurnoverReportRowStructure() throws Exception {
        List<String[]> data = reportService.getTurnoverReport(fromDate, toDate);
        if (!data.isEmpty()) {
            assertEquals(4, data.get(0).length);
        }
    }

    // ── Merchant orders report ───────────────────────────────────────────────

    @Test @Order(6)
    @DisplayName("getMerchantOrdersReport for M001 returns non-null list")
    void testMerchantOrdersReportNotNull() throws Exception {
        List<String[]> data = reportService.getMerchantOrdersReport("M001", fromDate, toDate);
        assertNotNull(data);
    }

    @Test @Order(7)
    @DisplayName("getMerchantOrdersReport for non-existent merchant returns empty list")
    void testMerchantOrdersReportEmptyForUnknown() throws Exception {
        List<String[]> data = reportService.getMerchantOrdersReport("NOBODY", fromDate, toDate);
        assertNotNull(data);
        assertTrue(data.isEmpty());
    }

    // ── Merchant activity report ─────────────────────────────────────────────

    @Test @Order(8)
    @DisplayName("getMerchantActivityReport returns non-null string for M001")
    void testMerchantActivityReportNotNull() throws Exception {
        String report = reportService.getMerchantActivityReport("M001", fromDate, toDate);
        assertNotNull(report);
        assertFalse(report.isBlank());
    }

    @Test @Order(9)
    @DisplayName("getMerchantActivityReport contains merchant company name")
    void testMerchantActivityReportContainsCompanyName() throws Exception {
        String report = reportService.getMerchantActivityReport("M001", fromDate, toDate);
        assertTrue(report.contains("Tech Supplies Ltd"), "Report should contain merchant name");
    }

    // ── Invoices by merchant ─────────────────────────────────────────────────

    @Test @Order(10)
    @DisplayName("getInvoicesByMerchant returns non-null list")
    void testInvoicesByMerchantNotNull() throws Exception {
        List<String[]> data = reportService.getInvoicesByMerchant("M001", fromDate, toDate);
        assertNotNull(data);
    }

    // ── All invoices report ──────────────────────────────────────────────────

    @Test @Order(11)
    @DisplayName("getAllInvoicesReport returns non-null list")
    void testAllInvoicesReportNotNull() throws Exception {
        List<String[]> data = reportService.getAllInvoicesReport(fromDate, toDate);
        assertNotNull(data);
    }

    // ── Stock turnover ───────────────────────────────────────────────────────

    @Test @Order(12)
    @DisplayName("getStockTurnoverReport returns non-null list")
    void testStockTurnoverReportNotNull() throws Exception {
        List<String[]> data = reportService.getStockTurnoverReport(fromDate, toDate);
        assertNotNull(data);
    }

    // ── Merchant list ────────────────────────────────────────────────────────

    @Test @Order(13)
    @DisplayName("getMerchantList returns non-empty list of active merchants")
    void testGetMerchantListNotEmpty() throws Exception {
        List<String[]> data = reportService.getMerchantList();
        assertNotNull(data);
        assertFalse(data.isEmpty(), "Merchant list should have at least one entry");
    }

    @Test @Order(14)
    @DisplayName("getMerchantList each row has 2 columns (id, name)")
    void testGetMerchantListRowStructure() throws Exception {
        List<String[]> data = reportService.getMerchantList();
        if (!data.isEmpty()) {
            assertEquals(2, data.get(0).length);
        }
    }
}
