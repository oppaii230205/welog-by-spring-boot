// package com.example.welog.model;

// import jakarta.persistence.*;
// import java.util.ArrayList;
// import java.util.List;

// @Entity
// @Table(name = "tags")
// public class Tag {
    
//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Long id;
    
//     @Column(unique = true, nullable = false)
//     private String name;
    
//     @Column(name = "slug", unique = true)
//     private String slug;
    
//     @Column(columnDefinition = "TEXT")
//     private String description;
    
//     @ManyToMany(mappedBy = "tags")
//     private List<Post> posts = new ArrayList<>();
    
//     // Constructors
//     public Tag() {}
    
//     public Tag(String name) {
//         this.name = name;
//         this.slug = generateSlug(name);
//     }
    
//     public Tag(String name, String description) {
//         this(name);
//         this.description = description;
//     }
    
//     // Getters and Setters
//     public Long getId() {
//         return id;
//     }
    
//     public void setId(Long id) {
//         this.id = id;
//     }
    
//     public String getName() {
//         return name;
//     }
    
//     public void setName(String name) {
//         this.name = name;
//         this.slug = generateSlug(name);
//     }
    
//     public String getSlug() {
//         return slug;
//     }
    
//     public void setSlug(String slug) {
//         this.slug = slug;
//     }
    
//     public String getDescription() {
//         return description;
//     }
    
//     public void setDescription(String description) {
//         this.description = description;
//     }
    
//     public List<Post> getPosts() {
//         return posts;
//     }
    
//     public void setPosts(List<Post> posts) {
//         this.posts = posts;
//     }
    
//     private String generateSlug(String name) {
//         if (name == null) return null;
//         return name.toLowerCase()
//                    .replaceAll("[^a-z0-9\\s]", "")
//                    .replaceAll("\\s+", "-")
//                    .trim();
//     }
// }
