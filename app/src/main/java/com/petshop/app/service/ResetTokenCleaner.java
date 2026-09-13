package com.petshop.app.service;

import com.petshop.app.model.ResetToken;
import com.petshop.app.repository.ResetTokenRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ResetTokenCleaner {

    private final ResetTokenRepository resetTokenRepository;

    public ResetTokenCleaner(ResetTokenRepository resetTokenRepository) {
        this.resetTokenRepository = resetTokenRepository;
    }

    @Scheduled(fixedRateString = "${petshop.reset.cleaner.rate:300000}")
    public void clean() {
        List<ResetToken> expired = resetTokenRepository.findAll().stream()
                .filter(ResetToken::isExpired)
                .toList();

        if (!expired.isEmpty()) {
            resetTokenRepository.deleteAll(expired);
            System.out.println("[ResetTokenCleaner] Removed " + expired.size() + " expired tokens");
        }
    }
}
