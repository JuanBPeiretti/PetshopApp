package com.petshop.app.model;

import java.time.Instant;

public class ResetToken {
    public String token;
    public String email;
    public long expiryEpochMillis;

    public ResetToken() {}

    public ResetToken(String token, String email, long expiryEpochMillis) {
        this.token = token;
        this.email = email;
        this.expiryEpochMillis = expiryEpochMillis;
    }

    public boolean isExpired() {
        return Instant.now().toEpochMilli() > expiryEpochMillis;
    }
}
