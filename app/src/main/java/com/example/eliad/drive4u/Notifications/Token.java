package com.example.eliad.drive4u.Notifications;

public class Token {

    public static final String TOKEN_PATH = "Tokens";

    private String token;

    public Token(String token) {
        this.token = token;
    }

    public Token() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
