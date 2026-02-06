package com.growfund.seedtowealth.user;

import com.google.firebase.auth.FirebaseToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User syncUser(FirebaseToken token) {
        String uid = token.getUid();
        String email = token.getEmail();
        String name = token.getName();
        String picture = token.getPicture();

        Optional<User> existingUser = userRepository.findByFirebaseUid(uid);

        if (existingUser.isPresent()) {
            User user = existingUser.get();
            // Update mutable fields if changed
            user.setDisplayName(name);
            user.setPhotoUrl(picture);
            user.setLastLoginAt(LocalDateTime.now());
            return userRepository.save(user);
        } else {
            // Create new user
            User newUser = new User();
            newUser.setFirebaseUid(uid);
            newUser.setEmail(email != null ? email : "");
            newUser.setDisplayName(name);
            newUser.setPhotoUrl(picture);
            newUser.setCoins(10000); // Initial bonus
            newUser.setCreatedAt(LocalDateTime.now());
            newUser.setLastLoginAt(LocalDateTime.now());
            return userRepository.save(newUser);
        }
    }

    @Transactional(readOnly = true)
    public User getUser(String uid) {
        return userRepository.findByFirebaseUid(uid)
                .orElseThrow(() -> new RuntimeException("User not found: " + uid));
    }
}
