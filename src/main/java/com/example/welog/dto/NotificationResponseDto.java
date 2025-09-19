package com.example.welog.dto;

import java.time.OffsetDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponseDto {
    private Long id;
    private UserResponseDto recipient;
    private UserResponseDto sender;
    private PostResponseDto post;
    private String type;
    private String message;
    private boolean isRead;
    private OffsetDateTime createdAt;
}
