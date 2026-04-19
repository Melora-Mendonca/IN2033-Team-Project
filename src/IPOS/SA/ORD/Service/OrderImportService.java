package IPOS.SA.ORD.Service;

import IPOS.SA.Comms.PUClient.PUOrderClient;
import IPOS.SA.DB.DBConnection;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.ResultSet;
import java.time.LocalDate;

/**
 * Service responsible for importing undelivered orders from IPOS-PU into IPOS-SA.
 * Called from OrderManagement when the Sync from IPOS-PU button is clicked.
 */
public class OrderImportService {

    /** Database connection used for all queries and inserts. */
    private final DBConnection db;

    /**
     * Default constructor — initialises the service with a database connection.
     */
    public OrderImportService() {
        this.db = new DBConnection();
    }

    /**
     * Fetches undelivered orders from IPOS-PU and imports any that do not
     * already exist in the database.
     *
     * For each order in the JSON response:
     * - Skips entries with missing order ID or email
     * - Looks up the merchant by email — skips if not found
     * - Skips orders that already exist in the order table
     * - Inserts the order with status pending
     * - Inserts all line items if present in the JSON
     *
     * @return the number of new orders successfully imported
     * @throws Exception if the PUOrderClient call fails or a database error occurs
     */
    public int importUndeliveredOrders() throws Exception {
        String json       = PUOrderClient.fetchUndeliveredOrders();
        JSONArray orders  = new JSONArray(json);
        int imported      = 0;

        for (int i = 0; i < orders.length(); i++) {
            JSONObject order = orders.getJSONObject(i);

            // Try multiple possible field names for order ID, email and date
            String orderId = str(order, "orderId", "order_id", "id");
            String email   = str(order, "memberName", "email", "merchantEmail");
            String dateStr = str(order, "orderDate", "order_date", "date", "createdAt");

            // Skip entries missing the required order ID or merchant email
            if (orderId.isEmpty() || email.isEmpty()) {
                System.out.println("OrderImportService: skipping entry with missing orderId/email");
                continue;
            }

            // Look up the merchant by email — skip if no matching merchant found
            ResultSet merchantRs = db.query(
                    "SELECT merchant_id FROM merchant WHERE email = ?", email);
            if (!merchantRs.next()) {
                System.out.println("OrderImportService: no merchant found for email " +
                        email + ", skipping order " + orderId);
                continue;
            }
            String merchantId = merchantRs.getString("merchant_id");

            // Skip if this order already exists in the database
            ResultSet existing = db.query(
                    "SELECT order_id FROM `order` WHERE order_id = ?", orderId);
            if (existing.next()) continue;

            // Parse order date — fall back to today if missing or unparseable
            LocalDate orderDate;
            try {
                orderDate = dateStr.isEmpty() ? LocalDate.now()
                        : LocalDate.parse(dateStr.substring(0, 10));
            } catch (Exception e) {
                orderDate = LocalDate.now();
            }

            // Try multiple possible field names for the order total
            double total = dbl(order, "totalAmount", "total_amount", "totalValue", "total");

            // Insert the order with status pending and no discount applied
            db.update(
                    "INSERT INTO `order` (order_id, merchant_id, order_date, status, " +
                            "total_amount, discount_applied, final_amount) " +
                            "VALUES (?, ?, ?, 'pending', ?, 0, ?)",
                    orderId,
                    merchantId,
                    java.sql.Date.valueOf(orderDate),
                    total,
                    total
            );

            // Insert line items if the order contains an items array
            if (order.has("items") && !order.isNull("items")) {
                JSONArray items = order.getJSONArray("items");
                for (int j = 0; j < items.length(); j++) {
                    JSONObject item = items.getJSONObject(j);

                    // Try multiple possible field names for item ID, quantity and price
                    String itemId = str(item, "productId", "itemId", "item_id",
                            "product_id", "catalogueItemId", "catalogue_item_id");
                    int    qty    = num(item, "quantity", "qty", "amount");
                    double price  = dbl(item, "unitPrice", "unit_price", "price");

                    // Skip line items with no recognisable item ID
                    if (itemId.isEmpty()) continue;

                    db.update(
                            "INSERT INTO orderitem " +
                                    "(order_id, catalogue_item_id, quantity, unit_price, total_price) " +
                                    "VALUES (?, ?, ?, ?, ?)",
                            orderId, itemId, qty, price, qty * price
                    );
                }
            }

            System.out.println("OrderImportService: imported order " + orderId +
                    " for merchant " + merchantId);
            imported++;
        }

        return imported;
    }

    /**
     * Safely retrieves a string value from a JSONObject by trying multiple
     * possible key names in order. Returns the first match found.
     * Returns an empty string if none of the keys exist or all values are null.
     *
     * @param obj  the JSONObject to read from
     * @param keys the candidate key names to try in order
     * @return the string value of the first matching key, or empty string if none found
     */
    private String str(JSONObject obj, String... keys) {
        for (String key : keys) {
            if (obj.has(key) && !obj.isNull(key)) return obj.get(key).toString();
        }
        return "";
    }

    /**
     * Safely retrieves a double value from a JSONObject by trying multiple
     * possible key names in order. Returns the first match found.
     * Returns 0.0 if none of the keys exist, all values are null or parsing fails.
     *
     * @param obj  the JSONObject to read from
     * @param keys the candidate key names to try in order
     * @return the double value of the first matching key, or 0.0 if none found
     */
    private double dbl(JSONObject obj, String... keys) {
        for (String key : keys) {
            if (obj.has(key) && !obj.isNull(key)) {
                try { return obj.getDouble(key); } catch (Exception ignored) {}
            }
        }
        return 0.0;
    }

    /**
     * Safely retrieves an integer value from a JSONObject by trying multiple
     * possible key names in order. Returns the first match found.
     * Returns 0 if none of the keys exist, all values are null or parsing fails.
     *
     * @param obj  the JSONObject to read from
     * @param keys the candidate key names to try in order
     * @return the integer value of the first matching key, or 0 if none found
     */
    private int num(JSONObject obj, String... keys) {
        for (String key : keys) {
            if (obj.has(key) && !obj.isNull(key)) {
                try { return obj.getInt(key); } catch (Exception ignored) {}
            }
        }
        return 0;
    }
}
