package IPOS.SA.ORD.Service;

import IPOS.SA.Comms.PUClient.PUOrderClient;
import IPOS.SA.DB.DBConnection;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.ResultSet;
import java.time.LocalDate;

/**
 * Pulls undelivered orders from IPOS-PU and imports them into the local database.
 *
 * Expected IPOS-PU order JSON fields (tries multiple name variants for each):
 *   orderId / order_id / id
 *   merchantId / merchant_id / merchantID
 *   orderDate / order_date / date
 *   totalAmount / total_amount / total
 *   items[] -> itemId / item_id / productId / catalogueItemId
 *              quantity / qty
 *              unitPrice / unit_price / price
 */
public class OrderImportService {

    private final DBConnection db;

    public OrderImportService() {
        this.db = new DBConnection();
    }

    /**
     * Fetches undelivered orders from IPOS-PU and inserts any new ones as 'pending'.
     *
     * @return number of newly imported orders (duplicates are skipped)
     * @throws Exception if IPOS-PU is unreachable or the DB insert fails
     */
    public int importUndeliveredOrders() throws Exception {
        String json = PUOrderClient.fetchUndeliveredOrders();

        JSONArray orders = new JSONArray(json);
        int imported = 0;

        for (int i = 0; i < orders.length(); i++) {
            JSONObject order = orders.getJSONObject(i);

            String orderId    = str(order, "orderId", "order_id", "id");
            String merchantId = str(order, "merchantId", "merchant_id", "merchantID");
            String dateStr    = str(order, "orderDate", "order_date", "date", "createdAt");

            if (orderId.isEmpty() || merchantId.isEmpty()) {
                System.out.println("OrderImportService: skipping entry with missing orderId/merchantId");
                continue;
            }

            // Skip if already imported
            ResultSet existing = db.query("SELECT order_id FROM `order` WHERE order_id = ?", orderId);
            if (existing.next()) continue;

            LocalDate orderDate;
            try {
                // ISO date strings may carry time component — take first 10 chars
                orderDate = dateStr.isEmpty() ? LocalDate.now()
                        : LocalDate.parse(dateStr.substring(0, 10));
            } catch (Exception e) {
                orderDate = LocalDate.now();
            }

            double total = dbl(order, "totalAmount", "total_amount", "total");

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

            // Import line items if present
            if (order.has("items") && !order.isNull("items")) {
                JSONArray items = order.getJSONArray("items");
                for (int j = 0; j < items.length(); j++) {
                    JSONObject item = items.getJSONObject(j);
                    String itemId = str(item, "itemId", "item_id", "productId",
                                               "product_id", "catalogueItemId", "catalogue_item_id");
                    int qty       = num(item, "quantity", "qty", "amount");
                    double price  = dbl(item, "unitPrice", "unit_price", "price");

                    if (itemId.isEmpty()) continue;

                    db.update(
                            "INSERT INTO orderitem " +
                            "(order_id, catalogue_item_id, quantity, unit_price, total_price) " +
                            "VALUES (?, ?, ?, ?, ?)",
                            orderId, itemId, qty, price, qty * price
                    );
                }
            }

            System.out.println("OrderImportService: imported order " + orderId
                    + " for merchant " + merchantId);
            imported++;
        }

        return imported;
    }

    // ── Field extraction helpers ─────────────────────────────────────────────

    private String str(JSONObject obj, String... keys) {
        for (String key : keys) {
            if (obj.has(key) && !obj.isNull(key)) return obj.get(key).toString();
        }
        return "";
    }

    private double dbl(JSONObject obj, String... keys) {
        for (String key : keys) {
            if (obj.has(key) && !obj.isNull(key)) {
                try { return obj.getDouble(key); } catch (Exception ignored) {}
            }
        }
        return 0.0;
    }

    private int num(JSONObject obj, String... keys) {
        for (String key : keys) {
            if (obj.has(key) && !obj.isNull(key)) {
                try { return obj.getInt(key); } catch (Exception ignored) {}
            }
        }
        return 0;
    }
}
