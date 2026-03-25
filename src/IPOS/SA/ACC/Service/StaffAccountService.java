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
     * Create a new staff account
     */
    public boolean createStaff(Staff staff) throws Exception {
        // Check if staff ID already exists
        if (staffExists(staff.getStaffId())) {
            return false;
        }

        // Check if username already exists
        if (usernameExists(staff.getUsername())) {
            return false;
        }

        // Default password is the username (or you can set a default)
        String defaultPassword = staff.getUsername();

        int rowsAffected = db.update(
                "INSERT INTO Staff_Details (staff_id, username, first_name, surname, email, phone, " +
                        "address, role, password, is_active) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 1)",
                staff.getStaffId(),
                staff.getUsername(),
                staff.getFirstName(),
                staff.getSurName(),
                staff.getEmail(),
                staff.getPhone(),
                staff.getAddress(),
                staff.getRole(),
                defaultPassword
        );

        return rowsAffected > 0;
    }

    /**
     * Load a staff account by ID
     */
    public Staff loadStaff(String staffId) throws Exception {
        ResultSet rs = db.query(
                "SELECT * FROM Staff_Details WHERE staff_id = ?",
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
                "SELECT * FROM Staff_Details WHERE username = ?",
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
                "UPDATE Staff_Details SET username=?, first_name=?, surname=?, email=?, phone=?, " +
                        "address=?, role=? WHERE staff_id=?",
                staff.getUsername(),
                staff.getFirstName(),
                staff.getSurName(),
                staff.getEmail(),
                staff.getPhone(),
                staff.getAddress(),
                staff.getRole(),
                staff.getStaffId()
        );

        return rowsAffected > 0;
    }

    /**
     * Update staff status (active/inactive)
     */
    public boolean updateStaffStatus(String staffId, boolean isActive) throws Exception {
        int activeFlag = isActive ? 1 : 0;
        int rowsAffected = db.update(
                "UPDATE Staff_Details SET is_active = ? WHERE staff_id = ?",
                activeFlag, staffId
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
                "DELETE FROM Staff_Details WHERE staff_id = ?",
                staffId
        );

        return rowsAffected > 0;
    }

    /**
     * Check if staff ID exists
     */
    public boolean staffExists(String staffId) throws Exception {
        ResultSet rs = db.query(
                "SELECT staff_id FROM Staff_Details WHERE staff_id = ?",
                staffId
        );
        return rs.next();
    }

    /**
     * Check if username exists
     */
    public boolean usernameExists(String username) throws Exception {
        ResultSet rs = db.query(
                "SELECT username FROM Staff_Details WHERE username = ?",
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
                "SELECT * FROM Staff_Details WHERE is_active = 1 ORDER BY first_name, surname"
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
                "SELECT * FROM Staff_Details ORDER BY first_name, surname"
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
                "SELECT * FROM Staff_Details WHERE first_name LIKE ? OR surname LIKE ? ORDER BY first_name",
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
                "UPDATE Staff_Details SET password = ? WHERE staff_id = ?",
                newPassword, staffId
        );
        return rowsAffected > 0;
    }

    /**
     * Extract Staff from ResultSet
     */
    private Staff extractStaffFromResultSet(ResultSet rs) throws Exception {
        Staff staff = new Staff();
        staff.setStaffId(rs.getString("staff_id"));
        staff.setUsername(rs.getString("username"));
        staff.setFirstName(rs.getString("first_name"));
        staff.setSurName(rs.getString("surname"));
        staff.setEmail(rs.getString("email"));
        staff.setPhone(rs.getString("phone"));
        staff.setAddress(rs.getString("address"));
        staff.setRole(rs.getString("role"));
        staff.setActive(rs.getInt("is_active") == 1);
        return staff;
    }
}
