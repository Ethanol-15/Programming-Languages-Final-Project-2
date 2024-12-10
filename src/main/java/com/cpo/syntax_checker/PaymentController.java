package com.cpo.syntax_checker;

import com.paypal.api.payments.Payment;
import com.paypal.api.payments.Links;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/paypal")
public class PaymentController {

    @Autowired
    private PayPalService payPalService;

    @Autowired
    private RateLimitService rateLimitService;

    @PostMapping("/makePayment")
    public String makePayment(@RequestParam("amount") Double amount) {
        // Log the amount received
        System.out.println("Received payment request with amount: " + amount);

        // Define payment details (currency, description, etc.)
        String currency = "USD";
        String method = "paypal";
        String intent = "sale";  // Other options: 'authorize', 'order'
        String description = "Payment for service";
        String cancelUrl = "http://localhost:2424/paypal/cancel";
        String successUrl = "http://localhost:2424/paypal/success";

        // Log payment details before creating payment
        System.out.println("Creating payment with the following details:");
        System.out.println("Currency: " + currency);
        System.out.println("Method: " + method);
        System.out.println("Intent: " + intent);
        System.out.println("Description: " + description);
        System.out.println("Cancel URL: " + cancelUrl);
        System.out.println("Success URL: " + successUrl);

        // Create payment
        Payment payment = payPalService.createPayment(amount, currency, method, intent, description, cancelUrl, successUrl);

        if (payment != null) {
            // Log payment details and check for approval URL
            System.out.println("Payment created successfully. Payment ID: " + payment.getId());

            // Extract the PayPal approval URL from the payment links
            for (Links link : payment.getLinks()) {
                if (link.getRel().equals("approval_url")) {
                    System.out.println("Approval URL: " + link.getHref());
                    // Return the approval URL to redirect the user
                    return link.getHref();  // This sends the approval URL to the frontend
                }
            }
            // If no approval URL is found, log and return failure
            System.out.println("Payment creation failed: No approval URL found.");
            return "Payment creation failed: No approval URL found.";
        } else {
            // Log if the payment object is null and return failure
            System.out.println("Payment creation failed: Payment is null.");
            return "Payment creation failed.";
        }
    }

    @GetMapping("/success")
    public String paymentSuccess(@RequestParam("paymentId") String paymentId,
                                 @RequestParam("PayerID") String payerId) {
        Payment payment = payPalService.executePayment(paymentId, payerId);

        if (payment != null) {
            int purchasedCalls = calculatePurchasedCalls(payment);

            // Update the rate limit with the new calls (no argument needed)
            rateLimitService.incrementCallCountAfterPayment();  // No argument here

            // Include total calls in the response
            int totalCallsMade = rateLimitService.getTotalCalls();

            return "<html>" +
                    "<head>" +
                    "<title>Payment Successful</title>" +
                    "<style>" +
                    "body { font-family: Arial, sans-serif; text-align: center; padding: 50px; }" +
                    ".button { background-color: #4CAF50; border: none; color: white; padding: 15px 32px; text-align: center; text-decoration: none; display: inline-block; font-size: 16px; margin: 4px 2px; cursor: pointer; border-radius: 4px; }" +
                    ".success-message { font-size: 24px; color: green; }" +
                    ".details { font-size: 18px; margin-top: 20px; }" +
                    "</style>" +
                    "</head>" +
                    "<body>" +
                    "<h1 class='success-message'>Payment Successful!</h1>" +
                    "<p class='details'>Thank you for your purchase! Your payment has been processed successfully.</p>" +
                    "<p>Payment ID: " + paymentId + "</p>" +
                    "<p>Payer ID: " + payerId + "</p>" +
                    "<p>Total API Calls Made: " + totalCallsMade + "</p>" +
                    "<button class='button' onclick=\"window.location.href='/';\">Back to Program</button>" +
                    "</body>" +
                    "</html>";
        } else {
            return "<html><body><h1>Payment execution failed.</h1></body></html>";
        }
    }


    @GetMapping("/cancel")
    public String paymentCancel(@RequestParam("token") String token) {
        // Log when payment is canceled
        System.out.println("Payment was canceled. Token: " + token);

        return "<html>" +
                "<head>" +
                "<title>Payment Canceled</title>" +
                "<style>" +
                "body { font-family: Arial, sans-serif; text-align: center; padding: 50px; }" +
                ".button { background-color: #4CAF50; border: none; color: white; padding: 15px 32px; text-align: center; text-decoration: none; display: inline-block; font-size: 16px; margin: 4px 2px; cursor: pointer; border-radius: 4px; }" +
                ".error-message { font-size: 24px; color: red; }" +
                ".details { font-size: 18px; margin-top: 20px; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<h1 class='error-message'>Payment Canceled</h1>" +
                "<p class='details'>The payment was canceled. No charges have been made.</p>" +
                "<p>Cancellation Token: " + token + "</p>" +
                "<button class='button' onclick=\"window.location.href='/';\">Back to Program</button>" +
                "</body>" +
                "</html>";
    }

    // Calculate the number of purchased API calls based on the payment amount
    private int calculatePurchasedCalls(Payment payment) {
        // Ensure that the payment and transactions list are not null or empty
        if (payment != null && payment.getTransactions() != null && !payment.getTransactions().isEmpty()) {
            // Get the amount as a String and convert it to double
            String amountString = payment.getTransactions().get(0).getAmount().getTotal();

            try {
                // Parse the String amount to double
                double amount = Double.parseDouble(amountString);

                // Add custom logic based on the payment amount
                if (amount >= 50) {
                    return 100; // User purchased 100 API calls for $50
                } else if (amount >= 10) {
                    return 20; // User purchased 20 API calls for $10
                } else {
                    return 10; // Default number of API calls for smaller payments
                }
            } catch (NumberFormatException e) {
                // Handle error if the string cannot be parsed to a double
                System.err.println("Invalid payment amount: " + amountString);
                return 0; // Return a fallback value in case of error
            }
        } else {
            // Handle the case where payment or transactions are invalid
            System.out.println("Invalid payment or transactions.");
            return 0; // Return a fallback value in case of error
        }
    }

}
