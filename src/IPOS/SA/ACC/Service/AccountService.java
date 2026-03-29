package IPOS.SA.ACC.Service;

import IPOS.SA.ACC.Model.MerchantAccount;
import IPOS.SA.DB.DBConnection;
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
                        "registration_date, is_Active, last_payment_date) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
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
                account.getDiscountType(),
                account.getFixedDiscountRate(),
                account.getFlexibleDiscountRate(),
                account.getRegistrationDate(),
                account.isActive() ? 1 : 0,
                account.getLastPaymentDate()
        );

        return rowsAffected > 0;
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
     * @param status the new account status
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
}