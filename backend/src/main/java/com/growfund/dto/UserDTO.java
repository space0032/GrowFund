package com.growfund.dto;

import lombok.Data;

@Data
public class UserDTO {
    private String email;
    private String name;
    private String photoUrl; // Optional, if we want to store profile pic
    private String uid; // Firebase UID
}
