package com.example.welog.repository;

import com.example.welog.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setPassword("password");
        entityManager.persist(user);
        entityManager.flush();
    }

    @Test
    void findByEmail_UserExists_ReturnsUser() {
        // Act
        Optional<User> found = userRepository.findByEmail(user.getEmail());

        // Assert
        assertTrue(found.isPresent());
        assertEquals(user.getEmail(), found.get().getEmail());
    }

    @Test
    void findByEmail_UserNotExists_ReturnsEmpty() {
        // Act
        Optional<User> found = userRepository.findByEmail("nonexistent@example.com");

        // Assert
        assertFalse(found.isPresent());
    }

    @Test
    void existsByEmail_UserExists_ReturnsTrue() {
        // Act
        boolean exists = userRepository.existsByEmail(user.getEmail());

        // Assert
        assertTrue(exists);
    }

//    @Test
//    void softDelete_WhenCalled_SetsDeletedAt() {
//        // Act
//        userRepository.softDelete(user.getId());
//        entityManager.clear(); // Clear persistence context to force reload from DB
//
//        // Assert
//        User deletedUser = entityManager.find(User.class, user.getId());
//        assertNotNull(deletedUser.getDeletedAt());
//    }

    @Test
    void findByEmail_WhenSoftDeleted_ReturnsNothing() {
        userRepository.softDelete(user.getId());
        entityManager.clear();

        Optional<User> found = userRepository.findByEmail(user.getEmail());
        assertFalse(found.isPresent());
    }
}