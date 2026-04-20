package com.credit.userms.controller;

import com.credit.userms.config.AuthServerConfig;
import com.credit.userms.dto.UserDTO;
import com.credit.userms.entity.User;
import com.credit.userms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthServerConfig authServerConfig;

    /**
     * POST /register
     * Register a new user. No token required.
     * Body: { "username": "john", "email": "john@test.com", "password": "123", "role": "ROLE_USER" }
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String email    = request.get("email");
        String password = request.get("password");
        String role     = request.get("role");

        User user = userService.registerUser(username, email, password, role);
        return ResponseEntity.ok(Map.of(
                "message", "User registered successfully",
                "username", user.getUsername()
        ));
    }

    /**
     * POST /login
     * Login and receive a JWT token. No token required.
     * Body: { "username": "john", "password": "123" }
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");

        User user = userService.validateLogin(username, password);
        if (user != null) {
            String token = authServerConfig.generateToken(user.getUsername(), user.getRole());
            return ResponseEntity.ok(Map.of("token", token));
        }
        return ResponseEntity.status(401).body(Map.of("error", "Invalid username or password"));
    }

    /**
     * GET /users/{userId}
     * Get user details by ID.
     * This API is consumed by CreditScoringServiceMS to fetch user email.
     * Requires: Authorization: Bearer <token>
     */
    @GetMapping("/users/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable Long userId) {
        UserDTO userDTO = userService.getUserDetails(userId);
        if (userDTO != null) {
            // CreditScoringServiceMS expects the email in the response
            return ResponseEntity.ok(userDTO.getEmail());
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * GET /users/all
     * Get all users.
     * Requires: Authorization: Bearer <token>
     */
    @GetMapping("/users/all")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(Map.of("message", "All users endpoint - extend as needed"));
    }

    /**
     * PUT /users/{userId}
     * Update user details.
     * Body: { "email": "new@email.com", "role": "ROLE_ADMIN" }
     * Requires: Authorization: Bearer <token>
     */
    @PutMapping("/users/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable Long userId,
                                        @RequestBody Map<String, String> request) {
        String email = request.get("email");
        String role  = request.get("role");

        User updated = userService.updateUser(userId, email, role);
        if (updated != null) {
            return ResponseEntity.ok(Map.of("message", "User updated successfully"));
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * DELETE /users/{userId}
     * Delete a user.
     * Requires: Authorization: Bearer <token>
     */
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        boolean deleted = userService.deleteUser(userId);
        if (deleted) {
            return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * GET /users/{userId}/status
     * Check if a user account is active.
     * Requires: Authorization: Bearer <token>
     */
    @GetMapping("/users/{userId}/status")
    public ResponseEntity<?> getUserStatus(@PathVariable Long userId) {
        UserDTO userDTO = userService.getUserDetails(userId);
        if (userDTO != null) {
            return ResponseEntity.ok(Map.of("status", "active", "username", userDTO.getUsername()));
        }
        return ResponseEntity.notFound().build();
    }
}
