package org.example.sema.controllers;

import org.example.sema.dtos.*;
import org.example.sema.entities.ApplicationUser;
import org.example.sema.service.AuthenticationService;
import org.example.sema.service.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Date;


@RequestMapping("/auth")
@RestController
public class AuthenticationController {
    private final JwtService jwtService;

    private final AuthenticationService authenticationService;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/signup")
    public ResponseEntity<ApplicationUser> register(@RequestBody RegisterUserDto registerUserDto) {
        ApplicationUser registeredUser = authenticationService.signup(registerUserDto);

        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginDTO> authenticate(@RequestBody LoginUserDto loginUserDto) {
        ApplicationUser authenticatedUser = authenticationService.authenticate(loginUserDto);

        String jwtToken = jwtService.generateToken(authenticatedUser);

        LoginDTO loginResponse = new LoginDTO().setToken(jwtToken).setExpiresIn(jwtService.getExpirationTime());

        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordDto request) {
        String jwtToken = jwtService.generateResetToken(request.getUsername());
        Date expiration = jwtService.extractExpiration(jwtToken);
        authenticationService.sendPasswordResetToken(request.getUsername(), jwtToken, expiration);
        return ResponseEntity.ok("Reset token sent to email.");
    }

    @PostMapping("/set-password")
    public ResponseEntity<String> resetPassword(@RequestBody ChangePasswordDto request) {
        boolean result = authenticationService.resetPassword(request.getToken(), request.getNewPassword());
        if (result) {
            return ResponseEntity.ok("Password has been successfully reset.");
        } else {
            return ResponseEntity.badRequest().body("Invalid token or token expired.");
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody PasswordChangeDto passwordChangeRequest) {
        try {
            authenticationService.changePassword(
                    passwordChangeRequest.getUsername(),
                    passwordChangeRequest.getOldPassword(),
                    passwordChangeRequest.getNewPassword()
            );
            return ResponseEntity.ok("Password changed successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }



}
