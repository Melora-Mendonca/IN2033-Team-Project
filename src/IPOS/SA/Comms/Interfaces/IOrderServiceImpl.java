package IPOS.SA.Comms.Interfaces;

import IPOS.SA.DB.DBConnection;

import java.sql.ResultSet;

/**
 * Default implementation of {@link IOrderService}.
 *
 * This class provides order-related behaviour for the subsystem.
 */

public class IOrderServiceImpl implements IOrderService {
    private final DBConnection db;

    public IOrderServiceImpl() {
        this.db = new DBConnection();
    }

    /**
     * Places a restock order for a merchant.
     *
     * @param merchantID unique identifier of the merchant placing the order
     * @param orderDetails textual representation of order details
     * @return a reference string representing the created order
     */
    public String placeRestockOrder(String merchantID, String orderDetails) {
        try {
            String orderId = "ORD_" + System.currentTimeMillis();
            int rows = db.update(
                    "INSERT INTO `Order` (order_id, merchant_id, order_date, status, total_amount) " +
                            "VALUES (?, ?, CURRENT_DATE(), 'pending', 0)",
                    orderId, merchantID
            );
            return rows > 0 ? orderId : "FAILED";
        } catch (Exception e) {
            return "FAILED: " + e.getMessage();
        }
    }

    /**
     * Retrieves tracking information for a previously placed order.
     *
     * @param orderID unique identifier of the order
     * @return delivery tracking status or reference
     */
    public String trackDelivery(String orderID) {
        try {
            ResultSet rs = db.query(
                    "SELECT status, courier_name, courier_ref_no FROM `Order` WHERE order_id = ?",
                    orderID
            );
            if (rs.next()) {
                String status = rs.getString("status");
                String courier = rs.getString("courier_name");
                String tracking = rs.getString("courier_ref_no");

                if (courier != null && tracking != null) {
                    return "Status: " + status + ", Courier: " + courier + ", Tracking: " + tracking;
                }
                return "Status: " + status;
            }
            return "Order not found";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    /**
     * Returns the outstanding balance for a merchant account.
     *
     * @param merchantID unique identifier of the merchant
     * @return current outstanding balance as a numeric value
     */

    public double queryOutstandingBalance(String merchantID) {
        try {
            ResultSet rs = db.query(
                    "SELECT outstanding_balance FROM Merchant WHERE merchant_id = ?",
                    merchantID
            );
            return rs.next() ? rs.getDouble("outstanding_balance") : -1;
        } catch (Exception e) {
            return -1;
        }
    }
    /**
     * Retrieves invoice information for a specific order.
     *
     * @param orderID unique identifier of the order
     * @return invoice reference or formatted invoice content
     */
    public String getInvoice(String orderID) {
        try {
            ResultSet rs = db.query(
                    "SELECT invoice_id, total_amount, amount_paid, status FROM Invoice WHERE order_id = ?",
                    orderID
            );
            if (rs.next()) {
                return "Invoice ID: " + rs.getString("invoice_id") +
                        ", Total: £" + rs.getDouble("total_amount") +
                        ", Paid: £" + rs.getDouble("amount_paid") +
                        ", Status: " + rs.getString("status");
            }
            return "Invoice not found for order: " + orderID;
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    /**
     * Checks whether a merchant account matches a specified status.
     *
     * @param merchantID unique identifier of the merchant
     * @param status account status to check (e.g., active, suspended)
     * @return true if the account matches the given status, false otherwise
     */
    public boolean getAccStatus(String merchantID, String status) {
        try {
            ResultSet rs = db.query(
                    "SELECT account_status FROM Merchant WHERE merchant_id = ?",
                    merchantID
            );
            return rs.next() && rs.getString("account_status").equalsIgnoreCase(status);
        } catch (Exception e) {
            return false;
        }
    }
    /**
     * displays the discount plan to a merchant account.
     *
     * @param merchantID unique identifier of the merchant
     * @return true if the discount plan was successfully displayed, false otherwise
     */
    public boolean viewDiscountPlan(String merchantID) {
        try {
            ResultSet rs = db.query(
                    "SELECT fixed_discount_rate, discount_type FROM Merchant WHERE merchant_id = ?",
                    merchantID
            );
            if (rs.next()) {
                double discount = rs.getDouble("fixed_discount_rate");
                String type = rs.getString("discount_type");
                System.out.println("Discount Plan - Type: " + type + ", Rate: " + discount + "%");
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Assigns the credit limit to a merchant account.
     *
     * @param merchantID unique identifier of the merchant
     * @return true if the credit limit was successfully displayed, false otherwise
     */
    public boolean viewCreditLimit(String merchantID) {
        try {
            ResultSet rs = db.query(
                    "SELECT credit_limit FROM Merchant WHERE merchant_id = ?",
                    merchantID
            );
            if (rs.next()) {
                double limit = rs.getDouble("credit_limit");
                System.out.println("Credit Limit: £" + String.format("%.2f", limit));
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}