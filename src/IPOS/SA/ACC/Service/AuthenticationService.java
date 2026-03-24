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
            // Map the database role to your application's expected role format
            String normalizedRole = normalizeRole(user.getRole());
            user.setRole(normalizedRole);
            System.out.println("Normalized role from '" + user.getRole() + "' to '" + normalizedRole + "'");
        }

        return user;
    }

    /**
     * Normalizes role names from database format to application format
     */
    private String normalizeRole(String dbRole) {
        if (dbRole == null) return null;

        // Convert to lowercase and replace spaces with underscores
        String normalized = dbRole.toLowerCase().replace(" ", "_");

        // Map specific role names if needed
        switch (normalized) {
            case "admin_user":
                return "administrator";
            case "director_of_operations":
                return "director_of_operations";
            case "senior_accountant":
                return "senior_accountant";
            case "accountant":
                return "accountant";
            case "warehouse_employee":
                return "warehouse_employee";
            case "delivery_employee":
                return "delivery_employee";
            default:
                return normalized;
        }
    }

    public List<String> getStockWarnings() {
        return dbConnector.getStockWarnings();
    }

    public boolean hasPermission(User user, String requiredRole) {
        if (user == null) return false;

        // Administrator has all permissions
        if (user.getRole().equals("administrator")) return true;

        // Director of operations has most permissions
        if (user.getRole().equals("director_of_operations") &&
                (requiredRole.equals("director_of_operations") ||
                        requiredRole.equals("staff"))) {
            return true;
        }

        // Staff only have staff permissions
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
