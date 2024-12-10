package com.cpo.syntax_checker;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/api")
public class SyntaxCheckerController {

    private final SyntaxCheckerServiceInterface syntaxCheckerService;

    public SyntaxCheckerController(SyntaxCheckerServiceInterface syntaxCheckerService) {
        this.syntaxCheckerService = syntaxCheckerService;
    }

    /**
     * Endpoint to check the syntax of the provided code.
     * Concurrency: Syntax checking is handled in parallel using ExecutorService in SyntaxCheckerService.
     */
    @PostMapping("/check")
    public ResponseEntity<Map<String, String>> checkSyntax(@RequestBody SyntaxCheckerRequest request) {
        String result = syntaxCheckerService.checkSyntax(request);

        // Check if API call limit exceeded (rate-limiting logic)
        if ("API call limit exceeded. Please wait for the cooldown period.".equals(result)) {
            return new ResponseEntity<>(Map.of("status", "error", "message", result), HttpStatus.TOO_MANY_REQUESTS);
        }

        // Check if syntax is invalid
        if (result.startsWith("Invalid")) {
            return new ResponseEntity<>(Map.of("status", "error", "message", result), HttpStatus.BAD_REQUEST);
        }

        // If syntax is valid, return success response
        return new ResponseEntity<>(Map.of("status", "success", "result", result), HttpStatus.OK);
    }

    /**
     * Endpoint to get the total API calls and the remaining calls in the current rate limit window.
     * Concurrency: The total calls and remaining calls are handled safely using RateLimitService.
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Integer>> getStats() {
        int totalCalls = syntaxCheckerService.getTotalCalls();
        int remainingCalls = Math.max(0, 10 - totalCalls); // Calculate remaining calls

        return new ResponseEntity<>(Map.of("totalCalls", totalCalls, "remainingCalls", remainingCalls), HttpStatus.OK);
    }

    /**
     * Endpoint to reset the API call count.
     * Concurrency: The reset operation is safely handled by RateLimitService.
     */
    @PostMapping("/reset")
    public ResponseEntity<String> resetApiCalls() {
        syntaxCheckerService.resetTotalCalls();
        return new ResponseEntity<>("API call count has been reset.", HttpStatus.NO_CONTENT);
    }

    /**
     * Reflection demo endpoint to access the call count in the SyntaxCheckerService.
     * Concurrency: Reflecting on the call count can be done safely using synchronized or thread-safe mechanisms.
     */
    @GetMapping("/reflection-demo")
    public ResponseEntity<Map<String, String>> reflectOnCallCount() {
        try {
            // Reflection demo logic to access private field 'callCount' in SyntaxCheckerService
            Field field = SyntaxCheckerService.class.getDeclaredField("callCount");
            field.setAccessible(true);  // Make the field accessible to get its value
            AtomicInteger callCount = (AtomicInteger) field.get(syntaxCheckerService);  // Get the callCount from SyntaxCheckerService

            // Return the call count as part of the response
            return new ResponseEntity<>(Map.of("status", "success", "callCount", String.valueOf(callCount.get())), HttpStatus.OK);
        } catch (Exception e) {
            // If reflection fails, return an error message with a 500 Internal Server Error response
            return new ResponseEntity<>(Map.of("status", "error", "message", "Failed to access call count: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
