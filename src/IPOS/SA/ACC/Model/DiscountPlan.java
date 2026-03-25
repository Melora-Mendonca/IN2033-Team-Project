package IPOS.SA.ACC.Model;

public abstract class DiscountPlan {

    public final String planName;

    public DiscountPlan(String planName) {
        this.planName = planName;
    }

    public String getPlanName() {
        return planName;
    }

    public abstract double calculateDiscount(double orderAmount);
}
