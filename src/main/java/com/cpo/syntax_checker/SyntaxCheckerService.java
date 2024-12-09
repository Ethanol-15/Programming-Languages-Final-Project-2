package com.cpo.syntax_checker;

import org.springframework.stereotype.Service;

@Service
public class SyntaxCheckerService implements SyntaxCheckerServiceInterface {

    private final RateLimitService rateLimitService;

    public SyntaxCheckerService(RateLimitService rateLimitService) {
        this.rateLimitService = rateLimitService;
    }

    @Override
    public String checkSyntax(SyntaxCheckerRequest request) {
        try {
            // Check for rate limit
            if (rateLimitService.isRateLimitExceeded()) {
                long remainingCooldown = rateLimitService.getRemainingCooldown();
                return "API call limit exceeded. Please wait " + remainingCooldown + " seconds for the cooldown period.";
            }

            // Validate request
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

    @Override
    public int getTotalCalls() {
        return rateLimitService.getTotalCalls();
    }

    @Override
    public void resetTotalCalls() {
        rateLimitService.resetTotalCalls();
    }
}
