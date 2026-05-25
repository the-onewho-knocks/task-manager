package com.taskmanager.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponseDTO {

    private String token;
    private String tokenType;
    private String username;
    private String email;
    private String role;
    private long expiresIn;
}