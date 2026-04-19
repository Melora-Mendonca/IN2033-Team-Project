package IPOS.SA.Comms.PUClient;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

// AI (Claude) was used to implement the HTTP POST logic, JSON body construction and the async send method.

/**
 * Service responsible for sending emails via the IPOS-PU email API.
 * Communicates with the IPOS-PU REST endpoint to deliver emails
 * to merchants and staff when account events occur.
 */
public class EmailService {

    // The REST API endpoint URL for the IPOS-PU email service
    private static final String EMAIL_API_URL = "http://host.docker.internal:8080/api/comms/email";

    /**
     * Sends an email synchronously via the IPOS-PU email API.
     * Constructs a JSON payload and posts it to the email endpoint.
     * Returns true if the server responds with HTTP 200 or 201.
     *
     * @param to the recipient email address
     * @param subject the email subject line
     * @param body the email body text
     * @return true if the email was sent successfully, false otherwise
     */
    public boolean sendEmail(String to, String subject, String body) {
        try {
            URL url = new URL(EMAIL_API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            // creates a JSON payload string containing the email
            String jsonPayload = String.format(
                    "{\"to\":\"%s\", \"subject\":\"%s\", \"body\":\"%s\"}",
                    escapeJson(to),
                    escapeJson(subject),
                    escapeJson(body)
            );

            // posts the email to the requesting endpoint
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Checks response code — 200 or 201 indicates success
            int responseCode = conn.getResponseCode();
            return responseCode == 200 || responseCode == 201;

        } catch (Exception e) {
            System.err.println("Failed to send email: " + e.getMessage());
            return false;
        }
    }

    /**
     * Escapes special characters in a string for safe inclusion in a JSON payload.
     * Handles backslashes, quotes, newlines and carriage returns.
     *
     * @param value the string to escape
     * @return the escaped string, or an empty string if the value is null
     */
    private String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}
