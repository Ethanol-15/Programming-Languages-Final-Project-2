package com.cpo.syntax_checker;

import java.util.ArrayList;
import java.util.List;

public class SyntaxChecker {

    public static boolean isValidAssignment(String input) {
        try {
            input = input.trim(); // Remove leading/trailing spaces

            if (input.isEmpty()) {
                throw new IllegalArgumentException("Input cannot be empty.");
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
                throw new SyntaxException("Invalid or missing variable name.");
            }

            // Check for assignment operator (=)
            if (tokens.size() < 3 || !tokens.get(1).equals("=")) {
                throw new SyntaxException("Missing or incorrect assignment operator.");
            }

            // Validate the expression after the assignment operator
            String expression = String.join(" ", tokens.subList(2, tokens.size() - 1));
            if (!isValidExpression(expression)) {
                throw new SyntaxException("Invalid expression.");
            }

            // Check for semicolon at the end
            if (!input.endsWith(";")) {
                throw new SyntaxException("Statement must end with a semicolon.");
            }

            return true; // Valid assignment statement
        } catch (SyntaxException e) {
            System.err.println("Syntax Error: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("Unexpected Error: " + e.getMessage());
            return false;
        }
    }

    // Helper method to tokenize the input string
    public static List<String> tokenize(String input) {
        try {
            List<String> tokens = new ArrayList<>();
            StringBuilder currentToken = new StringBuilder();
            boolean inStringLiteral = false;

            for (int i = 0; i < input.length(); i++) {
                char ch = input.charAt(i);

                // Handle string literals
                if (ch == '"') {
                    if (inStringLiteral) {
                        currentToken.append(ch); // Close the string literal
                        tokens.add(currentToken.toString());
                        currentToken.setLength(0);
                        inStringLiteral = false;
                    } else {
                        if (currentToken.length() > 0) {
                            tokens.add(currentToken.toString());
                            currentToken.setLength(0);
                        }
                        inStringLiteral = true;
                        currentToken.append(ch); // Start the string literal
                    }
                    continue;
                }

                // Inside a string literal
                if (inStringLiteral) {
                    currentToken.append(ch);
                    continue;
                }

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
        } catch (Exception e) {
            throw new RuntimeException("Error during tokenization: " + e.getMessage());
        }
    }

    // Helper method to validate variable names
    public static boolean isValidVariable(String token) {
        return token.matches("[a-zA-Z_][a-zA-Z0-9_]*"); // Must start with a letter or underscore
    }

    // Helper method to validate expressions
    public static boolean isValidExpression(String expression) {
        try {
            if (expression.isEmpty()) {
                throw new SyntaxException("Expression cannot be empty.");
            }

            List<String> tokens = tokenize(expression);

            // Check for valid operands and operators
            for (String token : tokens) {
                if (!isValidOperand(token) && !isOperator(token)) {
                    throw new SyntaxException("Invalid token in expression: " + token);
                }
            }

            return true; // Valid expression
        } catch (SyntaxException e) {
            System.err.println("Expression Error: " + e.getMessage());
            return false;
        }
    }

    // Helper method to validate operands
    public static boolean isValidOperand(String token) {
        return token.matches("[a-zA-Z_][a-zA-Z0-9_]*|(-?\\d+)|true|false|\".*\""); // Variable, integer (positive/negative), boolean, or string literals
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

    // Custom exception class for syntax errors
    public static class SyntaxException extends Exception {
        public SyntaxException(String message) {
            super(message);
        }
    }
}
