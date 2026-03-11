package IPOS.SA.ACC;

public class FixedDiscountPlan extends DiscountPlan {

    private final double percentage;

    public FixedDiscountPlan(String planName, double percentage) {
        super(planName);
        this.percentage = percentage;
    }

    public double getPercentage() {
        return percentage;
    }

    @Override
    public double calculateDiscount(double orderAmount) {
        return orderAmount * (percentage / 100.0);
    }

}
