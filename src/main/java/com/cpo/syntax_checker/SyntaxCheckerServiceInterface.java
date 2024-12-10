package com.cpo.syntax_checker;

public interface SyntaxCheckerServiceInterface {
    String checkSyntax(SyntaxCheckerRequest request); // Abstract method for syntax checking
    int getTotalCalls(); // Abstract method to get total calls
    void resetTotalCalls(); // Abstract method to reset calls
}
