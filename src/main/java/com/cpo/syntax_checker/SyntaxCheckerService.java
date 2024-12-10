package com.cpo.syntax_checker;

import org.springframework.stereotype.Service;

import java.util.concurrent.*;

@Service
public class SyntaxCheckerService implements SyntaxCheckerServiceInterface {

    private final RateLimitService rateLimitService;
    private final ExecutorService executorService;  // Executor for concurrency handling

    public SyntaxCheckerService(RateLimitService rateLimitService) {
        this.rateLimitService = rateLimitService;
        this.executorService = Executors.newFixedThreadPool(10);  // Initialize ExecutorService with a fixed thread pool
    }

    @Override
    public String checkSyntax(SyntaxCheckerRequest request) {
        // Increment the API call count atomically whenever a new request is processed
        rateLimitService.incrementCallCount();  // Thread-safe increment in RateLimitService

        // Check if the rate limit has been exceeded
        if (rateLimitService.isRateLimitExceeded()) {
            long remainingCooldown = rateLimitService.getRemainingCooldown();
            return "API call limit exceeded. Please wait " + remainingCooldown + " seconds for the cooldown period.";
        }

        // Use ExecutorService to handle syntax check concurrently
        Callable<String> task = () -> {
            return performSyntaxCheck(request);  // Run the syntax check in a separate thread
        };

        try {
            // Submit the task to the executor and wait for completion
            Future<String> future = executorService.submit(task);
            return future.get();  // Blocks until the task is finished and result is obtained
        } catch (InterruptedException | ExecutionException e) {
            return "Error occurred during syntax checking: " + e.getMessage();  // Handle any execution errors
        }
    }

    private String performSyntaxCheck(SyntaxCheckerRequest request) {
        // Logic for syntax validation (dummy logic for now)
        String code = request.getCode();
        boolean isValid = SyntaxChecker.isValidAssignment(code);  // Assume SyntaxChecker performs validation
        return isValid ? "Valid" : "Invalid";
    }

    @Override
    public int getTotalCalls() {
        return rateLimitService.getTotalCalls();  // Returns the total API calls from RateLimitService
    }

    @Override
    public void resetTotalCalls() {
        rateLimitService.resetTotalCalls();  // Reset the call count in RateLimitService
    }

    /**
     * Gracefully shuts down the ExecutorService when it's no longer needed.
     */
    public void shutdownExecutor() {
        executorService.shutdown();  // Shut down the ExecutorService gracefully
    }
}
