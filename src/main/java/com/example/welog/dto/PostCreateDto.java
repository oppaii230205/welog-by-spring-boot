// package com.example.welog.dto;

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
