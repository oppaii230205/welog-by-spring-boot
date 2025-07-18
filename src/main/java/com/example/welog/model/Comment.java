// package com.example.welog.model;

// import jakarta.persistence.*;
// import java.time.LocalDateTime;

// @Entity
// @Table(name = "comments")
// public class Comment {
    
//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Long id;
    
//     @Column(columnDefinition = "TEXT", nullable = false)
//     private String content;
    
//     @Column(name = "author_name")
//     private String authorName;
    
//     @Column(name = "author_email")
//     private String authorEmail;
    
//     @Column(name = "created_at")
//     private LocalDateTime createdAt;
    
//     @ManyToOne(fetch = FetchType.LAZY)
//     @JoinColumn(name = "post_id", nullable = false)
//     private Post post;
    
//     // Constructors
//     public Comment() {
//         this.createdAt = LocalDateTime.now();
//     }
    
//     public Comment(String content, String authorName, String authorEmail, Post post) {
//         this();
//         this.content = content;
//         this.authorName = authorName;
//         this.authorEmail = authorEmail;
//         this.post = post;
//     }
    
//     // Getters and Setters
//     public Long getId() {
//         return id;
//     }
    
//     public void setId(Long id) {
//         this.id = id;
//     }
    
//     public String getContent() {
//         return content;
//     }
    
//     public void setContent(String content) {
//         this.content = content;
//     }
    
//     public String getAuthorName() {
//         return authorName;
//     }
    
//     public void setAuthorName(String authorName) {
//         this.authorName = authorName;
//     }
    
//     public String getAuthorEmail() {
//         return authorEmail;
//     }
    
//     public void setAuthorEmail(String authorEmail) {
//         this.authorEmail = authorEmail;
//     }
    
//     public LocalDateTime getCreatedAt() {
//         return createdAt;
//     }
    
//     public void setCreatedAt(LocalDateTime createdAt) {
//         this.createdAt = createdAt;
//     }
    
//     public Post getPost() {
//         return post;
//     }
    
//     public void setPost(Post post) {
//         this.post = post;
//     }
// }
