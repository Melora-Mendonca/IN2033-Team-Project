package IPOS.SA.ACC;

public class PaymentService {

    private final AccountService accountService;

    public PaymentService(AccountService accountService) {
        this.accountService = accountService;
    }

    public void recordPayment(MerchantAccount account, Payment payment) {
        accountService.recordPayment(account, payment.getAmount());
    }
}