package com.example.welog.dto;

import java.util.Set;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SignInResponseDto {
    private String token;
    private final String type = "Bearer";
    private Long id;
    private String username;
    private String email;
    private Set<String> roles;
}
