package IPOS.SA.RPT.Service;

import IPOS.SA.Comms.PUClient.IPOSPUEmailClient;
import IPOS.SA.RPT.Model.CommercialApplication;
import IPOS.SA.DB.DBConnection;

import java.io.IOException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CommercialAppService {

    private final DBConnection db;

    public CommercialAppService() {
        this.db = new DBConnection();
    }

    // Gets all applications with optional status and search filter
    public List<CommercialApplication> getApplications(String status, String search) throws Exception {
        List<CommercialApplication> applications = new ArrayList<>();

        String sql =
                "SELECT * FROM commercial_applications WHERE 1=1";

        if (!status.equals("All"))  sql += " AND status = '" + status + "'";
        if (!search.isEmpty())      sql += " AND (company_name LIKE '%" + search +
                "%' OR email LIKE '%" + search + "%')";
        sql += " ORDER BY application_date DESC";

        ResultSet rs = db.query(sql);
        while (rs.next()) {
            applications.add(extractFromResultSet(rs));
        }
        return applications;
    }

    // Gets a single application by ID
    public CommercialApplication getApplication(int applicationId) throws Exception {
        ResultSet rs = db.query(
                "SELECT * FROM commercial_applications WHERE application_id = ?",
                applicationId);
        if (rs.next()) return extractFromResultSet(rs);
        return null;
    }

    // Approves or rejects an application
    public boolean processApplication(int applicationId, String decision,
                                      String notes, int reviewedBy) throws Exception {
        int rows = db.update(
                "UPDATE commercial_applications SET status=?, review_date=CURDATE(), " +
                        "review_notes=?, reviewed_by=? WHERE application_id=?",
                decision, notes.isEmpty() ? null : notes, reviewedBy, applicationId
        );
        return rows > 0;
    }

    // Creates a merchant account from an approved application
    public String createMerchantFromApplication(int applicationId) throws Exception {
        CommercialApplication app = getApplication(applicationId);
        if (app == null) throw new Exception("Application not found.");

        // Check merchant doesn't already exist by email
        ResultSet check = db.query(
                "SELECT merchant_id FROM merchant WHERE email = ?", app.getEmail());
        if (check.next()) return check.getString("merchant_id");

        // Generate merchant ID
        String merchantId = "M" + String.format("%04d", applicationId);

        // Generate plain text password for email (using email as username)
        String originalPassword = app.getEmail() + "123";

        // Hash the password for database storage
        String hashedPassword = hashPassword(originalPassword);

        int rowsAffected = db.update(
                "INSERT INTO merchant (merchant_id, company_name, business_type, " +
                        "registration_number, email, phone, fax, address, " +
                        "credit_limit, outstanding_balance, account_status, " +
                        "discount_type, fixed_discount_rate, flexible_discount_rate, " +
                        "is_Active, username, password_hash) " +
                        "VALUES (?,?,?,?,?,?,?,?,1000.00,0.00,'normal','fixed',0.00,0.00,1,?,?)",
                merchantId,
                app.getCompanyName(),
                app.getBusinessType(),
                app.getRegistrationNo(),
                app.getEmail(),
                app.getPhone(),
                app.getFax(),
                app.getAddress(),
                app.getEmail(),  // username (using email as username)
                hashedPassword   // password_hash
        );

        // Calls the emailService API to send an email to the merchant with credentials
        if (rowsAffected > 0) {
            sendMerchantFromApplicationEmail(app, originalPassword, merchantId);
            return merchantId;
        }

        throw new Exception("Failed to create merchant account");
    }

    // Add this method to send merchant creation email
    private void sendMerchantFromApplicationEmail(CommercialApplication app, String originalPassword, String merchantId) {
        String emailContent = buildMerchantFromApplicationEmailContent(app, originalPassword, merchantId);

        try {
            // Using IPOS-PU email API
            boolean emailSent = IPOSPUEmailClient.produceEmail(
                    app.getEmail(),                          // recipient
                    emailContent,                            // email body
                    merchantId,                              // reference (merchant ID)
                    "IPOS-SA",                               // sender
                    "MerchantCreation"                       // subsystem
            );

            if (emailSent) {
                System.out.println("Merchant credentials sent to " + app.getEmail());
            } else {
                System.err.println("Failed to send merchant email to " + app.getEmail());
            }
        } catch (IOException e) {
            System.err.println("Email service error for merchant " + app.getEmail() + ": " + e.getMessage());
        }
    }

    // Add this method to build the email content
    private String buildMerchantFromApplicationEmailContent(CommercialApplication app, String originalPassword, String merchantId) {
        return "Dear " + app.getCompanyName() + ",\n\n"
                + "Congratulations! Your commercial application has been approved and your merchant account has been created.\n\n"
                + "Merchant Account Details:\n"
                + "Merchant ID: " + merchantId + "\n"
                + "Company Name: " + app.getCompanyName() + "\n"
                + "Business Type: " + app.getBusinessType() + "\n"
                + "Registration Number: " + app.getRegistrationNo() + "\n\n"
                + "Login Details:\n"
                + "Username: " + app.getEmail() + "\n"
                + "Password: " + originalPassword + "\n\n"
                + "Account Information:\n"
                + "Credit Limit: $1,000.00\n"
                + "Outstanding Balance: $0.00\n"
                + "Account Status: Normal\n"
                + "Discount Type: Fixed\n\n"
                + "Please login and change your password after first access.\n\n"
                + "Regards,\nIPOS System Administrator";
    }

    // Gets count of pending applications for dashboard
    public int getPendingCount() throws Exception {
        ResultSet rs = db.query(
                "SELECT COUNT(*) as count FROM commercial_applications WHERE status = 'pending'");
        if (rs.next()) return rs.getInt("count");
        return 0;
    }

    private String hashPassword(String password) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return password;
        }
    }

    // Extracts a CommercialApplication from a ResultSet row
    private CommercialApplication extractFromResultSet(ResultSet rs) throws Exception {
        CommercialApplication app = new CommercialApplication();
        app.setApplicationId(rs.getInt("application_id"));
        app.setCompanyName(rs.getString("company_name"));
        app.setRegistrationNo(rs.getString("registration_no"));
        app.setBusinessType(rs.getString("business_type"));
        app.setDirectorName(rs.getString("director_name"));
        app.setEmail(rs.getString("email"));
        app.setPhone(rs.getString("phone"));
        app.setFax(rs.getString("fax"));
        app.setAddress(rs.getString("address"));
        app.setStatus(rs.getString("status"));
        app.setReviewNotes(rs.getString("review_notes"));
        if (rs.getDate("application_date") != null)
            app.setApplicationDate(rs.getDate("application_date").toLocalDate());
        if (rs.getDate("review_date") != null)
            app.setReviewDate(rs.getDate("review_date").toLocalDate());
        if (rs.getObject("reviewed_by") != null)
            app.setReviewedBy(rs.getInt("reviewed_by"));
        return app;
    }
}
