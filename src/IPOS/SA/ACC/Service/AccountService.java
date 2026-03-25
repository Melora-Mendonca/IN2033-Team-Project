package IPOS.SA.ACC.Service;

import IPOS.SA.ACC.Model.MerchantAccount;
import IPOS.SA.DB.DBConnection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class AccountService {
    private DBConnection db;

    public AccountService() {
        this.db = new DBConnection();
    }

    /**
     * Add a new merchant account (CREATE)
     * Uses the exact INSERT query from your createAccount() method
     */
    public boolean addAccount(MerchantAccount account) throws Exception {
        // Check if account already exists (using your exact query)
        ResultSet checkRs = db.query(
                "SELECT ipos_account_no FROM Merchant_Details WHERE ipos_account_no = ?",
                account.getMerchantId()
        );

        if (checkRs.next()) {
            return false; // Account already exists
        }

        // Using your exact INSERT query from createAccount()
        int rowsAffected = db.update(
                "INSERT INTO Merchant_Details (ipos_account_no, company_name, email, phone, address, credit_limit, fixed_rate, discount_type, account_status) VALUES (?,?,?,?,?,?,?,'fixed','normal')",
                account.getMerchantId(),
                account.getBusinessName(),
                account.getEmail(),
                account.getPhone(),
                account.getAddress(),
                account.getCreditLimit(),
                account.getDiscountPercentage()
        );

        return rowsAffected > 0;
    }

    /**
     * Get a merchant account by ID (READ)
     * Uses the exact SELECT query from your loadAccount() method
     */
    public MerchantAccount getAccount(String merchantId) throws Exception {
        ResultSet rs = db.query(
                "SELECT * FROM Merchant_Details WHERE ipos_account_no = ?",
                merchantId
        );

        if (rs.next()) {
            return new MerchantAccount(
                    rs.getString("ipos_account_no"),
                    rs.getString("company_name"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getString("address"),
                    rs.getDouble("credit_limit"),
                    rs.getDouble("current_balance"),
                    rs.getString("account_status"),
                    rs.getDouble("fixed_rate")
            );
        }
        return null;
    }

    /**
     * Check if account exists
     * Uses the exact check query from your createAccount() method
     */
    public boolean accountExists(String merchantId) throws Exception {
        ResultSet rs = db.query(
                "SELECT ipos_account_no FROM Merchant_Details WHERE ipos_account_no = ?",
                merchantId
        );
        return rs.next();
    }

    /**
     * Update account (UPDATE)
     * Uses the exact UPDATE query from your updateAccount() method
     */
    public boolean updateAccount(MerchantAccount account) throws Exception {
        int rowsAffected = db.update(
                "UPDATE Merchant_Details SET company_name=?, email=?, phone=?, address=?, credit_limit=?, fixed_rate=? WHERE ipos_account_no=?",
                account.getBusinessName(),
                account.getEmail(),
                account.getPhone(),
                account.getAddress(),
                account.getCreditLimit(),
                account.getDiscountPercentage(),
                account.getMerchantId()
        );

        return rowsAffected > 0;
    }

    /**
     * Update account status (suspend/reinstate)
     * Uses the exact UPDATE query from your updateStatus() method
     */
    public boolean updateAccountStatus(String merchantId, String status) throws Exception {
        int rowsAffected = db.update(
                "UPDATE Merchant_Details SET account_status=?, status_changed_date=CURRENT_DATE() WHERE ipos_account_no=?",
                status.toLowerCase(),
                merchantId
        );

        return rowsAffected > 0;
    }

    /**
     * Delete discount plan
     * Uses the exact SQL from your deleteDiscountPlan() method
     */
    public boolean deleteDiscountPlan(String merchantId) throws Exception {
        int rowsAffected = db.update(
                "UPDATE Merchant_Details SET fixed_rate = 0, discount_type = 'fixed' WHERE ipos_account_no = ?",
                merchantId
        );

        return rowsAffected > 0;
    }

    /**
     * Delete account (deactivate)
     * Uses the exact SQL from your deleteAccount() method
     */
    public boolean deleteAccount(String merchantId) throws Exception {
        int rowsAffected = db.update(
                "UPDATE Merchant_Details SET account_status = 'suspended', current_balance = 0 WHERE ipos_account_no = ?",
                merchantId
        );

        return rowsAffected > 0;
    }

    /**
     * Restore account from default
     * Uses the exact queries from your restoreFromDefault() method
     */
    public boolean restoreFromDefault(String merchantId) throws Exception {
        // Check current balance (using your exact query)
        ResultSet rs = db.query(
                "SELECT current_balance FROM Merchant_Details WHERE ipos_account_no = ?",
                merchantId
        );

        if (rs.next()) {
            double balance = rs.getDouble("current_balance");
            if (balance > 0) {
                return false; // Cannot restore - outstanding balance
            }
        }

        // Restore to normal (using your exact query)
        int rowsAffected = db.update(
                "UPDATE Merchant_Details SET account_status = 'normal', status_changed_date = CURRENT_DATE() WHERE ipos_account_no = ?",
                merchantId
        );

        return rowsAffected > 0;
    }

    /**
     * Get account balance
     */
    public double getAccountBalance(String merchantId) throws Exception {
        ResultSet rs = db.query(
                "SELECT current_balance FROM Merchant_Details WHERE ipos_account_no = ?",
                merchantId
        );

        if (rs.next()) {
            return rs.getDouble("current_balance");
        }
        return 0.0;
    }

    /**
     * Get all active accounts
     */
    public List<MerchantAccount> getAllAccounts() throws Exception {
        List<MerchantAccount> accounts = new ArrayList<>();
        ResultSet rs = db.query(
                "SELECT * FROM Merchant_Details ORDER BY company_name"
        );

        while (rs.next()) {
            accounts.add(new MerchantAccount(
                    rs.getString("ipos_account_no"),
                    rs.getString("company_name"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getString("address"),
                    rs.getDouble("credit_limit"),
                    rs.getDouble("current_balance"),
                    rs.getString("account_status"),
                    rs.getDouble("fixed_rate")
            ));
        }
        return accounts;
    }
}