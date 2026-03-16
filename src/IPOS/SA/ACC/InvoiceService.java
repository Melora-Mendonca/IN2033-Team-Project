package IPOS.SA.ACC;

import java.time.LocalDate;
import IPOS.SA.ACC.MerchantAccount;

public class InvoiceService {

    public Invoice generateInvoice(Order order, MerchantAccount account) {
        double grossTotal = order.calculateOrderTotal();
        double discountAmount = account.getDiscountPlan().calculateDiscount(grossTotal);
        double finalTotal = grossTotal - discountAmount;

        return new Invoice(
                "INV-" + order.getOrderId(),
                order.getOrderId(),
                order.getMerchantId(),
                LocalDate.now(),
                order.getItems(),
                grossTotal,
                discountAmount,
                finalTotal
        );
    }
}