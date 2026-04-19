package IPOS.SA.RPT.Service;

import IPOS.SA.Comms.PUClient.IPOSPUEmailClient;
import IPOS.SA.RPT.Model.CommercialApplication;
import IPOS.SA.DB.DBConnection;

import java.io.IOException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class responsible for managing commercial membership applications.
 * Provides methods to retrieve, approve and reject applications, create
 * merchant accounts from approved applications and send notification emails
 * to applicants via the IPOS-PU email service.
 */
public class CommercialAppService {

    /** Database connection used for all queries and updates. */
    private final DBConnection db;

    /**
     * Default constructor — initialises the service with a database connection.
     */
    public CommercialAppService() {
        this.db = new DBConnection();
    }

    /**
     * Retrieves a filtered list of commercial applications.
     * Supports filtering by status and searching by company name or email.
     * Results are ordered by application date descending.
     *
     * @param status the status to filter by — "All" returns all applications
     * @param search the search text to match against company name or email
     * @return list of matching CommercialApplication objects
     * @throws Exception if a database error occurs
     */
    // Gets all applications with optional status and search filter
    public List<CommercialApplication> getApplications(String status,
                                                       String search) throws Exception {
        List<CommercialApplication> applications = new ArrayList<>();

        String sql = "SELECT * FROM commercial_applications WHERE 1=1";

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

    /**
     * Retrieves a single commercial application by its ID.
     *
     * @param applicationId the unique application identifier
     * @return the CommercialApplication if found, null otherwise
     * @throws Exception if a database error occurs
     */
    // Gets a single application by ID
    public CommercialApplication getApplication(int applicationId) throws Exception {
        ResultSet rs = db.query(
                "SELECT * FROM commercial_applications WHERE application_id = ?",
                applicationId);
        if (rs.next()) return extractFromResultSet(rs);
        return null;
    }

    /**
     * Approves or rejects a commercial application.
     * Updates the status, review date, review notes and reviewer ID.
     *
     * @param applicationId the unique application identifier
     * @param decision  the decision — "approved" or "rejected"
     * @param notes optional review notes from the reviewer
     * @param reviewedBy  the user ID of the staff member making the decision
     * @return true if the update was successful
     * @throws Exception if a database error occurs
     */
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

    /**
     * Sends an approval notification email to the applicant via the IPOS-PU email service.
     * Informs the applicant their application has been approved and provides their merchant ID.
     *
     * @param email the applicant's email address
     * @param companyName the applicant's company name
     * @param merchantId  the newly created merchant account ID
     */
    public void sendApprovalEmail(String email, String companyName, String merchantId) {
        String content = "Dear " + companyName + ",\n\n"
                + "We are pleased to inform you that your commercial application has been approved.\n\n"
                + "Your merchant account has been created:\n"
                + "Merchant ID: " + merchantId + "\n\n"
                + "Our team will be in touch with your login credentials shortly.\n\n"
                + "Regards,\nIPOS System Administrator";

        try {
            boolean sent = IPOSPUEmailClient.produceEmail(
                    email,
                    content,
                    companyName,
                    "IPOS-SA",
                    "CommercialApplications"
            );
            if (sent) {
                System.out.println("Approval email sent to " + email);
            } else {
                System.err.println("Failed to send approval email to " + email);
            }
        } catch (IOException e) {
            System.err.println("Email error: " + e.getMessage());
        }
    }

    /**
     * Sends a rejection notification email to the applicant via the IPOS-PU email service.
     * Includes the reviewer's notes if provided.
     *
     * @param email the applicant's email address
     * @param companyName the applicant's company name
     * @param notes optional rejection notes from the reviewer
     */
    public void sendRejectionEmail(String email, String companyName, String notes) {
        String content = "Dear " + companyName + ",\n\n"
                + "We regret to inform you that your commercial application has not been approved at this time.\n\n"
                + (notes != null && !notes.isEmpty() ? "Notes: " + notes + "\n\n" : "")
                + "If you have any questions, please contact us.\n\n"
                + "Regards,\nIPOS System Administrator";

        try {
            boolean sent = IPOSPUEmailClient.produceEmail(
                    email,
                    content,
                    companyName,
                    "IPOS-SA",
                    "CommercialApplications"
            );
            if (sent) {
                System.out.println("Rejection email sent to " + email);
            } else {
                System.err.println("Failed to send rejection email to " + email);
            }
        } catch (IOException e) {
            System.err.println("Email error: " + e.getMessage());
        }
    }

    /**
     * Creates a new merchant account from an approved commercial application.
     * Checks if a merchant with the same email already exists to prevent duplicates.
     * Generates a merchant ID, hashes a default password and inserts the merchant record.
     * Sends a welcome email with login credentials on successful creation.
     *
     * @param applicationId the ID of the approved application to create a merchant from
     * @return the newly created merchant ID, or the existing merchant ID if already created
     * @throws Exception if the application is not found or the merchant creation fails
     */
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

    /**
     * Sends a welcome email to a newly created merchant with their login credentials.
     * Called automatically after a merchant account is created from an application.
     *
     * @param app the approved commercial application
     * @param originalPassword the plain text default password
     * @param merchantId the newly assigned merchant ID
     */
    // Add this method to send merchant creation email
    private void sendMerchantFromApplicationEmail(CommercialApplication app,
                                                  String originalPassword,
                                                  String merchantId) {
        String emailContent = buildMerchantFromApplicationEmailContent(
                app, originalPassword, merchantId);

        try {
            // Using IPOS-PU email API
            boolean emailSent = IPOSPUEmailClient.produceEmail(
                    app.getEmail(),       // recipient
                    emailContent,         // email body
                    merchantId,           // reference (merchant ID)
                    "IPOS-SA",            // sender
                    "MerchantCreation"    // subsystem
            );

            if (emailSent) {
                System.out.println("Merchant credentials sent to " + app.getEmail());
            } else {
                System.err.println("Failed to send merchant email to " + app.getEmail());
            }
        } catch (IOException e) {
            System.err.println("Email service error for merchant " +
                    app.getEmail() + ": " + e.getMessage());
        }
    }

    /**
     * Builds the email body for the merchant welcome email.
     * Includes merchant account details and login credentials.
     *
     * @param app the approved commercial application
     * @param originalPassword the plain text default password
     * @param merchantId  the newly assigned merchant ID
     * @return the formatted email body as a string
     */
    // Add this method to build the email content
    private String buildMerchantFromApplicationEmailContent(CommercialApplication app,
                                                            String originalPassword,
                                                            String merchantId) {
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

    /**
     * Returns the number of applications currently awaiting review.
     * Used by dashboard screens to display the pending applications count.
     *
     * @return the count of pending applications
     * @throws Exception if a database error occurs
     */
    // Gets count of pending applications for dashboard
    public int getPendingCount() throws Exception {
        ResultSet rs = db.query(
                "SELECT COUNT(*) as count FROM commercial_applications WHERE status = 'pending'");
        if (rs.next()) return rs.getInt("count");
        return 0;
    }

    /**
     * Hashes a plain text password using SHA-256 encoding.
     * Returns the hex string representation of the hash.
     *
     * @param password the plain text password to hash
     * @return the SHA-256 hashed password as a hex string
     */
    private String hashPassword(String password) {
        try {
            java.security.MessageDigest md =
                    java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return password;
        }
    }

    /**
     * Extracts a CommercialApplication object from the current row of a ResultSet.
     * Handles nullable date and integer fields safely.
     * Used as a shared helper to avoid duplicated mapping code.
     *
     * @param rs the ResultSet positioned at the row to extract
     * @return a fully populated CommercialApplication object
     * @throws Exception if a database error occurs
     */
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