package IPOS.SA;

import IPOS.SA.ACC.UI.LoginForm;

import javax.swing.SwingUtilities;

/**
 * Main entry point of the system, which begins from the system login page, allowing all IPOS-SA staff to login and access subsequent pages.
 */
public class Main {
    public static void main(String[] a) {
        // Launches the login form by scheduling the code inside to run on the EDT thread
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // Creates a new instance of login form, and sets it to visible.
                LoginForm loginForm = new LoginForm();
                loginForm.setVisible(true);
            }
        });
    }
}