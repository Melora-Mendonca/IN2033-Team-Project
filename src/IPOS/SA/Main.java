package IPOS.SA;

import IPOS.SA.ACC.UI.LoginForm;

import javax.swing.SwingUtilities;

/**
 * Main entry point of the system, which begins from the system login page, allowing all IPOS-SA staff to login and access subsequent pages.
 */
public class Main {
    public static void main(String[] a) {
        // Launch the login form
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // Create new instance of login form, and set it to visible.
                LoginForm loginForm = new LoginForm();
                loginForm.setVisible(true);
            }
        });
    }
}