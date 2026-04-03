package IPOS.SA.RPT.Service;

import IPOS.SA.RPT.Model.CommercialApplication;
import IPOS.SA.DB.DBConnection;

import java.sql.ResultSet;
import java.time.LocalDate;
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

        // Check merchant doesn't already exist
        ResultSet check = db.query(
                "SELECT merchant_id FROM merchant WHERE email = ?", app.getEmail());
        if (check.next()) return check.getString("merchant_id");

        // Generate merchant ID
        String merchantId = "M" + String.format("%04d", applicationId);

        db.update(
                "INSERT INTO merchant (merchant_id, company_name, business_type, " +
                        "registration_number, email, phone, fax, address, " +
                        "credit_limit, outstanding_balance, account_status, is_Active) " +
                        "VALUES (?,?,?,?,?,?,?,?,1000.00,0.00,'normal',1)",
                merchantId,
                app.getCompanyName(),
                app.getBusinessType(),
                app.getRegistrationNo(),
                app.getEmail(),
                app.getPhone(),
                app.getFax(),
                app.getAddress()
        );

        return merchantId;
    }

    // Gets count of pending applications for dashboard
    public int getPendingCount() throws Exception {
        ResultSet rs = db.query(
                "SELECT COUNT(*) as count FROM commercial_applications WHERE status = 'pending'");
        if (rs.next()) return rs.getInt("count");
        return 0;
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
