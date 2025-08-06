package com.example.welog.service;

import java.util.List;
import java.util.stream.Collectors;

import com.example.welog.service.impl.UserDetailsImpl;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.welog.utils.ResponseDtoMapper;
import com.example.welog.dto.CommentCreateDto;
import com.example.welog.dto.CommentPatchDto;
import com.example.welog.dto.CommentResponseDto;
import com.example.welog.exception.ResourceNotFoundException;
import com.example.welog.model.Comment;
import com.example.welog.model.Post;
import com.example.welog.model.User;
import com.example.welog.repository.CommentRepository;
import com.example.welog.repository.PostRepository;
import com.example.welog.repository.UserRepository;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final AuthService authService;

    public CommentService(CommentRepository commentRepository, PostRepository postRepository, UserRepository userRepository,  AuthService authService) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.authService = authService;
    }

    public List<CommentResponseDto> getAllComments() {
        return commentRepository.findAll().stream()
//                .filter(comment -> comment.getDeletedAt() == null)
                .map(ResponseDtoMapper::mapToCommentResponseDto)
                .collect(Collectors.toList());
    }

    public List<CommentResponseDto> getCommentsByPostId(Long postId) {
        return commentRepository.findByPostId(postId).stream()
//                .filter(comment -> comment.getDeletedAt() == null)
                .map(ResponseDtoMapper::mapToCommentResponseDto)
                .collect(Collectors.toList());
    }

    public CommentResponseDto getComment(Long id) {
        Comment comment = commentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + id));

//        if (comment.getDeletedAt() != null) {
//            throw new ResourceNotFoundException("Comment not found with id: " + id);
//        }

        return ResponseDtoMapper.mapToCommentResponseDto(comment);
    }

    public CommentResponseDto createComment(CommentCreateDto commentCreateDto) {
        Post post = postRepository.findById(commentCreateDto.getPostId())
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + commentCreateDto.getPostId()));

        UserDetailsImpl userDetails = authService.getCurrentUser();

        User user = userRepository.findById(userDetails.getId()).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Comment comment = new Comment(commentCreateDto.getContent(), post, user);

        return ResponseDtoMapper.mapToCommentResponseDto(commentRepository.save(comment));
    }

    public CommentResponseDto updateComment(Long id, CommentPatchDto commentPatchDto) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + id));

//        if (comment.getDeletedAt() != null) {
//            throw new ResourceNotFoundException("Comment not found with id: " + id);
//        }

        if (commentPatchDto.getContent() != null) {
            comment.setContent(commentPatchDto.getContent());
        }

        if (commentPatchDto.getPostId() != null) {
            Post post = postRepository.findById(commentPatchDto.getPostId())
                    .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + commentPatchDto.getPostId()));
            comment.setPost(post);
        }

        if (commentPatchDto.getUserId() != null) {
            User user = userRepository.findById(commentPatchDto.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + commentPatchDto.getUserId()));
            comment.setUser(user);
        }

        return ResponseDtoMapper.mapToCommentResponseDto(commentRepository.save(comment));
    }

    public void deleteComment(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + id));

//        if (comment.getDeletedAt() != null) {
//            throw new ResourceNotFoundException("Comment not found with id: " + id);
//        }

        commentRepository.softDelete(id);
    }
    
}
