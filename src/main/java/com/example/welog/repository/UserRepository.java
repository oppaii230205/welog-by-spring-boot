package com.example.welog.repository;

import com.example.welog.model.User;

import java.util.Optional;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Boolean existsByEmail(String email);

    @Modifying
    @Transactional
    @NativeQuery("UPDATE users SET deleted_at = NOW() where id=?1")
    void softDelete(Long id);
}

