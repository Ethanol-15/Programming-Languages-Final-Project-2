package com.cpo.syntax_checker;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SyntaxCheckerService implements SyntaxCheckerServiceInterface {

    private final RateLimitService rateLimitService;

    public SyntaxCheckerService(RateLimitService rateLimitService) {
        this.rateLimitService = rateLimitService;
    }

    @Override
    public String checkSyntax(SyntaxCheckerRequest request) {
        try {
            // Check if there are remaining calls
            int remainingCalls = rateLimitService.getRemainingCalls();
            if (remainingCalls <= 0) {
                return "You have used up all your API calls. Please purchase more API calls.";
            }
            // Check for rate limit
            if (rateLimitService.isRateLimitExceeded()) {
                return "API calls per minute is exceeded, wait for cooldown.";
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
        rateLimitService.resetRemainingCalls();  // Reset remaining calls and total calls
    }

    // Functional Programming: Validate multiple syntax requests
    public List<String> validateMultipleRequests(List<SyntaxCheckerRequest> requests) {
        return requests.stream()
                .map(request -> {
                    // Use the existing checkSyntax method for validation
                    if (request != null && request.getCode() != null) {
                        return checkSyntax(request);
                    } else {
                        return "Invalid input: Request or code is null.";
                    }
                })
                .collect(Collectors.toList()); // Collect results into a list
    }

    // Get remaining API calls (use the method in RateLimitService)
    @Override
    public int getRemainingCalls() {
        return rateLimitService.getRemainingCalls();
    }
}
