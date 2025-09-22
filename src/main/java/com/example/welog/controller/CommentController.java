package com.example.welog.controller;

import java.util.List;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.welog.dto.CommentCreateDto;
import com.example.welog.dto.CommentPatchDto;
import com.example.welog.dto.CommentResponseDto;
import com.example.welog.service.CommentService;


@RestController
@RequestMapping("/api/v1")
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/comments")
    public ResponseEntity<List<CommentResponseDto>> getAllComments() {
        List<CommentResponseDto> comments = commentService.getAllComments();

        return ResponseEntity.status(HttpStatus.OK).body(comments);
    }

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<List<CommentResponseDto>> getCommentsByPostId(@PathVariable Long postId) {
        List<CommentResponseDto> comments = commentService.getCommentsByPostId(postId);

        return ResponseEntity.status(HttpStatus.OK).body(comments);
    }

    @GetMapping("/posts/{postId}/root-comments")
    public ResponseEntity<List<CommentResponseDto>> getRootCommentsByPostId(@PathVariable Long postId) {
        List<CommentResponseDto> comments = commentService.getRootCommentsByPostId(postId);

        return ResponseEntity.status(HttpStatus.OK).body(comments);
    }

    @GetMapping("comments/{id}")
    public ResponseEntity<CommentResponseDto> getComment(@PathVariable Long id) {
        CommentResponseDto comment = commentService.getComment(id);

        return ResponseEntity.status(HttpStatus.OK).body(comment);
    }

    @PostMapping("/comments")
    public ResponseEntity<CommentResponseDto> createComment(@Valid @RequestBody CommentCreateDto commentCreateDto) {
        // FINISH: Get current user from security context (in service layer)
//        Long userId = 1L; // Placeholder for current user ID, replace with actual
//        if (commentCreateDto.getUserId() == null) {
//            commentCreateDto.setUserId(userId);
//        }

        CommentResponseDto createdComment = commentService.createComment(commentCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdComment);
    }

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<CommentResponseDto> createComment(@PathVariable Long postId, @Valid @RequestBody CommentCreateDto commentCreateDto) {
        // FINISH: Get current user from security context
//        Long userId = 1L; // Placeholder for current user ID, replace with actual
//        if (commentCreateDto.getUserId() == null) {
//            commentCreateDto.setUserId(userId);
//        }

        commentCreateDto.setPostId(postId);

        CommentResponseDto createdComment = commentService.createComment(commentCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdComment);
    }

    @PatchMapping("/comments/{id}")
    public ResponseEntity<CommentResponseDto> updateComment(@PathVariable Long id, @RequestBody CommentPatchDto commentPatchDto) {
        CommentResponseDto commentResponseDto = commentService.updateComment(id, commentPatchDto);

        return ResponseEntity.status(HttpStatus.OK).body(commentResponseDto);
    }

    @DeleteMapping("/comments/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}