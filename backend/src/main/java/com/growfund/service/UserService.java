package com.growfund.service;

import com.growfund.dto.UserDTO;
import com.growfund.model.User;
import com.growfund.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public User syncUser(UserDTO userDTO) {
        return userRepository.findByEmail(userDTO.getEmail())
                .map(existingUser -> {
                    existingUser.setLastLoginAt(LocalDateTime.now());
                    // Update other fields if needed, e.g., name or photo if changed
                    if (userDTO.getName() != null && !userDTO.getName().equals(existingUser.getFullName())) {
                        existingUser.setFullName(userDTO.getName());
                    }
                    return userRepository.save(existingUser);
                })
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(userDTO.getEmail());
                    newUser.setFullName(userDTO.getName() != null ? userDTO.getName() : "Farmer");
                    // Generate a username from email or uid if not provided.
                    // Using email prefix for now, handling potential duplicates is a refinement.
                    String username = userDTO.getEmail().split("@")[0];
                    // Ensure username uniqueness logic could go here, keeping simple for MVP.
                    newUser.setUsername(username);

                    // Password is required by model but handled by Firebase. Setting a dummy value.
                    newUser.setPassword("FIREBASE_AUTH_" + userDTO.getUid());

                    newUser.setCreatedAt(LocalDateTime.now());
                    newUser.setLastLoginAt(LocalDateTime.now());
                    newUser.setState("Not Set"); // Default
                    newUser.setPreferredLanguage("en");

                    return userRepository.save(newUser);
                });
    }
}
