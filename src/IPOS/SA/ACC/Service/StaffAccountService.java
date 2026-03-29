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
        // Checks if staff ID already exists (using user_id)
        if (staffExists(staff.getStaffId())) {
            return false;
        }

        // Checks if username already exists
        if (usernameExists(staff.getUsername())) {
            return false;
        }

        // Creates a default password for all staff when creating account
        String defaultPassword = staff.getUsername() + "123";

        int rowsAffected = db.update(
                "INSERT INTO UserLogin (username, password_hash, first_Name, sur_Name, email, role, is_Active) " +
                        "VALUES (?, ?, ?, ?, ?, ?, 1)",
                staff.getUsername(),
                defaultPassword,
                staff.getFirstName(),
                staff.getSurName(),
                staff.getEmail(),
                staff.getRole()
        );

        return rowsAffected > 0;
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
                "SELECT user_id, username, first_Name, sur_Name, email, role, is_Active " +
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
                "UPDATE UserLogin SET username=?, first_Name=?, sur_Name=?, email=?, role=? " +
                        "WHERE user_id=?",
                staff.getUsername(),
                staff.getFirstName(),
                staff.getSurName(),
                staff.getEmail(),
                staff.getRole(),
                Integer.parseInt(staff.getStaffId())  // Convert staffId (String) to int for user_id
        );

        return rowsAffected > 0;
    }

    /**
     * Updates staff status to active or inactive status.
     *
     * @param staffId the staff ID
     * @param isActive true for active, false for inactive
     * @return true if update was successful
     * @throws Exception if a database error occurs
     */
    public boolean updateStaffStatus(String staffId, boolean isActive) throws Exception {
        int activeFlag = isActive ? 1 : 0;
        int rowsAffected = db.update(
                "UPDATE UserLogin SET is_Active = ? WHERE user_id = ?",
                activeFlag, Integer.parseInt(staffId)
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
     * Checks to see if a staff ID exists.
     *
     * @param staffId the staff ID
     * @return true if exists
     * @throws Exception if a database error occurs
     */
    public boolean staffExists(String staffId) throws Exception {
        ResultSet rs = db.query(
                "SELECT user_id FROM UserLogin WHERE user_id = ?",
                Integer.parseInt(staffId)
        );
        return rs.next();
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
        return staff;
    }
}