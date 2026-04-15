package IPOS.SA.ACC.Model;

import IPOS.SA.ACC.AccountStatus;

import java.sql.Date;
import java.time.LocalDate;

public class MerchantAccount {

    private final String merchantId;
    private String businessName;
    private String email;
    private String phone;
    private String fax;
    private String address;
    private double creditLimit;
    private double outstandingBalance;
    private DiscountPlan discountPlan;
    private AccountStatus status;
    private double discountPercentage; // For database storage
    private String businessType;
    private String registrationNumber;
    private String accountStatus;
    private String discountType;
    private double fixedDiscountRate;
    private double flexibleDiscountRate;
    private java.sql.Date registrationDate;
    private boolean isActive;
    private java.sql.Date lastPaymentDate;
    private String username;
    private String password;

    // Original constructor
    public MerchantAccount(String merchantId,
                           String businessName,
                           String businessType,
                           String registrationNumber,
                           String email,
                           String phone,
                           String fax,
                           String address,
                           double creditLimit,
                           double outstandingBalance,
                           String accountStatus,
                           String discountType,
                           double fixedDiscountRate,
                           double flexibleDiscountRate,
                           Date registrationDate,
                           boolean isActive,
                           Date lastPaymentDate,
                           String username) {
        this.merchantId = merchantId;
        this.username = username;  // ← Set username
        this.businessName = businessName;
        this.businessType = businessType;
        this.registrationNumber = registrationNumber;
        this.email = email;
        this.phone = phone;
        this.fax = fax;
        this.address = address;
        this.creditLimit = creditLimit;
        this.outstandingBalance = outstandingBalance;
        this.accountStatus = accountStatus;
        this.discountType = discountType;
        this.fixedDiscountRate = fixedDiscountRate;
        this.flexibleDiscountRate = flexibleDiscountRate;
        this.registrationDate = registrationDate;
        this.isActive = isActive;
        this.lastPaymentDate = lastPaymentDate;

        // Set discount percentage based on discount type
        if ("fixed".equals(discountType)) {
            this.discountPercentage = fixedDiscountRate;  // ← This sets the discount
            this.discountPlan = new FixedDiscountPlan("Fixed Plan", fixedDiscountRate);
        } else {
            this.discountPercentage = flexibleDiscountRate;
            this.discountPlan = new FixedDiscountPlan("Flexible Plan", flexibleDiscountRate);
        }
    }

        // Constructor for database loading
    public MerchantAccount(String merchantId,
                           String businessName,
                           String businessType,
                           String registrationNumber,
                           String email,
                           String phone,
                           String fax,
                           String address,
                           double creditLimit,
                           double outstandingBalance,
                           String accountStatus,
                           String discountType,
                           double fixedDiscountRate,
                           double flexibleDiscountRate,
                           Date registrationDate,
                           boolean isActive,
                           Date lastPaymentDate) {
        this.merchantId = merchantId;
        this.businessName = businessName;
        this.businessType = businessType;
        this.registrationNumber = registrationNumber;
        this.email = email;
        this.phone = phone;
        this.fax = fax;
        this.address = address;
        this.creditLimit = creditLimit;
        this.outstandingBalance = outstandingBalance;
        this.accountStatus = accountStatus;
        this.discountType = discountType;
        this.fixedDiscountRate = fixedDiscountRate;
        this.flexibleDiscountRate = flexibleDiscountRate;
        this.registrationDate = registrationDate;
        this.isActive = isActive;
        this.lastPaymentDate = lastPaymentDate;

        // Set discount percentage based on discount type
        if ("fixed".equals(discountType)) {
            this.discountPercentage = fixedDiscountRate;
            this.discountPlan = new FixedDiscountPlan("Fixed Plan", fixedDiscountRate);
        } else {
            this.discountPercentage = flexibleDiscountRate;
            this.discountPlan = new FixedDiscountPlan("Flexible Plan", flexibleDiscountRate);
        }

        // Set status
        if (accountStatus != null) {
            this.status = AccountStatus.valueOf(accountStatus.toUpperCase());
        } else {
            this.status = AccountStatus.NORMAL;
        }

        // Convert sql.Date to LocalDate
        if (lastPaymentDate != null) {
            this.lastPaymentDate = Date.valueOf(lastPaymentDate.toLocalDate());
        } else {
            this.lastPaymentDate = Date.valueOf(LocalDate.now());
        }
    }

