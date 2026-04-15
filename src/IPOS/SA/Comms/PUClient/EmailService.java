package IPOS.SA.Comms.PUClient;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class EmailService {

    private static final String EMAIL_API_URL = "http://host.docker.internal:8080/api/comms/email";

    public boolean sendEmail(String to, String subject, String body) {
        try {
            URL url = new URL(EMAIL_API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            String jsonPayload = String.format(
                    "{\"to\":\"%s\", \"subject\":\"%s\", \"body\":\"%s\"}",
                    escapeJson(to),
                    escapeJson(subject),
                    escapeJson(body)
            );

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            return responseCode == 200 || responseCode == 201;

        } catch (Exception e) {
            System.err.println("Failed to send email: " + e.getMessage());
            return false;
        }
    }

    public void sendEmailAsync(String to, String subject, String body) {
        new Thread(() -> sendEmail(to, subject, body)).start();
    }

    private String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}
