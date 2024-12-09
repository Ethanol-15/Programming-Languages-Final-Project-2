package com.cpo.syntax_checker;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class SyntaxCheckerService {

    private static final int API_CALL_LIMIT = 10; // Max 10 calls per minute
    private AtomicInteger callCount = new AtomicInteger(0); // Tracks the number of calls
    private Instant windowStart = Instant.now(); // Tracks the start of the 1-minute window

    // Method to check the syntax of the provided code
    public String checkSyntax(SyntaxCheckerRequest request) {
        try {
            Instant now = Instant.now();
            long elapsedSeconds = now.getEpochSecond() - windowStart.getEpochSecond();

            // Reset the window if more than 1 minute has passed
            if (elapsedSeconds >= 60) {
                windowStart = now;
                callCount.set(0); // Reset call count
            }

            // Enforce rate limit
            if (callCount.incrementAndGet() > API_CALL_LIMIT) {
                long remainingCooldown = 60 - elapsedSeconds;
                return "API call limit exceeded. Please wait " + remainingCooldown + " seconds for the cooldown period.";
            }

            // Perform syntax checking
            if (request == null || request.getCode() == null) {
                throw new IllegalArgumentException("Request or code input cannot be null.");
            }

            String code = request.getCode();
            boolean isValid = SyntaxChecker.isValidAssignment(code);

            return isValid ? "Valid" : "Invalid";
        } catch (IllegalArgumentException e) {
            // Handle cases where the input is invalid
            return "Invalid input: " + e.getMessage();
        } catch (Exception e) {
            // Handle any unexpected errors
            return "An error occurred while processing the request: " + e.getMessage();
        }
    }

    // Method to get the total number of API calls
    public int getTotalCalls() {
        try {
            return callCount.get();
        } catch (Exception e) {
            // Handle any unexpected errors
            throw new RuntimeException("Failed to retrieve total calls: " + e.getMessage(), e);
        }
    }

    // Optional: Reset total calls manually (if required)
    public void resetTotalCalls() {
        try {
            callCount.set(0);
            windowStart = Instant.now();
        } catch (Exception e) {
            // Handle any unexpected errors during reset
            throw new RuntimeException("Failed to reset total calls: " + e.getMessage(), e);
        }
    }
}
