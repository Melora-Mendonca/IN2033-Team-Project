package IPOS.SA.ACC.UI;

import IPOS.SA.ACC.Service.StaffAccountService;
import IPOS.SA.DB.DBConnection;
import IPOS.SA.UI.BaseFrame;
import IPOS.SA.UI.ScreenRouter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.ResultSet;

/**
 * Settings screen for IPOS-SA staff members.
 * Displays the logged-in user's profile information and
 * allows them to change their password securely.
 * Accessible to all staff roles via the Settings nav button.
 */
public class SettingsForm extends BaseFrame {

    private final StaffAccountService staffService;

    // Profile fields to display the username, fullname, role and email
    private JLabel usernameValue;
    private JLabel fullNameValue;
    private JLabel roleValue;
    private JLabel emailValue;

    // Password fields to store the user's passwords
    private JPasswordField currentPassField;
    private JPasswordField newPassField;
    private JPasswordField confirmPassField;
    private JLabel messageLabel;

    private final String username;

    /**
     * Constructor ; builds the settings screen and loads the user's profile.
     *
     * @param fullname the full name of the logged-in user
     * @param role the role of the logged-in user
     * @param username the username of the logged-in user
     * @param router the screen router used for navigation
     */
    public SettingsForm(String fullname, String role, String username, ScreenRouter router) {
        super(fullname, role, username,"Settings", router);
        this.staffService = new StaffAccountService();
        this.username  = username;
        buildContent();
        loadProfile();
    }

    /**
     * Returns the title displayed in the page header.
     *
     * @return the header title string
     */
    @Override
    protected String getHeaderTitle() {
        return "Settings";
    }

    private void buildContent() {
        CenterPanel.setLayout(new GridLayout(1, 2, 20, 0));
        CenterPanel.setBackground(new Color(245, 247, 250));
        CenterPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        CenterPanel.add(buildProfileCard());
        CenterPanel.add(buildPasswordCard());
    }

    /**
     * Builds the profile card showing the user's account details.
     * All fields are read-only display labels.
     *
     * @return the profile card panel
     */
    private JPanel buildProfileCard() {
        JPanel card = card("MY PROFILE");

        // Sets a default value if the user details cannot be accessed, or were not passed correctly
        usernameValue = infoLabel("-");
        fullNameValue = infoLabel("-");
        roleValue     = infoLabel("-");
        emailValue    = infoLabel("-");

        // Updates the fields with the user's details passed in from whichever form the setting form was called from
        // Adds spacing between the fields for formatting
        card.add(fieldRow("Username",  usernameValue));
        card.add(Box.createVerticalStrut(12));
        card.add(fieldRow("Full Name", fullNameValue));
        card.add(Box.createVerticalStrut(12));
        card.add(fieldRow("Role",      roleValue));
        card.add(Box.createVerticalStrut(12));
        card.add(fieldRow("Email",     emailValue));

        return card;
    }

    /**
     * Builds the profile card showing the user's account details.
     * All fields are read-only display labels.
     *
     * @return the profile card panel
     */
    private JPanel buildPasswordCard() {
        JPanel card = card("CHANGE PASSWORD");

        // text fields to store the passwords entered by the user
        currentPassField = passField();
        newPassField = passField();
        confirmPassField = passField();

        // message label to alert the user if any errors occur
        messageLabel = new JLabel(" ");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        messageLabel.setBorder(new EmptyBorder(8, 0, 0, 0));

        // Button to change the password
        JButton changeBtn = new JButton("Change Password");
        changeBtn.setBackground(new Color(30, 70, 90));
        changeBtn.setForeground(Color.WHITE);
        changeBtn.setFocusPainted(false);
        changeBtn.setBorderPainted(false);
        changeBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        changeBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        changeBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        changeBtn.addActionListener(e -> changePassword());

        // Adds the text fields onto the panel and formats it with vertical spacing
        card.add(fieldRow("Current Password", currentPassField));
        card.add(Box.createVerticalStrut(12));
        card.add(fieldRow("New Password",     newPassField));
        card.add(Box.createVerticalStrut(12));
        card.add(fieldRow("Confirm Password", confirmPassField));
        card.add(Box.createVerticalStrut(16));
        card.add(changeBtn);
        card.add(messageLabel);

        return card;
    }

    /**
     * Loads the logged-in user's profile from the database.
     * Splits the fullname into first and last name to query the userlogin table.
     * Falls back to displaying the passed-in values if the database query fails.
     */
    private void loadProfile() {
        try {
            DBConnection db = new DBConnection();

            // Splits fullname into first and last name
            String[] nameParts = fullname.split(" ");
            String firstName = nameParts[0];
            String lastName = nameParts.length > 1 ? nameParts[1] : "";

            // Finds user by first and last name
            ResultSet rs = db.query(
                    "SELECT username, first_Name, sur_Name, email, role " +
                            "FROM userlogin WHERE first_Name = ? AND sur_Name = ?",
                    firstName, lastName);

            if (rs.next()) {
                // populate labels from the database values
                usernameValue.setText(rs.getString("username"));
                fullNameValue.setText(rs.getString("first_Name") + " " + rs.getString("sur_Name"));
                roleValue.setText(rs.getString("role"));
                emailValue.setText(rs.getString("email") != null ? rs.getString("email") : "-");
            } else {
                // Fallback to what was passed in at login if user is not found
                usernameValue.setText(username != null ? username : "Unknown");
                fullNameValue.setText(fullname);
                roleValue.setText(role);
                emailValue.setText("-");
            }
        } catch (Exception e) {
            e.printStackTrace();
            usernameValue.setText("Error loading profile");
        }
    }

