package com.example.welog.controller;

import static org.mockito.ArgumentMatchers.*;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.catalina.connector.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.security.test.context.support.WithMockUser;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import com.example.welog.dto.PostCreateDto;
import com.example.welog.dto.PostPatchDto;
import com.example.welog.dto.PostResponseDto;
import com.example.welog.exception.ResourceNotFoundException;
import com.example.welog.model.ERole;
import com.example.welog.model.Post;
import com.example.welog.model.Role;
import com.example.welog.model.User;
import com.example.welog.service.PostService;
import com.example.welog.utils.ResponseDtoMapper;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(PostController.class)
@AutoConfigureMockMvc(addFilters = false)
public class PostControllerTest {
  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private PostService postService;

  @Autowired
  private ObjectMapper objectMapper;

  // @Autowired
  // private WebApplicationContext webApplicationContext;

  private Post post;
  private PostCreateDto postCreateDto;
  private PostPatchDto postPatchDto;
  private PostResponseDto postResponseDto;

  private User user;
  private Role role;

  @BeforeEach
  void setUp() {
    // mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

    role = new Role();
    role.setName(ERole.ROLE_USER);

    user = new User();
    user.setId(1L);
    user.setName("Test User");
    user.setEmail("test@example.com");
    user.setPassword("encodedPassword");
    user.setRoles(Set.of(role));

    post = new Post();
    post.setId(1L);
    post.setTitle("Test Post");
    post.setSlug("test-post");
    post.setContent("This is a test post content.");
    post.setExcerpt("Test excerpt");
    post.setCoverImage("test-cover.jpg");
    post.setAuthor(user);
    post.setCreatedAt(OffsetDateTime.now());
    post.setUpdatedAt(OffsetDateTime.now());
    post.setDeletedAt(null);
    post.setTags(new HashSet<>());
    post.setComments(new HashSet<>());

    postCreateDto = new PostCreateDto();
    postCreateDto.setTitle("Test Post");
    postCreateDto.setContent("This is a test post content.");
    postCreateDto.setCoverImage("test-cover.jpg");

    postPatchDto = new PostPatchDto();
    postPatchDto.setTitle("Updated Test Post");

    postResponseDto = ResponseDtoMapper.mapToPostResponseDto(post);
  }

  // @Test
  // void getAllPosts_Unauthenticated_ReturnsUnauthorized() throws Exception {
  //     mockMvc.perform(get("/api/v1/posts"))
  //             .andExpect(status().isUnauthorized());
  // }

  @Test
  void getAllPosts_ValidRequest_ReturnsListOfPosts() throws Exception {
    // Arrange
    when(postService.getAllPosts(any(Pageable.class))).thenReturn(List.of(postResponseDto));

    // Act & Assert
    mockMvc.perform(get("/api/v1/posts"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[0].title").value("Test Post"));

    verify(postService, times(1)).getAllPosts(any(Pageable.class));
  }

  @Test
  void getPost_ExistingPostId_ReturnsPostResponseDto() throws Exception {
    // Arrange
    when(postService.getPost(1L)).thenReturn(postResponseDto);

    // Act & Assert
    mockMvc.perform(get("/api/v1/posts/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.title").value("Test Post"));

    verify(postService, times(1)).getPost(1L);
  }

  @Test
  void getPost_WhenNotExists_ReturnsNotFound() throws Exception {
    // Arrange
    when(postService.getPost(anyLong())).thenThrow(new ResourceNotFoundException("Post not found"));

    // Act & Assert
    mockMvc.perform(get("/api/v1/posts/999"))
        .andExpect(status().isNotFound());

    verify(postService, times(1)).getPost(999L);
  }

  @Test
  void createPost_ValidData_ReturnsCreated() throws Exception {
      // Arrange
      when(postService.createPost(any())).thenReturn(postResponseDto);

      String requestBody = """
          {
              "title": "Test Post",
              "content": "This is a test post content.",
              "coverImage": "test-cover.jpg"
          }
          """;

      // Act & Assert
      mockMvc.perform(post("/api/v1/posts")
              .contentType(MediaType.APPLICATION_JSON) // Required
              .content(requestBody))
              .andExpect(status().isCreated())
              .andExpect(jsonPath("$.id", is(1)))
              .andExpect(jsonPath("$.title", is("Test Post")));

      verify(postService, times(1)).createPost(any());
  }

  @Test
  void createPost_InvalidData_ReturnsBadRequest() throws Exception {
      // Arrange - missing required title field
      String invalidRequestBody = """
          {
              "content": "Post content",
              "authorId": 1
          }
          """;

      // Act & Assert
      mockMvc.perform(post("/api/v1/posts")
              .contentType(MediaType.APPLICATION_JSON)
              .content(invalidRequestBody))
              .andExpect(status().isBadRequest());

      verify(postService, never()).createPost(any());
  }

  @Test
  void updatePost_WithValidData_ShouldReturnUpdatedPost() throws Exception {
    // Arrange
    postResponseDto.setTitle("Updated Test Post");
    when(postService.updatePost(eq(1L), any())).thenReturn(postResponseDto);

    // Act & Assert
    mockMvc.perform(patch("/api/v1/posts/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(postPatchDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title", is("Updated Test Post")));

    verify(postService, times(1)).updatePost(eq(1L), any());
  }

  @Test
  void deletePost_WhenExists_ShouldReturnNoContent() throws Exception {
    // Arrange
    doNothing().when(postService).deletePost(1L);

    // Act & Assert
    mockMvc.perform(delete("/api/v1/posts/1")
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

    verify(postService, times(1)).deletePost(1L);
  }

  @Test
  void deletePost_WhenNotExists_ShouldReturnNotFound() throws Exception {
    // Arrange
    doThrow(new ResourceNotFoundException("Post not found with id: 999"))
            .when(postService).deletePost(999L);

    // Act & Assert
    mockMvc.perform(delete("/api/v1/posts/999")
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());

    verify(postService, times(1)).deletePost(999L);
  }
}
