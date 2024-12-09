package com.cpo.syntax_checker;

import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/api")
public class SyntaxCheckerController {

    private final SyntaxCheckerServiceInterface syntaxCheckerService; // Use the interface instead of the concrete class

    public SyntaxCheckerController(SyntaxCheckerServiceInterface syntaxCheckerService) {
        this.syntaxCheckerService = syntaxCheckerService;
    }

    @PostMapping("/check")
    public Map<String, String> checkSyntax(@RequestBody SyntaxCheckerRequest request) {
        String result = syntaxCheckerService.checkSyntax(request);

        if ("API call limit exceeded. Please wait for the cooldown period.".equals(result)) {
            return Map.of("status", "error", "message", result);
        }

        return Map.of("status", "success", "result", result);
    }

    @GetMapping("/stats")
    public Map<String, Integer> getStats() {
        int totalCalls = syntaxCheckerService.getTotalCalls();
        int remainingCalls = Math.max(0, 10 - totalCalls); // Calculate remaining calls
        return Map.of("totalCalls", totalCalls, "remainingCalls", remainingCalls);
    }

    @PostMapping("/reset")
    public String resetApiCalls() {
        syntaxCheckerService.resetTotalCalls();
        return "API call count has been reset.";
    }

    // Reflection demonstration endpoint
    @GetMapping("/reflection-demo")
    public Map<String, String> reflectOnCallCount() {
        try {
            // Access the private field 'callCount' in SyntaxCheckerService
            Field field = SyntaxCheckerService.class.getDeclaredField("callCount");
            field.setAccessible(true); // Make the field accessible
            AtomicInteger callCount = (AtomicInteger) field.get(syntaxCheckerService); // Get the value of the field

            return Map.of("status", "success", "callCount", String.valueOf(callCount.get()));
        } catch (Exception e) {
            return Map.of("status", "error", "message", "Failed to access call count: " + e.getMessage());
        }
    }
}
