package com.example.welog.service;

import com.example.welog.dto.PostCreateDto;
import com.example.welog.dto.PostPatchDto;
import com.example.welog.dto.PostResponseDto;
import com.example.welog.exception.ResourceNotFoundException;
import com.example.welog.model.Post;
import com.example.welog.model.User;
import com.example.welog.repository.PostRepository;
import com.example.welog.repository.UserRepository;
import com.example.welog.service.impl.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc(addFilters = false)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthService authService;

    @InjectMocks
    private PostService postService;

    private Post post;
    private User user;
    private PostCreateDto postCreateDto;
    private PostPatchDto postPatchDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");

        post = new Post();
        post.setId(1L);
        post.setTitle("Test Post");
        post.setContent("Test Content");
        post.setAuthor(user);

        postCreateDto = new PostCreateDto();
        postCreateDto.setTitle("Test Post");
        postCreateDto.setContent("Test Content");

        postPatchDto = new PostPatchDto();
        postPatchDto.setTitle("Updated Title");
    }

    @Test
    void getAllPosts_ValidRequest_ReturnsAllPosts() {
        // Arrange
        Page<Post> page = new PageImpl<>(List.of(post));
        when(postRepository.findAll(any(Pageable.class))).thenReturn(page);

        // Act
        Pageable pageable = PageRequest.of(0, 10);
        List<PostResponseDto> result = postService.getAllPosts(pageable);

        // Assert
        assertThat(result.isEmpty()).isFalse();
        assertThat(post.getId()).isEqualTo(result.get(0).getId());
    }

    @Test
    void getPost_ValidRequest_ReturnsPost() {
        // Arrange
        when(postRepository.existsById(anyLong())).thenReturn(true);
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));

        // Act
        PostResponseDto result = postService.getPost(1L);

        // Assert
        assertNotNull(result);
        assertEquals(post.getId(), result.getId());
    }

    @Test
    void getPost_NotFound_ThrowsException() {
        // Arrange
        when(postRepository.existsById(anyLong())).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> postService.getPost(1L));
    }

    @Test
    void createPost_ValidRequest_ReturnsPost() {
        // Arrange
        when(authService.getCurrentUser()).thenReturn(UserDetailsImpl.build(user));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(postRepository.save(any(Post.class))).thenReturn(post);

        // Act
        PostResponseDto result = postService.createPost(postCreateDto);

        // Assert
        assertNotNull(result);
        assertEquals(post.getId(), result.getId());
    }

    @Test
    void updatePost_ValidRequest_ReturnsPost() {
        // Arrange
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        when(postRepository.save(any(Post.class))).thenReturn(post);

        // Act
        PostResponseDto result = postService.updatePost(1L, postPatchDto);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Title", result.getTitle());
    }

    @Test
    void deletePost_ValidRequest_ReturnsNoContent() {
        // Arrange
        when(postRepository.existsById(anyLong())).thenReturn(true);
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        doNothing().when(postRepository).softDelete(anyLong());

        // Act & Assert
        assertDoesNotThrow(() -> postService.deletePost(1L));
    }

    @Test
    void deletePost_NotFound_ThrowsException() {
        // Arrange
        when(postRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> postService.deletePost(1L));
        verify(postRepository, times(0)).softDelete(anyLong());
    }
}