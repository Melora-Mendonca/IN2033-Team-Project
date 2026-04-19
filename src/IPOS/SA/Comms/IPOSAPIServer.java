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
import IPOS.SA.ORD.Model.Order;
import IPOS.SA.ORD.Model.OrderItem;
import IPOS.SA.ACC.Model.MerchantAccount;

/**
 * REST API server for IPOS-SA inter-system communication.
 * Listens on port 8081 and exposes endpoints for IPOS-CA and IPOS-PU
 * to interact with the catalogue, orders, invoices and merchant accounts.
 */
public class IPOSAPIServer {

    private HttpServer server;
    private final catalogueService catalogueService;
    private final AccountService accountService;
    private final OrderService orderService;
    private final InvoiceService invoiceService;

    /**
     * Constructor — initialises all required services.
     */
    public IPOSAPIServer() {
        this.catalogueService = new catalogueService();
        this.accountService = new AccountService();
        this.invoiceService = new InvoiceService();
        this.orderService = new OrderService(accountService, invoiceService);
    }

    /**
     * Starts the HTTP server on port 8081.
     * Registers all endpoint handlers and uses a cached thread pool
     * to handle concurrent requests.
     *
     * @throws IOException if the server fails to start
     */
    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(8081), 0);

        // Inventory Service endpoints - to send all the active catalogue items and to deduct stock for an item
        server.createContext("/api/inventory/catalogue", new GetCatalogueHandler());
        server.createContext("/api/inventory/deduct", new DeductStockHandler());

        // Order Service endpoints - to place orders, track orders, get account balance, get credit limits, get order invoices, get account status, and get discount rate
        server.createContext("/api/orders/place", new PlaceOrderHandler());
        server.createContext("/api/orders/track", new TrackDeliveryHandler());
        server.createContext("/api/orders/balance", new GetBalanceHandler());
        server.createContext("/api/orders/invoice", new GetInvoiceHandler());
        server.createContext("/api/orders/status", new GetAccountStatusHandler());
        server.createContext("/api/orders/discount", new GetDiscountPlanHandler());
        server.createContext("/api/orders/credit", new GetCreditLimitHandler());

        // Membership Service endpoint - to submit commercial membership applications
        server.createContext("/api/membership/request", new RequestMembershipHandler());

        // Use a cached thread pool to handle multiple simultaneous requests
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

    /**
     * Stops the HTTP server.
     * Called on application shutdown.
     */
    public void stop() {
        if (server != null) {
            server.stop(0);
            System.out.println("IPOS-SA API Server stopped");
        }
    }

    /**
     * Reads the full request body from an HTTP exchange as a string.
     *
     * @param exchange the HTTP exchange containing the request
     * @return the request body as a string
     * @throws IOException if reading the body fails
     */
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

    /**
     * Sends a JSON response back to the caller.
     * Sets Content-Type to application/json and allows cross-origin requests.
     *
     * @param exchange   the HTTP exchange to respond to
     * @param statusCode the HTTP status code (e.g. 200, 400, 500)
     * @param response   the JSON response body string
     * @throws IOException if writing the response fails
     */
    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    // INVENTORY HANDLERS METHODS


    /**
     * Handles GET /api/inventory/catalogue
     * Returns a JSON array of all active catalogue items including
     * item ID, description, package cost and availability.
     */
    class GetCatalogueHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                try {
                    // retireves all the active items in the catalogue
                    var items = catalogueService.getAllActiveItems();

                    // builds a JSON string containing each of the active item, and their accociated details
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

                    // the JSON string is posted to the calling endpoint
                    sendResponse(exchange, 200, json.toString());
                } catch (Exception e) {
                    sendResponse(exchange, 500, "{\"error\":\"" + e.getMessage() + "\"}");
                }
            } else {
                sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
            }
        }
    }

    /**
     * Handles POST /api/inventory/deduct
     * Deducts a specified quantity from a catalogue item's stock.
     *
     */
    class DeductStockHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                try {
                    // reads the message sent from the sending endpoint
                    String body = readBody(exchange);

                    // extracts the item id and quantity of the item whose stock is to be deducted
                    String itemId = extractValue(body, "itemId");
                    int quantity = Integer.parseInt(extractValue(body, "quantity"));

                    // deducts the stock of the tiem and returns a true value to mark the deduction as successful
                    boolean success = catalogueService.UpdateCatalogue(itemId, quantity);
                    if (success) {

                        // sends a response back to the endpoint that the request has been completed
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

    /**
     * Handles POST /api/orders/place
     * Places a new order for a merchant. Parses the merchant ID and items
     * array from the request body, validates the merchant account, calculates
     * the discount rate and delegates to OrderService.
     */
    class PlaceOrderHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                try {
                    // reads the message send by the calling endpoint
                    String body = readBody(exchange);

                    // extracts the merchant id that the order belongs to
                    String merchantId = extractValue(body, "merchantId");

                    // Gets the merchant account
                    MerchantAccount account = accountService.getAccount(merchantId);
                    if (account == null) {
                        sendResponse(exchange, 400, "{\"error\":\"Merchant not found: " + merchantId + "\"}");
                        return;
                    }

                    // Generates an order ID using the current timestamp
                    String orderId = "ORD_" + System.currentTimeMillis();

                    // Parses the items array in the order JSON
                    java.util.List<OrderItem> orderItems = new java.util.ArrayList<>();
                    int itemsStart = body.indexOf("\"items\"");
                    if (itemsStart != -1) {
                        int arrStart = body.indexOf("[", itemsStart);
                        int arrEnd   = body.indexOf("]", arrStart);
                        String itemsArr = body.substring(arrStart + 1, arrEnd);
                        String[] itemObjs = itemsArr.split("\\},\\s*\\{");
                        for (String itemObj : itemObjs) {
                            String cleaned = itemObj.replace("{", "").replace("}", "");
                            String itemId  = extractValue("{" + cleaned + "}", "itemId");
                            String qtyStr  = extractValue("{" + cleaned + "}", "quantity");
                            if (!itemId.isEmpty() && !qtyStr.isEmpty()) {

                                // Parses rhe quanity and price of each item and stores it as an individual order item, for picking
                                int qty      = Integer.parseInt(qtyStr);
                                double price = catalogueService.getItemPrice(itemId);
                                orderItems.add(new OrderItem(itemId, qty, price));
                            }
                        }
                    }

                    // Builds an order and places it with the merchant's discount rate
                    Order order = new Order(orderId, merchantId, java.time.LocalDate.now(), orderItems);

                    // Gets discount rate
                    double discountRate = accountService.getDiscountRate(merchantId);

                    // Places order and sends a response back to the calling endpoint that the request is complete
                    boolean success = orderService.placeOrder(order, account, discountRate);
                    if (success) {
                        sendResponse(exchange, 200, "{\"orderId\":\"" + orderId + "\"}");
                    } else {
                        sendResponse(exchange, 400, "{\"error\":\"Failed to place order\"}");
                    }
                } catch (Exception e) {
                    sendResponse(exchange, 500, "{\"error\":\"" + e.getMessage() + "\"}");
                }
            } else {
                sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
            }
        }
    }

    /**
     * Handles GET /api/orders/track?orderID=xxx
     * Returns tracking details for a specific order including status,
     * dispatch date, courier name and expected delivery date.
     */
    class TrackDeliveryHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                try {
                    String query = exchange.getRequestURI().getQuery();

                    // retrieves the order ID and the details of the order
                    String orderId = extractQueryParam(query, "orderID");
                    String tracking = orderService.getOrderDetailsText(orderId);

                    // creates a response, sending the order details of that order through to the endpoint
                    sendResponse(exchange, 200, "{\"tracking\":\"" + escapeJson(tracking) + "\"}");
                } catch (Exception e) {
                    sendResponse(exchange, 500, "{\"error\":\"" + e.getMessage() + "\"}");
                }
            } else {
                sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
            }
        }
    }


    /**
     * Handles GET /api/orders/balance?merchantID=xxx
     * Returns the current outstanding balance for a merchant.
     */
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

    /**
     * Handles GET /api/orders/invoice?orderID=xxx
     * Returns the invoice details for a specific order as a formatted string.
     */
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

    /**
     * Handles GET /api/orders/status?merchantID=xxx&status=xxx
     * Checks whether a merchant's account status matches the given status string.
     * Returns { "matches": true } or { "matches": false }.
     */
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

    /**
     * Handles GET /api/orders/discount?merchantID=xxx
     * Returns the applicable discount rate percentage for a merchant.
     */
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

    /**
     * Handles GET /api/orders/discount?merchantID=xxx
     * Returns the applicable discount rate percentage for a merchant.
     */
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

    /**
     * Handles POST /api/membership/request
     * Submits a new commercial membership application on behalf of IPOS-CA.
     * Inserts the application into the commercial_applications table with
     * a status of pending for review by InfoPharma staff.
     */
    class RequestMembershipHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                try {
                    // reads the JSON sent by the IPOS-PU endpoint, and extract the strings related to the membership account
                    String body = readBody(exchange);
                    String companyName = extractValue(body, "companyName");
                    String registrationNumber = extractValue(body, "companyRegNumber");
                    String directors = extractValue(body, "directorName");
                    String businessType = extractValue(body, "businessType");
                    String address = extractValue(body, "address");
                    String email = extractValue(body, "userEmail");
                    String phone = extractValue(body, "phone");
                    String fax = extractValue(body, "fax");
                    boolean preferPhysicalMail = "true".equals(extractValue(body, "preferPhysicalMail"));

                    // Connects to the database and insearts the extracted details as a new record in the commercial membership table
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
                        // sends a response back to the IPOS-PU endpoint that the submission of the membership application was successful
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

    /**
     * Extracts a value from a JSON string by key.
     * Handles both string values (quoted) and primitive values (boolean, number).
     * Note: This is a simple parser — does not handle nested objects or arrays.
     *
     * @param json the JSON string to parse
     * @param key  the key whose value to extract
     * @return the extracted value as a string, or empty string if not found
     */
    private String extractValue(String json, String key) {
        String search = "\"" + key + "\"";
        int keyIndex = json.indexOf(search);
        if (keyIndex == -1) return "";

        // Finds the colon after the key
        int colonIndex = json.indexOf(":", keyIndex + search.length());
        if (colonIndex == -1) return "";

        // Skips whitespace after colon
        int start = colonIndex + 1;
        while (start < json.length() && Character.isWhitespace(json.charAt(start))) {
            start++;
        }

        if (start >= json.length()) return "";

        // Extract quoted String value
        if (json.charAt(start) == '"') {
            start++;
            int end = json.indexOf("\"", start);
            if (end == -1) return "";
            return json.substring(start, end);
        }

        // extract boolean or number value
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

    /**
     * Extracts a query parameter value from a URL query string.
     * For example, given "merchantID=M001&status=normal" and key "merchantID",
     * returns "M001".
     *
     * @param query the URL query string
     * @param key   the parameter name to extract
     * @return the parameter value, or empty string if not found
     */
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

    /**
     * Escapes special characters in a string for safe inclusion in a JSON response.
     * Handles backslashes, quotes, newlines and carriage returns.
     *
     * @param value the string to escape
     * @return the escaped string, or empty string if the value is null
     */
    private String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}
