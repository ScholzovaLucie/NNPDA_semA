package org.example.sema.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.sema.dtos.RegisterUserDTO;
import org.example.sema.entities.ApplicationUser;
import org.example.sema.service.JwtService;
import org.example.sema.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/users")
@RestController
@Tag(name = "User", description = "Info about users.")
public class UserController {
    private final UserService userService;

    private final JwtService jwtService;

    public UserController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

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
    public ResponseEntity<?> authenticatedUser(@RequestHeader("Authorization") String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        String username = jwtService.extractUsername(token);

        ApplicationUser user = userService.findUserByUsername(username);

        if (user == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        return ResponseEntity.ok(user);
    }

    @GetMapping("/all")
    @Operation(
            summary = "Retrieve all users",
            description = "Returns the details of the currently authenticated user, based on the JWT token provided in the Authorization header.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Users information retrieved successfully", content = @Content(schema = @Schema(implementation = ApplicationUser.class))),
            }
    )
    public ResponseEntity<?> allUsers(@RequestHeader("Authorization") String token) {
        List<ApplicationUser> users = userService.allUsers();
        return ResponseEntity.ok(users);
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
    public ResponseEntity<?> updateUser(@RequestBody RegisterUserDTO updateUserRequest, @RequestBody int id, @RequestHeader("Authorization") String token) {
        try {
            userService.updateUser(id, updateUserRequest);
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
    public ResponseEntity<?> deleteUser(@RequestBody int id, @RequestHeader("Authorization") String token) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok("User deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete user: " + e.getMessage());
        }
    }

}
