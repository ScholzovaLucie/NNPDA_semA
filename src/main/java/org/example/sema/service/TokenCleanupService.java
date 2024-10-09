package org.example.sema.service;

import org.example.sema.entities.PasswordResetToken;
import org.example.sema.repository.PasswordResetTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class TokenCleanupService {

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanupExpiredTokens() {
        Date now = new Date();
        List<PasswordResetToken> expiredTokens = passwordResetTokenRepository.findAllByExpiryDateBefore(now);

        if (!expiredTokens.isEmpty()) {
            passwordResetTokenRepository.deleteAll(expiredTokens);
            System.out.println("Deleted " + expiredTokens.size() + " expired tokens.");
        }
    }
}
