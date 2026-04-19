package IPOS.SA.ACC.Model;

/**
 * Represents a flexible tiered discount plan for a merchant account.
 * The discount percentage applied depends on the merchant's monthly order total.
 * Three tiers are defined by two thresholds ; lower and middle.
 *
 *  * @see DiscountPlan
 *  */
public class FlexibleDiscountPlan extends DiscountPlan {

    // Orders below this threshold recieve the lowe discount rate
    private final double lowerThreshold;
    // Orders between the lower threshold and this value receive the middle discount rate
    private final double middleThreshold;
    // Discount rate applied when monthly total is below the lowe threshold
    private final double lowerRate;
    // Discount rate applied when monthly total is between the two thresholds
    private final double middleRate;
    // Disocunt rate applied when the monthly total exceeds the middle threshold
    private final double upperRate;

    /**
     * Constructor ; creates a flexible tiered discount plan.
     *
     * @param planName the name of the discount plan
     * @param lowerThreshold the lower order total threshold
     * @param middleThreshold the middle order total threshold
     * @param lowerRate discount percentage for totals below lower threshold
     * @param middleRate discount percentage for totals between thresholds
     * @param upperRate discount percentage for totals above middle threshold
     */
    public FlexibleDiscountPlan(String planName,
                                double lowerThreshold,
                                double middleThreshold,
                                double lowerRate,
                                double middleRate,
                                double upperRate) {
        super(planName);
        this.lowerThreshold = lowerThreshold;
        this.middleThreshold = middleThreshold;
        this.lowerRate = lowerRate;
        this.middleRate = middleRate;
        this.upperRate = upperRate;
    }

    /**
     * Calculates the discount amount based on the merchant's monthly order total.
     * The applicable rate is determined by which tier the total falls into.
     *
     * @param monthlyOrderTotal the merchant's total order value for the month
     * @return the discount amount to deduct from the order total
     */
    @Override
    public double calculateDiscount(double monthlyOrderTotal) {
        if (monthlyOrderTotal < lowerThreshold) {
            return monthlyOrderTotal * (lowerRate / 100.0);
        }
        if (monthlyOrderTotal <= middleThreshold) {
            return monthlyOrderTotal * (middleRate / 100.0);
        }
        return monthlyOrderTotal * (upperRate / 100.0);
    }
}