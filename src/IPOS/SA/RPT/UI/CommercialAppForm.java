package IPOS.SA.RPT.UI;

import IPOS.SA.RPT.Model.CommercialApplication;
import IPOS.SA.RPT.Service.CommercialAppService;
import IPOS.SA.UI.BaseFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CommercialAppForm extends BaseFrame {

    private final CommercialAppService appService;

    private JTable applicationTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> statusFilter;
    private JLabel messageLabel;

    public CommercialAppForm(String fullname, String role) {
        super(fullname, role, "Commercial Applications");
        this.appService = new CommercialAppService();
        buildContent();
        loadApplications();
    }

    @Override
    protected String getHeaderTitle() {
        return "Commercial Applications";
    }

    private void buildContent() {
        CenterPanel.setLayout(new BorderLayout(0, 0));
        CenterPanel.setBackground(new Color(245, 247, 250));

        // ── TOP BAR ──────────────────────────────────────────
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(17, 24, 39));
        topBar.setBorder(new EmptyBorder(10, 16, 10, 16));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchPanel.setBackground(new Color(17, 24, 39));

        JLabel searchLbl = new JLabel("Search:");
        searchLbl.setForeground(Color.WHITE);
        searchLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        searchField = new JTextField(16);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { loadApplications(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { loadApplications(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { loadApplications(); }
        });

        JLabel filterLbl = new JLabel("Status:");
        filterLbl.setForeground(Color.WHITE);
        filterLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        statusFilter = new JComboBox<>(new String[]{"All", "pending", "approved", "rejected"});
        statusFilter.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusFilter.addActionListener(e -> loadApplications());

        JButton refreshBtn = new JButton("Refresh");
        styleBtn(refreshBtn);
        refreshBtn.addActionListener(e -> loadApplications());

        searchPanel.add(searchLbl);
        searchPanel.add(searchField);
        searchPanel.add(filterLbl);
        searchPanel.add(statusFilter);
        searchPanel.add(refreshBtn);
        topBar.add(searchPanel, BorderLayout.WEST);

        // ── TABLE ─────────────────────────────────────────────
        String[] cols = {
                "ID", "Company Name", "Reg No", "Business Type",
                "Director", "Email", "Phone", "Date Applied", "Status"
        };

        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        applicationTable = new JTable(tableModel);
        applicationTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        applicationTable.setRowHeight(30);
        applicationTable.setShowGrid(false);
        applicationTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        applicationTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        applicationTable.getTableHeader().setBackground(new Color(17, 24, 39));
        applicationTable.getTableHeader().setForeground(Color.WHITE);

        // Colour code status column
        applicationTable.getColumnModel().getColumn(8).setCellRenderer(
                new javax.swing.table.DefaultTableCellRenderer() {
                    public Component getTableCellRendererComponent(JTable t, Object val,
                                                                   boolean sel, boolean foc, int row, int col) {
                        JLabel lbl = new JLabel(
                                val != null ? val.toString() : "", SwingConstants.CENTER);
                        lbl.setOpaque(true);
                        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
                        if (val != null) switch (val.toString()) {
                            case "approved": lbl.setBackground(new Color(198, 239, 206)); lbl.setForeground(new Color(0, 97, 0));    break;
                            case "rejected": lbl.setBackground(new Color(255, 199, 206)); lbl.setForeground(new Color(156, 0, 6));   break;
                            case "pending":  lbl.setBackground(new Color(255, 243, 205)); lbl.setForeground(new Color(133, 100, 4)); break;
                            default:         lbl.setBackground(Color.WHITE);              lbl.setForeground(Color.BLACK);
                        }
                        return lbl;
                    }
                }
        );

        JScrollPane scroll = new JScrollPane(applicationTable);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        // ── BOTTOM BUTTONS ────────────────────────────────────
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(17, 24, 39));
        bottomPanel.setBorder(new EmptyBorder(10, 12, 10, 12));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonPanel.setBackground(new Color(17, 24, 39));

        JButton viewBtn    = new JButton("View Details");
        JButton approveBtn = new JButton("Approve");
        JButton rejectBtn  = new JButton("Reject");

        styleBtn(viewBtn);
        styleBtn(approveBtn);
        styleBtn(rejectBtn);

        approveBtn.setBackground(new Color(20, 83, 45));
        rejectBtn.setBackground(new Color(127, 29, 29));

        viewBtn.addActionListener(e    -> viewApplicationDetails());
        approveBtn.addActionListener(e -> processApplication("approved"));
        rejectBtn.addActionListener(e  -> processApplication("rejected"));

        buttonPanel.add(viewBtn);
        buttonPanel.add(approveBtn);
        buttonPanel.add(rejectBtn);

        messageLabel = new JLabel(" ");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        messageLabel.setForeground(Color.WHITE);

        bottomPanel.add(buttonPanel,  BorderLayout.WEST);
        bottomPanel.add(messageLabel, BorderLayout.EAST);

        CenterPanel.add(topBar,      BorderLayout.NORTH);
        CenterPanel.add(scroll,      BorderLayout.CENTER);
        CenterPanel.add(bottomPanel, BorderLayout.SOUTH);
    }

    // ── DATA METHODS ─────────────────────────────────────────
    private void loadApplications() {
        tableModel.setRowCount(0);
        try {
            String search = searchField != null ? searchField.getText().trim() : "";
            String status = statusFilter != null
                    ? statusFilter.getSelectedItem().toString() : "All";

            List<CommercialApplication> apps = appService.getApplications(status, search);
            for (CommercialApplication app : apps) {
                tableModel.addRow(new Object[]{
                        app.getApplicationId(),
                        app.getCompanyName(),
                        app.getRegistrationNo(),
                        app.getBusinessType(),
                        app.getDirectorName(),
                        app.getEmail(),
                        app.getPhone(),
                        app.getApplicationDate(),
                        app.getStatus()
                });
            }
            if (messageLabel != null)
                messageLabel.setText(apps.size() + " applications loaded");
        } catch (Exception e) {
            if (messageLabel != null)
                messageLabel.setText("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void viewApplicationDetails() {
        int row = applicationTable.getSelectedRow();
        if (row == -1) { setMsg("Select an application to view.", false); return; }

        try {
            int appId = (int) tableModel.getValueAt(row, 0);
            CommercialApplication app = appService.getApplication(appId);

            if (app != null) {
                String details =
                        "APPLICATION DETAILS\n" +
                                "=".repeat(45) + "\n" +
                                "ID:             " + app.getApplicationId()                              + "\n" +
                                "Company:        " + app.getCompanyName()                                + "\n" +
                                "Reg No:         " + app.getRegistrationNo()                             + "\n" +
                                "Business Type:  " + nullSafe(app.getBusinessType())                     + "\n" +
                                "Director:       " + nullSafe(app.getDirectorName())                     + "\n" +
                                "Email:          " + app.getEmail()                                      + "\n" +
                                "Phone:          " + nullSafe(app.getPhone())                            + "\n" +
                                "Fax:            " + nullSafe(app.getFax())                              + "\n" +
                                "Address:        " + nullSafe(app.getAddress())                          + "\n" +
                                "Date Applied:   " + app.getApplicationDate()                            + "\n" +
                                "-".repeat(45)                                                            + "\n" +
                                "Status:         " + app.getStatus().toUpperCase()                       + "\n" +
                                "Review Date:    " + (app.getReviewDate() != null
                                ? app.getReviewDate().toString() : "—")                              + "\n" +
                                "Review Notes:   " + nullSafe(app.getReviewNotes());

                JTextArea textArea = new JTextArea(details);
                textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
                textArea.setEditable(false);
                textArea.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

                JScrollPane detailScroll = new JScrollPane(textArea);
                detailScroll.setPreferredSize(new Dimension(440, 340));

                JOptionPane.showMessageDialog(this, detailScroll,
                        "Application — " + app.getCompanyName(),
                        JOptionPane.PLAIN_MESSAGE);
            }
        } catch (Exception e) {
            setMsg("Error: " + e.getMessage(), false);
        }
    }

    private void processApplication(String decision) {
        int row = applicationTable.getSelectedRow();
        if (row == -1) { setMsg("Select an application first.", false); return; }

        String currentStatus = tableModel.getValueAt(row, 8).toString();
        if (!currentStatus.equals("pending")) {
            setMsg("Application has already been " + currentStatus + ".", false);
            return;
        }

        int    appId   = (int) tableModel.getValueAt(row, 0);
        String company = tableModel.getValueAt(row, 1).toString();
        String email   = tableModel.getValueAt(row, 5).toString();

        String notes = JOptionPane.showInputDialog(this,
                "Enter notes for this decision (optional):",
                decision.equals("approved") ? "Approve Application" : "Reject Application",
                JOptionPane.PLAIN_MESSAGE);
        if (notes == null) return;

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to " + decision +
                        " the application from " + company + "?",
                "Confirm " + decision.substring(0, 1).toUpperCase() + decision.substring(1),
                JOptionPane.YES_NO_OPTION,
                decision.equals("approved")
                        ? JOptionPane.QUESTION_MESSAGE
                        : JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            // Process the application — use 1 as reviewed_by placeholder
            appService.processApplication(appId, decision, notes, 1);

            if (decision.equals("approved")) {
                String merchantId = appService.createMerchantFromApplication(appId);
                setMsg("Application approved. Merchant account " + merchantId + " created.", true);
            } else {
                setMsg("Application rejected successfully.", true);
            }

            loadApplications();

            // Remind staff to send outcome email
            JOptionPane.showMessageDialog(this,
                    "Application " + decision + " successfully.\n\n" +
                            "Please send a notification email to:\n" +
                            email + "\n\n" +
                            "Outcome: " + decision.toUpperCase() +
                            (notes.isEmpty() ? "" : "\nNotes: " + notes),
                    "Send Outcome Email", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            setMsg("Error: " + e.getMessage(), false);
        }
    }

    // ── HELPERS ──────────────────────────────────────────────
    private void styleBtn(JButton btn) {
        btn.setBackground(new Color(30, 70, 90));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    }

    private void setMsg(String text, boolean success) {
        messageLabel.setText(text);
        messageLabel.setForeground(success
                ? new Color(0, 200, 100)
                : new Color(255, 100, 100));
    }

    private String nullSafe(String value) {
        return value != null ? value : "—";
    }
}