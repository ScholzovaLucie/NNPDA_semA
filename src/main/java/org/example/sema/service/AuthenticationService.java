package org.example.sema.service;

import io.jsonwebtoken.Claims;
import org.example.sema.dtos.LoginUserDto;
import org.example.sema.dtos.PasswordChangeDto;
import org.example.sema.dtos.RegisterUserDto;
import org.example.sema.entities.ApplicationUser;
import org.example.sema.entities.PasswordResetToken;
import org.example.sema.repository.ApplicationUserRepository;
import org.example.sema.repository.PasswordResetTokenRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;


@Service
public class AuthenticationService {
    private final ApplicationUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final PasswordResetTokenRepository tokenRepository;

    public AuthenticationService(ApplicationUserRepository userRepository, PasswordEncoder passwordEncoder, EmailService emailService, PasswordResetTokenRepository tokenRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.tokenRepository = tokenRepository;
    }

    public ApplicationUser signup(RegisterUserDto registerUserDto) {
        ApplicationUser user = new ApplicationUser();
        user.setUsername(registerUserDto.getUsername());
        user.setPassword(passwordEncoder.encode(registerUserDto.getPassword()));
        user.setEmail(registerUserDto.getEmail());
        return userRepository.save(user);
    }

    public ApplicationUser authenticate(LoginUserDto loginUserDto) {
        ApplicationUser user = userRepository.findByUsername(loginUserDto.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        if (!passwordEncoder.matches(loginUserDto.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        return user;
    }

    public void sendPasswordResetToken(String username, String resetToken, Date expiration) {
        ApplicationUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        String subject = "Reset Password";
        String body = "Pro resetování hesla použijte neto token: " + resetToken;

        emailService.sendEmail(user.getEmail(), subject, body);

        PasswordResetToken resetTokenDb = new PasswordResetToken();
        resetTokenDb.setToken(resetToken);
        resetTokenDb.setExpiryDate(expiration);
        resetTokenDb.setUser(user);

        tokenRepository.save(resetTokenDb);
    }

    public boolean resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token);

        if (resetToken == null || resetToken.getExpiryDate().before(new Date())) {
            return false;
        }

        ApplicationUser user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        tokenRepository.delete(resetToken);

        return true;
    }

    public void changePassword(String username, String oldPassword, String newPassword) throws Exception {
        ApplicationUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new Exception("Old password is incorrect");
        }

        String encodedNewPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedNewPassword);

        userRepository.save(user);
    }
}
