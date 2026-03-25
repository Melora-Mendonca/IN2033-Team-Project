package IPOS.SA.ACC.Model;

import IPOS.SA.ACC.AccountStatus;

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
    private double discountPercentage; // For database storage

    // Original constructor
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

        // Extract discount percentage
        if (discountPlan instanceof FixedDiscountPlan) {
            this.discountPercentage = ((FixedDiscountPlan) discountPlan).getPercentage();
        }
    }

    // Constructor for database loading
    public MerchantAccount(String merchantId, String businessName, String email,
                           String phone, String address, double creditLimit,
                           double outstandingBalance, String status, double discountPercentage) {
        this.merchantId = merchantId;
        this.businessName = businessName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.creditLimit = creditLimit;
        this.outstandingBalance = outstandingBalance;
        this.status = AccountStatus.valueOf(status.toUpperCase());
        this.discountPercentage = discountPercentage;
        this.discountPlan = new FixedDiscountPlan("Fixed Plan", discountPercentage);
        this.lastPaymentDate = LocalDate.now();
    }

    // Getters
    public String getMerchantId() { return merchantId; }
    public String getBusinessName() { return businessName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getAddress() { return address; }
    public double getCreditLimit() { return creditLimit; }
    public double getOutstandingBalance() { return outstandingBalance; }
    public DiscountPlan getDiscountPlan() { return discountPlan; }
    public AccountStatus getStatus() { return status; }
    public LocalDate getLastPaymentDate() { return lastPaymentDate; }
    public double getDiscountPercentage() { return discountPercentage; }

    // Setters
    public void setBusinessName(String businessName) { this.businessName = businessName; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setAddress(String address) { this.address = address; }
    public void setCreditLimit(double creditLimit) { this.creditLimit = creditLimit; }
    public void setDiscountPlan(DiscountPlan discountPlan) {
        this.discountPlan = discountPlan;
        if (discountPlan instanceof FixedDiscountPlan) {
            this.discountPercentage = ((FixedDiscountPlan) discountPlan).getPercentage();
        }
    }
    public void setStatus(AccountStatus status) { this.status = status; }
    public void setOutstandingBalance(double amount) { this.outstandingBalance = amount; }

    // Business methods
    public boolean canPlaceOrder(double orderValue) {
        return status == AccountStatus.NORMAL && (outstandingBalance + orderValue) <= creditLimit;
    }

    public void recordPayment(double paymentAmount) {
        outstandingBalance -= paymentAmount;
        if (outstandingBalance <= 0) {
            outstandingBalance = 0;
        }
        lastPaymentDate = LocalDate.now();
    }
}




