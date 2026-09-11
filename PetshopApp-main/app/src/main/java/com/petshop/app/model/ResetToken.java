package com.petshop.app.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "reset_token")
public class ResetToken {

    @Id
    private String token;

    private String email;

    private long expiryEpochMillis;

    public ResetToken() {
    }

    public ResetToken(String token, String email, long expiryEpochMillis) {
        this.token = token;
        this.email = email;
        this.expiryEpochMillis = expiryEpochMillis;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getExpiryEpochMillis() {
        return expiryEpochMillis;
    }

    public void setExpiryEpochMillis(long expiryEpochMillis) {
        this.expiryEpochMillis = expiryEpochMillis;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > expiryEpochMillis;
    }
}