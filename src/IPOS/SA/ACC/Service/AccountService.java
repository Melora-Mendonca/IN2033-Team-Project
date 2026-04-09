package IPOS.SA.ACC.Service;

import IPOS.SA.ACC.Model.MerchantAccount;
import IPOS.SA.Comms.PUClient.IPOSPUEmailClient;
import IPOS.SA.DB.DBConnection;

import java.io.IOException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class responsible for managing merchant accounts.
 * Provides CRUD operations and additional account-related business logic.
 */
public class AccountService {
    private DBConnection db;

    /**
     * Default constructor initializes the service with a database connection.
     */
    public AccountService() {
        this.db = new DBConnection();
    }

    /**
     * Adds a new merchant account to the system.
     *
     * @param account the merchant account to be added
     * @return true if account was successfully created, false if it already exists
     * @throws Exception if a database error occurs
     */
    public boolean addAccount(MerchantAccount account) throws Exception {
        if (usernameExists(account.getUsername())) {
            return false;
        }

        // Generate plain text password for email
        String originalPassword = account.getUsername() + "123";

        // Hash the default password to match authentication
        String defaultPassword = hashPassword(account.getUsername() + "123");

        // Checks if account already exists
        ResultSet checkRs = db.query(
                "SELECT merchant_id FROM Merchant WHERE merchant_id = ?",
                account.getMerchantId()
        );

        if (checkRs.next()) {
            return false; // Account already exists
        }

        // Inserts new merchant account if account doesnt exist
        int rowsAffected = db.update(
                "INSERT INTO Merchant (merchant_id, company_name, business_type, registration_number, " +
                        "email, phone, fax, address, credit_limit, outstanding_balance, " +
                        "account_status, discount_type, fixed_discount_rate, flexible_discount_rate, " +
                        "registration_date, is_Active, last_payment_date, username, password_hash) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                account.getMerchantId(),
                account.getBusinessName(),
                account.getBusinessType(),
                account.getRegistrationNumber(),
                account.getEmail(),
                account.getPhone(),
                account.getFax(),
                account.getAddress(),
                account.getCreditLimit(),
                account.getOutstandingBalance(),
                account.getAccountStatus(),
                account.getDiscountType(),        // ← "fixed"
                account.getFixedDiscountRate(),   // ← discount value from form
                account.getFlexibleDiscountRate(),
                account.getRegistrationDate(),
                account.isActive() ? 1 : 0,
                account.getLastPaymentDate(),
                account.getUsername(),
                defaultPassword
        );

        // Calls the emailService API to send an email to the staff with credentials
        if (rowsAffected > 0) {
            sendMerchantEmail(account, originalPassword);
            return true;
        }

        return false;
    }

    // Add this method to send merchant email
    private void sendMerchantEmail(MerchantAccount merchant, String originalPassword) {
        String emailContent = buildMerchantEmailContent(merchant, originalPassword);

        try {
            // Using your email service API (IPOS-PU)
            boolean emailSent = IPOSPUEmailClient.produceEmail(
                    merchant.getEmail(),
                    emailContent,
                    merchant.getUsername(),
                    "IPOS-SA",
                    "MerchantManagement"
            );

            if (emailSent) {
                System.out.println("Merchant credentials sent to " + merchant.getEmail());
            } else {
                System.err.println("Failed to send merchant email to " + merchant.getEmail());
            }
        } catch (IOException e) {
            System.err.println("Email service error for merchant " + merchant.getEmail() + ": " + e.getMessage());
        }
    }

    // Add this method to build the email content
    private String buildMerchantEmailContent(MerchantAccount merchant, String originalPassword) {
        return "Dear " + merchant.getBusinessName() + ",\n\n"
                + "Your merchant account has been created in IPOS system.\n\n"
                + "Login Details:\n"
                + "Username: " + merchant.getUsername() + "\n"
                + "Password: " + originalPassword + "\n\n"
                + "Company Information:\n"
                + "Registration Number: " + merchant.getRegistrationNumber() + "\n"
                + "Email: " + merchant.getEmail() + "\n"
                + "Phone: " + merchant.getPhone() + "\n"
                + "Address: " + merchant.getAddress() + "\n\n"
                + "Please login and change your password after first access.\n\n"
                + "Regards,\nIPOS System Administrator";
    }

    // Add this helper method
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

