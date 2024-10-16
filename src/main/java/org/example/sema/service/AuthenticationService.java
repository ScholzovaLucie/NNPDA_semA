package org.example.sema.service;

import org.example.sema.dto.LoginUserDTO;
import org.example.sema.dto.RegisterUserDTO;
import org.example.sema.entity.ApplicationUser;
import org.example.sema.repository.ApplicationUserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class AuthenticationService {
    private final ApplicationUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public AuthenticationService(ApplicationUserRepository userRepository, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    public ApplicationUser signup(RegisterUserDTO registerUserDto) {
        ApplicationUser user = new ApplicationUser();
        user.setUsername(registerUserDto.getUsername());
        user.setPassword(passwordEncoder.encode(registerUserDto.getPassword()));
        user.setEmail(registerUserDto.getEmail());
        return userRepository.save(user);
    }

    public ApplicationUser authenticate(LoginUserDTO loginUserDto) {
        ApplicationUser user = userRepository.findByUsername(loginUserDto.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        if (!passwordEncoder.matches(loginUserDto.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        return user;
    }

    public void sendPasswordResetToken(String username, String resetToken) {
        ApplicationUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        String subject = "Reset Password";
        String body = "Pro resetování hesla použijte neto token: " + resetToken;

        emailService.sendEmail(user.getEmail(), subject, body);
    }

    public boolean resetPassword(String username, String newPassword) {

        Optional<ApplicationUser> optionalUser = userRepository.findByUsername(username);

        if (optionalUser.isEmpty()) {
            return false;
        }

        ApplicationUser user = optionalUser.get();

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

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
