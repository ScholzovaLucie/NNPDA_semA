package org.example.sema.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.example.sema.dto.GetByIdDTO;
import org.example.sema.dto.UpdateUserDTO;
import org.example.sema.entity.ApplicationUser;
import org.example.sema.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@RequestMapping("/users")
@RestController
@Tag(name = "User", description = "Info about users.")
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    @Operation(
            summary = "Retrieve information about the authenticated user",
            description = "Returns the details of the currently authenticated user, based on the JWT token provided in the Authorization header.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User information retrieved successfully", content = @Content(schema = @Schema(implementation = ApplicationUser.class))),
                    @ApiResponse(responseCode = "404", description = "User not found"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token")
            }
    )
    public ResponseEntity<?> authenticatedUser() {
        try {
            // Gets the authentication of the currently logged in user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication(); // Retrieves the current authentication object from the security context
            ApplicationUser user = (ApplicationUser) authentication.getPrincipal(); // Gets the user from the authentication object
            String username = user.getUsername();

            user = userService.findUserByUsername(username);

            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }

            Map<String, Object> responseData = new LinkedHashMap<>();
            responseData.put("id", user.getId());
            responseData.put("username", user.getUsername());
            responseData.put("email", user.getEmail());

            responseData.put("enabled", user.isEnabled());
            responseData.put("accountNonExpired", user.isAccountNonExpired());
            responseData.put("accountNonLocked", user.isAccountNonLocked());
            responseData.put("credentialsNonExpired", user.isCredentialsNonExpired());
            responseData.put("authorities", user.getAuthorities());

            return ResponseEntity.ok(responseData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/all")
    @Operation(
            summary = "Retrieve all users",
            description = "Returns the details of the currently authenticated user, based on the JWT token provided in the Authorization header.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Users information retrieved successfully", content = @Content(schema = @Schema(implementation = ApplicationUser.class))),
            }
    )
    public ResponseEntity<?> allUsers() {
        try {
            List<ApplicationUser> users = userService.allUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/")
    @Operation(
            summary = "Update the authenticated user's information",
            description = "Allows the currently authenticated user to update their account details, such as name or email, based on the provided request body.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User updated successfully"),
                    @ApiResponse(responseCode = "500", description = "Internal server error - Failed to update user"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token")
            }
    )
    public ResponseEntity<?> updateUser(@Valid @RequestBody UpdateUserDTO updateUserRequest) {
        try {
            userService.updateUser(updateUserRequest);
            return ResponseEntity.ok("User updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update user: " + e.getMessage());
        }
    }

    @DeleteMapping("/")
    @Operation(
            summary = "Delete the authenticated user's account",
            description = "Deletes the account of the currently authenticated user, removing all associated data.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User deleted successfully"),
                    @ApiResponse(responseCode = "500", description = "Internal server error - Failed to delete user"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token")
            }
    )
    public ResponseEntity<?> deleteUser(@Valid @RequestBody GetByIdDTO data) {
        try {
            userService.deleteUser(data.getId());
            return ResponseEntity.ok("User deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete user: " + e.getMessage());
        }
    }

}
