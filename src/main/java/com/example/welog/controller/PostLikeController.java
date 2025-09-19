package com.example.welog.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.welog.dto.UserResponseDto;
import com.example.welog.model.PostLike;
import com.example.welog.service.PostLikeService;

@RestController
@RequestMapping("/api/v1")
public class PostLikeController {
    private final PostLikeService postLikeService;

    public PostLikeController(PostLikeService postLikeService) {
        this.postLikeService = postLikeService;
    }

    @PostMapping("/posts/{postId}/likes")
    public ResponseEntity<?> likePost(@PathVariable Long postId, @RequestParam Long userId) {
        postLikeService.likePost(userId, postId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/posts/{postId}/likes")
    public ResponseEntity<?> unlikePost(@PathVariable Long postId, @RequestParam Long userId) {
        postLikeService.unlikePost(userId, postId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/posts/{postId}/likes")
    public ResponseEntity<List<UserResponseDto>> getLikes(@PathVariable Long postId) {
        return ResponseEntity.ok(postLikeService.getLikes(postId));
    }
}
