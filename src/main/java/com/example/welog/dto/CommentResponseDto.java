package com.example.welog.dto;

import java.time.LocalDateTime;

public class CommentResponseDto {
    private Long id;
    private String content;
    // private PostResponseDto post; // prevent infinite recursion
    private UserResponseDto user;
    private LocalDateTime createdAt;

    public CommentResponseDto() {}

    public CommentResponseDto(Long id, String content, UserResponseDto user, LocalDateTime createdAt) {
        this.id = id;
        this.content = content;
        // this.post = post;
        this.user = user;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    // public PostResponseDto getPost() {
    //     return post;
    // }

    // public void setPost(PostResponseDto post) {
    //     this.post = post;
    // }

    public UserResponseDto getUser() {
        return user;
    }

    public void setUser(UserResponseDto user) {
        this.user = user;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
