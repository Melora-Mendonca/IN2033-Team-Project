package IPOS.SA.Comms;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import IPOS.SA.CAT.Service.catalogueService;
import IPOS.SA.ACC.Service.AccountService;
import IPOS.SA.ORD.Service.OrderService;
import IPOS.SA.ORD.Service.InvoiceService;
import IPOS.SA.DB.DBConnection;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import org.json.JSONObject;

public class IPOSAPIServer {

    private HttpServer server;
    private final catalogueService catalogueService;
    private final AccountService accountService;
    private final OrderService orderService;
    private final InvoiceService invoiceService;

    public IPOSAPIServer() {
        this.catalogueService = new catalogueService();
        this.accountService = new AccountService();
        this.invoiceService = new InvoiceService();
        this.orderService = new OrderService(accountService, invoiceService);
    }

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(8081), 0);

        // Inventory Service endpoints
        server.createContext("/api/inventory/catalogue", new GetCatalogueHandler());
        server.createContext("/api/inventory/deduct", new DeductStockHandler());

        // Order Service endpoints
        server.createContext("/api/orders/place", new PlaceOrderHandler());
        server.createContext("/api/orders/track", new TrackDeliveryHandler());
        server.createContext("/api/orders/balance", new GetBalanceHandler());
        server.createContext("/api/orders/invoice", new GetInvoiceHandler());
        server.createContext("/api/orders/status", new GetAccountStatusHandler());
        server.createContext("/api/orders/discount", new GetDiscountPlanHandler());
        server.createContext("/api/orders/credit", new GetCreditLimitHandler());

        // Membership Service endpoint
        server.createContext("/api/membership/request", new RequestMembershipHandler());

        server.setExecutor(Executors.newCachedThreadPool());
        server.start();
        System.out.println("=== IPOS-SA API SERVER STARTED ===");
        System.out.println("Port: 8081");
        System.out.println("Available endpoints:");
        System.out.println("  GET  /api/inventory/catalogue");
        System.out.println("  POST /api/inventory/deduct");
        System.out.println("  POST /api/orders/place");
        System.out.println("  GET  /api/orders/track?orderID=xxx");
        System.out.println("  GET  /api/orders/balance?merchantID=xxx");
        System.out.println("  GET  /api/orders/invoice?orderID=xxx");
        System.out.println("  GET  /api/orders/status?merchantID=xxx&status=xxx");
        System.out.println("  GET  /api/orders/discount?merchantID=xxx");
        System.out.println("  GET  /api/orders/credit?merchantID=xxx");
        System.out.println("  POST /api/membership/request");
        System.out.println("==================================");
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
            System.out.println("IPOS-SA API Server stopped");
        }
    }

    private String readBody(HttpExchange exchange) throws IOException {
        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(isr);
        StringBuilder body = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            body.append(line);
        }
        return body.toString();
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    // INVENTORY HANDLERS METHODS

    class GetCatalogueHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                try {
                    var items = catalogueService.getAllActiveItems();
                    StringBuilder json = new StringBuilder("[");
                    for (int i = 0; i < items.size(); i++) {
                        var item = items.get(i);
                        if (i > 0) json.append(",");
                        json.append("{")
                                .append("\"itemId\":\"").append(item.getItemId()).append("\",")
                                .append("\"description\":\"").append(item.getDescription()).append("\",")
                                .append("\"packageCost\":").append(item.getPackageCost()).append(",")
                                .append("\"availability\":").append(item.getAvailabilityPacks())
                                .append("}");
                    }
                    json.append("]");
                    sendResponse(exchange, 200, json.toString());
                } catch (Exception e) {
                    sendResponse(exchange, 500, "{\"error\":\"" + e.getMessage() + "\"}");
                }
            } else {
                sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
            }
        }
    }

    class DeductStockHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                try {
                    String body = readBody(exchange);
                    String itemId = extractValue(body, "itemID");
                    int quantity = Integer.parseInt(extractValue(body, "quantity"));
                    boolean success = catalogueService.UpdateCatalogue(itemId, quantity);
                    if (success) {
                        sendResponse(exchange, 200, "{\"message\":\"Stock deducted successfully\"}");
                    } else {
                        sendResponse(exchange, 400, "{\"error\":\"Failed to deduct stock\"}");
                    }
                } catch (Exception e) {
                    sendResponse(exchange, 500, "{\"error\":\"" + e.getMessage() + "\"}");
                }
            } else {
                sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
            }
        }
    }

    // ORDER HANDLER METHODS

    class PlaceOrderHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                try {
                    String body = readBody(exchange);
                    String merchantId = extractValue(body, "merchantID");
                    String orderDetails = extractValue(body, "orderDetails");
                    String orderId = "ORD_" + System.currentTimeMillis();
                    sendResponse(exchange, 200, "{\"orderId\":\"" + orderId + "\"}");
                } catch (Exception e) {
                    sendResponse(exchange, 500, "{\"error\":\"" + e.getMessage() + "\"}");
                }
            } else {
                sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
            }
        }
    }

    class TrackDeliveryHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                try {
                    String query = exchange.getRequestURI().getQuery();
                    String orderId = extractQueryParam(query, "orderID");
                    String tracking = orderService.getOrderDetailsText(orderId);
                    sendResponse(exchange, 200, "{\"tracking\":\"" + escapeJson(tracking) + "\"}");
                } catch (Exception e) {
                    sendResponse(exchange, 500, "{\"error\":\"" + e.getMessage() + "\"}");
                }
            } else {
                sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
            }
        }
    }

    class GetBalanceHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                try {
                    String query = exchange.getRequestURI().getQuery();
                    String merchantId = extractQueryParam(query, "merchantID");
                    double balance = accountService.getAccountBalance(merchantId);
                    sendResponse(exchange, 200, "{\"balance\":" + balance + "}");
                } catch (Exception e) {
                    sendResponse(exchange, 500, "{\"error\":\"" + e.getMessage() + "\"}");
                }
            } else {
                sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
            }
        }
    }

    class GetInvoiceHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                try {
                    String query = exchange.getRequestURI().getQuery();
                    String orderId = extractQueryParam(query, "orderID");
                    String invoice = invoiceService.getInvoiceAsString(orderId);
                    sendResponse(exchange, 200, "{\"invoice\":\"" + escapeJson(invoice) + "\"}");
                } catch (Exception e) {
                    sendResponse(exchange, 500, "{\"error\":\"" + e.getMessage() + "\"}");
                }
            } else {
                sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
            }
        }
    }

    class GetAccountStatusHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                try {
                    String query = exchange.getRequestURI().getQuery();
                    String merchantId = extractQueryParam(query, "merchantID");
                    String status = extractQueryParam(query, "status");
                    String currentStatus = accountService.getAccountStatus(merchantId);
                    boolean matches = currentStatus.equalsIgnoreCase(status);
                    sendResponse(exchange, 200, "{\"matches\":" + matches + "}");
                } catch (Exception e) {
                    sendResponse(exchange, 500, "{\"error\":\"" + e.getMessage() + "\"}");
                }
            } else {
                sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
            }
        }
    }

    class GetDiscountPlanHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                try {
                    String query = exchange.getRequestURI().getQuery();
                    String merchantId = extractQueryParam(query, "merchantID");
                    double discount = accountService.getDiscountRate(merchantId);
                    sendResponse(exchange, 200, "{\"discountRate\":" + discount + "}");
                } catch (Exception e) {
                    sendResponse(exchange, 500, "{\"error\":\"" + e.getMessage() + "\"}");
                }
            } else {
                sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
            }
        }
    }

    class GetCreditLimitHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                try {
                    String query = exchange.getRequestURI().getQuery();
                    String merchantId = extractQueryParam(query, "merchantID");
                    double limit = accountService.getCreditLimit(merchantId);
                    sendResponse(exchange, 200, "{\"creditLimit\":" + limit + "}");
                } catch (Exception e) {
                    sendResponse(exchange, 500, "{\"error\":\"" + e.getMessage() + "\"}");
                }
            } else {
                sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
            }
        }
    }

    // MEMBERSHIP HANDLER METHOD

    class RequestMembershipHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                try {
                    String body = readBody(exchange);
                    String companyName = extractValue(body, "companyName");
                    String registrationNumber = extractValue(body, "registrationNumber");
                    String directors = extractValue(body, "directors");
                    String businessType = extractValue(body, "businessType");
                    String address = extractValue(body, "address");
                    String email = extractValue(body, "email");
                    String fax = extractValue(body, "fax");
                    String phone = extractValue(body, "phone");
                    boolean preferPhysicalMail = "true".equals(extractValue(body, "preferPhysicalMail"));

                    DBConnection db = new DBConnection();
                    int rows = db.update(
                            "INSERT INTO commercial_applications " +
                                    "(company_name, registration_no, director_name, " +
                                    "business_type, address, email, phone, fax, prefer_physical_mail, status, application_date) " +
                                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 'pending', CURRENT_DATE())",
                            companyName, registrationNumber, directors,
                            businessType, address, email, phone, fax, preferPhysicalMail ? 1 : 0
                    );

                    if (rows > 0) {
                        sendResponse(exchange, 200, "{\"success\":true}");
                    } else {
                        sendResponse(exchange, 400, "{\"success\":false}");
                    }
                } catch (Exception e) {
                    sendResponse(exchange, 500, "{\"success\":false,\"error\":\"" + e.getMessage() + "\"}");
                }
            } else {
                sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
            }
        }
    }

    private String extractValue(String json, String key) {
        String search = "\"" + key + "\"";
        int keyIndex = json.indexOf(search);
        if (keyIndex == -1) return "";

        // Find the colon after the key
        int colonIndex = json.indexOf(":", keyIndex + search.length());
        if (colonIndex == -1) return "";

        // Skip whitespace after colon
        int start = colonIndex + 1;
        while (start < json.length() && Character.isWhitespace(json.charAt(start))) {
            start++;
        }

        if (start >= json.length()) return "";

        // String value
        if (json.charAt(start) == '"') {
            start++;
            int end = json.indexOf("\"", start);
            if (end == -1) return "";
            return json.substring(start, end);
        }

        // Boolean or number value
        int end = start;
        while (end < json.length() &&
                json.charAt(end) != ',' &&
                json.charAt(end) != '}' &&
                json.charAt(end) != '\n' &&
                !Character.isWhitespace(json.charAt(end))) {
            end++;
        }
        return json.substring(start, end).trim();
    }

    private String extractQueryParam(String query, String key) {
        if (query == null) return "";
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] kv = pair.split("=");
            if (kv.length == 2 && kv[0].equals(key)) {
                return kv[1];
            }
        }
        return "";
    }

    private String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}