    /**
     * Loads the logged-in user's profile from the database.
     * Splits the fullname into first and last name to query the userlogin table.
     * Falls back to displaying the passed-in values if the database query fails.
     */
    private void changePassword() {
        String current = new String(currentPassField.getPassword());
        String newPass  = new String(newPassField.getPassword());
        String confirm  = new String(confirmPassField.getPassword());

        // Checks if all fields are filled
        if (current.isEmpty() || newPass.isEmpty() || confirm.isEmpty()) {
            setMsg("Please fill in all password fields.", false);
            return;
        }

        // Minimum length check
        if (newPass.length() < 6) {
            setMsg("New password must be at least 6 characters.", false);
            return;
        }

        // Maximum length check
        if (newPass.length() > 50) {
            setMsg("New password cannot exceed 50 characters.", false);
            return;
        }

        // No spaces allowed
        if (newPass.contains(" ")) {
            setMsg("Password cannot contain spaces.", false);
            return;
        }

        // New passwords must match
        if (!newPass.equals(confirm)) {
            setMsg("New passwords do not match.", false);
            return;
        }

        // New password must differ from current
        if (newPass.equals(current)) {
            setMsg("New password must be different from current password.", false);
            return;
        }

        try {
            DBConnection db = new DBConnection();

            // Gets the actual username from database using fullname
            String[] nameParts = fullname.split(" ");
            String firstName = nameParts[0];
            String lastName  = nameParts.length > 1 ? nameParts[1] : "";

            ResultSet userRs = db.query(
                    "SELECT username FROM userlogin WHERE first_Name = ? AND sur_Name = ?",
                    firstName, lastName);

            if (!userRs.next()) {
                setMsg("User not found. Please log out and log in again.", false);
                return;
            }

            String actualUsername = userRs.getString("username");

            // Verify the current password is correct
            ResultSet rs = db.query(
                    "SELECT user_id FROM userlogin WHERE username = ? AND password_hash = ?",
                    actualUsername, hashPassword(current));

            if (!rs.next()) {
                setMsg("Current password is incorrect.", false);
                // Clears the current password field on failed attempt for security
                currentPassField.setText("");
                return;
            }

            String userId = rs.getString("user_id");
            boolean success = staffService.resetPassword(userId, hashPassword(newPass));

            if (success) {
                setMsg("Password changed successfully.", true);
                // Clear all fields on successful password change
                currentPassField.setText("");
                newPassField.setText("");
                confirmPassField.setText("");
            } else {
                setMsg("Failed to change password.", false);
            }

        } catch (Exception e) {
            setMsg("Error: " + e.getMessage(), false);
        }
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
            java.security.MessageDigest md =
                    java.security.MessageDigest.getInstance("SHA-256");
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
     * Creates a styled card panel with a title label.
     * Used as the container for both the profile and password sections.
     *
     * @param title the card section title displayed at the top
     * @return the styled card panel
     */
    private JPanel card(String title) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(221, 225, 231), 1),
                new EmptyBorder(20, 20, 20, 20)));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        titleLbl.setForeground(new Color(107, 114, 128));
        titleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleLbl.setBorder(new EmptyBorder(0, 0, 16, 0));
        card.add(titleLbl);

        return card;
    }

    /**
     * Creates a labelled field row with a label above and a component below.
     * Used for both display labels and input fields.
     *
     * @param label the field label displayed above the component
     * @param field the component to display below the label
     * @return the assembled field row panel
     */
    private JPanel fieldRow(String label, JComponent field) {
        JPanel row = new JPanel(new BorderLayout(0, 4));
        row.setBackground(Color.WHITE);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lbl = new JLabel(label.toUpperCase());
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lbl.setForeground(new Color(107, 114, 128));

        row.add(lbl,BorderLayout.NORTH);
        row.add(field,BorderLayout.CENTER);
        return row;
    }

    /**
     * Creates a read-only display label styled as a greyed input field.
     * Used in the profile card to show user information.
     *
     * @param text the initial text to display
     * @return the styled read-only label
     */
    private JLabel infoLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl.setForeground(new Color(17, 24, 39));
        lbl.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(221, 225, 231)),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        lbl.setOpaque(true);
        lbl.setBackground(new Color(249, 250, 251));
        return lbl;
    }

    /**
     * Creates a styled password input field.
     * Used for the current, new and confirm password fields.
     *
     * @return the styled password field
     */
    private JPasswordField passField() {
        JPasswordField field = new JPasswordField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(221, 225, 231)),
                BorderFactory.createEmptyBorder(0, 12, 0, 12)));
        return field;
    }

    /**
     * Displays a success or error message below the Change Password button.
     *
     * @param text the message to display
     * @param success true for a green success message, false for a red error message
     */
    private void setMsg(String text, boolean success) {
        messageLabel.setText(text);
        messageLabel.setForeground(success
                ? new Color(0, 97, 0)
                : new Color(200, 80, 80));
    }
}