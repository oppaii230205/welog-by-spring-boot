package com.example.welog.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.welog.model.PostLike;
import com.example.welog.model.PostLikeId;

import jakarta.transaction.Transactional;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, PostLikeId> {
    @Transactional
    boolean existsByUserIdAndPostId(Long userId, Long postId);

    @Transactional
    List<PostLike> findByPostId(Long postId);
}
