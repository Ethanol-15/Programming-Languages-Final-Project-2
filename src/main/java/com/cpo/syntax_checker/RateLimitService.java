package com.cpo.syntax_checker;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class RateLimitService {

    private static final int API_CALL_LIMIT = 10; // Max 10 calls per minute
    private AtomicInteger callCount = new AtomicInteger(0); // Tracks the number of calls
    private Instant windowStart = Instant.now(); // Tracks the start of the 1-minute window

    public boolean isRateLimitExceeded() {
        Instant now = Instant.now();
        long elapsedSeconds = now.getEpochSecond() - windowStart.getEpochSecond();

        // Reset the window if more than 1 minute has passed
        if (elapsedSeconds >= 60) {
            windowStart = now;
            callCount.set(0); // Reset call count
        }

        // Increment the call count and check if the rate limit is exceeded
        return callCount.incrementAndGet() > API_CALL_LIMIT;
    }

    public long getRemainingCooldown() {
        Instant now = Instant.now();
        long elapsedSeconds = now.getEpochSecond() - windowStart.getEpochSecond();
        return Math.max(0, 60 - elapsedSeconds); // Remaining cooldown in seconds
    }

    public int getTotalCalls() {
        return callCount.get();
    }

    public void resetTotalCalls() {
        callCount.set(0);
        windowStart = Instant.now();
    }
}
