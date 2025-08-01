package com.example.welog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostResponseDto {
    private Long id;
    private String slug;
    private String title;
    private String content;
    private String excerpt;
    private String coverImage;
    private UserResponseDto author;
    private OffsetDateTime createdAt;
    private Set<TagResponseDto> tags;
    private Set<CommentResponseDto> comments;
}
