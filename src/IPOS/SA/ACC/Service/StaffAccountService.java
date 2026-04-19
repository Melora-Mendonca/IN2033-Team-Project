package IPOS.SA.ACC.Service;

import IPOS.SA.ACC.Model.Staff;
import IPOS.SA.Comms.PUClient.IPOSPUEmailClient;
import IPOS.SA.DB.DBConnection;

import java.io.IOException;
import java.sql.ResultSet;

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
    public boolean createStaff(Staff staff, String plainPassword) throws Exception {
        if (usernameExists(staff.getUsername())) {
            return false;
        }

        // Hashes the password when stored in the database
        String hashedPassword = hashPassword(plainPassword);

        // checks if an account for that staff already exists
        ResultSet checkRs = db.query(
                "SELECT user_id FROM userlogin WHERE user_id = ?",
                staff.getStaffId()
        );
        if (checkRs.next()) return false;

        int rowsAffected = db.update(
                "INSERT INTO userlogin (username, password_hash, first_Name, sur_Name, email, role, is_Active, phone, address) " +
                        "VALUES (?, ?, ?, ?, ?, ?, 1, ?, ?)",
                staff.getUsername(),
                hashedPassword,
                staff.getFirstName(),
                staff.getSurName(),
                staff.getEmail(),
                staff.getRole(),
                staff.getPhone(),
                staff.getAddress()
        );

        // Sends an email when the staff account is created
        if (rowsAffected > 0) {
            sendStaffEmail(staff, plainPassword);
            return true;
        }
        return false;
    }

    /**
     * Sends an email to the newly created staff via the IPOS-PU email service.
     * Email contains the staff's login credentials.
     *
     * @param staff the staff account that was created
     * @param plainTextPassword the plain text default password to include in the email
     */
    private void sendStaffEmail(Staff staff, String plainTextPassword) {
        String emailContent = buildStaffAccountEmail(staff, plainTextPassword);

        try {
            // Call IPOS-PU email API
            boolean emailSent = IPOSPUEmailClient.produceEmail(
                    staff.getEmail(),
                    emailContent,
                    staff.getUsername(),
                    "IPOS-SA",
                    "StaffManagement"
            );

            if (emailSent) {
                System.out.println("Login details sent to " + staff.getEmail());
            } else {
                System.err.println("Failed to send email to " + staff.getEmail());
            }
        } catch (IOException e) {
            System.err.println("Email service error for " + staff.getEmail() + ": " + e.getMessage());
        }
    }

    /**
     * Builds the email body content for the staff welcome email.
     * Includes login credentials and company information.
     *
     * @param staff the staff account
     * @param plainTextPassword the plain text default password
     * @return the formatted email body as a string
     */
    private String buildStaffAccountEmail(Staff staff, String plainTextPassword) {
        return "Dear " + staff.getFirstName() + " " + staff.getSurName() + ",\n\n"
                + "Your staff account has been created in IPOS system.\n\n"
                + "Login Details:\n"
                + "Username: " + staff.getUsername() + "\n"
                + "Password: " + plainTextPassword + "\n\n"
                + "Role: " + staff.getRole() + "\n\n"
                + "Please login and change your password after first access.\n\n"
                + "Regards,\nIPOS System Administrator";
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
                        "FROM userlogin WHERE user_id = ?",
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
                "UPDATE userlogin SET username=?, first_Name=?, sur_Name=?, email=?, role=?, phone=?, address=?" +
                        "WHERE user_id=?",
                staff.getUsername(),
                staff.getFirstName(),
                staff.getSurName(),
                staff.getEmail(),
                staff.getRole(),
                staff.getPhone(),
                staff.getAddress(),
                Integer.parseInt(staff.getStaffId())
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
                "DELETE FROM userlogin WHERE user_id = ?",
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
                "SELECT username FROM userlogin WHERE username = ?",
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
                "UPDATE userlogin SET password_hash = ? WHERE user_id = ?",
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