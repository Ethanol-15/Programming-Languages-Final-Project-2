package com.cpo.syntax_checker;

public class SyntaxCheckerRequest {
    private String code;

    // Constructors, getters, and setters
    public SyntaxCheckerRequest() {}

    public SyntaxCheckerRequest(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
