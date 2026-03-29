package IPOS.SA.RPT.Model;

import java.util.Date;

public abstract class ReportData {
    protected Date reportDate;
    protected String generatedBy;

    public Date getReportDate() { return reportDate; }
    public void setReportDate(Date reportDate) { this.reportDate = reportDate; }

    public String getGeneratedBy() { return generatedBy; }
    public void setGeneratedBy(String generatedBy) { this.generatedBy = generatedBy; }
}