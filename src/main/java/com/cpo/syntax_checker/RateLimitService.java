package com.cpo.syntax_checker;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class RateLimitService {

    private static final int API_CALL_LIMIT = 10;  // Max API calls per minute
    private final AtomicInteger callCount = new AtomicInteger(0);  // Tracks the number of calls
    private Instant windowStart = Instant.now();  // Tracks the start of the 1-minute window

    /**
     * Checks if the rate limit has been exceeded.
     * Concurrency is handled by using an AtomicInteger for thread-safe counting.
     * @return true if rate limit is exceeded
     */
    public boolean isRateLimitExceeded() {
        Instant now = Instant.now();
        long elapsedSeconds = now.getEpochSecond() - windowStart.getEpochSecond();

        // Reset the window if more than 60 seconds have passed
        if (elapsedSeconds >= 60) {
            windowStart = now;
            callCount.set(0);  // Reset call count for the new window
        }

        // Concurrency: The AtomicInteger is thread-safe, ensuring that the callCount is updated correctly across threads
        return callCount.get() >= API_CALL_LIMIT;
    }

    /**
     * Gets the remaining cooldown time in seconds.
     * @return remaining time in seconds until the rate limit is reset
     */
    public long getRemainingCooldown() {
        Instant now = Instant.now();
        long elapsedSeconds = now.getEpochSecond() - windowStart.getEpochSecond();
        return 60 - elapsedSeconds;  // Time remaining until the cooldown resets
    }

    /**
     * Increments the API call count atomically.
     * Concurrency: The AtomicInteger ensures thread-safe updates to the call count.
     */
    public void incrementCallCount() {
        // Concurrency: AtomicInteger ensures that updates to callCount are thread-safe.
        callCount.incrementAndGet();  // Increment the call count atomically
    }

    /**
     * Returns the total number of API calls made.
     * @return current total API calls
     */
    public int getTotalCalls() {
        return callCount.get();  // Return the current API call count
    }

    /**
     * Resets the total API calls and the window start time.
     * Concurrency: The AtomicInteger is reset atomically.
     */
    public void resetTotalCalls() {
        callCount.set(0);  // Reset the API call count atomically
        windowStart = Instant.now();  // Reset the window start time
    }
}
