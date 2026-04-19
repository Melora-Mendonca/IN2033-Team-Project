package IPOS.SA.RPT.Model;

import java.util.Date;
/**
 * Abstract base class for all report data models in IPOS-SA.
 * Provides common metadata fields shared by every report type —
 * the date the report was generated and the name of the staff member
 * who generated it.
 *
 * All specific report data classes extend this class.
 */
public abstract class ReportData {
    /** The date this report was generated. */
    protected Date reportDate;
    /** The full name or username of the staff member who generated the report. */
    protected String generatedBy;
    /**
     * Returns the date this report was generated.
     *
     * @return the report generation date
     */
    public Date getReportDate() { return reportDate; }
    /**
     * Sets the date this report was generated.
     *
     * @param reportDate the report generation date
     */

    public void setReportDate(Date reportDate) { this.reportDate = reportDate; }
    /**
     * Returns the name of the staff member who generated the report.
     *
     * @return the name of the report generator
     */
    public String getGeneratedBy() { return generatedBy; }
    /**
     * Sets the name of the staff member who generated the report.
     *
     * @param generatedBy the name of the report generator
     */
    public void setGeneratedBy(String generatedBy) { this.generatedBy = generatedBy; }
}