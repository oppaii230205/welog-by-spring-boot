// package com.example.welog.controller;

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
