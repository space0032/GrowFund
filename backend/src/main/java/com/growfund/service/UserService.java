package com.growfund.service;

import com.growfund.dto.UserDTO;
import com.growfund.model.User;
import com.growfund.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public User syncUser(UserDTO userDTO) {
        // First try to find by Firebase UID if available
        if (userDTO.getUid() != null && !userDTO.getUid().isEmpty()) {
            Optional<User> existingByUid = userRepository.findByFirebaseUid(userDTO.getUid());
            if (existingByUid.isPresent()) {
                User user = existingByUid.get();
                user.setLastLoginAt(LocalDateTime.now());
                // Update other fields if needed
                if (userDTO.getName() != null && !userDTO.getName().equals(user.getFullName())) {
                    user.setFullName(userDTO.getName());
                }
                return userRepository.save(user);
            }
        }

        // Fall back to email lookup for existing users without Firebase UID
        return userRepository.findByEmail(userDTO.getEmail())
                .map(existingUser -> {
                    existingUser.setLastLoginAt(LocalDateTime.now());
                    existingUser.setFirebaseUid(userDTO.getUid()); // Update Firebase UID
                    // Update other fields if needed, e.g., name or photo if changed
                    if (userDTO.getName() != null && !userDTO.getName().equals(existingUser.getFullName())) {
                        existingUser.setFullName(userDTO.getName());
                    }
                    return userRepository.save(existingUser);
                })
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(userDTO.getEmail());
                    newUser.setFirebaseUid(userDTO.getUid());
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

    public User getUserByFirebaseUid(String firebaseUid) {
        return userRepository.findByFirebaseUid(firebaseUid)
                .orElseThrow(() -> new RuntimeException("User not found with Firebase UID: " + firebaseUid));
    }
}
