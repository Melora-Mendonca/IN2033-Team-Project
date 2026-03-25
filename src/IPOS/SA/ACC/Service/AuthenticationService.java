package IPOS.SA.ACC.Service;

import IPOS.SA.ACC.Model.User;
import IPOS.SA.DB.LoginDBConnector;
import java.util.List;

public class AuthenticationService {
    private LoginDBConnector dbConnector;

    public AuthenticationService() {
        this.dbConnector = new LoginDBConnector();
    }

    public User authenticate(String username, String password, String selectedRole) {
        if (username == null || username.trim().isEmpty() ||
                password == null || password.trim().isEmpty() ||
                selectedRole == null || selectedRole.trim().isEmpty()) {
            return null;
        }

        // Get the user from database
        User user = dbConnector.authenticate(username, password, selectedRole);

        if (user != null) {
            System.out.println("Database returned user with role: " + user.getRole());
            System.out.println("Selected role from UI: " + selectedRole);

            // IMPORTANT: Set the role to match what was selected in the UI
            // This ensures the user gets the correct dashboard based on their selection
            user.setRole(selectedRole);

            System.out.println("Normalized role set to: " + user.getRole());
        }

        return user;
    }

    public List<String> getStockWarnings() {
        return dbConnector.getStockWarnings();
    }

    public boolean hasPermission(User user, String requiredRole) {
        if (user == null) return false;

        if (user.getRole().equals("administrator")) return true;

        if (user.getRole().equals("director_of_operations") &&
                (requiredRole.equals("director_of_operations") ||
                        requiredRole.equals("staff"))) {
            return true;
        }

        return user.getRole().equals(requiredRole);
    }

    public String formatRoleForDisplay(String role) {
        if (role == null || role.isEmpty()) return "";

        String[] words = role.split("_");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            if (word.length() > 0) {
                sb.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1))
                        .append(" ");
            }
        }
        return sb.toString().trim();
    }

    public String getDashboardTitle(User user) {
        switch (user.getRole()) {
            case "administrator":
                return "Admin Dashboard";
            case "director_of_operations":
                return "Operations Dashboard";
            case "senior_accountant":
            case "accountant":
                return "Accounts Dashboard";
            case "warehouse_employee":
                return "Warehouse Dashboard";
            case "delivery_employee":
                return "Delivery Dashboard";
            default:
                return "Staff Dashboard";
        }
    }
}
