package com.example.welog.dto;

public class PostCreateDto {
    private String title;
    private String content;
    private String coverImage;
    private Long author;

    public PostCreateDto() {}

    public PostCreateDto(String title, String content, String coverImage, Long author) {
        this.title = title;
        this.content = content;
        this.coverImage = coverImage;
        this.author = author;
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

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public Long getAuthor() {
        return author;
    }

    public void setAuthor(Long author) {
        this.author = author;
    }
}

// import jakarta.validation.constraints.NotBlank;
// import jakarta.validation.constraints.Size;
// import java.util.List;

// public class PostCreateDto {
    
//     @NotBlank(message = "Title is required")
//     @Size(max = 255, message = "Title must not exceed 255 characters")
//     private String title;
    
//     @NotBlank(message = "Content is required")
//     private String content;
    
//     private boolean published = false;
//     private List<String> tagNames;
    
//     // Constructors
//     public PostCreateDto() {}
    
//     public PostCreateDto(String title, String content) {
//         this.title = title;
//         this.content = content;
//     }
    
//     // Getters and Setters
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
    
//     public boolean isPublished() {
//         return published;
//     }
    
//     public void setPublished(boolean published) {
//         this.published = published;
//     }
    
//     public List<String> getTagNames() {
//         return tagNames;
//     }
    
//     public void setTagNames(List<String> tagNames) {
//         this.tagNames = tagNames;
//     }
// }
