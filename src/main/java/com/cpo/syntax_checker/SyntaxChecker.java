package com.cpo.syntax_checker;

import java.util.ArrayList;
import java.util.List;

public class SyntaxChecker {

    public static boolean isValidAssignment(String input) {
        input = input.trim(); // Remove leading/trailing spaces

        if (input.isEmpty()) {
            return false; // Empty input is invalid
        }

        // Tokenize the input
        List<String> tokens = tokenize(input);

        // Check for type declaration (optional first token)
        String firstToken = tokens.get(0);
        if (isDataType(firstToken)) {
            tokens.remove(0); // Remove the type declaration for further validation
        }

        // Ensure there's a variable name after the optional type declaration
        if (tokens.isEmpty() || !isValidVariable(tokens.get(0))) {
            return false; // Invalid variable name
        }

        // Check for assignment operator (=)
        if (tokens.size() < 3 || !tokens.get(1).equals("=")) {
            return false; // Missing or incorrect assignment operator
        }

        // Validate the expression after the assignment operator
        String expression = String.join(" ", tokens.subList(2, tokens.size() - 1));
        if (!isValidExpression(expression)) {
            return false; // Invalid expression
        }

        // Check for semicolon at the end
        if (!input.endsWith(";")) {
            return false; // Statement must end with a semicolon
        }

        return true; // Valid assignment statement
    }

    // Helper method to tokenize the input string
    public static List<String> tokenize(String input) {
        List<String> tokens = new ArrayList<>();
        StringBuilder currentToken = new StringBuilder();

        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);

            // Check for whitespace
            if (Character.isWhitespace(ch)) {
                if (currentToken.length() > 0) {
                    tokens.add(currentToken.toString());
                    currentToken.setLength(0); // Clear current token
                }
                continue;
            }

            // Handle delimiters like =, ;, etc.
            if ("=;".indexOf(ch) != -1) {
                if (currentToken.length() > 0) {
                    tokens.add(currentToken.toString());
                    currentToken.setLength(0); // Clear current token
                }
                tokens.add(Character.toString(ch)); // Add delimiter as a token
                continue;
            }

            // Append other characters to the current token
            currentToken.append(ch);
        }

        // Add the last token if exists
        if (currentToken.length() > 0) {
            tokens.add(currentToken.toString());
        }

        return tokens;
    }

    // Helper method to validate variable names
    public static boolean isValidVariable(String token) {
        return token.matches("[a-zA-Z_][a-zA-Z0-9_]*"); // Must start with a letter or underscore
    }

    // Helper method to validate expressions
    public static boolean isValidExpression(String expression) {
        if (expression.isEmpty()) {
            return false; // Empty expression is invalid
        }

        List<String> tokens = tokenize(expression);

        // Check for valid operands and operators
        for (String token : tokens) {
            if (!isValidOperand(token) && !isOperator(token)) {
                return false; // Invalid token in expression
            }
        }

        return true; // Valid expression
    }

    // Helper method to validate operands
    public static boolean isValidOperand(String token) {
        return token.matches("[a-zA-Z_][a-zA-Z0-9_]*|(-?\\d+)|true|false"); // Variable, integer (positive/negative), or boolean literals
    }

    // Helper method to validate operators
    public static boolean isOperator(String token) {
        return token.equals("+") || token.equals("-") || token.equals("*") || token.equals("/") || token.equals("%");
    }

    // Helper method to check if a token is a valid data type
    private static boolean isDataType(String token) {
        return token.equals("int") || token.equals("float") || token.equals("double") ||
               token.equals("char") || token.equals("boolean") || token.equals("String");
    }
}
