package com.example.androidexample;

public class LoginResponse {
    private boolean success;
    private String message;

    // Constructor
    public LoginResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public LoginResponse(String username, String password) {
    }

    // Getter for success as a boolean
    public boolean isSuccess() {
        return success;
    }

    // Getter for success status as String
    public String getSuccessStatus() {
        return success ? "success" : "failure";
    }

    // Getter for message
    public String getMessage() {
        return message;
    }
}
