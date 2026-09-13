package com.petshop.app.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "reset_tokens")
public class ResetToken {
    @Id
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
