package com.example.welog.service;

import java.util.List;

import com.example.welog.service.impl.UserDetailsImpl;
import jakarta.transaction.Transactional;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.welog.dto.PostCreateDto;
import com.example.welog.dto.PostPatchDto;
import com.example.welog.dto.PostResponseDto;
import com.example.welog.exception.ResourceNotFoundException;
import com.example.welog.model.Post;
import com.example.welog.model.User;
import com.example.welog.repository.PostRepository;
import com.example.welog.repository.UserRepository;
import com.example.welog.utils.ResponseDtoMapper;

@Service
public class PostService {
    private static final Logger logger = LoggerFactory.getLogger(PostService.class);
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PostService(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    public UserDetailsImpl getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthenticationCredentialsNotFoundException("User is not authenticated");
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        return userDetails;

    }

    public List<PostResponseDto> getAllPosts(Pageable pageable) {
        Page<Post> page = postRepository.findAll(PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSortOr(Sort.by(Sort.Direction.ASC, "id"))));

        return page.getContent()
                .stream()
//                .filter(post -> post.getDeletedAt() == null)
                .map(ResponseDtoMapper::mapToPostResponseDto)
                .toList();
    }

    @Transactional
    public PostResponseDto getPost(Long id) {
        if (!postRepository.existsById(id)) {
            throw new ResourceNotFoundException("Post not found with id: " + id);
        }

        Post post = postRepository.findById(id).get();
//        Post post = postRepository.findByIdWithTagsAndComments(id).get();

//        if (post.getDeletedAt() != null) {
//            throw new ResourceNotFoundException("Post not found with id: " + id);
//        }

        /*
        logger.debug("========= PostService =========");

        logger.debug("Post loaded: " + post.getId());
        logger.debug("Tags collection class: " + post.getTags().getClass().getName());
        logger.debug("Comments collection class: " + post.getComments().getClass().getName());

        try {
            Hibernate.initialize(post.getTags());
            logger.debug("Tags initialized successfully");
        } catch (Exception e) {
            logger.debug("Error initializing tags: " + e.getMessage());
            e.printStackTrace();
        }

        try {
            Hibernate.initialize(post.getComments());
            logger.debug("Comments initialized successfully");
        } catch (Exception e) {
            logger.debug("Error initializing comments: " + e.getMessage());
            e.printStackTrace();
        }
        */

        return ResponseDtoMapper.mapToPostResponseDto(post);
    }

    @Transactional
    public PostResponseDto createPost(PostCreateDto postCreateDto) {
        UserDetailsImpl userDetails = getCurrentUser();

        User author = userRepository.findById(userDetails.getId()).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Post post = new Post(
                postCreateDto.getTitle(),
                postCreateDto.getContent(),
                postCreateDto.getCoverImage(),
                author
        );

        Post savedPost = postRepository.save(post);
        return ResponseDtoMapper.mapToPostResponseDto(savedPost);
    }

    public PostResponseDto updatePost(Long id, PostPatchDto postPatchDto) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + id));

//        if (post.getDeletedAt() != null) {
//            throw new ResourceNotFoundException("Post not found with id: " + id);
//        }

        if (postPatchDto.getTitle() != null) {
            post.setTitle(postPatchDto.getTitle());
        }
        if (postPatchDto.getContent() != null) {
            post.setContent(postPatchDto.getContent());
        }
        if (postPatchDto.getCoverImage() != null) {
            post.setCoverImage(postPatchDto.getCoverImage());
        }
        if (postPatchDto.getAuthorId() != null) {
            User author = userRepository.findById(postPatchDto.getAuthorId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + postPatchDto.getAuthorId()));

            post.setAuthor(author);
        }

        Post updatedPost = postRepository.save(post);
        return ResponseDtoMapper.mapToPostResponseDto(updatedPost);
    }

    public void deletePost(Long id) {
        if (!postRepository.existsById(id)) {
            throw new ResourceNotFoundException("Post not found with id: " + id);
        }

        Post post = postRepository.findById(id).get();

//        if (post.getDeletedAt() != null) {
//            throw new ResourceNotFoundException("Post not found with id: " + id);
//        }

        postRepository.softDelete(id);
    }
}
