package IPOS.SA.ACC.Model;

/**
 * Represents a fixed percentage discount plan for a merchant account.
 * A fixed discount applies the same percentage reduction to every order
 * regardless of the order amount.
 *
 * @see DiscountPlan
 */
public class FixedDiscountPlan extends DiscountPlan {

    private final double percentage;

    /**
     * Constructor — creates a fixed discount plan with the given name and percentage.
     *
     * @param planName   the name of the discount plan
     * @param percentage the fixed discount percentage (e.g. 10.0 for 10%)
     */
    public FixedDiscountPlan(String planName, double percentage) {
        super(planName);
        this.percentage = percentage;
    }

    /**
     * Returns the fixed discount percentage.
     *
     * @return the discount percentage
     */
    public double getPercentage() {
        return percentage;
    }

    /**
     * Calculates the discount amount for a given order total.
     * Applies the fixed percentage to the order amount.
     *
     * @param orderAmount the total order amount before discount
     * @return the discount amount to deduct from the order total
     */
    @Override
    public double calculateDiscount(double orderAmount) {
        return orderAmount * (percentage / 100.0);
    }

}
