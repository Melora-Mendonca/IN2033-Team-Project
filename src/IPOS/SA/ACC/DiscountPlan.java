package IPOS.SA.ACC;

public abstract class DiscountPlan {

    public final String planName;

    public DiscountPlan(String planName) {
        this.planName = planName;
    }

    public String getPlanName() {
        return planName;
    }

    /**
     * Calculates discount amount for a given order value.
     *
     * @param orderAmount total order value
     * @return discount amount
     */

    public abstract double calculateDiscount(double orderAmount);
}
