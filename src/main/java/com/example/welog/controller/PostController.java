package com.example.welog.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.welog.dto.PostCreateDto;
import com.example.welog.dto.PostPatchDto;
import com.example.welog.dto.PostResponseDto;
import com.example.welog.service.PostService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/v1/posts")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    // @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<List<PostResponseDto>> getAllPosts(Pageable pageable) {
        return ResponseEntity.ok(postService.getAllPosts(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponseDto> getPost(@PathVariable Long id) {
        return ResponseEntity.ok(postService.getPost(id));
    }

    // Cannot distinguish between slug and id in the URL, so we will not implement this method
    // @GetMapping("/{slug}")
    // public ResponseEntity<PostResponseDto> getPostBySlug(@PathVariable String slug) {
    //     return ResponseEntity.ok(postService.getPostBySlug(slug));
    // }

    @PostMapping
    public ResponseEntity<PostResponseDto> createPost(@Valid @RequestBody PostCreateDto postCreateDto) {
        PostResponseDto createdPost = postService.createPost(postCreateDto);
        return ResponseEntity.status(201).body(createdPost);
    }

    @PostMapping("/{id}/coverImage")
    public ResponseEntity<String> uploadCoverImage(@PathVariable Long id, @RequestParam("coverImage") MultipartFile coverImage) {
        postService.uploadCoverImage(id, coverImage);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PostResponseDto> updatePost(@PathVariable Long id, @RequestBody PostPatchDto postPatchDto) {
        PostResponseDto updatedPost = postService.updatePost(id, postPatchDto);
        return ResponseEntity.ok(updatedPost);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<Page<PostResponseDto>> searchPostsByTitle(@RequestParam String title, @RequestParam( required = false, defaultValue = "0") int page, @RequestParam(required = false, defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PostResponseDto> searchResults = postService.searchPostsByTitle(title, pageable);
        return ResponseEntity.ok(searchResults);
    }
    
}
