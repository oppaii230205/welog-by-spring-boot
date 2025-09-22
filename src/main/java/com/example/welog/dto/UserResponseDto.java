package com.example.welog.dto;

import java.time.OffsetDateTime;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {
    private Long id;
    private String name;
    private String email;
    private String photo;
    private OffsetDateTime createdAt;
    private Set<String> roles;
}