    /**
     * Retrieves a merchant account by its ID.
     *
     * @param merchantId the unique merchant identifier
     * @return MerchantAccount object if found, otherwise null
     * @throws Exception if a database error occurs
     */
    public MerchantAccount getAccount(String merchantId) throws Exception {
        ResultSet rs = db.query(
                "SELECT merchant_id, company_name, business_type, registration_number, email, phone, fax, " +
                        "address, credit_limit, outstanding_balance, account_status, " +
                        "discount_type, fixed_discount_rate, flexible_discount_rate, registration_date, " +
                        "is_Active, last_payment_date FROM Merchant WHERE merchant_id = ?",
                merchantId
        );

        if (rs.next()) {
            return new MerchantAccount(
                    rs.getString("merchant_id"),
                    rs.getString("company_name"),
                    rs.getString("business_type"),
                    rs.getString("registration_number"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getString("fax"),
                    rs.getString("address"),
                    rs.getDouble("credit_limit"),
                    rs.getDouble("outstanding_balance"),
                    rs.getString("account_status"),
                    rs.getString("discount_type"),
                    rs.getDouble("fixed_discount_rate"),
                    rs.getDouble("flexible_discount_rate"),
                    rs.getDate("registration_date"),
                    rs.getInt("is_Active") == 1,
                    rs.getDate("last_payment_date")
            );
        }
        return null;
    }

    /**
     * Updates details of a merchant account when edited.
     *
     * @param account the account with updated information
     * @return true if update was successful
     * @throws Exception if a database error occurs
     */
    public boolean updateAccount(MerchantAccount account) throws Exception {
        int rowsAffected = db.update(
                "UPDATE Merchant SET company_name=?, business_type=?, registration_number=?, " +
                        "email=?, phone=?, fax=?, address=?, credit_limit=?, " +
                        "fixed_discount_rate=? WHERE merchant_id=?",
                account.getBusinessName(),
                account.getBusinessType(),
                account.getRegistrationNumber(),
                account.getEmail(),
                account.getPhone(),
                account.getFax(),
                account.getAddress(),
                account.getCreditLimit(),
                account.getDiscountPercentage(),
                account.getMerchantId()
        );
        return rowsAffected > 0;
    }

    /**
     * Updates the status of a merchant account when suspended or reinstated.
     *
     * @param merchantId the merchant ID
     * @param status     the new account status
     * @return true if update was successful
     * @throws Exception if a database error occurs
     */
    public boolean updateAccountStatus(String merchantId, String status) throws Exception {
        int rowsAffected = db.update(
                "UPDATE Merchant SET account_status=? WHERE merchant_id=?",
                status.toLowerCase(),
                merchantId
        );

        return rowsAffected > 0;
    }

    /**
     * Deletes the discount plan by resetting it to default values.
     *
     * @param merchantId the merchant ID
     * @return true if update was successful
     * @throws Exception if a database error occurs
     */
    public boolean deleteDiscountPlan(String merchantId) throws Exception {
        int rowsAffected = db.update(
                "UPDATE Merchant SET fixed_discount_rate = 0, discount_type = 'fixed' WHERE merchant_id = ?",
                merchantId
        );

        return rowsAffected > 0;
    }

    /**
     * Deletes a merchant account.
     *
     * @param merchantId the merchant ID
     * @return true if update was successful
     * @throws Exception if a database error occurs
     */
    public boolean deleteAccount(String merchantId) throws Exception {
        int rowsAffected = db.update(
                "DELETE FROM Merchant WHERE merchant_id = ?",
                merchantId
        );

        return rowsAffected > 0;
    }

    /**
     * Restores an account from default status if no outstanding balance exists.
     *
     * @param merchantId the merchant ID
     * @return true if restored successfully, false if balance is still outstanding
     * @throws Exception if a database error occurs
     */
    public boolean restoreFromDefault(String merchantId) throws Exception {
        // Check current balance
        ResultSet rs = db.query(
                "SELECT outstanding_balance FROM Merchant WHERE merchant_id = ?",
                merchantId
        );

        if (rs.next()) {
            double balance = rs.getDouble("outstanding_balance");
            if (balance > 0) {
                return false; // Cannot restore - outstanding balance
            }
        }

        // Restore to normal
        int rowsAffected = db.update(
                "UPDATE Merchant SET account_status = 'normal' WHERE merchant_id = ?",
                merchantId
        );

        return rowsAffected > 0;
    }

    /**
     * Retrieves the current outstanding balance of an account.
     *
     * @param merchantId the merchant ID
     * @return account balance, or 0.0 if not found
     * @throws Exception if a database error occurs
     */
    public double getAccountBalance(String merchantId) throws Exception {
        ResultSet rs = db.query(
                "SELECT outstanding_balance FROM Merchant WHERE merchant_id = ?",
                merchantId
        );

        if (rs.next()) {
            return rs.getDouble("outstanding_balance");
        }
        return 0.0;
    }

    /**
     * Retrieves all merchant accounts ordered by company name.
     *
     * @return list of merchant accounts
     * @throws Exception if a database error occurs
     */
    public List<MerchantAccount> getAllAccounts() throws Exception {
        List<MerchantAccount> accounts = new ArrayList<>();
        ResultSet rs = db.query(
                "SELECT merchant_id, company_name, business_type, registration_number, email, phone, fax, " +
                        "address, credit_limit, outstanding_balance, account_status, " +
                        "discount_type, fixed_discount_rate, flexible_discount_rate, registration_date, " +
                        "is_Active, last_payment_date FROM Merchant ORDER BY company_name"
        );

        while (rs.next()) {
            accounts.add(new MerchantAccount(
                    rs.getString("merchant_id"),
                    rs.getString("company_name"),
                    rs.getString("business_type"),
                    rs.getString("registration_number"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getString("fax"),
                    rs.getString("address"),
                    rs.getDouble("credit_limit"),
                    rs.getDouble("outstanding_balance"),
                    rs.getString("account_status"),
                    rs.getString("discount_type"),
                    rs.getDouble("fixed_discount_rate"),
                    rs.getDouble("flexible_discount_rate"),
                    rs.getDate("registration_date"),
                    rs.getInt("is_Active") == 1,
                    rs.getDate("last_payment_date")
            ));
        }
        return accounts;
    }

    public void addToBalance(String merchantId, double finalTotal) {
    }

    public boolean canMerchantPlaceOrder(MerchantAccount account, double grossTotal) {
        return true;
    }

    /**
     * Checks to see if a username exists.
     *
     * @param username the username
     * @return true if exists
     * @throws Exception if a database error occurs
     */
    public boolean usernameExists(String username) throws Exception {
        ResultSet rs = db.query(
                "SELECT username FROM merchant WHERE username = ?",
                username
        );
        return rs.next();
    }

    public double getCreditLimit(String merchantId) throws Exception {
        ResultSet rs = db.query("SELECT credit_limit FROM Merchant WHERE merchant_id = ?", merchantId);
        if (rs.next()) {
            return rs.getDouble("credit_limit");
        }
        return 0.0;
    }

    public String getAccountStatus(String merchantId) throws Exception {
        ResultSet rs = db.query("SELECT account_status FROM Merchant WHERE merchant_id = ?", merchantId);
        if (rs.next()) {
            return rs.getString("account_status");
        }
        return "normal";
    }

    public double getDiscountRate(String merchantId) {
        try {
            ResultSet rs = db.query(
                    "SELECT discount_type, fixed_discount_rate, flexible_discount_rate " +
                            "FROM Merchant WHERE merchant_id = ?", merchantId);

            if (rs.next()) {
                String discountType = rs.getString("discount_type");
                if ("fixed".equalsIgnoreCase(discountType)) {
                    return rs.getDouble("fixed_discount_rate");
                } else if ("flexible".equalsIgnoreCase(discountType)) {
                    return rs.getDouble("flexible_discount_rate");
                }
            }
        } catch (Exception e) {
            System.err.println("Error getting discount rate: " + e.getMessage());
        }
        return 0.0;
    }

    public void AutoUpdateStatus() throws Exception {
        ResultSet rs = db.query(
                "SELECT DISTINCT m.merchant_id, m.company_name, m.account_status, " +
                        "m.outstanding_balance, MAX(DATEDIFF(CURDATE(), i.due_date)) as max_days_overdue " +
                        "FROM merchant m " +
                        "LEFT JOIN `order` o ON m.merchant_id = o.merchant_id " +
                        "LEFT JOIN invoice i ON o.order_id = i.order_id " +
                        "AND i.status IN ('unpaid', 'partial', 'overdue') " +
                        "GROUP BY m.merchant_id, m.company_name, m.account_status, m.outstanding_balance");

        while (rs.next()){
            String merchantId = rs.getString("Merchant_id");
            String currentStatus = rs.getString("account_Status");
            double balance = rs.getDouble("outstanding_balance");
            int daysOverdue = rs.getInt("max_days_overdue");

            if (currentStatus == null) continue;
            String newStatus = null;

            if (balance <= 0 && currentStatus.equals("suspended")){
                newStatus = "normal";
            } else if (daysOverdue > 30 && (currentStatus.equals("normal") || currentStatus.equals("suspended"))){
                newStatus = "in_default";
            } else if (daysOverdue >= 15 && daysOverdue <= 30 && currentStatus.equals("normal")){
                newStatus = "suspended";
            }

            if (newStatus != null) {
                db.update("UPDATE merchant SET account_status = ? WHERE merchant_id = ?", newStatus, merchantId);
            }
        }
    }

}