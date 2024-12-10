package com.cpo.syntax_checker;

import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

@Service
public class RateLimitService {

    private static final int DEFAULT_API_CALL_LIMIT = 10; // Default limit (before payment)
    private AtomicInteger callCount = new AtomicInteger(0);
    private int apiCallLimit = DEFAULT_API_CALL_LIMIT;  // This will be updated after payment

    // Check if the rate limit is exceeded
    public boolean isRateLimitExceeded() {
        // Check if the number of calls exceeds the limit
        if (callCount.get() >= apiCallLimit) {
            return true;
        }
        callCount.incrementAndGet();
        return false;
    }

    // Increment call count after payment to allow additional calls
    public void incrementCallCountAfterPayment(int purchasedCalls) {
        // Update the total API call limit
        this.apiCallLimit = DEFAULT_API_CALL_LIMIT;
        // Reset the current call count after the payment
        this.callCount.set(0);
    }

    // Get the total number of API calls made
    public int getTotalCalls() {
        return callCount.get();
    }

    // Get the remaining API calls available
    public int getRemainingCalls() {
        return apiCallLimit - callCount.get();
    }

    // Reset the total calls and set back to the default limit
    public void resetTotalCalls() {
        callCount.set(0);
        this.apiCallLimit = DEFAULT_API_CALL_LIMIT; // Reset to default limit
    }
}