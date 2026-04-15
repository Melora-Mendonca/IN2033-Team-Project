package IPOS.SA.Comms.PUClient;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class IPOSPUEmailClient {

    // IPOS-PU's email service endpoint - NO "/produce" suffix
    private static final String BASE_URL = "http://host.docker.internal:8080/api/comms/email";

    /**
     * Calls IPOS-PU's email service to send an email
     *
     * @param email     Recipient email address (maps to IPOS-PU's "to" field)
     * @param content   Email content/body (maps to IPOS-PU's "body" field)
     * @param reference Reference ID (maps to IPOS-PU's "subject" field)
     * @param sender    Sender system identifier (NOT used by IPOS-PU)
     * @param subsystem Subsystem requesting the email (NOT used by IPOS-PU)
     * @return true if email was sent successfully, false otherwise
     */
    public static boolean produceEmail(String email, String content,
                                       String reference, String sender,
                                       String subsystem) throws IOException {

        // Build JSON matching what IPOS-PU actually expects
        // IPOS-PU expects: "to", "subject", "body"
        String json = "{"
                + "\"to\":\""      + escapeJson(email)    + "\","
                + "\"subject\":\"" + escapeJson(reference) + "\","
                + "\"body\":\""    + escapeJson(content)   + "\""
                + "}";

        System.out.println("[IPOSPUEmailClient] Sending email to IPOS-PU at: " + BASE_URL);
        System.out.println("[IPOSPUEmailClient] JSON: " + json);

        // Make POST request to IPOS-PU (no "/produce" suffix)
        String response = post(BASE_URL, json);

        // IPOS-PU returns "email sent" on success
        boolean success = response != null && response.contains("email sent");
        System.out.println("[IPOSPUEmailClient] Email send result: " + success);

        return success;
    }

    private static String post(String urlStr, String json) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");
        conn.setDoOutput(true);
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = json.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        int status = conn.getResponseCode();
        InputStream is = (status >= 200 && status < 300)
                ? conn.getInputStream()
                : conn.getErrorStream();

        if (is == null) {
            conn.disconnect();
            if (status >= 200 && status < 300) return "OK";
            throw new IOException("HTTP " + status + " — no response body");
        }

        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) response.append(line.trim());
        } finally {
            conn.disconnect();
        }

        if (status < 200 || status >= 300) {
            throw new IOException("HTTP " + status + ": " + response);
        }

        return response.toString();
    }

    private static String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
