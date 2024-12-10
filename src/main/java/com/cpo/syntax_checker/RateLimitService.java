package com.cpo.syntax_checker;

import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

@Service
public class RateLimitService {

    private static final int DEFAULT_API_CALL_LIMIT = 10;  // 10 free calls
    private static final int PURCHASED_API_CALL_LIMIT = 100; // 100 calls per purchase
    private AtomicInteger callCount = new AtomicInteger(0); // Tracks the number of calls made
    private AtomicInteger totalCallCount = new AtomicInteger(0); // Tracks total API calls made
    private int freeCallLimit = DEFAULT_API_CALL_LIMIT; // Start with 10 free calls
    private int purchasedCallLimit = 0; // Track purchased calls separately

    // Check if the rate limit is exceeded
    public boolean isRateLimitExceeded() {
        // Check if free calls have been used up
        if (callCount.get() < freeCallLimit) {
            callCount.incrementAndGet();
            totalCallCount.incrementAndGet();
            return false;  // Within the free call limit
        }

        // Check if purchased calls are available
        if (purchasedCallLimit > 0 && callCount.get() < (freeCallLimit + purchasedCallLimit)) {
            callCount.incrementAndGet();
            totalCallCount.incrementAndGet();
            return false;  // Within purchased call limit
        }

        return true;  // Exceeded call limit
    }

    // Increment call count after payment to allow additional calls
    public void incrementCallCountAfterPayment() {
        // Set the purchased call limit to 100
        this.purchasedCallLimit = PURCHASED_API_CALL_LIMIT;
        this.callCount.set(freeCallLimit);  // Reset current call count to the free call limit
    }

    // Get the total number of API calls made
    public int getTotalCalls() {
        return totalCallCount.get();
    }

    // Get the remaining API calls available
    public int getRemainingCalls() {
        // If there are purchased calls, show remaining calls as purchased limit
        if (purchasedCallLimit > 0) {
            return purchasedCallLimit - (callCount.get() - freeCallLimit);
        }
        // Otherwise, show remaining calls as the free limit
        return freeCallLimit - callCount.get();
    }

    // Reset remaining calls without affecting total call count
    public void resetRemainingCalls() {
        this.callCount.set(0); // Reset the current call count
        this.purchasedCallLimit = 0; // Reset the purchased call limit
    }
}
