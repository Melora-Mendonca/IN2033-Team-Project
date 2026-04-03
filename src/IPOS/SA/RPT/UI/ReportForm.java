package IPOS.SA.RPT.UI;

import IPOS.SA.RPT.Service.ReportService;
import IPOS.SA.UI.BaseFrame;

import com.itextpdf.text.*;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.jfree.chart.*;
import org.jfree.chart.plot.*;
import org.jfree.data.category.*;
import org.jfree.data.general.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.Color;
import java.io.*;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ReportForm extends BaseFrame {

    private final ReportService reportService;

    private JComboBox<String> reportTypeCombo;
    private JComboBox<String> merchantCombo;
    private JSpinner fromDateSpinner;
    private JSpinner toDateSpinner;
    private JTable reportTable;
    private JTextArea reportTextArea;
    private JTabbedPane tabbedPane;
    private JLabel statusLabel;
    private JPanel chartPanel;

    private List<String[]> currentData;
    private String[] currentColumns;
    private String currentReportType = "";

    public ReportForm(String fullname, String role) {
        super(fullname, role, "Report Viewer");
        this.reportService = new ReportService();
        buildContent();
        loadMerchants();
    }

    @Override
    protected String getHeaderTitle() {
        return "Report Generator";
    }

    private void buildContent() {
        CenterPanel.setLayout(new BorderLayout());
        CenterPanel.setBackground(new Color(245, 247, 250));

        // ── FILTER BAR ────────────────────────────────────────
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        filterPanel.setBackground(new Color(17, 24, 39));
        filterPanel.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));

        JLabel reportTypeLbl = new JLabel("Report Type:");
        reportTypeLbl.setForeground(Color.WHITE);
        reportTypeLbl.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));

        reportTypeCombo = new JComboBox<>(new String[]{
                "Low Stock Report",
                "Turnover Report",
                "Merchant Orders",
                "Merchant Activity",
                "Invoices by Merchant",
                "All Invoices",
                "Stock Turnover"
        });
        reportTypeCombo.setPreferredSize(new Dimension(180, 30));
        reportTypeCombo.addActionListener(e -> updateMerchantDropdown());

        JLabel merchantLbl = new JLabel("Merchant:");
        merchantLbl.setForeground(Color.WHITE);
        merchantLbl.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));

        merchantCombo = new JComboBox<>();
        merchantCombo.setPreferredSize(new Dimension(200, 30));

        JLabel fromLbl = new JLabel("From:");
        fromLbl.setForeground(Color.WHITE);
        fromLbl.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));

        fromDateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor fromEditor = new JSpinner.DateEditor(fromDateSpinner, "dd-MM-yyyy");
        fromDateSpinner.setEditor(fromEditor);
        fromDateSpinner.setValue(new Date(System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000));

        JLabel toLbl = new JLabel("To:");
        toLbl.setForeground(Color.WHITE);
        toLbl.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));

        toDateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor toEditor = new JSpinner.DateEditor(toDateSpinner, "dd-MM-yyyy");
        toDateSpinner.setEditor(toEditor);
        toDateSpinner.setValue(new Date());

        filterPanel.add(reportTypeLbl);  filterPanel.add(reportTypeCombo);
        filterPanel.add(merchantLbl);    filterPanel.add(merchantCombo);
        filterPanel.add(fromLbl);        filterPanel.add(fromDateSpinner);
        filterPanel.add(toLbl);          filterPanel.add(toDateSpinner);

        // ── TABBED PANE ───────────────────────────────────────
        tabbedPane = new JTabbedPane();

        // Table view
        reportTable = new JTable();
        reportTable.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
        reportTable.setRowHeight(25);
        reportTable.getTableHeader().setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
        reportTable.getTableHeader().setBackground(new Color(17, 24, 39));
        reportTable.getTableHeader().setForeground(Color.WHITE);
        tabbedPane.addTab("Table View", new JScrollPane(reportTable));

        // Formatted view
        // Replace the formatted view tab with a split pane
        JSplitPane formattedSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        formattedSplit.setResizeWeight(0.5);
        formattedSplit.setDividerSize(6);

