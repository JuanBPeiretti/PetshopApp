package com.petshop.app.repositories;

import com.petshop.app.model.ResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResetTokenRepository extends JpaRepository<ResetToken, String> {

    Optional<ResetToken> findByEmail(String email);
}