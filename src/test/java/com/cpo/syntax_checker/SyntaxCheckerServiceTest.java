package com.cpo.syntax_checker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SyntaxCheckerServiceTest {

    @Mock
    private RateLimitService rateLimitService;

    @InjectMocks
    private SyntaxCheckerService syntaxCheckerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCheckSyntax_ValidCode() {
        SyntaxCheckerRequest request = new SyntaxCheckerRequest("int a = 5;");
        when(rateLimitService.isRateLimitExceeded()).thenReturn(false);

        String result = syntaxCheckerService.checkSyntax(request);

        assertEquals("Valid", result);
    }

    @Test
    void testCheckSyntax_InvalidCode() {
        SyntaxCheckerRequest request = new SyntaxCheckerRequest("int a 5;");
        when(rateLimitService.isRateLimitExceeded()).thenReturn(false);

        String result = syntaxCheckerService.checkSyntax(request);

        assertEquals("Invalid", result);
    }

    @Test
    void testCheckSyntax_RateLimitExceeded() {
        SyntaxCheckerRequest request = new SyntaxCheckerRequest("int a = 5;");
        when(rateLimitService.isRateLimitExceeded()).thenReturn(true);
        when(rateLimitService.getRemainingCalls()).thenReturn(0);

        String result = syntaxCheckerService.checkSyntax(request);

        assertEquals("You have used up all your API calls. Please purchase more API calls.", result);
    }

    @Test
    void testGetTotalCalls() {
        when(rateLimitService.getTotalCalls()).thenReturn(5);

        int totalCalls = syntaxCheckerService.getTotalCalls();

        assertEquals(5, totalCalls);
    }

    @Test
    void testResetTotalCalls() {
        syntaxCheckerService.resetTotalCalls();

        verify(rateLimitService, times(1)).resetRemainingCalls();
    }

}