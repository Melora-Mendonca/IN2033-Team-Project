package IPOS.SA.ACC.Service;

import java.time.LocalDate;

import IPOS.SA.ACC.Model.Invoice;
import IPOS.SA.ACC.Model.MerchantAccount;
import IPOS.SA.ORD.Order;

/**
 * Service responsible for generating invoices based on orders
 * and applying the merchant's discount rules.
 */
public class InvoiceService {

    /**
     * Generates an invoice for a given order and merchant account.
     *
     * @param order the order from which the invoice is generated
     * @param account the merchant account containing discount rules
     * @return a fully constructed Invoice object
     */
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