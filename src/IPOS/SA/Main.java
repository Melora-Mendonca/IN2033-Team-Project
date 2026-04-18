package IPOS.SA;

import IPOS.SA.Comms.IPOSAPIServer;
import IPOS.SA.UI.AppFrame;

import javax.swing.SwingUtilities;
import java.io.IOException;

/**
 * Main entry point of the IPOS-SA application.
 * Starts the REST API server for inter-system communication
 * and launches the Swing GUI on the Event Dispatch Thread.
 */

public class Main {

    // The REST API server instance used for communication with IPOS-CA and IPOS-PU
    private static IPOSAPIServer apiServer;

    /**
     * Application entry point.
     * Starts the REST API server on a background thread,
     * then launches the main application window on the Swing EDT.
     *
     * @param a string list of command line arguments
     */
    public static void main(String[] a) {
        // Starts the REST API server before launching the GUI
        startAPIServer();

        // Launches the GUI on the Event Dispatch Thread
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

    /**
     * Starts the REST API server on a separate background thread
     * so it does not block the GUI from loading.
     * The server listens on port 8081 for requests from IPOS-CA and IPOS-PU.
     */
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

    /**
     * Stops the REST API server.
     * Called on application shutdown.
     */
    public static void stopAPIServer() {
        if (apiServer != null) {
            apiServer.stop();
        }
    }
}
