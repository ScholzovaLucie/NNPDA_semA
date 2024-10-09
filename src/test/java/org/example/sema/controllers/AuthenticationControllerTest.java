package org.example.sema.controllers;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.example.sema.dtos.*;
import org.example.sema.entities.ApplicationUser;
import org.example.sema.service.JwtService;
import org.example.sema.service.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Date;

class AuthenticationControllerTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private AuthenticationController authenticationController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testRegister_Success() {
        // Mock input data
        RegisterUserDTO registerUserDTO = new RegisterUserDTO();
        registerUserDTO.setUsername("testuser");
        registerUserDTO.setEmail("testuser@example.com");
        registerUserDTO.setPassword("password123");

        // Mock ApplicationUser returned from the service
        ApplicationUser registeredUser = new ApplicationUser();
        registeredUser.setUsername("testuser");
        registeredUser.setEmail("testuser@example.com");

        // Mock service behavior
        when(authenticationService.signup(any(RegisterUserDTO.class))).thenReturn(registeredUser);

        // Call the method
        ResponseEntity<?> response = authenticationController.register(registerUserDTO);

        // Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(registeredUser, response.getBody());

        // Verify that the service was called
        verify(authenticationService, times(1)).signup(any(RegisterUserDTO.class));
    }

    @Test
    void testLogin_Success() {
        LoginUserDTO loginUserDTO = new LoginUserDTO();
        ApplicationUser authenticatedUser = new ApplicationUser();
        String token = "jwtToken";

        when(authenticationService.authenticate(any(LoginUserDTO.class))).thenReturn(authenticatedUser);
        when(jwtService.generateToken(any(ApplicationUser.class))).thenReturn(token);

        ResponseEntity<?> response = authenticationController.authenticate(loginUserDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testResetPassword_Success() {
        ResetPasswordDTO resetPasswordDTO = new ResetPasswordDTO();
        resetPasswordDTO.setUsername("testuser");
        String token = "resetToken";

        when(jwtService.generateResetToken(anyString())).thenReturn(token);

        ResponseEntity<?> response = authenticationController.resetPassword(resetPasswordDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Reset token sent to email.", response.getBody());
    }

    @Test
    void testSetPassword_Success() {
        // Mock input data
        ChangePasswordDTO request = new ChangePasswordDTO();
        request.setToken("resetToken");
        request.setNewPassword("newPassword123");

        // Mock extracted username and expiration from token
        String username = "testuser";
        Date expiration = new Date(System.currentTimeMillis() + 100000); // some future expiration date

        // Mock service behavior
        when(jwtService.extractUsername(anyString())).thenReturn(username);
        when(jwtService.extractExpiration(anyString())).thenReturn(expiration);
        when(authenticationService.resetPassword(username, expiration, request.getNewPassword())).thenReturn(true);

        // Call the method
        ResponseEntity<?> response = authenticationController.resetPassword(request);

        // Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Password has been successfully reset.", response.getBody());

        // Verify that the services were called correctly
        verify(jwtService, times(1)).extractUsername(anyString());
        verify(jwtService, times(1)).extractExpiration(anyString());
        verify(authenticationService, times(1)).resetPassword(username, expiration, request.getNewPassword());
    }

    @Test
    void testSetPassword_Failure_InvalidToken() {
        // Mock input data
        ChangePasswordDTO request = new ChangePasswordDTO();
        request.setToken("invalidToken");
        request.setNewPassword("newPassword123");

        // Mock extracted username and expiration from token
        String username = "testuser";
        Date expiration = new Date(System.currentTimeMillis() - 100000); // expired token

        // Mock service behavior
        when(jwtService.extractUsername(anyString())).thenReturn(username);
        when(jwtService.extractExpiration(anyString())).thenReturn(expiration);
        when(authenticationService.resetPassword(username, expiration, request.getNewPassword())).thenReturn(false);

        // Call the method
        ResponseEntity<?> response = authenticationController.resetPassword(request);

        // Verify the response
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid token or token expired.", response.getBody());

        // Verify that the services were called correctly
        verify(jwtService, times(1)).extractUsername(anyString());
        verify(jwtService, times(1)).extractExpiration(anyString());
        verify(authenticationService, times(1)).resetPassword(username, expiration, request.getNewPassword());
    }

    @Test
    void testChangePassword_Success() throws Exception {
        // Mock input data
        PasswordChangeDto passwordChangeRequest = new PasswordChangeDto();
        passwordChangeRequest.setUsername("testuser");
        passwordChangeRequest.setOldPassword("oldPassword123");
        passwordChangeRequest.setNewPassword("newPassword123");

        // Mock service behavior
        doNothing().when(authenticationService).changePassword(
                passwordChangeRequest.getUsername(),
                passwordChangeRequest.getOldPassword(),
                passwordChangeRequest.getNewPassword()
        );

        // Call the method
        ResponseEntity<?> response = authenticationController.changePassword(passwordChangeRequest);

        // Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Password changed successfully", response.getBody());

        // Verify that the service was called correctly
        verify(authenticationService, times(1)).changePassword(
                passwordChangeRequest.getUsername(),
                passwordChangeRequest.getOldPassword(),
                passwordChangeRequest.getNewPassword()
        );
    }

    @Test
    void testChangePassword_Failure() throws Exception {
        // Mock input data
        PasswordChangeDto passwordChangeRequest = new PasswordChangeDto();
        passwordChangeRequest.setUsername("testuser");
        passwordChangeRequest.setOldPassword("wrongOldPassword");
        passwordChangeRequest.setNewPassword("newPassword123");

        // Mock service behavior - throw an exception
        doThrow(new RuntimeException("Old password is incorrect"))
                .when(authenticationService)
                .changePassword(
                        passwordChangeRequest.getUsername(),
                        passwordChangeRequest.getOldPassword(),
                        passwordChangeRequest.getNewPassword()
                );

        // Call the method
        ResponseEntity<?> response = authenticationController.changePassword(passwordChangeRequest);

        // Verify the response
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Old password is incorrect", response.getBody());

        // Verify that the service was called correctly
        verify(authenticationService, times(1)).changePassword(
                passwordChangeRequest.getUsername(),
                passwordChangeRequest.getOldPassword(),
                passwordChangeRequest.getNewPassword()
        );
    }
}
