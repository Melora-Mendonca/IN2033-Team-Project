package IPOS.SA;

import IPOS.SA.ACC.LoginForm;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] a) {
        // Launch the login form
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                LoginForm loginForm = new LoginForm();
                loginForm.setVisible(true);
            }
        });
    }
}