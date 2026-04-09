package IPOS.SA.Comms.Interfaces;

/**
 * Defines order-related operations exposed by the ordering component.
 */
public interface IOrderService {
    /**
     * Places a re-stock order for a merchant.
     *
     * @param merchantID identifier of the merchant placing the order
     * @param orderDetails textual order payload (format defined by your team)
     * @return a reference for the created order (e.g., an order ID) if successful
     */
    public String placeRestockOrder(String merchantID, String orderDetails);

    /**
     * Returns delivery tracking information for an existing order.
     *
     * @param orderID identifier of the order to track
     * @return a textual tracking status or reference
     */
    public String trackDelivery(String orderID);

    /**
     * Queries the current outstanding balance for a merchant.
     *
     * @param merchantID identifier of the merchant
     * @return outstanding balance value
     */
    public double queryOutstandingBalance(String merchantID);

    /**
     * Retrieves invoice information for an order.
     *
     * @param orderID identifier of the order
     * @return invoice reference or invoice content (as a String)
     */
    public String getInvoice(String orderID);

    /**
     * Checks whether a merchant account matches a given status.
     *
     * @param merchantID identifier of the merchant account
     * @param status status to check against (format defined by your team)
     * @return true if the account matches the status, false otherwise
     */
    public boolean getAccStatus(String merchantID, String status);

    /**
     * Displays the discount plan for a merchant account.
     *
     * @param merchantID identifier of the merchant
     * @return true if the plan was successfully retrieved and displayed, false otherwise, false otherwise
     */
    public boolean viewDiscountPlan(String merchantID);

    /**
     * Displays the credit limit for a merchant account.
     *
     * @param merchantID identifier of the merchant
     * @return true if the credit Limit was successfully retrieved and displayed, false otherwise, false otherwise
     */
    public boolean viewCreditLimit(String merchantID);
}
