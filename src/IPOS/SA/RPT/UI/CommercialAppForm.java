package IPOS.SA.RPT.UI;

import IPOS.SA.RPT.Model.CommercialApplication;
import IPOS.SA.RPT.Service.CommercialAppService;
import IPOS.SA.UI.BaseFrame;
import IPOS.SA.UI.ScreenRouter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

// AI (Claude) was used to implement the approve/reject processing flow, status colour coding and the sendResponseToPU HTTP POST method.

/**
 * Screen for viewing and processing commercial membership applications in IPOS-SA.
 * Displays a searchable, filterable table of all applications submitted via IPOS-CA.
 * Administrators can approve or reject applications from this screen.
 *
 * On approval — a merchant account is automatically created and a welcome email
 * is sent to the applicant via the IPOS-PU email service.
 * On rejection — a rejection email is sent with optional review notes.
 *
 * The Status column is colour-coded:
 * approved (green), rejected (red), pending (yellow).
 */
public class CommercialAppForm extends BaseFrame {

    /** Service used for all commercial application operations. */
    private final CommercialAppService appService;

    /** The table displaying commercial applications. */
    private JTable applicationTable;

    /** The data model backing the application table. */
    private DefaultTableModel tableModel;

    /** Input field for searching applications by company name or email. */
    private JTextField searchField;

    /** Dropdown for filtering applications by status. */
    private JComboBox<String> statusFilter;

    /** Status bar label showing record count or messages. */
    private JLabel messageLabel;

    /**
     * Constructor — builds the screen and loads all applications.
     *
     * @param fullname the full name of the logged-in user
     * @param role     the role of the logged-in user
     * @param router   the screen router used for navigation
     */
    public CommercialAppForm(String fullname, String role, ScreenRouter router) {
        super(fullname, role, "Commercial Applications", router);
        this.appService = new CommercialAppService();
        buildContent();
        loadApplications();
    }

    /**
     * Returns the title displayed in the page header.
     *
     * @return the header title string
     */
    @Override
    protected String getHeaderTitle() {
        return "Commercial Applications";
    }

    /**
     * Builds and arranges all UI components for this screen.
     * Includes a search bar, status filter, application table and action buttons.
     */
    private void buildContent() {
        CenterPanel.setLayout(new BorderLayout(0, 0));
        CenterPanel.setBackground(new Color(245, 247, 250));

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

        // Trigger live search as user types
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

        String[] cols = {
                "ID", "Company Name", "Reg No", "Business Type",
                "Director", "Email", "Phone", "Date Applied", "Status"
        };

        // Table cells are not directly editable
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

    /**
     * Loads applications from the database into the table.
     * Applies the current search text and status filter.
     */
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

    /**
     * Opens a popup showing full details of the selected application.
     * Displays all fields including review status, review date and notes.
     */
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
                                "ID:             " + app.getApplicationId()  + "\n" +
                                "Company:        " + app.getCompanyName()   + "\n" +
                                "Reg No:         " + app.getRegistrationNo()   + "\n" +
                                "Business Type:  " + nullSafe(app.getBusinessType()) + "\n" +
                                "Director:       " + nullSafe(app.getDirectorName()) + "\n" +
                                "Email:          " + app.getEmail() + "\n" +
                                "Phone:          " + nullSafe(app.getPhone()) + "\n" +
                                "Fax:            " + nullSafe(app.getFax()) + "\n" +
                                "Address:        " + nullSafe(app.getAddress())+ "\n" +
                                "Date Applied:   " + app.getApplicationDate()  + "\n" +
                                "-".repeat(45) + "\n" +
                                "Status:         " + app.getStatus().toUpperCase() + "\n" +
                                "Review Date:    " + (app.getReviewDate() != null
                                ? app.getReviewDate().toString() : "—")  + "\n" +
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

    /**
     * Processes the selected application as approved or rejected.
     * Validates the application is still pending before proceeding.
     * Prompts for optional review notes and a confirmation dialog.
     * On approval — creates the merchant account and sends an approval email.
     * On rejection — sends a rejection email with the review notes.
     * Notifies the IPOS-PU system of the outcome after processing.
     *
     * @param decision the decision to apply — "approved" or "rejected"
     */
    private void processApplication(String decision) {
        int row = applicationTable.getSelectedRow();
        if (row == -1) { setMsg("Select an application first.", false); return; }

        String currentStatus = tableModel.getValueAt(row, 8).toString();

        // Prevent re-processing an application that has already been decided
        if (!currentStatus.equals("pending")) {
            setMsg("Application has already been " + currentStatus + ".", false);
            return;
        }

        int    appId   = (int) tableModel.getValueAt(row, 0);
        String company = tableModel.getValueAt(row, 1).toString();
        String email   = tableModel.getValueAt(row, 5).toString();

        // Prompt for optional review notes before confirming the decision
        String notes = JOptionPane.showInputDialog(this,
                "Enter notes for this decision (optional):",
                decision.equals("approved") ? "Approve Application" : "Reject Application",
                JOptionPane.PLAIN_MESSAGE);
        if (notes == null) return; // User cancelled the dialog

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
                // Create merchant account and send approval email
                String merchantId = appService.createMerchantFromApplication(appId);
                setMsg("Application approved. Merchant account " + merchantId + " created.", true);
                appService.sendApprovalEmail(email, company, merchantId);
                // Send approval response to PU system
                sendResponseToPU("http://localhost:8080/merchant/response", email);
            } else {
                setMsg("Application rejected successfully.", true);
                appService.sendRejectionEmail(email, company, notes);
                // Send rejection response to PU system
                sendResponseToPU("http://localhost:8080/merchant/reject", email);
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

    /**
     * Applies a consistent visual style to an action button.
     *
     * @param btn the button to style
     */
    private void styleBtn(JButton btn) {
        btn.setBackground(new Color(30, 70, 90));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    }

    /**
     * Displays a success or error message in the status bar.
     *
     * @param text    the message to display
     * @param success true for a green success message, false for a red error message
     */
    private void setMsg(String text, boolean success) {
        messageLabel.setText(text);
        messageLabel.setForeground(success
                ? new Color(0, 200, 100)
                : new Color(255, 100, 100));
    }

    /**
     * Sends a POST request to the IPOS-PU system notifying it of an application outcome.
     * Uses the merchant's email as the request body identifier.
     * Silently logs any connection errors rather than surfacing them to the user.
     * sendResponseToPU - generated by Claude
     *
     * @param url   the IPOS-PU endpoint URL to post to
     * @param email the merchant email to include in the JSON body
     */
    private void sendResponseToPU(String url, String email) {
        try {
            String jsonBody = "{\"email\": \"" + email + "\"}";

            java.net.URL puUrl = new java.net.URL(url);
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) puUrl.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            try (java.io.OutputStream os = conn.getOutputStream()) {
                os.write(jsonBody.getBytes("UTF-8"));
            }

            int responseCode = conn.getResponseCode();
            System.out.println("PU response code: " + responseCode);
            conn.disconnect();

        } catch (Exception e) {
            // Log failure but do not interrupt the user — PU notification is non-critical
            System.err.println("Could not notify PU system: " + e.getMessage());
        }
    }

    /**
     * Returns the value if non-null, or a dash placeholder if null.
     * Used when building the application details popup.
     *
     * @param value the string to check
     * @return the original value, or "—" if null
     */
    private String nullSafe(String value) {
        return value != null ? value : "—";
    }
}