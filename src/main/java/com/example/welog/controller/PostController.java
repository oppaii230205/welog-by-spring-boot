package com.example.welog.controller;

import java.util.List;

import com.example.welog.service.impl.UserDetailsImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.welog.dto.PostCreateDto;
import com.example.welog.dto.PostPatchDto;
import com.example.welog.dto.PostResponseDto;
import com.example.welog.service.PostService;

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
    public ResponseEntity<PostResponseDto> createPost(@RequestBody PostCreateDto postCreateDto) {
        PostResponseDto createdPost = postService.createPost(postCreateDto);
        return ResponseEntity.status(201).body(createdPost);
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
}

// import com.example.welog.dto.PostCreateDto;
// import com.example.welog.dto.PostResponseDto;
// import com.example.welog.service.PostService;
// import jakarta.validation.Valid;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.PageRequest;
// import org.springframework.data.domain.Pageable;
// import org.springframework.data.domain.Sort;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// @RestController
// @RequestMapping("/api/posts")
// @CrossOrigin(origins = "*", maxAge = 3600)
// public class PostController {
    
//     private final PostService postService;
    
//     @Autowired
//     public PostController(PostService postService) {
//         this.postService = postService;
//     }
    
//     @PostMapping
//     public ResponseEntity<PostResponseDto> createPost(
//             @Valid @RequestBody PostCreateDto postCreateDto,
//             @RequestParam Long authorId) {
//         PostResponseDto createdPost = postService.createPost(postCreateDto, authorId);
//         return new ResponseEntity<>(createdPost, HttpStatus.CREATED);
//     }
    
//     @GetMapping("/{id}")
//     public ResponseEntity<PostResponseDto> getPostById(@PathVariable Long id) {
//         PostResponseDto post = postService.getPostById(id);
//         return ResponseEntity.ok(post);
//     }
    
//     @GetMapping("/slug/{slug}")
//     public ResponseEntity<PostResponseDto> getPostBySlug(@PathVariable String slug) {
//         PostResponseDto post = postService.getPostBySlug(slug);
//         return ResponseEntity.ok(post);
//     }
    
//     @GetMapping
//     public ResponseEntity<Page<PostResponseDto>> getAllPosts(
//             @RequestParam(defaultValue = "0") int page,
//             @RequestParam(defaultValue = "10") int size,
//             @RequestParam(defaultValue = "createdAt") String sortBy,
//             @RequestParam(defaultValue = "desc") String sortDir) {
        
//         Sort sort = sortDir.equalsIgnoreCase("desc") ? 
//             Sort.by(sortBy).descending() : 
//             Sort.by(sortBy).ascending();
        
//         Pageable pageable = PageRequest.of(page, size, sort);
//         Page<PostResponseDto> posts = postService.getAllPosts(pageable);
//         return ResponseEntity.ok(posts);
//     }
    
//     @GetMapping("/published")
//     public ResponseEntity<Page<PostResponseDto>> getPublishedPosts(
//             @RequestParam(defaultValue = "0") int page,
//             @RequestParam(defaultValue = "10") int size,
//             @RequestParam(defaultValue = "createdAt") String sortBy,
//             @RequestParam(defaultValue = "desc") String sortDir) {
        
//         Sort sort = sortDir.equalsIgnoreCase("desc") ? 
//             Sort.by(sortBy).descending() : 
//             Sort.by(sortBy).ascending();
        
//         Pageable pageable = PageRequest.of(page, size, sort);
//         Page<PostResponseDto> posts = postService.getPublishedPosts(pageable);
//         return ResponseEntity.ok(posts);
//     }
    
//     @GetMapping("/author/{authorId}")
//     public ResponseEntity<Page<PostResponseDto>> getPostsByAuthor(
//             @PathVariable Long authorId,
//             @RequestParam(defaultValue = "0") int page,
//             @RequestParam(defaultValue = "10") int size,
//             @RequestParam(defaultValue = "createdAt") String sortBy,
//             @RequestParam(defaultValue = "desc") String sortDir) {
        
//         Sort sort = sortDir.equalsIgnoreCase("desc") ? 
//             Sort.by(sortBy).descending() : 
//             Sort.by(sortBy).ascending();
        
//         Pageable pageable = PageRequest.of(page, size, sort);
//         Page<PostResponseDto> posts = postService.getPostsByAuthor(authorId, pageable);
//         return ResponseEntity.ok(posts);
//     }
    
//     @GetMapping("/search")
//     public ResponseEntity<Page<PostResponseDto>> searchPosts(
//             @RequestParam String keyword,
//             @RequestParam(defaultValue = "0") int page,
//             @RequestParam(defaultValue = "10") int size,
//             @RequestParam(defaultValue = "createdAt") String sortBy,
//             @RequestParam(defaultValue = "desc") String sortDir) {
        
//         Sort sort = sortDir.equalsIgnoreCase("desc") ? 
//             Sort.by(sortBy).descending() : 
//             Sort.by(sortBy).ascending();
        
//         Pageable pageable = PageRequest.of(page, size, sort);
//         Page<PostResponseDto> posts = postService.searchPosts(keyword, pageable);
//         return ResponseEntity.ok(posts);
//     }
    
//     @PutMapping("/{id}")
//     public ResponseEntity<PostResponseDto> updatePost(
//             @PathVariable Long id, 
//             @Valid @RequestBody PostCreateDto postUpdateDto) {
//         PostResponseDto updatedPost = postService.updatePost(id, postUpdateDto);
//         return ResponseEntity.ok(updatedPost);
//     }
    
//     @DeleteMapping("/{id}")
//     public ResponseEntity<Void> deletePost(@PathVariable Long id) {
//         postService.deletePost(id);
//         return ResponseEntity.noContent().build();
//     }
// }
