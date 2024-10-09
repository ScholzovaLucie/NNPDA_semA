package org.example.sema.service;

import org.example.sema.dtos.LoginUserDto;
import org.example.sema.dtos.RegisterUserDto;
import org.example.sema.entities.ApplicationUser;
import org.example.sema.repository.ApplicationUserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    private final ApplicationUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationService(ApplicationUserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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
}
