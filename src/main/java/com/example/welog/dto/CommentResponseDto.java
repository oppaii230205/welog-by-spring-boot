package com.example.welog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Data
//@AllArgsConstructor
@NoArgsConstructor
public class CommentResponseDto {
    private Long id;
    private String content;
    // private PostResponseDto post; // prevent infinite recursion
    private UserResponseDto user;
    private OffsetDateTime createdAt;

    public CommentResponseDto(Long id, String content, UserResponseDto user, OffsetDateTime createdAt) {
        this.id = id;
        this.content = content;
        // this.post = post;
        this.user = user;
        this.createdAt = createdAt;
    }
}
