package com.example.welog.dto;

import java.time.LocalDateTime;
import java.util.Set;

public class PostResponseDto {
    private Long id;
    private String slug;
    private String title;
    private String content;
    private String excerpt;
    private String coverImage;
    private UserResponseDto author;
    private LocalDateTime createdAt;
    private Set<TagResponseDto> tags;
    private Set<CommentResponseDto> comments;

    public PostResponseDto() {}

    public PostResponseDto(Long id, String slug, String title, String content, String excerpt, String coverImage, UserResponseDto author, LocalDateTime createdAt, Set<TagResponseDto> tags, Set<CommentResponseDto> comments) {
        this.id = id;
        this.slug = slug;
        this.title = title;
        this.content = content;
        this.excerpt = excerpt;
        this.coverImage = coverImage;
        this.author = author;
        this.createdAt = createdAt;
        this.tags = tags;
        this.comments = comments;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getExcerpt() {
        return excerpt;
    }

    public void setExcerpt(String excerpt) {
        this.excerpt = excerpt;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public UserResponseDto getAuthor() {
        return author;
    }

    public void setAuthor(UserResponseDto author) {
        this.author = author;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Set<TagResponseDto> getTags() {
        return tags;
    }

    public void setTags(Set<TagResponseDto> tags) {
        this.tags = tags;
    }

    public Set<CommentResponseDto> getComments() {
        return comments;
    }

    public void setComments(Set<CommentResponseDto> comments) {
        this.comments = comments;
    }
}

// import java.time.LocalDateTime;
// import java.util.List;

// public class PostResponseDto {
    
//     private Long id;
//     private String title;
//     private String content;
//     private String slug;
//     private boolean published;
//     private LocalDateTime createdAt;
//     private LocalDateTime updatedAt;
//     private UserResponseDto author;
//     private List<TagResponseDto> tags;
//     private int commentCount;
    
//     // Constructors
//     public PostResponseDto() {}
    
//     public PostResponseDto(Long id, String title, String content, String slug, 
//                           boolean published, LocalDateTime createdAt, LocalDateTime updatedAt) {
//         this.id = id;
//         this.title = title;
//         this.content = content;
//         this.slug = slug;
//         this.published = published;
//         this.createdAt = createdAt;
//         this.updatedAt = updatedAt;
//     }
    
//     // Getters and Setters
//     public Long getId() {
//         return id;
//     }
    
//     public void setId(Long id) {
//         this.id = id;
//     }
    
//     public String getTitle() {
//         return title;
//     }
    
//     public void setTitle(String title) {
//         this.title = title;
//     }
    
//     public String getContent() {
//         return content;
//     }
    
//     public void setContent(String content) {
//         this.content = content;
//     }
    
//     public String getSlug() {
//         return slug;
//     }
    
//     public void setSlug(String slug) {
//         this.slug = slug;
//     }
    
//     public boolean isPublished() {
//         return published;
//     }
    
//     public void setPublished(boolean published) {
//         this.published = published;
//     }
    
//     public LocalDateTime getCreatedAt() {
//         return createdAt;
//     }
    
//     public void setCreatedAt(LocalDateTime createdAt) {
//         this.createdAt = createdAt;
//     }
    
//     public LocalDateTime getUpdatedAt() {
//         return updatedAt;
//     }
    
//     public void setUpdatedAt(LocalDateTime updatedAt) {
//         this.updatedAt = updatedAt;
//     }
    
//     public UserResponseDto getAuthor() {
//         return author;
//     }
    
//     public void setAuthor(UserResponseDto author) {
//         this.author = author;
//     }
    
//     public List<TagResponseDto> getTags() {
//         return tags;
//     }
    
//     public void setTags(List<TagResponseDto> tags) {
//         this.tags = tags;
//     }
    
//     public int getCommentCount() {
//         return commentCount;
//     }
    
//     public void setCommentCount(int commentCount) {
//         this.commentCount = commentCount;
//     }
// }
