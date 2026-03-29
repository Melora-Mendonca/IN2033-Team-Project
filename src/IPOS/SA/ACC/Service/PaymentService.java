package IPOS.SA.ACC.Service;

/**
 * Service class responsible for handling payment recording by staff.
 */
public class PaymentService {

    private final AccountService accountService;

    /**
     * Constructs a PaymentService with the required AccountService dependency.
     *
     * @param accountService service used to update account balances
     */
    public PaymentService(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * Records a payment against a merchant account.
     * Delegates the balance update to AccountService.
     *
     * @param account the merchant account receiving the payment
     * @param payment the payment being recorded
     */
//    public void recordPayment(MerchantAccount account, Payment payment) {
//        accountService.recordPayment(account, payment.getAmount());
//    }
}