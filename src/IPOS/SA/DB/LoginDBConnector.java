package IPOS.SA.DB;

import IPOS.SA.ACC.Model.User;

import java.security.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LoginDBConnector {

    // Authenticates a user against the database
    // Returns a User object if successful, null if credentials are invalid
    public User authenticate(String username, String password, String role) {
        try {
            System.out.println("=== AUTH ATTEMPT ===");
            System.out.println("Username: " + username);
            System.out.println("Role: " + role);
            System.out.println("Hash: " + hashPassword(password));

            Connection conn = new DBConnection().getConn();
            System.out.println("Connection: " + (conn != null ? "OK" : "NULL"));

            String sql = "SELECT * FROM userlogin WHERE username = ? AND password_hash = ? AND role = ? AND is_active = 1";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, hashPassword(password));
            stmt.setString(3, role);

            System.out.println("Executing query...");
            ResultSet rs = stmt.executeQuery();
            boolean found = rs.next();
            System.out.println("User found: " + found);

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

    public List<String> getStockWarnings() {
        List<String> warnings = new ArrayList<>();
        try {
            Connection conn = new DBConnection().getConn();
            // Updated: changed stock_limit to minimum_stock_level
            String sql = "SELECT item_id, description, availability, minimum_stock_level " +
                    "FROM catalogue WHERE is_active = 1 AND availability < minimum_stock_level " +
                    "ORDER BY (minimum_stock_level - availability) DESC";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
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
