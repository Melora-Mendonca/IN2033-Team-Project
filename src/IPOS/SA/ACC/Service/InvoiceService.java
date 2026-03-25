package IPOS.SA.ACC.Service;

import java.time.LocalDate;

import IPOS.SA.ACC.Model.Invoice;
import IPOS.SA.ACC.Model.MerchantAccount;
import IPOS.SA.ORD.Order;

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