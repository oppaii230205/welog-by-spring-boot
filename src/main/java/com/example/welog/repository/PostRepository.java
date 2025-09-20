package com.example.welog.repository;

import java.util.Optional;

import jakarta.transaction.Transactional;
import org.hibernate.annotations.Where;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.welog.model.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    // Define methods for querying posts, e.g., by slug, author, etc.
    Optional<Post> findBySlug(String slug);

    Page<Post> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    // Additional query methods can be defined here
    @Modifying
    @Transactional
    @NativeQuery("UPDATE posts SET deleted_at = NOW() where id=?1")
    void softDelete(Long id);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.tags LEFT JOIN FETCH p.comments WHERE p.id = :id")
    Optional<Post> findByIdWithTagsAndComments(@Param("id") Long id);
}
