package IPOS.SA.ACC.Model;

/**
 * Represents an authenticated user session in IPOS-SA.
 * Created by the authentication service on successful login
 * and used to navigate to the correct dashboard and
 * pass user details throughout the application.
 */
public class User {
    private String username; // the user's login username
    private String fullName; // the user's full name
    private String role; // the user's role
    private String email; // the user's email

    /**
     * Constructor — creates a user with username, full name and role.
     *
     * @param username the login username
     * @param fullName the user's full name
     * @param role the user's role
     */
    public User(String username, String fullName, String role) {
        this.username = username;
        this.fullName = fullName;
        this.role = role;
    }

    /**
     * Constructor — creates a user with username, full name, role and email.
     *
     * @param username the login username
     * @param fullName the user's full name
     * @param role the user's role
     * @param email the user's email address
     */
    public User(String username, String fullName, String role, String email) {
        this(username, fullName, role);
        this.email = email;
    }

    // Getter methods to retrieve the data
    /**
     * Returns the user's login username.
     *
     * @return the login username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns the user's full name.
     *
     * @return the full name
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Returns the user's role.
     *
     * @return the role string
     */
    public String getRole() {
        return role;
    }

    /**
     * Returns the user's email address.
     *
     * @return the email address
     */
    public String getEmail() {
        return email;
    }

    // Setters
    /**
     * Sets the user's login username.
     *
     * @param username the login username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Sets the user's full name.
     *
     * @param fullName the full name
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /**
     * Sets the user's role.
     *
     * @param role the role string
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * Sets the user's email address.
     *
     * @param email the email address
     */
    public void setEmail(String email) {
        this.email = email;
    }


    /**
     * Returns a string representation of the user
     * showing their full name and username.
     *
     * @return formatted string with the full name and username
     */
    @Override
    public String toString() {
        return fullName + " (" + username + ")";
    }
}
