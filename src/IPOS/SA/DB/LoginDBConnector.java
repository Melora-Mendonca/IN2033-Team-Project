package IPOS.SA.DB;

import IPOS.SA.ACC.Model.User;

import java.security.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
/**
 * Database connector for authentication and stock warning queries in IPOS-SA.
 * Used by AuthenticationService to verify staff login credentials and
 * retrieve low stock warnings shown on admin and director login.
 */
public class LoginDBConnector {

    /**
     * Authenticates a user against the userlogin table.
     * Hashes the provided password using SHA-256 and compares it
     * against the stored password hash. Also checks the user's role
     * matches the selected role and that the account is active.
     *
     * @param username the username entered on the login form
     * @param password the plain text password entered on the login form
     * @param role the role selected on the login form
     * @return a User object if authentication succeeds, null if credentials are invalid
     */
    public User authenticate(String username, String password, String role) {
        try {
            // creates a connection with the database
            Connection conn = new DBConnection().getConn();
            System.out.println("Connection: " + (conn != null ? "OK" : "NULL"));

            // retrieves the login details of the current user for authentication
            String sql = "SELECT * FROM userlogin WHERE username = ? AND password_hash = ? AND role = ? AND is_active = 1";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, hashPassword(password));
            stmt.setString(3, role);

            System.out.println("Executing query...");
            ResultSet rs = stmt.executeQuery();
            boolean found = rs.next();
            System.out.println("User found: " + found);

            // if account is found, the details of the user are stored for the subsequent forms
            if (found) {
                String fullName = rs.getString("first_Name") + " " + rs.getString("sur_Name");
                return new User(rs.getString("username"), fullName, rs.getString("role"));
            }
        } catch (Exception e) {
            System.out.println("AUTH ERROR: " + e.getMessage());
            e.printStackTrace(System.out);
        }
        return null;
    }

    // Hashes the password using SHA-256 to match what is stored in the database
    /**
     * Hashes a plain text password using SHA-256 encoding.
     * Returns the hex string representation of the hash to match
     * the format stored in the userlogin table.
     *
     * @param password the plain text password to hash
     * @return the SHA-256 hashed password as a hex string, or null if hashing fails
     */

    // CLAUDE AI WAS USED IN THE FOLLOWING CODE TO GET THE HASHING FUNCTION TO HASH THE PASSWORDS ACCURATELY
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * Retrieves a list of catalogue items that are below their minimum stock level.
     * Results are ordered by the largest shortfall first so the most urgent
     * items appear at the top of the warning dialog.
     * Called on admin and director login to generate the low stock warning popup.
     *
     * @return list of warning strings formatted as
     *         "itemId — description (Stock: X / Min: Y)"
     */
    public List<String> getStockWarnings() {
        List<String> warnings = new ArrayList<>();
        try {
            Connection conn = new DBConnection().getConn();
            // Queries the database to identify all the items that are currently below their minimum stock levels
            String sql = "SELECT item_id, description, availability, minimum_stock_level " +
                    "FROM catalogue WHERE is_active = 1 AND availability < minimum_stock_level " +
                    "ORDER BY (minimum_stock_level - availability) DESC";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            // Each item is formated so that they can be displayed correctly in the woarning message
            while (rs.next()) {
                warnings.add(
                        rs.getString("item_id") + " — " +
                                rs.getString("description") +
                                " (Stock: " + rs.getInt("availability") +
                                " / Min: " + rs.getInt("minimum_stock_level") + ")"
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return warnings;
    }
}
