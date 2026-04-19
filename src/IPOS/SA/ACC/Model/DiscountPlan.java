package IPOS.SA.ACC.Model;

/**
 * Abstract base class representing a discount plan for a merchant account.
 * Different discount plan types extend this class and implement
 * their own discount calculation logic.
 *
 * @see FixedDiscountPlan
 */

public abstract class DiscountPlan {

    public final String planName;

    /**
     * Constructor — sets the name of the discount plan.
     *
     * @param planName the name of the discount plan
     */
    public DiscountPlan(String planName) {
        this.planName = planName;
    }

    /**
     * Returns the name of this discount plan.
     *
     * @return the plan name
     */
    public String getPlanName() {
        return planName;
    }

    /**
     * Calculates the discount amount to apply to an order.
     * Each subclass implements this differently based on the discount type.
     *
     * @param orderAmount the total order amount before discount
     * @return the discount amount to deduct from the order total
     */
    public abstract double calculateDiscount(double orderAmount);
}
