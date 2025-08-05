package com.example.welog.repository;

import com.example.welog.model.Post;
import com.example.welog.model.User;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class PostRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PostRepository postRepository;

    private Post post;

    @BeforeEach
    void setUp() {
        // Clear any existing data
        entityManager.clear();

        User author = new User();
        // author.setId(1L); // ID is auto-generated, so we don't set it manually
        author.setName("Test User");
        author.setEmail("test@example.com");
        author.setPassword("password");
        author = entityManager.persist(author); // This returns the managed entity

        post = new Post("Test Title", "Test Content", null, author);
        // post.setId(1L);
        post.setCreatedAt(OffsetDateTime.now());
        
        entityManager.persist(post);
        entityManager.flush();
    }

    @Test
    void findById_PostExists_ReturnsPost() {
        // Act
        Optional<Post> found = postRepository.findById(post.getId());

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo(post.getTitle());
    }

    @Test
    void findById_PostNotExists_ReturnsEmpty() {
        // Act
        Optional<Post> found = postRepository.findById(999L);

        // Assert
        assertThat(found).isNotPresent();
    }

    @Test
    void existsById_PostExists_ReturnsTrue() {
        // Act
        boolean exists = postRepository.existsById(post.getId());

        // Assert
        assertThat(exists).isTrue();
    }

    @Test
    void findById_WhenSoftDeleted_ReturnsNothing() {
        // Arrange
        Long postId = post.getId();
        
        // Act - perform soft delete
        postRepository.softDelete(postId);
        entityManager.flush();
        entityManager.clear(); // Clear persistence context to force reload from DB
        
        // Assert
        Optional<Post> deletedPost = postRepository.findById(postId);
        assertThat(deletedPost).isNotPresent();
    }
}