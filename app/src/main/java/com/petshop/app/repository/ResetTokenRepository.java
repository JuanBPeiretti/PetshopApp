package com.petshop.app.repository;

import com.petshop.app.model.ResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResetTokenRepository extends JpaRepository<ResetToken, String> {
}
