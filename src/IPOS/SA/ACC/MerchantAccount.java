package IPOS.SA.ACC;

import java.time.LocalDate;

public class MerchantAccount {

    private final String merchantId;
    private String businessName;
    private String email;
    private String phone;
    private String address;

    private double creditLimit;
    private double outstandingBalance;
    private DiscountPlan discountPlan;
    private AccountStatus status;
    private LocalDate lastPaymentDate;

    public MerchantAccount(String merchantId,
                           String businessName,
                           String email,
                           String phone,
                           String address,
                           double creditLimit,
                           DiscountPlan discountPlan) {
    this.merchantId = merchantId;
    this.businessName = businessName;
    this.email = email;
    this.phone = phone;
    this.address = address;
    this.creditLimit = creditLimit;
    this.discountPlan = discountPlan;
    this.outstandingBalance = 0.0;
    this.status = AccountStatus.NORMAL;
    this.lastPaymentDate = LocalDate.now();
    }

    public String getMerchantId() {
        return merchantId;
    }
    public String getBusinessName() {
        return businessName;
    }
    public String getEmail() {
        return email;
    }
    public String getPhone() {
        return phone;
    }
    public String getAddress() {
        return address;
    }
    public double getCreditLimit() {
        return creditLimit;
    }
    public double getOutstandingBalance() {
        return outstandingBalance;
    }
    public DiscountPlan getDiscountPlan() {
        return discountPlan;
    }
    public AccountStatus getStatus() {
        return status;
    }
    public LocalDate getLastPaymentDate() {
        return lastPaymentDate;
    }
    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public void setCreditLimit(double creditLimit) {
        this.creditLimit = creditLimit;
    }
    public void setDiscountPlan(DiscountPlan discountPlan) {
        this.discountPlan = discountPlan;
    }
    public void setStatus(AccountStatus status) {
        this.status = status;
    }
    public boolean canPlaceOrder(double orderValue) {
        return status == AccountStatus.NORMAL
                && (outstandingBalance + orderValue) <= creditLimit;
    }

    public void addToOutstandingBalance(double amount) {
        outstandingBalance += amount;
    }

    public void recordPayment(double paymentAmount) {
        outstandingBalance -= paymentAmount;
        if (outstandingBalance <= 0) {
            outstandingBalance = 0;
        }
        lastPaymentDate = LocalDate.now();
    }
}




