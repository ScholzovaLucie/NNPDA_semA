package org.example.sema.service;

import org.example.sema.dtos.LoginUserDto;
import org.example.sema.dtos.RegisterUserDto;
import org.example.sema.entities.ApplicationUser;
import org.example.sema.repository.ApplicationUserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


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

    public void sendPasswordResetToken(String username, String resetToken) {
        ApplicationUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        String subject = "Reset Password";
        String body = "Pro resetování hesla použijte neto token: " + resetToken;

        emailService.sendEmail(user.getEmail(), subject, body);
    }
}
