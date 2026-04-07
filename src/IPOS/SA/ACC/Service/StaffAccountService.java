package IPOS.SA.ACC.Service;

import IPOS.SA.ACC.Model.Staff;
import IPOS.SA.DB.DBConnection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Service responsible for managing staff accounts,
 * including creation, updates, deletion, and retrieval.
 */
public class StaffAccountService {
    private DBConnection db;

    /**
     * Default constructor initialises the service with a database connection.
     */
    public StaffAccountService() {
        this.db = new DBConnection();
    }

    /**
     * Creates a new staff account in the system.
     *
     * @param staff the staff object to be created
     * @return true if the account was created successfully, false otherwise
     * @throws Exception if a database error occurs
     */
    public boolean createStaff(Staff staff) throws Exception {
        // Only check username uniqueness
        if (usernameExists(staff.getUsername())) {
            return false;
        }

        // Hash the default password to match authentication
        String defaultPassword = hashPassword(staff.getUsername() + "123");

        int rowsAffected = db.update(
                "INSERT INTO UserLogin (username, password_hash, first_Name, sur_Name, email, role, is_Active, phone, address) " +
                        "VALUES (?, ?, ?, ?, ?, ?, 1, ?, ?)",
                staff.getUsername(),
                defaultPassword,
                staff.getFirstName(),
                staff.getSurName(),
                staff.getEmail(),
                staff.getRole(),
                staff.getPhone(),
                staff.getAddress()
        );

        return rowsAffected > 0;
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
     * Retrieves a staff member by ID.
     *
     * @param staffId the ID of the staff member
     * @return Staff object if found, otherwise null
     * @throws Exception if a database error occurs
     */
    public Staff loadStaff(String staffId) throws Exception {
        ResultSet rs = db.query(
                "SELECT user_id, username, first_Name, sur_Name, email, role, is_Active, phone, address " +
                        "FROM UserLogin WHERE user_id = ?",
                staffId
        );

        if (rs.next()) {
            return extractStaffFromResultSet(rs);
        }
        return null;
    }

    /**
     * Updates staff details when modified by the administrator.
     *
     * @param staff the staff object containing updated data
     * @return true if update was successful
     * @throws Exception if a database error occurs
     */
    public boolean updateStaff(Staff staff) throws Exception {
        int rowsAffected = db.update(
                "UPDATE UserLogin SET username=?, first_Name=?, sur_Name=?, email=?, role=?, phone=?, address=?" +
                        "WHERE user_id=?",
                staff.getUsername(),
                staff.getFirstName(),
                staff.getSurName(),
                staff.getEmail(),
                staff.getRole(),
                staff.getPhone(),
                staff.getAddress(),
                Integer.parseInt(staff.getStaffId())  // Convert staffId (String) to int for user_id
        );

        return rowsAffected > 0;
    }


    /**
     * Permanently deletes a staff account.
     *
     * @param staffId the staff ID
     * @return true if deletion was successful
     * @throws Exception if a database error occurs
     */
    public boolean deleteStaff(String staffId) throws Exception {
        int rowsAffected = db.update(
                "DELETE FROM UserLogin WHERE user_id = ?",
                Integer.parseInt(staffId)
        );

        return rowsAffected > 0;
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
                "SELECT username FROM UserLogin WHERE username = ?",
                username
        );
        return rs.next();
    }

    /**
     * Resets a staff member's password.
     *
     * @param staffId the staff ID
     * @param newPassword the new password
     * @return true if successful
     * @throws Exception if a database error occurs
     */
    public boolean resetPassword(String staffId, String newPassword) throws Exception {
        int rowsAffected = db.update(
                "UPDATE UserLogin SET password_hash = ? WHERE user_id = ?",
                newPassword, Integer.parseInt(staffId)
        );
        return rowsAffected > 0;
    }

    /**
     * Converts a ResultSet row into a Staff object.
     *
     * @param rs the result set
     * @return Staff object
     * @throws Exception if a database error occurs
     */
    private Staff extractStaffFromResultSet(ResultSet rs) throws Exception {
        Staff staff = new Staff();
        staff.setStaffId(String.valueOf(rs.getInt("user_id")));  // Convert int to String for staffId
        staff.setUsername(rs.getString("username"));
        staff.setFirstName(rs.getString("first_Name"));
        staff.setSurName(rs.getString("sur_Name"));
        staff.setEmail(rs.getString("email"));
        staff.setRole(rs.getString("role"));
        staff.setActive(rs.getInt("is_Active") == 1);
        staff.setPhone(rs.getString("phone"));
        staff.setAddress(rs.getString("address"));
        return staff;
    }
}