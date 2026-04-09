package IPOS.SA.ACC.UI;

import IPOS.SA.ACC.Service.StaffAccountService;
import IPOS.SA.DB.DBConnection;
import IPOS.SA.UI.BaseFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.ResultSet;

public class SettingsForm extends BaseFrame {

    private final StaffAccountService staffService;

    // Profile fields
    private JLabel usernameValue;
    private JLabel fullNameValue;
    private JLabel roleValue;
    private JLabel emailValue;

    // Password fields
    private JPasswordField currentPassField;
    private JPasswordField newPassField;
    private JPasswordField confirmPassField;
    private JLabel messageLabel;

    private final String username;

    public SettingsForm(String fullname, String role, String username) {
        super(fullname, role, username,"Settings");
        this.staffService = new StaffAccountService();
        this.username  = username;
        buildContent();
        loadProfile();
    }

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

    // ── PROFILE CARD ─────────────────────────────────────────
    private JPanel buildProfileCard() {
        JPanel card = card("MY PROFILE");

        usernameValue = infoLabel("—");
        fullNameValue = infoLabel("—");
        roleValue     = infoLabel("—");
        emailValue    = infoLabel("—");

        card.add(fieldRow("Username",  usernameValue));
        card.add(Box.createVerticalStrut(12));
        card.add(fieldRow("Full Name", fullNameValue));
        card.add(Box.createVerticalStrut(12));
        card.add(fieldRow("Role",      roleValue));
        card.add(Box.createVerticalStrut(12));
        card.add(fieldRow("Email",     emailValue));

        return card;
    }

    // ── PASSWORD CARD ─────────────────────────────────────────
    private JPanel buildPasswordCard() {
        JPanel card = card("CHANGE PASSWORD");

        currentPassField = passField();
        newPassField     = passField();
        confirmPassField = passField();

        messageLabel = new JLabel(" ");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        messageLabel.setBorder(new EmptyBorder(8, 0, 0, 0));

        JButton changeBtn = new JButton("Change Password");
        changeBtn.setBackground(new Color(30, 70, 90));
        changeBtn.setForeground(Color.WHITE);
        changeBtn.setFocusPainted(false);
        changeBtn.setBorderPainted(false);
        changeBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        changeBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        changeBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        changeBtn.addActionListener(e -> changePassword());

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

    // ── DATA METHODS ─────────────────────────────────────────
    private void loadProfile() {
        try {
            DBConnection db = new DBConnection();

            // Split fullname into first and last name
            String[] nameParts = fullname.split(" ");
            String firstName = nameParts[0];
            String lastName = nameParts.length > 1 ? nameParts[1] : "";

            // Find user by first and last name
            ResultSet rs = db.query(
                    "SELECT username, first_Name, sur_Name, email, role " +
                            "FROM userlogin WHERE first_Name = ? AND sur_Name = ?",
                    firstName, lastName);

            if (rs.next()) {
                usernameValue.setText(rs.getString("username"));
                fullNameValue.setText(rs.getString("first_Name") + " " + rs.getString("sur_Name"));
                roleValue.setText(rs.getString("role"));
                emailValue.setText(rs.getString("email") != null ? rs.getString("email") : "—");
            } else {
                // Fallback to what was passed
                usernameValue.setText(username != null ? username : "Unknown");
                fullNameValue.setText(fullname);
                roleValue.setText(role);
                emailValue.setText("—");
            }
        } catch (Exception e) {
            e.printStackTrace();
            usernameValue.setText("Error loading profile");
        }
    }

    private void changePassword() {
        String current = new String(currentPassField.getPassword());
        String newPass  = new String(newPassField.getPassword());
        String confirm  = new String(confirmPassField.getPassword());

        // Check all fields filled
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

            // Get the actual username from database using fullname
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

            // Verify current password is correct
            ResultSet rs = db.query(
                    "SELECT user_id FROM userlogin WHERE username = ? AND password_hash = ?",
                    actualUsername, hashPassword(current));

            if (!rs.next()) {
                setMsg("Current password is incorrect.", false);
                // Clear current password field on failed attempt
                currentPassField.setText("");
                return;
            }

            String userId = rs.getString("user_id");
            boolean success = staffService.resetPassword(userId, hashPassword(newPass));

            if (success) {
                setMsg("Password changed successfully.", true);
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

    // ── HELPERS ──────────────────────────────────────────────
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

    private JPanel fieldRow(String label, JComponent field) {
        JPanel row = new JPanel(new BorderLayout(0, 4));
        row.setBackground(Color.WHITE);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lbl = new JLabel(label.toUpperCase());
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lbl.setForeground(new Color(107, 114, 128));

        row.add(lbl,   BorderLayout.NORTH);
        row.add(field, BorderLayout.CENTER);
        return row;
    }

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

    private JPasswordField passField() {
        JPasswordField field = new JPasswordField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(221, 225, 231)),
                BorderFactory.createEmptyBorder(0, 12, 0, 12)));
        return field;
    }

    private void setMsg(String text, boolean success) {
        messageLabel.setText(text);
        messageLabel.setForeground(success
                ? new Color(0, 97, 0)
                : new Color(200, 80, 80));
    }
}