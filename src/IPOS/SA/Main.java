package IPOS.SA;

import IPOS.SA.Comms.IPOSAPIServer;
import IPOS.SA.UI.AppFrame;

import javax.swing.SwingUtilities;
import java.io.IOException;

public class Main {

    private static IPOSAPIServer apiServer;

    public static void main(String[] a) {
        startAPIServer();
        SwingUtilities.invokeLater(() -> {
            try {
                System.out.println("Creating AppFrame...");
                AppFrame.getInstance();
                System.out.println("AppFrame created!");
            } catch (Throwable t) {
                System.out.println("APPFRAME ERROR: " + t.getMessage());
                t.printStackTrace(System.out);
            }
        });
    }

    private static void startAPIServer() {
        new Thread(() -> {
            try {
                apiServer = new IPOSAPIServer();
                apiServer.start();
                System.out.println("REST API Server is running on port 8081");
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