    // Constructor for loading from database (simplified version used in getAccount)
    public MerchantAccount(String merchantId,
                           String businessName,
                           String email,
                           String phone,
                           String fax,
                           String address,
                           double creditLimit,
                           double outstandingBalance,
                           String accountStatus,
                           double fixedDiscountRate,
                           String username,
                           String password) {
        this.merchantId = merchantId;
        this.businessName = businessName;
        this.email = email;
        this.phone = phone;
        this.fax = fax;
        this.address = address;
        this.creditLimit = creditLimit;
        this.outstandingBalance = outstandingBalance;
        this.accountStatus = accountStatus;
        this.fixedDiscountRate = fixedDiscountRate;
        this.discountPercentage = fixedDiscountRate;
        this.discountPlan = new FixedDiscountPlan("Fixed Plan", fixedDiscountRate);
        this.username = username;
        this.password = password;

        if (accountStatus != null) {
            this.status = AccountStatus.valueOf(accountStatus.toUpperCase());
        } else {
            this.status = AccountStatus.NORMAL;
        }

        this.isActive = true;
        this.lastPaymentDate = Date.valueOf(LocalDate.now());
    }

    // Getters
    public String getMerchantId() { return merchantId; }
    public String getBusinessName() { return businessName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getFax() { return fax; }
    public String getAddress() { return address; }
    public double getCreditLimit() { return creditLimit; }
    public double getOutstandingBalance() { return outstandingBalance; }
    public DiscountPlan getDiscountPlan() { return discountPlan; }
    public AccountStatus getStatus() { return status; }
    public LocalDate getLastPaymentDate() { return lastPaymentDate.toLocalDate(); }
    public double getDiscountPercentage() { return discountPercentage; }
    public String getBusinessType() {return businessType;}
    public String getRegistrationNumber() { return registrationNumber; }
    public String getAccountStatus() { return accountStatus; }
    public String getDiscountType() { return discountType; }
    public double getFixedDiscountRate() { return fixedDiscountRate; }
    public double getFlexibleDiscountRate() { return flexibleDiscountRate; }
    public java.sql.Date getRegistrationDate() { return registrationDate; }
    public boolean isActive() { return isActive; }
    public String getPassword() {
        return password;
    }
    public String getUsername() {
        return username;
    }

    // Setters
    public void setBusinessName(String businessName) { this.businessName = businessName; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setFax(String fax) { this.fax = fax; }
    public void setAddress(String address) { this.address = address; }
    public void setCreditLimit(double creditLimit) { this.creditLimit = creditLimit; }
    public void setDiscountPlan(DiscountPlan discountPlan) {
        this.discountPlan = discountPlan;
        if (discountPlan instanceof FixedDiscountPlan) {
            this.discountPercentage = ((FixedDiscountPlan) discountPlan).getPercentage();
        }
    }

        public void setDiscountType(String discountType) {
            this.discountType = discountType;
        }

        public void setFlexibleDiscountRate(double flexibleDiscountRate) {
            this.flexibleDiscountRate = flexibleDiscountRate;
        }

        public void setFixedDiscountRate(double fixedDiscountRate) {
            this.fixedDiscountRate = fixedDiscountRate;
        }

    public void setUsername(String username) {
        this.username = username;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public void setStatus(AccountStatus status) { this.status = status; }
    public void setOutstandingBalance(double amount) { this.outstandingBalance = amount; }
    public void setBusinessType(String businessType) {this.businessType = businessType;}
    public void setRegistrationDate(java.sql.Date registrationDate) { this.registrationDate = registrationDate; }
    public void setActive(boolean isActive) { this.isActive = isActive; }
    public void setLastPaymentDate(LocalDate lastPaymentDate) { this.lastPaymentDate = Date.valueOf(lastPaymentDate); }
    public void setDiscountPercentage(double discountPercentage) { this.discountPercentage = discountPercentage; }

    // Business methods
    public boolean canPlaceOrder(double orderValue) {
        return status == AccountStatus.NORMAL && (outstandingBalance + orderValue) <= creditLimit;
    }

    public void recordPayment(double paymentAmount) {
        outstandingBalance -= paymentAmount;
        if (outstandingBalance <= 0) {
            outstandingBalance = 0;
        }
        lastPaymentDate = Date.valueOf(LocalDate.now());
    }
}




