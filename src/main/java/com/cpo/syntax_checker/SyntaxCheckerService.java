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
        Instant now = Instant.now();
        long elapsedSeconds = now.getEpochSecond() - windowStart.getEpochSecond();

        // Reset the window if more than 1 minute has passed
        if (elapsedSeconds >= 60) {
            windowStart = now;
            callCount.set(0); // Reset call count
        }

        // Enforce rate limit
        if (callCount.incrementAndGet() > API_CALL_LIMIT) {
            return "API call limit exceeded. Please wait for the cooldown period.";
        }

        // Perform syntax checking
        String code = request.getCode();
        boolean isValid = SyntaxChecker.isValidAssignment(code);

        return isValid ? "Valid" : "Invalid";
    }

    // Method to get the total number of API calls
    public int getTotalCalls() {
        return callCount.get();
    }

    // Optional: Reset total calls manually (if required)
    public void resetTotalCalls() {
        callCount.set(0);
        windowStart = Instant.now();
    }
}
