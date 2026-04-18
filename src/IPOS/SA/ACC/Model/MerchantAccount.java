package IPOS.SA.ACC.Model;

import IPOS.SA.ACC.AccountStatus;

import java.sql.Date;
import java.time.LocalDate;

/**
 * Represents a merchant account in the IPOS-SA system.
 * Stores all business details, financial information, discount plan
 * and account status for a registered merchant.
 *
 */
public class MerchantAccount {

    private final String merchantId; // the Unique ID for the merchant
    private String businessName; // the registered business name for the merchant
    private String email; // the merchant's email for contact
    private String phone; // the merchant's phone number for contact
    private String fax; // the merchant's fax for contact
    private String address; // the merchant's registered address
    private double creditLimit; // the max credit limit allowed for this merchant
    private double outstandingBalance; // the current unpaid balance for the merchant that they owe
    private DiscountPlan discountPlan; // the discount plan assigned to the merchant
    private AccountStatus status; // the current status of the account, either normal, suspended or in default
    private double discountPercentage; // the discount percentage applied to orders placed by the merchant
    private String businessType; // the type of business
    private String registrationNumber; // the company registration number
    private String accountStatus; // the account status stored as a string
    private String discountType; // the type of discount assigned to the merchant, either fixed or flexible
    private double fixedDiscountRate;  // the fixed discount percentage
    private double flexibleDiscountRate; // the flexible discount percentage
    private java.sql.Date registrationDate; // the date that the merchant registered - the date the commercial application was accepted
    private boolean isActive; // whether the merchant account is currently active or not
    private java.sql.Date lastPaymentDate; // the date that the merchant last made a payment
    private String username; // the username of the merchant
    private String password; // ther password of the merchant

    /**
     * Original constructor - used when creating a new merchant account with a username
     *
     * @param merchantId          unique merchant identifier
     * @param businessName        registered business name
     * @param businessType        type of business
     * @param registrationNumber  company registration number
     * @param email               contact email
     * @param phone               contact phone number
     * @param fax                 fax number
     * @param address             registered address
     * @param creditLimit         maximum credit allowed
     * @param outstandingBalance  current outstanding balance
     * @param accountStatus       account status string
     * @param discountType        discount plan type — "fixed" or "flexible"
     * @param fixedDiscountRate   fixed discount percentage
     * @param flexibleDiscountRate flexible discount percentage
     * @param registrationDate    date account was registered
     * @param isActive            whether account is active
     * @param lastPaymentDate     date of last payment
     * @param username            login username for IPOS-CA
     */

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

    /**
     * Database loading constructor — used when retrieving a full merchant
     * record from the database, without a username field.
     *
     * @param merchantId          unique merchant identifier
     * @param businessName        registered business name
     * @param businessType        type of business
     * @param registrationNumber  company registration number
     * @param email               contact email
     * @param phone               contact phone number
     * @param fax                 fax number
     * @param address             registered address
     * @param creditLimit         maximum credit allowed
     * @param outstandingBalance  current outstanding balance
     * @param accountStatus       account status string
     * @param discountType        discount plan type
     * @param fixedDiscountRate   fixed discount percentage
     * @param flexibleDiscountRate flexible discount percentage
     * @param registrationDate    date account was registered
     * @param isActive            whether account is active
     * @param lastPaymentDate     date of last payment
     */
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

        // Set status by converting accountStatus string to AccountStatus enum
        if (accountStatus != null) {
            this.status = AccountStatus.valueOf(accountStatus.toUpperCase());
        } else {
            this.status = AccountStatus.NORMAL;
        }

        // Sets the default last payment date the today if a date is not provided
        if (lastPaymentDate != null) {
            this.lastPaymentDate = Date.valueOf(lastPaymentDate.toLocalDate());
        } else {
            this.lastPaymentDate = Date.valueOf(LocalDate.now());
        }
    }

    /**
     * Simplified constructor — used for lightweight account retrieval
     * where only key fields are needed, including username and password.
     *
     * @param merchantId         unique merchant identifier
     * @param businessName       registered business name
     * @param email              contact email
     * @param phone              contact phone number
     * @param fax                fax number
     * @param address            registered address
     * @param creditLimit        maximum credit allowed
     * @param outstandingBalance current outstanding balance
     * @param accountStatus      account status string
     * @param fixedDiscountRate  fixed discount percentage
     * @param username           login username for IPOS-CA
     * @param password           hashed login password for IPOS-CA
     */
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

        // Converts accountStatus string to AccountStatus Enum
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

    /**
     * Sets the discount plan and updates the discount percentage accordingly.
     *
     * @param discountPlan the new discount plan
     */
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

    /**
     * Checks whether the merchant is eligible to place an order.
     * The account must be in NORMAL status and the new order must not
     * exceed the merchant's credit limit.
     *
     * @param orderValue the value of the new order
     * @return true if the merchant can place the order
     */
    public boolean canPlaceOrder(double orderValue) {
        return status == AccountStatus.NORMAL && (outstandingBalance + orderValue) <= creditLimit;
    }
    /**
    * Records a payment made by the merchant.
    * Reduces the outstanding balance by the payment amount.
    * Balance cannot go below zero.
    *
    * @param paymentAmount the amount paid by the merchant
    */
    public void recordPayment(double paymentAmount) {
        outstandingBalance -= paymentAmount;
        if (outstandingBalance <= 0) {
            outstandingBalance = 0;
        }
        lastPaymentDate = Date.valueOf(LocalDate.now());
    }
}




