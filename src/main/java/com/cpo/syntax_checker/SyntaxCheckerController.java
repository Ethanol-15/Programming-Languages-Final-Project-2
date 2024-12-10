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

    @PostMapping("/check")
    public ResponseEntity<Map<String, String>> checkSyntax(@RequestBody SyntaxCheckerRequest request) {
        String result = syntaxCheckerService.checkSyntax(request);

        // If the API call limit is exceeded
        if ("API call limit exceeded. Please wait for the cooldown period.".equals(result)) {
            return new ResponseEntity<>(Map.of("status", "error", "message", result), HttpStatus.TOO_MANY_REQUESTS);
        }

        // If the result indicates invalid syntax
        if (result.startsWith("Invalid")) {
            return new ResponseEntity<>(Map.of("status", "error", "message", result), HttpStatus.BAD_REQUEST);
        }

        // If syntax is valid
        return new ResponseEntity<>(Map.of("status", "success", "result", result), HttpStatus.OK);
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Integer>> getStats() {
        int totalCalls = syntaxCheckerService.getTotalCalls();
        int remainingCalls = Math.max(0, 10 - totalCalls); // Calculate remaining calls

        return new ResponseEntity<>(Map.of("totalCalls", totalCalls, "remainingCalls", remainingCalls), HttpStatus.OK);
    }

    @PostMapping("/reset")
    public ResponseEntity<String> resetApiCalls() {
        syntaxCheckerService.resetTotalCalls();
        return new ResponseEntity<>("API call count has been reset.", HttpStatus.NO_CONTENT);
    }

    // Reflection demonstration endpoint
    @GetMapping("/reflection-demo")
    public ResponseEntity<Map<String, String>> reflectOnCallCount() {
        try {
            // Access the private field 'callCount' in SyntaxCheckerService
            Field field = SyntaxCheckerService.class.getDeclaredField("callCount");
            field.setAccessible(true); // Make the field accessible
            AtomicInteger callCount = (AtomicInteger) field.get(syntaxCheckerService); // Get the value of the field

            return new ResponseEntity<>(Map.of("status", "success", "callCount", String.valueOf(callCount.get())), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("status", "error", "message", "Failed to access call count: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
