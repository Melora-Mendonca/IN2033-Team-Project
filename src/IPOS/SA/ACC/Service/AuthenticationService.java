package IPOS.SA.ACC.Service;

import IPOS.SA.ACC.Model.User;
import IPOS.SA.DB.LoginDBConnector;
import java.util.List;

/**
 * Service responsible for handling user authentication
 * and retrieving authentication-related data for logging in.
 */
public class AuthenticationService {
    private LoginDBConnector dbConnector;

    /**
     * Initialises the authentication service with a database connector.
     */
    public AuthenticationService() {
        this.dbConnector = new LoginDBConnector();
    }

    /**
     * Authenticates the current user based on username, hashed password, and selected role.
     *
     * @param username the entered username
     * @param password the entered password
     * @param selectedRole the role selected from the UI
     * @return authenticated User object or null if authentication fails
     */
    public User authenticate(String username, String password, String selectedRole) {
        if (username == null || username.trim().isEmpty() ||
                password == null || password.trim().isEmpty() ||
                selectedRole == null || selectedRole.trim().isEmpty()) {
            return null;
        }

        // Gets the user from database
        User user = dbConnector.authenticate(username, password, selectedRole);

        if (user != null) {

            // Sets the role of the user to what they selected, so that the user is guided to the correct dashboard
            user.setRole(selectedRole);
        }

        return user;
    }

    /**
     * Retrieves automatic stock warning message upon login if there is a item that is currently low in stock
     *
     * @return list of stock warnings
     */
    public List<String> getStockWarnings() {
        return dbConnector.getStockWarnings();
    }
}
