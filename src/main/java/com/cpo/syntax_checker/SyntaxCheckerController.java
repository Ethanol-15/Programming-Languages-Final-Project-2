package com.cpo.syntax_checker;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class SyntaxCheckerController {

    private final SyntaxCheckerService syntaxCheckerService;

    public SyntaxCheckerController(SyntaxCheckerService syntaxCheckerService) {
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
}
