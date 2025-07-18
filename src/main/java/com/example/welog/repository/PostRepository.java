package com.example.welog.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.welog.model.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    // Define methods for querying posts, e.g., by slug, author, etc.
    Optional<Post> findBySlug(String slug);

    // Additional query methods can be defined here
}

// import com.example.welog.model.Post;
// import com.example.welog.model.User;
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.Pageable;
// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.Query;
// import org.springframework.data.repository.query.Param;
// import org.springframework.stereotype.Repository;
// import java.util.List;
// import java.util.Optional;

// @Repository
// public interface PostRepository extends JpaRepository<Post, Long> {
    
//     Optional<Post> findBySlug(String slug);
    
//     Page<Post> findByPublishedTrue(Pageable pageable);
    
//     Page<Post> findByAuthor(User author, Pageable pageable);
    
//     Page<Post> findByAuthorAndPublishedTrue(User author, Pageable pageable);
    
//     @Query("SELECT p FROM Post p WHERE p.published = true AND " +
//            "(LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
//            "LOWER(p.content) LIKE LOWER(CONCAT('%', :keyword, '%')))")
//     Page<Post> findPublishedPostsByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
//     @Query("SELECT p FROM Post p JOIN p.tags t WHERE t.name = :tagName AND p.published = true")
//     Page<Post> findPublishedPostsByTagName(@Param("tagName") String tagName, Pageable pageable);
    
//     List<Post> findTop5ByPublishedTrueOrderByCreatedAtDesc();
// }
