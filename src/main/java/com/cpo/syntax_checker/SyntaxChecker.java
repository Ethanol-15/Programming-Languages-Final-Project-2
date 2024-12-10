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
            String dataType = null;
            if (isDataType(firstToken)) {
                dataType = firstToken; // Save the data type
                tokens.remove(0); // Remove the type declaration for further validation
            }

            // Ensure there's a variable name after the optional type declaration
            if (tokens.isEmpty() || !isValidVariable(tokens.get(0))) {
                throw new SyntaxException("Invalid or missing variable name.");
            }

            String variableName = tokens.get(0); // The variable name
            tokens.remove(0); // Remove the variable name

            // Check for assignment operator (=)
            if (tokens.size() < 2 || !tokens.get(0).equals("=")) {
                throw new SyntaxException("Missing or incorrect assignment operator.");
            }

            // Validate the expression after the assignment operator
            String expression = String.join(" ", tokens.subList(1, tokens.size()));

            // Ensure that the expression is compatible with the variable type
            if (!isValidExpression(expression, dataType)) {
                throw new SyntaxException("Invalid expression for the variable type.");
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

                // Check for whitespace
                if (!inStringLiteral && Character.isWhitespace(ch)) {
                    if (currentToken.length() > 0) {
                        tokens.add(currentToken.toString());
                        currentToken.setLength(0); // Clear current token
                    }
                    continue;
                }

                // Handle delimiters like =, ;, ( ), etc.
                if (!inStringLiteral && "=;()".indexOf(ch) != -1) {
                    if (currentToken.length() > 0) {
                        tokens.add(currentToken.toString());
                        currentToken.setLength(0); // Clear current token
                    }
                    if (ch == ';') {
                        continue; // Skip semicolon in the expression
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
    public static boolean isValidExpression(String expression, String dataType) {
        try {
            if (expression.isEmpty()) {
                throw new SyntaxException("Expression cannot be empty.");
            }

            List<String> tokens = tokenize(expression);

            // Track parentheses for matching
            int parenthesesCount = 0;

            // Check for valid operands and operators
            for (String token : tokens) {
                if (token.equals("(")) {
                    parenthesesCount++;
                } else if (token.equals(")")) {
                    if (parenthesesCount == 0) {
                        throw new SyntaxException("Unmatched closing parenthesis.");
                    }
                    parenthesesCount--;
                } else if (!isValidOperand(token, dataType) && !isOperator(token) && !isComparisonOperator(token)) {
                    throw new SyntaxException("Invalid token in expression: " + token);
                }
            }

            if (parenthesesCount != 0) {
                throw new SyntaxException("Unmatched opening parenthesis.");
            }

            return true; // Valid expression
        } catch (SyntaxException e) {
            System.err.println("Expression Error: " + e.getMessage());
            return false;
        }
    }

    // Helper method to validate operands
    public static boolean isValidOperand(String token, String dataType) {
        if (dataType == null) {
            return false; // No data type, can't check operand validity
        }

        switch (dataType) {
            case "int":
                return token.matches("-?\\d+"); // Only integers are valid
            case "float":
            case "double":
                return token.matches("-?\\d*(\\.\\d+)?([eE][+-]?\\d+)?"); // Floating point numbers
            case "boolean":
                return token.equals("true") || token.equals("false"); // Only true/false for booleans
            case "String":
                return token.matches("\".*\""); // String literals (allow quotes)
            case "char":
                return token.matches("'.'"); // Single character literals
            default:
                return false;
        }
    }

    // Helper method to validate operators
    public static boolean isOperator(String token) {
        return token.equals("+") || token.equals("-") || token.equals("*") || token.equals("/") || token.equals("%");
    }

    // Helper method to validate comparison operators
    public static boolean isComparisonOperator(String token) {
        return token.equals(">") || token.equals("<") || token.equals(">=") || token.equals("<=") ||
                token.equals("==") || token.equals("!=");
    }

    // Helper method to check if a token is a valid data type
    public static boolean isDataType(String token) {
        return token.equals("int") || token.equals("float") || token.equals("double") ||
                token.equals("char") || token.equals("boolean") || token.equals("long") || token.equals("String");
    }

    // Custom exception class for syntax errors
    public static class SyntaxException extends Exception {
        public SyntaxException(String message) {
            super(message);
        }
    }
}
