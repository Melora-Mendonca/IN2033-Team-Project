package IPOS.SA.ACC;

public class OrderService {

    private final AccountService accountService;
    private final InvoiceService invoiceService;

    public OrderService(AccountService accountService, InvoiceService invoiceService) {
        this.accountService = accountService;
        this.invoiceService = invoiceService;
    }

    public Invoice placeOrder(Order order, MerchantAccount account) {
        double total = order.calculateOrderTotal();

        if (!accountService.canMerchantPlaceOrder(account, total)) {
            return null;
        }

        accountService.applyOrderToAccount(account, total);
        return invoiceService.generateInvoice(order, account);
    }

    public void updateOrderStatus(Order order, OrderStatus status) {
        order.setStatus(status);
    }
}