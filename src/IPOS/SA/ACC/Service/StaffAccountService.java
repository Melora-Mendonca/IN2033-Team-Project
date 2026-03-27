package IPOS.SA.ACC.Service;

import IPOS.SA.ACC.Model.Staff;
import IPOS.SA.DB.DBConnection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class StaffAccountService {
    private DBConnection db;

    public StaffAccountService() {
        this.db = new DBConnection();
    }

    /**
     * Create a new staff account (using UserLogin table)
     */
    public boolean createStaff(Staff staff) throws Exception {
        // Check if staff ID already exists (using user_id)
        if (staffExists(staff.getStaffId())) {
            return false;
        }

        // Check if username already exists
        if (usernameExists(staff.getUsername())) {
            return false;
        }

        // Default password is the username + "123" (matching your login)
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
     * Load a staff account by ID (user_id from UserLogin)
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
     * Load a staff account by username
     */
    public Staff loadStaffByUsername(String username) throws Exception {
        ResultSet rs = db.query(
                "SELECT user_id, username, first_Name, sur_Name, email, role, is_Active " +
                        "FROM UserLogin WHERE username = ?",
                username
        );

        if (rs.next()) {
            return extractStaffFromResultSet(rs);
        }
        return null;
    }

    /**
     * Update an existing staff account
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
     * Update staff status (active/inactive)
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
     * Deactivate staff account (soft delete)
     */
    public boolean deactivateStaff(String staffId) throws Exception {
        return updateStaffStatus(staffId, false);
    }

    /**
     * Reactivate staff account
     */
    public boolean reactivateStaff(String staffId) throws Exception {
        return updateStaffStatus(staffId, true);
    }

    /**
     * Delete staff account (hard delete - use carefully)
     */
    public boolean deleteStaff(String staffId) throws Exception {
        int rowsAffected = db.update(
                "DELETE FROM UserLogin WHERE user_id = ?",
                Integer.parseInt(staffId)
        );

        return rowsAffected > 0;
    }

    /**
     * Check if staff ID exists (user_id)
     */
    public boolean staffExists(String staffId) throws Exception {
        ResultSet rs = db.query(
                "SELECT user_id FROM UserLogin WHERE user_id = ?",
                Integer.parseInt(staffId)
        );
        return rs.next();
    }

    /**
     * Check if username exists
     */
    public boolean usernameExists(String username) throws Exception {
        ResultSet rs = db.query(
                "SELECT username FROM UserLogin WHERE username = ?",
                username
        );
        return rs.next();
    }

    /**
     * Get all active staff members
     */
    public List<Staff> getAllActiveStaff() throws Exception {
        List<Staff> staffList = new ArrayList<>();
        ResultSet rs = db.query(
                "SELECT user_id, username, first_Name, sur_Name, email, role, is_Active " +
                        "FROM UserLogin WHERE is_Active = 1 ORDER BY first_Name, sur_Name"
        );

        while (rs.next()) {
            staffList.add(extractStaffFromResultSet(rs));
        }
        return staffList;
    }

    /**
     * Get all staff members (including inactive)
     */
    public List<Staff> getAllStaff() throws Exception {
        List<Staff> staffList = new ArrayList<>();
        ResultSet rs = db.query(
                "SELECT user_id, username, first_Name, sur_Name, email, role, is_Active " +
                        "FROM UserLogin ORDER BY first_Name, sur_Name"
        );

        while (rs.next()) {
            staffList.add(extractStaffFromResultSet(rs));
        }
        return staffList;
    }

    /**
     * Search staff by name
     */
    public List<Staff> searchStaffByName(String searchTerm) throws Exception {
        List<Staff> staffList = new ArrayList<>();
        ResultSet rs = db.query(
                "SELECT user_id, username, first_Name, sur_Name, email, role, is_Active " +
                        "FROM UserLogin WHERE first_Name LIKE ? OR sur_Name LIKE ? ORDER BY first_Name",
                "%" + searchTerm + "%", "%" + searchTerm + "%"
        );

        while (rs.next()) {
            staffList.add(extractStaffFromResultSet(rs));
        }
        return staffList;
    }

    /**
     * Reset staff password
     */
    public boolean resetPassword(String staffId, String newPassword) throws Exception {
        int rowsAffected = db.update(
                "UPDATE UserLogin SET password_hash = ? WHERE user_id = ?",
                newPassword, Integer.parseInt(staffId)
        );
        return rowsAffected > 0;
    }

    /**
     * Extract Staff from ResultSet
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