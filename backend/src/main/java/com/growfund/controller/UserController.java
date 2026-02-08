package com.growfund.controller;

import com.growfund.dto.UserDTO;
import com.growfund.model.User;
import com.growfund.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/sync")
    public ResponseEntity<User> syncUser(@RequestBody UserDTO userDTO,
            @AuthenticationPrincipal String uid) {
        try {
            // We could verify the UID here if needed, but the Filter already verified the
            // token.
            // Ideally we trust the token's claims.
            userDTO.setUid(uid);
            User user = userService.syncUser(userDTO);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            e.printStackTrace(); // Log to console
            return ResponseEntity.internalServerError().build();
        }
    }
}
