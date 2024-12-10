package com.cpo.syntax_checker;

import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PayPalService {

    @Value("${paypal.client.id}")
    private String clientId;

    @Value("${paypal.client.secret}")
    private String clientSecret;

    @Value("${paypal.mode}")
    private String mode;

    // Create PayPal APIContext with debug statements
    private APIContext getAPIContext() {
        System.out.println("Configuring PayPal APIContext with clientId: " + clientId);
        System.out.println("PayPal Mode: " + mode);  // This should print 'sandbox' or 'live'

        if (!mode.equalsIgnoreCase("sandbox") && !mode.equalsIgnoreCase("live")) {
            throw new IllegalArgumentException("Mode must be either 'sandbox' or 'live'.");
        }

        return new APIContext(clientId, clientSecret, mode);
    }

    // Method to create payment
    public Payment createPayment(Double totalAmount, String currency, String method,
                                 String intent, String description, String cancelUrl, String successUrl) {
        Amount amount = new Amount();
        amount.setTotal(String.format("%.2f", totalAmount));
        amount.setCurrency(currency);

        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setDescription(description);

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        Payment payment = new Payment();
        payment.setIntent(intent);
        payment.setPayer(new Payer().setPaymentMethod(method));
        payment.setTransactions(transactions);
        payment.setRedirectUrls(new RedirectUrls().setCancelUrl(cancelUrl).setReturnUrl(successUrl));

        try {
            // Create payment and get approval URL
            Payment createdPayment = payment.create(getAPIContext());
            return createdPayment;
        } catch (PayPalRESTException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Method to execute payment after user approval
    public Payment executePayment(String paymentId, String payerId) {
        try {
            Payment payment = new Payment();
            payment.setId(paymentId);

            PaymentExecution paymentExecution = new PaymentExecution();
            paymentExecution.setPayerId(payerId);

            // Execute the payment
            return payment.execute(getAPIContext(), paymentExecution);
        } catch (PayPalRESTException e) {
            e.printStackTrace();
            return null;
        }
    }
}
