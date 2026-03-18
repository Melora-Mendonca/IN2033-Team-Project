package IPOS.SA.ACC;

import java.util.HashMap;
import java.util.Map;

public class AccountService {

    private final Map<String, MerchantAccount> accounts = new HashMap<>();

    public void addAccount(MerchantAccount account) {
        accounts.put(account.getMerchantId(), account);
    }

    public MerchantAccount getAccount(String merchantId) {
        return accounts.get(merchantId);
    }

    public boolean accountExists(String merchantId) {
        return accounts.containsKey(merchantId);
    }

    public boolean hasRequiredActivationData(MerchantAccount account) {
        return account.getEmail() != null && !account.getEmail().isBlank()
                && account.getPhone() != null && !account.getPhone().isBlank()
                && account.getAddress() != null && !account.getAddress().isBlank()
                && account.getCreditLimit() >= 0
                && account.getDiscountPlan() != null;
    }

    public boolean canMerchantPlaceOrder(MerchantAccount account, double orderValue) {
        return account.canPlaceOrder(orderValue);
    }

    public void applyOrderToAccount(MerchantAccount account, double orderValue) {
        double discount = account.getDiscountPlan().calculateDiscount(orderValue);
        double finalAmount = orderValue - discount;
        account.addToOutstandingBalance(finalAmount);
    }

    public void updateAccountStatus(MerchantAccount account, int daysOverdue) {
        if (daysOverdue > 30) {
            account.setStatus(AccountStatus.IN_DEFAULT);
        } else if (daysOverdue > 15) {
            account.setStatus(AccountStatus.SUSPENDED);
        } else {
            account.setStatus(AccountStatus.NORMAL);
        }
    }

    public void recordPayment(MerchantAccount account, double paymentAmount) {
        account.recordPayment(paymentAmount);
        if (account.getOutstandingBalance() == 0
                && account.getStatus() == AccountStatus.SUSPENDED) {
            account.setStatus(AccountStatus.NORMAL);
        }
    }

    public void restoreDefaultedAccount(MerchantAccount account, boolean directorApproved) {
        if (directorApproved && account.getStatus() == AccountStatus.IN_DEFAULT) {
            account.setStatus(AccountStatus.NORMAL);
        }
    }
}