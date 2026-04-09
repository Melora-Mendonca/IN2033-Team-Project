package IPOS.SA;

import IPOS.SA.Comms.IPOSAPIServer;
import IPOS.SA.UI.LoginForm;

import javax.swing.SwingUtilities;
import java.io.IOException;

/**
 * Main entry point of the system, which begins from the system login page, allowing all IPOS-SA staff to login and access subsequent pages.
 */
public class Main {

    private static IPOSAPIServer apiServer;

    public static void main(String[] a) {
        // Start the REST API server in a background thread
        startAPIServer();

        // Launches the login form by scheduling the code inside to run on the EDT thread
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // Creates a new instance of login form, and sets it to visible.
                LoginForm loginForm = new LoginForm();
                loginForm.setVisible(true);
            }
        });
    }

    private static void startAPIServer() {
        new Thread(() -> {
            try {
                apiServer = new IPOSAPIServer();
                apiServer.start();
                System.out.println("REST API Server is running on port 8081");
                System.out.println("Other teams can call: http://localhost:8081/api/...");
            } catch (IOException e) {
                System.err.println("Failed to start API server: " + e.getMessage());
            }
        }).start();
    }

    public static void stopAPIServer() {
        if (apiServer != null) {
            apiServer.stop();
        }
    }
}


