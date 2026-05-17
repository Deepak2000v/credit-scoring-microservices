package com.credit.userms.service;

import com.credit.userms.dto.UserDTO;
import com.credit.userms.entity.User;
import com.credit.userms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * Register a new user into the database.
     */
    public User registerUser(String username, String email, String password, String role) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setRole(role != null ? role : "ROLE_USER");
        return userRepository.save(user);
    }

    /**
     * Get user details by userId.
     * This method is consumed by CreditScoringServiceMS via REST API.
     */
    public UserDTO getUserDetails(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            return new UserDTO(user.getUsername(), user.getEmail(), user.getRole());
        }
        return null;
    }

    /**
     * Validate login credentials.
     * Returns the User object if credentials are valid, otherwise null.
     */
    public User validateLogin(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordEncoder.matches(password, user.getPasswordHash())) {
                return user;
            }
        }
        return null;
    }

    /**
     * Update user details.
     */
    public User updateUser(Long userId, String email, String role) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (email != null) user.setEmail(email);
            if (role != null) user.setRole(role);
            return userRepository.save(user);
        }
        return null;
    }

    /**
     * Delete a user by userId.
     */
    public boolean deleteUser(Long userId) {
        if (userRepository.existsById(userId)) {
            userRepository.deleteById(userId);
            return true;
        }
        return false;
    }
}
