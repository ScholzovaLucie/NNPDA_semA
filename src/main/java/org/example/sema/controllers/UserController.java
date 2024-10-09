package org.example.sema.controllers;

import org.example.sema.entities.ApplicationUser;
import org.example.sema.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/users")
@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<ApplicationUser> authenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        ApplicationUser currentUser = (ApplicationUser) authentication.getPrincipal();

        return ResponseEntity.ok(currentUser);
    }

    @GetMapping("/")
    public ResponseEntity<List<ApplicationUser>> allUsers() {
        List<ApplicationUser> users = userService.allUsers();

        return ResponseEntity.ok(users);
    }
}
