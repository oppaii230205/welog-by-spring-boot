package com.example.welog.model;

import java.time.LocalDateTime;

// import jakarta.persistence.*;
// import java.time.LocalDateTime;
// import java.util.ArrayList;
// import java.util.List;

// @Entity
// @Table(name = "users")
// public class User {
    
//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Long id;
    
//     @Column(unique = true, nullable = false)
//     private String username;
    
//     @Column(unique = true, nullable = false)
//     private String email;
    
//     @Column(nullable = false)
//     private String password;
    
//     @Column(name = "first_name")
//     private String firstName;
    
//     @Column(name = "last_name")
//     private String lastName;
    
//     @Column(name = "created_at")
//     private LocalDateTime createdAt;
    
//     @Column(name = "updated_at")
//     private LocalDateTime updatedAt;
    
//     @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//     private List<Post> posts = new ArrayList<>();
    
//     // Constructors
//     public User() {
//         this.createdAt = LocalDateTime.now();
//         this.updatedAt = LocalDateTime.now();
//     }
    
//     public User(String username, String email, String password) {
//         this();
//         this.username = username;
//         this.email = email;
//         this.password = password;
//     }
    
//     // Getters and Setters
//     public Long getId() {
//         return id;
//     }
    
//     public void setId(Long id) {
//         this.id = id;
//     }
    
//     public String getUsername() {
//         return username;
//     }
    
//     public void setUsername(String username) {
//         this.username = username;
//     }
    
//     public String getEmail() {
//         return email;
//     }
    
//     public void setEmail(String email) {
//         this.email = email;
//     }
    
//     public String getPassword() {
//         return password;
//     }
    
//     public void setPassword(String password) {
//         this.password = password;
//     }
    
//     public String getFirstName() {
//         return firstName;
//     }
    
//     public void setFirstName(String firstName) {
//         this.firstName = firstName;
//     }
    
//     public String getLastName() {
//         return lastName;
//     }
    
//     public void setLastName(String lastName) {
//         this.lastName = lastName;
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
    
//     public List<Post> getPosts() {
//         return posts;
//     }
    
//     public void setPosts(List<Post> posts) {
//         this.posts = posts;
//     }
    
//     @PreUpdate
//     public void preUpdate() {
//         this.updatedAt = LocalDateTime.now();
//     }
// }

import jakarta.persistence.*;

@Entity
@Table(name = "users") // thêm dòng này
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String photo;
    private String role;
    private String password;

    @Column(name = "password_changed_at")
    private LocalDateTime passwordChangedAt;

    @Column(name = "password_reset_token")
    private String passwordResetToken;
    
    @Column(name = "password_reset_expires")
    private LocalDateTime passwordResetExpires;
    
    private Boolean active = true;

    public User() {} // JPA requires a no-arg constructor

    public User(String name, String email, String photo, String role, String password) {
        this.name = name;
        this.email = email;
        this.photo = photo;
        this.role = role;
        this.password = password;
    }

    // Getters and setters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhoto() { return photo; }
    public String getRole() { return role; }
    public String getPassword() { return password; }
    public LocalDateTime getPasswordChangedAt() { return passwordChangedAt; }
    public String getPasswordResetToken() { return passwordResetToken; }
    public LocalDateTime getPasswordResetExpires() { return passwordResetExpires; }
    public boolean isActive() { return active; }

    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPhoto(String photo) { this.photo = photo; }
    public void setRole(String role) { this.role = role; }
    public void setPassword(String password) { this.password = password; }
    public void setPasswordChangedAt(LocalDateTime passwordChangedAt) { this.passwordChangedAt = passwordChangedAt; }
    public void setPasswordResetToken(String passwordResetToken) { this.passwordResetToken = passwordResetToken; }
    public void setPasswordResetExpires(LocalDateTime passwordResetExpires) { this.passwordResetExpires = passwordResetExpires; }
    public void setActive(boolean active) { this.active = active; }
}
