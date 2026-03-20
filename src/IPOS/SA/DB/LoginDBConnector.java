package IPOS.SA.DB;

import IPOS.SA.ACC.User;

import java.security.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LoginDBConnector {

    // Authenticates a user against the database
    // Returns a User object if successful, null if credentials are invalid
    public User authenticate(String username, String password, String role) {
        try {
            Connection conn = new DBConnection().getConn();
            String sql = "SELECT * FROM User_Login WHERE username = ? AND password_hash = ? AND role = ? AND is_active = 1";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, hashPassword(password));
            stmt.setString(3, role.toLowerCase());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new User(rs.getString("username"), rs.getString("role"), rs.getString("full_name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
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

    public List<String> getStockWarnings(){
        List<String> warnings = new ArrayList<>();
        try{
            Connection conn = new DBConnection().getConn();
            String sql = "SELECT item_id, description, availability, stock_limit FROM Catalogue WHERE is_active = 1 AND availability < stock_limit ORDER BY availability ASC";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                warnings.add(
                        rs.getString("item_id") + " — " +
                                rs.getString("description") +
                                " (Stock: " + rs.getInt("availability") +
                                " / Min: " + rs.getInt("stock_limit") + ")"
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return warnings;
    }
}
