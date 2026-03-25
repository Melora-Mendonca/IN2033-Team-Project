package IPOS.SA.ACC.Model;

public class FlexibleDiscountPlan extends DiscountPlan {

    private final double lowerThreshold;
    private final double middleThreshold;
    private final double lowerRate;
    private final double middleRate;
    private final double upperRate;

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