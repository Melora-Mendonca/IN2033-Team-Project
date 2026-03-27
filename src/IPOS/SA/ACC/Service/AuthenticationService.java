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
}
