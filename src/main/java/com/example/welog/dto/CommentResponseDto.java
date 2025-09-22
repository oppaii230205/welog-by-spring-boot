package com.example.welog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponseDto {
    private Long id;
    private String content;
    // private PostResponseDto post; // prevent infinite recursion
    private UserResponseDto user;
    private Integer level;
    private Set<CommentResponseDto> replies;
    private OffsetDateTime createdAt;
}
