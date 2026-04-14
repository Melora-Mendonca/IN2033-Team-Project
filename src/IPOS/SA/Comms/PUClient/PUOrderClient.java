package IPOS.SA.Comms.PUClient;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

/**
 * HTTP client for fetching orders from IPOS-PU.
 *
 * Undelivered orders endpoint: GET http://localhost:8080/api/orders/undelivered
 */
public class PUOrderClient {

    private static final String BASE_URL = "http://localhost:8080/api/orders";

    /**
     * Fetches all undelivered orders from IPOS-PU.
     *
     * @return raw JSON string (array of order objects)
     * @throws IOException if IPOS-PU is unreachable or returns an error
     */
    public static String fetchUndeliveredOrders() throws IOException {
        return get(BASE_URL + "/undelivered");
    }

    /**
     * Notifies IPOS-PU of a status change for a given order.
     *
     * Endpoint: POST /api/orders/{orderId}/status
     * Body:     {"status": "<newStatus>"}
     *
     * @param orderId   the order ID as known by IPOS-PU
     * @param newStatus e.g. "accepted", "processing", "dispatched", "delivered", "rejected"
     */
    public static void updateOrderStatus(String orderId, String newStatus) {
        try {
            String json = "{\"status\":\"" + escapeJson(newStatus) + "\"}";
            post(BASE_URL + "/" + orderId + "/status", json);
            System.out.println("PUOrderClient: notified IPOS-PU — order " + orderId + " -> " + newStatus);
        } catch (IOException e) {
            // Non-fatal: log and continue — IPOS-SA still updates its own DB
            System.err.println("PUOrderClient: failed to notify IPOS-PU for order " + orderId + ": " + e.getMessage());
        }
    }

    // ── Internal HTTP helpers ─────────────────────────────────────────────────

    private static String get(String urlStr) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);

        int status = conn.getResponseCode();
        InputStream is = (status >= 200 && status < 300)
                ? conn.getInputStream()
                : conn.getErrorStream();

        if (is == null) {
            conn.disconnect();
            if (status >= 200 && status < 300) return "[]";
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
