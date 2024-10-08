package org.example.sema.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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

import java.util.Date;


@RequestMapping("/auth")
@RestController
@Tag(name = "Authentication", description = "Manage users.")
public class AuthenticationController {
    private final JwtService jwtService;

    private final AuthenticationService authenticationService;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/signup")
    @Operation(
            summary = "Create new user",
            description = "This endpoint allows you to create a new user account by providing necessary registration details.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User registered successfully", content = @Content(schema = @Schema(implementation = ApplicationUser.class))),
                    @ApiResponse(responseCode = "400", description = "Bad Request")
            }
    )
    public ResponseEntity<?> register(@RequestBody RegisterUserDTO registerUserDto) {
        ApplicationUser registeredUser = authenticationService.signup(registerUserDto);

        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    @Operation(
            summary = "Authenticate an existing user",
            description = "Login an existing user by providing a username and password. A JWT token is returned upon successful authentication.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User authenticated successfully", content = @Content(schema = @Schema(implementation = LoginDTO.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid credentials")
            }
    )
    public ResponseEntity<?> authenticate(@RequestBody LoginUserDTO loginUserDto) {
        ApplicationUser authenticatedUser = authenticationService.authenticate(loginUserDto);

        String jwtToken = jwtService.generateToken(authenticatedUser);

        LoginDTO loginResponse = new LoginDTO().setToken(jwtToken).setExpiresIn(jwtService.getExpirationTime());

        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/reset-password")
    @Operation(
            summary = "Request password reset",
            description = "Send a reset token to the email of the user. This token is used to verify the password reset request.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Reset token sent successfully"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordDTO request) {
        String jwtToken = jwtService.generateResetToken(request.getUsername());
        authenticationService.sendPasswordResetToken(request.getUsername(), jwtToken);
        return ResponseEntity.ok("Reset token sent to email.");
    }

    @PostMapping("/set-password")
    @Operation(
            summary = "Reset password using token",
            description = "Reset the user's password by providing a valid reset token and a new password.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Password reset successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid token or token expired")
            }
    )
    public ResponseEntity<?> resetPassword(@RequestBody ChangePasswordDTO request) {
        String token = request.getToken();
        String username = jwtService.extractUsername(token);
        Date expiration = jwtService.extractExpiration(token);
        boolean result = authenticationService.resetPassword(username, expiration, request.getNewPassword());
        if (result) {
            return ResponseEntity.ok("Password has been successfully reset.");
        } else {
            return ResponseEntity.badRequest().body("Invalid token or token expired.");
        }
    }

    @PostMapping("/change-password")
    @Operation(
            summary = "Change password",
            description = "Change the current user's password by providing the old password and a new password.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Password changed successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid old password or other error")
            }
    )
    public ResponseEntity<?> changePassword(@RequestBody PasswordChangeDto passwordChangeRequest) {
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
