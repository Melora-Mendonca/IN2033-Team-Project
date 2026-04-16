package IPOS.SA.Comms.PUClient;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

/**
 * HTTP client for cross-system communication APIs.
 *
 * Email endpoint  : POST http://localhost:8080/api/comms/email
 * Payment endpoint: POST http://localhost:8080/api/comms/payment
 */
public class CommsClient {

    private static final String BASE_URL = "http://host.docker.internal:8080/api/comms";

    /**
     * Sends an email via the comms subsystem.
     *
     * @param to      recipient email address
     * @param subject email subject
     * @param body    email body text
     * @return HTTP response body from the comms server
     * @throws IOException if the comms server is unreachable or returns an error
     */
    public static String sendEmail(String to, String subject, String body) throws IOException {
        String json = "{"
                + "\"to\":\""      + escapeJson(to)      + "\","
                + "\"subject\":\"" + escapeJson(subject) + "\","
                + "\"body\":\""    + escapeJson(body)    + "\""
                + "}";
        return post(BASE_URL + "/email", json);
    }

    /**
     * Processes a card payment via the comms subsystem.
     *
     * @param merchantId    merchant account ID (e.g. "M001")
     * @param orderId       order ID being paid (e.g. "0001")
     * @param fullName      cardholder full name
     * @param address       billing address
     * @param cardFirstFour first 4 digits of the card number
     * @param cardLastFour  last 4 digits of the card number
     * @param amount        payment amount
     * @return HTTP response body from the comms server
     * @throws IOException if the comms server is unreachable or returns an error
     */
    public static String processPayment(String merchantId, String orderId,
                                        String fullName,  String address,
                                        String cardFirstFour, String cardLastFour,
                                        double amount) throws IOException {
        String json = "{"
                + "\"merchantID\":\""    + escapeJson(merchantId)    + "\","
                + "\"orderID\":\""       + escapeJson(orderId)       + "\","
                + "\"fullName\":\""      + escapeJson(fullName)      + "\","
                + "\"address\":\""       + escapeJson(address)       + "\","
                + "\"cardFirstFour\":\"" + escapeJson(cardFirstFour) + "\","
                + "\"cardLastFour\":\""  + escapeJson(cardLastFour)  + "\","
                + "\"amount\":"          + amount
                + "}";
        return post(BASE_URL + "/payment", json);
    }

    // ── Internal HTTP POST ────────────────────────────────────────────────────

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
