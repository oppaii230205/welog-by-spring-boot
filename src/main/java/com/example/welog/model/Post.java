package com.example.welog.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(name = "slug", unique = true)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "excerpt")
    private String excerpt;

    @Column(name = "cover_image")
    private String coverImage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToMany
    @JoinTable(
        name = "posts_tags",
        joinColumns = @JoinColumn(name = "post_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags;

    private String generateSlug(String title) {
        if (title == null) return null;
        return title.toLowerCase()
                    .replaceAll("[^a-z0-9\\s]", "")
                    .replaceAll("\\s+", "-")
                    .trim();
    }

    public Post() {}

    public Post(String title, String content, String coverImage, User author) {
        this.title = title;
        this.slug = generateSlug(title);
        this.content = content;
        this.excerpt = content.length() > 200 ? content.substring(0, 200) + "..." : content;
        this.coverImage = coverImage;
        this.author = author;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        this.slug = generateSlug(title);
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
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

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }
}

// import jakarta.persistence.*;
// import java.time.LocalDateTime;
// import java.util.ArrayList;
// import java.util.List;

// @Entity
// @Table(name = "posts")
// public class Post {
    
//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Long id;
    
//     @Column(nullable = false)
//     private String title;
    
//     @Column(columnDefinition = "TEXT")
//     private String content;
    
//     @Column(name = "slug", unique = true)
//     private String slug;
    
//     @Column(name = "published")
//     private boolean published = false;
    
//     @Column(name = "created_at")
//     private LocalDateTime createdAt;
    
//     @Column(name = "updated_at")
//     private LocalDateTime updatedAt;
    
//     @ManyToOne(fetch = FetchType.LAZY)
//     @JoinColumn(name = "author_id", nullable = false)
//     private User author;
    
//     @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//     private List<Comment> comments = new ArrayList<>();
    
//     @ManyToMany
//     @JoinTable(
//         name = "post_tags",
//         joinColumns = @JoinColumn(name = "post_id"),
//         inverseJoinColumns = @JoinColumn(name = "tag_id")
//     )
//     private List<Tag> tags = new ArrayList<>();
    
//     // Constructors
//     public Post() {
//         this.createdAt = LocalDateTime.now();
//         this.updatedAt = LocalDateTime.now();
//     }
    
//     public Post(String title, String content, User author) {
//         this();
//         this.title = title;
//         this.content = content;
//         this.author = author;
//         this.slug = generateSlug(title);
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
//         this.slug = generateSlug(title);
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
    
//     public User getAuthor() {
//         return author;
//     }
    
//     public void setAuthor(User author) {
//         this.author = author;
//     }
    
//     public List<Comment> getComments() {
//         return comments;
//     }
    
//     public void setComments(List<Comment> comments) {
//         this.comments = comments;
//     }
    
//     public List<Tag> getTags() {
//         return tags;
//     }
    
//     public void setTags(List<Tag> tags) {
//         this.tags = tags;
//     }
    
//     @PreUpdate
//     public void preUpdate() {
//         this.updatedAt = LocalDateTime.now();
//     }
    
//     private String generateSlug(String title) {
//         if (title == null) return null;
//         return title.toLowerCase()
//                    .replaceAll("[^a-z0-9\\s]", "")
//                    .replaceAll("\\s+", "-")
//                    .trim();
//     }
// }