// Chart on top
        chartPanel = new JPanel(new BorderLayout());
        chartPanel.setBackground(Color.WHITE);
        chartPanel.setPreferredSize(new Dimension(800, 350));
        formattedSplit.setTopComponent(chartPanel);

// Formatted text below
        reportTextArea = new JTextArea();
        reportTextArea.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 12));
        reportTextArea.setEditable(false);
        formattedSplit.setBottomComponent(new JScrollPane(reportTextArea));

        tabbedPane.addTab("Formatted View", formattedSplit);

        // ── BOTTOM BUTTONS ────────────────────────────────────
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(new Color(17, 24, 39));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        JButton generateBtn = new JButton("Generate Report");
        JButton printBtn    = new JButton("Print");
        JButton csvBtn      = new JButton("Export CSV");
        JButton excelBtn    = new JButton("Export Excel");
        JButton pdfBtn      = new JButton("Export PDF");

        styleBtn(generateBtn);
        styleBtn(printBtn);
        styleBtn(csvBtn);
        styleBtn(excelBtn);
        styleBtn(pdfBtn);

        generateBtn.addActionListener(e -> generateReport());
        printBtn.addActionListener(e    -> printReport());
        csvBtn.addActionListener(e      -> exportCSV());
        excelBtn.addActionListener(e    -> exportExcel());
        pdfBtn.addActionListener(e      -> exportPDF());

        statusLabel = new JLabel("Ready");
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));

        buttonPanel.add(generateBtn);
        buttonPanel.add(printBtn);
        buttonPanel.add(csvBtn);
        buttonPanel.add(excelBtn);
        buttonPanel.add(pdfBtn);
        buttonPanel.add(statusLabel);

        CenterPanel.add(filterPanel,  BorderLayout.NORTH);
        CenterPanel.add(tabbedPane,   BorderLayout.CENTER);
        CenterPanel.add(buttonPanel,  BorderLayout.SOUTH);
    }

    // ── DATA METHODS ─────────────────────────────────────────
    private void loadMerchants() {
        try {
            List<String[]> merchants = reportService.getMerchantList();
            merchantCombo.removeAllItems();
            merchantCombo.addItem("All Merchants");
            for (String[] m : merchants) merchantCombo.addItem(m[0] + " - " + m[1]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateMerchantDropdown() {
        String selected = (String) reportTypeCombo.getSelectedItem();
        boolean needsMerchant = selected.equals("Merchant Orders") ||
                selected.equals("Merchant Activity") ||
                selected.equals("Invoices by Merchant");
        merchantCombo.setEnabled(needsMerchant);
    }

    private void generateReport() {
        String reportType        = (String) reportTypeCombo.getSelectedItem();
        Date   fromDate          = (Date)   fromDateSpinner.getValue();
        Date   toDate            = (Date)   toDateSpinner.getValue();
        String merchantSelection = (String) merchantCombo.getSelectedItem();

        try {
            statusLabel.setText("Generating report...");
            currentReportType = reportType;

            String merchantId = merchantSelection != null && merchantSelection.contains(" - ")
                    ? merchantSelection.split(" - ")[0] : "";

            List<String[]> data;
            String[]       columns;

            switch (reportType) {
                case "Low Stock Report": {
                    data    = reportService.getLowStockReport();
                    columns = new String[]{"Item ID", "Description", "Package", "Unit", "Stock", "Min Level"};
                    if (data.isEmpty()) data.add(new String[]{"No low stock items found", "", "", "", "", ""});
                    updateTableWithData(data, columns);
                    updateFormattedView("LOW STOCK REPORT", columns, data, fromDate, toDate);
                    updateChart(data, columns, reportType);
                    break;
                }
                case "Turnover Report": {
                    data    = reportService.getTurnoverReport(fromDate, toDate);
                    columns = new String[]{"Period", "Orders", "Items Sold", "Revenue (£)"};
                    if (data.isEmpty()) data.add(new String[]{"No data found", "", "", ""});
                    updateTableWithData(data, columns);
                    updateFormattedView("TURNOVER REPORT", columns, data, fromDate, toDate);
                    updateChart(data, columns, reportType);
                    break;
                }
                case "Merchant Orders": {
                    data    = reportService.getMerchantOrdersReport(merchantId, fromDate, toDate);
                    columns = new String[]{"Order ID", "Date", "Amount (£)", "Dispatched", "Status"};
                    if (data.isEmpty()) data.add(new String[]{"No orders found", "", "", "", ""});
                    updateTableWithData(data, columns);
                    updateFormattedView("MERCHANT ORDERS REPORT", columns, data, fromDate, toDate);
                    updateChart(data, columns, reportType);
                    break;
                }
                case "Merchant Activity": {
                    String text = reportService.getMerchantActivityReport(merchantId, fromDate, toDate);
                    reportTextArea.setText(text);
                    reportTextArea.setCaretPosition(0);
                    tabbedPane.setSelectedIndex(1);
                    break;
                }
                case "Invoices by Merchant": {
                    data    = reportService.getInvoicesByMerchant(merchantId, fromDate, toDate);
                    columns = new String[]{"Invoice ID", "Date", "Due Date", "Order ID", "Total (£)", "Paid (£)", "Status"};
                    if (data.isEmpty()) data.add(new String[]{"No invoices found", "", "", "", "", "", ""});
                    updateTableWithData(data, columns);
                    updateFormattedView("INVOICES BY MERCHANT", columns, data, fromDate, toDate);
                    updateChart(data, columns, reportType);
                    break;
                }
                case "All Invoices": {
                    data    = reportService.getAllInvoicesReport(fromDate, toDate);
                    columns = new String[]{"Invoice ID", "Merchant", "Date", "Due Date", "Order ID", "Total (£)", "Paid (£)", "Status"};
                    if (data.isEmpty()) data.add(new String[]{"No invoices found", "", "", "", "", "", "", ""});
                    updateTableWithData(data, columns);
                    updateFormattedView("ALL INVOICES REPORT", columns, data, fromDate, toDate);
                    updateChart(data, columns, reportType);
                    break;
                }
                case "Stock Turnover": {
                    data    = reportService.getStockTurnoverReport(fromDate, toDate);
                    columns = new String[]{"Item ID", "Description", "Sold", "Received", "Net Change", "Revenue (£)"};
                    if (data.isEmpty()) data.add(new String[]{"No stock movement found", "", "", "", "", ""});
                    updateTableWithData(data, columns);
                    updateFormattedView("STOCK TURNOVER REPORT", columns, data, fromDate, toDate);
                    updateChart(data, columns, reportType);
                    break;
                }
            }

            statusLabel.setText("Report generated — " + reportTable.getRowCount() + " records");

        } catch (Exception e) {
            statusLabel.setText("Error: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error generating report: " + e.getMessage());
        }
    }

    // ── CHART ─────────────────────────────────────────────────
    private void updateChart(List<String[]> data, String[] columns, String reportType) {
        chartPanel.removeAll();
        JFreeChart chart = null;

        switch (reportType) {
            case "Turnover Report":
                chart = createBarChart(data, "Revenue by Period",
                        "Period", "Revenue (£)", 0, 3);
                break;
            case "Low Stock Report":
                chart = createBarChart(data, "Stock vs Minimum Level",
                        "Item", "Quantity", 1, 4);
                break;
            case "Stock Turnover":
                chart = createGroupedBarChart(data, "Stock Turnover",
                        "Item", "Quantity", 1, 2, 3, "Sold", "Received");
                break;
            case "All Invoices":
            case "Invoices by Merchant":
                chart = createPieChart(data, "Invoice Status Breakdown",
                        columns.length - 1);
                break;
            case "Merchant Orders":
                chart = createBarChart(data, "Order Amounts",
                        "Order", "Amount (£)", 0, 2);
                break;
            default:
                break;
        }

        if (chart != null) {
            ChartPanel cp = new ChartPanel(chart);
            cp.setPreferredSize(new Dimension(800, 500));
            chartPanel.add(cp, BorderLayout.CENTER);
        } else {
            JLabel noChart = new JLabel("No chart available for this report type.",
                    SwingConstants.CENTER);
            noChart.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 14));
            noChart.setForeground(Color.GRAY);
            chartPanel.add(noChart, BorderLayout.CENTER);
        }

        chartPanel.revalidate();
        chartPanel.repaint();
        tabbedPane.setSelectedIndex(1);
    }

    private JFreeChart createBarChart(List<String[]> data, String title,
                                      String xLabel, String yLabel,
                                      int labelCol, int valueCol) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (String[] row : data) {
            try {
                double val = Double.parseDouble(row[valueCol].replaceAll("[^0-9.]", ""));
                String lbl = row[labelCol].length() > 10
                        ? row[labelCol].substring(0, 10) : row[labelCol];
                dataset.addValue(val, yLabel, lbl);
            } catch (Exception ignored) {}
        }

        JFreeChart chart = ChartFactory.createBarChart(
                title, xLabel, yLabel, dataset,
                PlotOrientation.VERTICAL, false, true, false
        );

        styleChart(chart);
        return chart;
    }

    private JFreeChart createGroupedBarChart(List<String[]> data, String title,
                                             String xLabel, String yLabel,
                                             int labelCol, int col1, int col2,
                                             String series1, String series2) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (String[] row : data) {
            try {
                double v1  = Double.parseDouble(row[col1].replaceAll("[^0-9.]", ""));
                double v2  = Double.parseDouble(row[col2].replaceAll("[^0-9.]", ""));
                String lbl = row[labelCol].length() > 10
                        ? row[labelCol].substring(0, 10) : row[labelCol];
                dataset.addValue(v1, series1, lbl);
                dataset.addValue(v2, series2, lbl);
            } catch (Exception ignored) {}
        }

        JFreeChart chart = ChartFactory.createBarChart(
                title, xLabel, yLabel, dataset,
                PlotOrientation.VERTICAL, true, true, false
        );

        styleChart(chart);
        return chart;
    }

    private JFreeChart createPieChart(List<String[]> data, String title, int statusCol) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        java.util.Map<String, Integer> counts = new java.util.LinkedHashMap<>();
        for (String[] row : data) {
            String status = row[statusCol];
            counts.put(status, counts.getOrDefault(status, 0) + 1);
        }
        for (java.util.Map.Entry<String, Integer> e : counts.entrySet()) {
            dataset.setValue(e.getKey(), e.getValue());
        }

        JFreeChart chart = ChartFactory.createPieChart(
                title, dataset, true, true, false
        );

        chart.setBackgroundPaint(Color.WHITE);
        chart.getTitle().setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 16));
        return chart;
    }

    private void styleChart(JFreeChart chart) {
        chart.setBackgroundPaint(Color.WHITE);
        chart.getTitle().setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 16));

        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setBackgroundPaint(new Color(245, 247, 250));
        plot.setRangeGridlinePaint(new Color(200, 200, 200));
        plot.setOutlineVisible(false);

        org.jfree.chart.renderer.category.BarRenderer renderer =
                (org.jfree.chart.renderer.category.BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(30, 70, 90));
        renderer.setSeriesPaint(1, new Color(81, 116, 136));
        renderer.setMaximumBarWidth(0.1);
    }

    // ── TABLE + FORMATTED VIEW ────────────────────────────────
    private void updateTableWithData(List<String[]> data, String[] columns) {
        currentData    = data;
        currentColumns = columns;

        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        for (String[] row : data) model.addRow(row);
        reportTable.setModel(model);
        for (int i = 0; i < reportTable.getColumnCount(); i++) {
            reportTable.getColumnModel().getColumn(i).setPreferredWidth(120);
        }
        tabbedPane.setSelectedIndex(0);
    }

    private void updateFormattedView(String title, String[] columns,
                                     List<String[]> data, Date fromDate, Date toDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        StringBuilder sb = new StringBuilder();

        sb.append("InfoPharma Ltd — IPOS-SA\n");
        sb.append(title).append("\n");
        sb.append("=".repeat(70)).append("\n");
        sb.append("Generated: ").append(sdf.format(new Date())).append("\n");
        sb.append("Period:    ").append(sdf.format(fromDate))
                .append(" to ").append(sdf.format(toDate)).append("\n");
        sb.append("-".repeat(70)).append("\n\n");

        for (String col : columns) sb.append(String.format("%-20s", col));
        sb.append("\n").append("-".repeat(70)).append("\n");

        for (String[] row : data) {
            for (String cell : row) sb.append(String.format("%-20s", cell != null ? cell : ""));
            sb.append("\n");
        }

        sb.append("\n").append("=".repeat(70)).append("\n");
        sb.append("Total records: ").append(data.size()).append("\n");

        reportTextArea.setText(sb.toString());
        reportTextArea.setCaretPosition(0);
    }

    // ── PRINT ─────────────────────────────────────────────────
    private void printReport() {
        try {
            java.awt.print.PrinterJob job = java.awt.print.PrinterJob.getPrinterJob();
            job.setJobName("InfoPharma — " + currentReportType);

            // Page format — landscape A4
            java.awt.print.PageFormat pf = job.defaultPage();
            pf.setOrientation(java.awt.print.PageFormat.LANDSCAPE);

            job.setPrintable((graphics, pageFormat, pageIndex) -> {
                if (pageIndex > 1) return java.awt.print.Printable.NO_SUCH_PAGE;

                Graphics2D g2 = (Graphics2D) graphics;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

                double pageWidth  = pageFormat.getImageableWidth();
                double pageHeight = pageFormat.getImageableHeight();

                if (pageIndex == 0) {
                    // Page 1 — title + chart
                    // Title
                    g2.setColor(new Color(14, 37, 48));
                    g2.fillRect(0, 0, (int) pageWidth, 36);
                    g2.setColor(Color.WHITE);
                    g2.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14));
                    g2.drawString("InfoPharma Ltd — " + currentReportType, 12, 24);

                    g2.setColor(Color.DARK_GRAY);
                    g2.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 9));
                    g2.drawString("Generated: " + new SimpleDateFormat("dd-MM-yyyy HH:mm").format(new Date()), 12, 50);

                    // Chart
                    if (chartPanel.getComponentCount() > 0 &&
                            chartPanel.getComponent(0) instanceof ChartPanel) {
                        ChartPanel cp    = (ChartPanel) chartPanel.getComponent(0);
                        JFreeChart chart = cp.getChart();
                        chart.draw(g2, new java.awt.geom.Rectangle2D.Double(
                                0, 60, pageWidth, pageHeight - 60));
                    } else {
                        g2.setColor(Color.GRAY);
                        g2.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
                        g2.drawString("No chart available for this report type.", 40, 120);
                    }
                    return java.awt.print.Printable.PAGE_EXISTS;
                }

                if (pageIndex == 1) {
                    // Page 2 — formatted text
                    g2.setColor(new Color(14, 37, 48));
                    g2.fillRect(0, 0, (int) pageWidth, 36);
                    g2.setColor(Color.WHITE);
                    g2.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14));
                    g2.drawString("InfoPharma Ltd — " + currentReportType + " (Detail)", 12, 24);

                    g2.setColor(Color.BLACK);
                    g2.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 7));
                    String[] lines = reportTextArea.getText().split("\n");
                    int y = 52;
                    for (String line : lines) {
                        if (y > pageHeight - 20) break;
                        g2.drawString(line, 0, y);
                        y += 10;
                    }
                    return java.awt.print.Printable.PAGE_EXISTS;
                }

                return java.awt.print.Printable.NO_SUCH_PAGE;
            }, pf);

            if (job.printDialog()) {
                job.print();
                statusLabel.setText("Print job sent — 2 pages.");
            } else {
                statusLabel.setText("Printing cancelled.");
            }

        } catch (Exception e) {
            statusLabel.setText("Print error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ── CSV EXPORT ────────────────────────────────────────────
    private void exportCSV() {
        if (currentData == null || currentData.isEmpty()) {
            statusLabel.setText("Generate a report first.");
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File(currentReportType.replace(" ", "_") + ".csv"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        try (PrintWriter writer = new PrintWriter(new FileWriter(chooser.getSelectedFile()))) {
            writer.println(String.join(",", currentColumns));
            for (String[] row : currentData) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < row.length; i++) {
                    if (i > 0) sb.append(",");
                    sb.append("\"").append(row[i] != null ? row[i] : "").append("\"");
                }
                writer.println(sb.toString());
            }
            statusLabel.setText("CSV exported successfully.");
            JOptionPane.showMessageDialog(this,
                    "Exported to:\n" + chooser.getSelectedFile().getAbsolutePath(),
                    "Export Successful", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            statusLabel.setText("Export error: " + e.getMessage());
        }
    }

    // ── EXCEL EXPORT ──────────────────────────────────────────
    private void exportExcel() {
        if (currentData == null || currentData.isEmpty()) {
            statusLabel.setText("Generate a report first.");
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File(currentReportType.replace(" ", "_") + ".xlsx"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet(currentReportType);

            // Header style
            XSSFCellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(new XSSFColor(new byte[]{14, 37, 48}, null));
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            XSSFFont headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(new XSSFColor(new byte[]{(byte)255, (byte)255, (byte)255}, null));
            headerFont.setFontName("Segoe UI");
            headerStyle.setFont(headerFont);

            // Alternating row style
            XSSFCellStyle altStyle = workbook.createCellStyle();
            altStyle.setFillForegroundColor(new XSSFColor(new byte[]{(byte)245, (byte)247, (byte)250}, null));
            altStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // Title row
            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("InfoPharma Ltd — " + currentReportType);
            XSSFCellStyle titleStyle = workbook.createCellStyle();
            XSSFFont titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 14);
            titleFont.setFontName("Segoe UI");
            titleStyle.setFont(titleFont);
            titleCell.setCellStyle(titleStyle);

            // Date row
            Row dateRow = sheet.createRow(1);
            dateRow.createCell(0).setCellValue(
                    "Generated: " + new SimpleDateFormat("dd-MM-yyyy HH:mm").format(new Date()));

            // Blank row
            sheet.createRow(2);

            // Header row
            Row headerRow = sheet.createRow(3);
            for (int i = 0; i < currentColumns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(currentColumns[i]);
                cell.setCellStyle(headerStyle);
            }

            // Data rows
            for (int i = 0; i < currentData.size(); i++) {
                Row row = sheet.createRow(i + 4);
                String[] rowData = currentData.get(i);
                for (int j = 0; j < rowData.length; j++) {
                    Cell cell = row.createCell(j);
                    cell.setCellValue(rowData[j] != null ? rowData[j] : "");
                    if (i % 2 == 1) cell.setCellStyle(altStyle);
                }
            }

            // Auto size columns
            for (int i = 0; i < currentColumns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Write file
            try (FileOutputStream fos = new FileOutputStream(chooser.getSelectedFile())) {
                workbook.write(fos);
            }

            statusLabel.setText("Excel exported successfully.");
            JOptionPane.showMessageDialog(this,
                    "Exported to:\n" + chooser.getSelectedFile().getAbsolutePath(),
                    "Export Successful", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            statusLabel.setText("Export error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ── PDF EXPORT ────────────────────────────────────────────
    private void exportPDF() {
        JOptionPane.showMessageDialog(this,
                "If the PDF is already open, please close it before exporting.",
                "Before You Export", JOptionPane.INFORMATION_MESSAGE);

        if (currentData == null || currentData.isEmpty()) {
            statusLabel.setText("Generate a report first.");
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File(currentReportType.replace(" ", "_") + ".pdf"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        try {
            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, new FileOutputStream(chooser.getSelectedFile()));
            document.open();

            // Fonts
            com.itextpdf.text.Font titleFont  = new com.itextpdf.text.Font(
                    com.itextpdf.text.Font.FontFamily.HELVETICA, 18,
                    com.itextpdf.text.Font.BOLD, BaseColor.WHITE);
            com.itextpdf.text.Font headerFont = new com.itextpdf.text.Font(
                    com.itextpdf.text.Font.FontFamily.HELVETICA, 10,
                    com.itextpdf.text.Font.BOLD, BaseColor.WHITE);
            com.itextpdf.text.Font cellFont   = new com.itextpdf.text.Font(
                    com.itextpdf.text.Font.FontFamily.HELVETICA, 9,
                    com.itextpdf.text.Font.NORMAL, BaseColor.DARK_GRAY);
            com.itextpdf.text.Font subFont    = new com.itextpdf.text.Font(
                    com.itextpdf.text.Font.FontFamily.HELVETICA, 10,
                    com.itextpdf.text.Font.NORMAL, BaseColor.GRAY);

            // Title banner
            PdfPTable titleTable = new PdfPTable(1);
            titleTable.setWidthPercentage(100);
            PdfPCell titleCell = new PdfPCell(
                    new Phrase("InfoPharma Ltd — " + currentReportType, titleFont));
            titleCell.setBackgroundColor(new BaseColor(14, 37, 48));
            titleCell.setPadding(12);
            titleCell.setBorder(Rectangle.NO_BORDER);
            titleTable.addCell(titleCell);
            document.add(titleTable);

            // Subtitle
            document.add(new Paragraph(
                    "Generated: " + new SimpleDateFormat("dd-MM-yyyy HH:mm").format(new Date()),
                    subFont));
            document.add(new Paragraph(
                    "Total records: " + currentData.size(), subFont));
            document.add(Chunk.NEWLINE);

            // Add chart image to PDF if available
            if (chartPanel.getComponentCount() > 0 && chartPanel.getComponent(0) instanceof ChartPanel) {
                ChartPanel cp = (ChartPanel) chartPanel.getComponent(0);
                JFreeChart chart = cp.getChart();

                // Render chart to image
                java.awt.image.BufferedImage chartImage = chart.createBufferedImage(700, 350);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                javax.imageio.ImageIO.write(chartImage, "png", baos);
                Image pdfImage = Image.getInstance(baos.toByteArray());
                pdfImage.setAlignment(Image.ALIGN_CENTER);
                pdfImage.scaleToFit(700, 350);
                document.add(pdfImage);
                document.add(Chunk.NEWLINE);
            }

            // Data table
            PdfPTable table = new PdfPTable(currentColumns.length);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10);

            // Header row
            for (String col : currentColumns) {
                PdfPCell cell = new PdfPCell(new Phrase(col, headerFont));
                cell.setBackgroundColor(new BaseColor(17, 54, 74));
                cell.setPadding(6);
                cell.setBorderColor(new BaseColor(255, 255, 255));
                table.addCell(cell);
            }

            // Data rows
            boolean alternate = false;
            for (String[] row : currentData) {
                for (String cellVal : row) {
                    PdfPCell cell = new PdfPCell(
                            new Phrase(cellVal != null ? cellVal : "", cellFont));
                    cell.setPadding(5);
                    cell.setBackgroundColor(alternate
                            ? new BaseColor(245, 247, 250)
                            : BaseColor.WHITE);
                    cell.setBorderColor(new BaseColor(221, 225, 231));
                    table.addCell(cell);
                }
                alternate = !alternate;
            }

            document.add(table);

            // Footer
            document.add(Chunk.NEWLINE);
            document.add(new Paragraph(
                    "InfoPharma Ordering System — IPOS-SA Report", subFont));

            document.close();

            statusLabel.setText("PDF exported successfully.");
            JOptionPane.showMessageDialog(this,
                    "Exported to:\n" + chooser.getSelectedFile().getAbsolutePath(),
                    "Export Successful", JOptionPane.INFORMATION_MESSAGE);

            // Open PDF
            int open = JOptionPane.showConfirmDialog(this,
                    "Open PDF now?", "Open Report", JOptionPane.YES_NO_OPTION);
            if (open == JOptionPane.YES_OPTION) {
                java.awt.Desktop.getDesktop().open(chooser.getSelectedFile());
            }

        } catch (Exception e) {
            statusLabel.setText("PDF error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // HELPERS
    private void styleBtn(JButton btn) {
        btn.setBackground(new Color(30, 70, 90));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
        btn.setPreferredSize(new Dimension(120, 32));
    }
}