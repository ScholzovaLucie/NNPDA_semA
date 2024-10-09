package org.example.sema.controllers;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.example.sema.dtos.RegisterUserDTO;
import org.example.sema.service.JwtService;
import org.example.sema.service.UserService;
import org.example.sema.entities.ApplicationUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testAuthenticatedUser_Success() {
        String token = "Bearer sampleToken";
        String username = "testuser";

        ApplicationUser mockUser = new ApplicationUser();
        mockUser.setUsername(username);

        when(jwtService.extractUsername(anyString())).thenReturn(username);
        when(userService.findUserByUsername(username)).thenReturn(mockUser);

        ResponseEntity<?> response = userController.authenticatedUser(token);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockUser, response.getBody());
    }

    @Test
    void testAuthenticatedUser_UserNotFound() {
        String token = "Bearer sampleToken";
        String username = "testuser";

        when(jwtService.extractUsername(anyString())).thenReturn(username);
        when(userService.findUserByUsername(username)).thenReturn(null);

        ResponseEntity<?> response = userController.authenticatedUser(token);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User not found", response.getBody());
    }

    @Test
    void testUpdateUser_Success() throws Exception {
        String token = "Bearer sampleToken";
        String username = "testuser";
        RegisterUserDTO updateUserDTO = new RegisterUserDTO();

        when(jwtService.extractUsername(anyString())).thenReturn(username);

        ResponseEntity<?> response = userController.updateUser(updateUserDTO, token);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User updated successfully", response.getBody());
        verify(userService, times(1)).updateUser(eq(username), eq(updateUserDTO));
    }

    @Test
    void testUpdateUser_Failure() throws Exception {
        String token = "Bearer sampleToken";
        String username = "testuser";
        RegisterUserDTO updateUserDTO = new RegisterUserDTO();

        when(jwtService.extractUsername(anyString())).thenReturn(username);
        doThrow(new RuntimeException("Update failed")).when(userService).updateUser(anyString(), any(RegisterUserDTO.class));

        ResponseEntity<?> response = userController.updateUser(updateUserDTO, token);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Failed to update user: Update failed", response.getBody());
    }

    @Test
    void testDeleteUser_Success() throws Exception {
        String token = "Bearer sampleToken";
        String username = "testuser";

        when(jwtService.extractUsername(anyString())).thenReturn(username);

        ResponseEntity<?> response = userController.deleteUser(token);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User deleted successfully", response.getBody());
        verify(userService, times(1)).deleteUser(eq(username));
    }

    @Test
    void testDeleteUser_Failure() throws Exception {
        String token = "Bearer sampleToken";
        String username = "testuser";

        when(jwtService.extractUsername(anyString())).thenReturn(username);
        doThrow(new RuntimeException("Deletion failed")).when(userService).deleteUser(anyString());

        ResponseEntity<?> response = userController.deleteUser(token);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Failed to delete user: Deletion failed", response.getBody());
    }
}
