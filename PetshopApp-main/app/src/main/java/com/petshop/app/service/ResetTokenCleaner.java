package com.petshop.app.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ResetTokenCleaner {

    private final InMemoryStore store;

    public ResetTokenCleaner(InMemoryStore store) {
        this.store = store;
    }

    @Scheduled(fixedRateString = "${petshop.reset.cleaner.rate:300000}") // configurable via application.properties (ms)
    public void clean() {
        int before = store.resetTokens.size();
        store.resetTokens.entrySet().removeIf(e -> e.getValue().isExpired());
        int after = store.resetTokens.size();
        if (before != after) {
            System.out.println("[ResetTokenCleaner] Removed " + (before - after) + " expired tokens");
        }
    }
}
