package org.example.sema.repository;

import org.example.sema.entities.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    PasswordResetToken findByToken(String token);
    List<PasswordResetToken> findAllByExpiryDateBefore(Date now);
}
