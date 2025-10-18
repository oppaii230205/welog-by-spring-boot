package com.example.welog.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import com.example.welog.service.impl.UserDetailsImpl;
import jakarta.transaction.Transactional;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.web.multipart.MultipartFile;

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
    private final AuthService authService;
    private final SupabaseStorageService supabaseStorageService;

    @Value("${app.upload.dir:uploads/img}")
    private String uploadDir;

    public PostService(PostRepository postRepository, UserRepository userRepository, AuthService authService, SupabaseStorageService supabaseStorageService) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.authService = authService;
        this.supabaseStorageService = supabaseStorageService;
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
        UserDetailsImpl userDetails = authService.getCurrentUser();

        User author = userRepository.findById(userDetails.getId()).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Post post = new Post(
                postCreateDto.getTitle(),
                postCreateDto.getContent(),
                postCreateDto.getCoverImage(),
                author
        );
        
        // Set custom excerpt if provided
        if (postCreateDto.getExcerpt() != null && !postCreateDto.getExcerpt().trim().isEmpty()) {
            post.setExcerpt(postCreateDto.getExcerpt());
        }

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

    @Transactional
    public void uploadCoverImage(Long id, MultipartFile coverImage) {
        if (!postRepository.existsById(id)) {
            throw new ResourceNotFoundException("Post not found with id: " + id);
        }

        Post post = postRepository.findById(id).get();

        // Handle cover image upload to Supabase Storage
        if (coverImage != null && !coverImage.isEmpty()) {
            try {
                // Delete old cover image if exists and is from Supabase
                if (post.getCoverImage() != null && post.getCoverImage().startsWith("http")) {
                    supabaseStorageService.deleteFile(post.getCoverImage(), "post-covers");
                }

                // Upload new cover image to Supabase Storage
                String coverImageUrl = supabaseStorageService.uploadFile(coverImage, "post-covers");
                post.setCoverImage(coverImageUrl);
                
                logger.info("Post cover image uploaded successfully: postId={}, url={}", post.getId(), coverImageUrl);
            } catch (IOException e) {
                logger.error("Failed to upload post cover image: postId={}, error={}", post.getId(), e.getMessage());
                throw new RuntimeException("Failed to upload cover image: " + e.getMessage(), e);
            }
        }

        postRepository.save(post);
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

    public Page<PostResponseDto> searchPostsByTitle(String title, Pageable pageable) {
        Page<Post> page = postRepository.findByTitleContainingIgnoreCase(title, pageable);

        return page.map(ResponseDtoMapper::mapToPostResponseDto);
    }
}
