package IPOS.SA.ORD;

import IPOS.SA.ACC.*;
import IPOS.SA.DB.InvoiceDBConnector;
import IPOS.SA.DB.OrderDBConnector;

public class OrderService {

    private final AccountService accountService;
    private final InvoiceService invoiceService;
    private final OrderDBConnector orderDB;
    private final InvoiceDBConnector invoiceDB;

    public OrderService(AccountService accountService, InvoiceService invoiceService) {
        this.accountService = accountService;
        this.invoiceService = invoiceService;
        this.orderDB = new OrderDBConnector();
        this.invoiceDB = new InvoiceDBConnector();
    }

    public Invoice placeOrder(Order order, MerchantAccount account) {
        double grossTotal = order.calculateOrderTotal();

        if (!accountService.canMerchantPlaceOrder(account, grossTotal)) {
            return null;
        }

        accountService.applyOrderToAccount(account, grossTotal);

        double discountAmount = account.getDiscountPlan().calculateDiscount(grossTotal);
        double finalTotal = grossTotal - discountAmount;

        orderDB.saveOrder(order, grossTotal, discountAmount, finalTotal);

        for (OrderItem item : order.getItems()) {
            orderDB.reduceStock(item.getItemId(), item.getQuantity());
        }

        Invoice invoice = invoiceService.generateInvoice(order, account);
        invoiceDB.saveInvoice(invoice);

        return invoice;
    }

    public void updateOrderStatus(Order order, OrderStatus status) {
        order.setStatus(status);
        orderDB.updateOrderStatus(order.getOrderId(), status);
    }
}
