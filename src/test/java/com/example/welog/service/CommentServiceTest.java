package com.example.welog.service;

import com.example.welog.dto.CommentCreateDto;
import com.example.welog.dto.CommentResponseDto;
import com.example.welog.exception.ResourceNotFoundException;
import com.example.welog.model.Comment;
import com.example.welog.model.Post;
import com.example.welog.model.User;
import com.example.welog.repository.CommentRepository;
import com.example.welog.repository.PostRepository;
import com.example.welog.repository.UserRepository;
import com.example.welog.service.impl.UserDetailsImpl;
import com.example.welog.utils.ResponseDtoMapper;
import jakarta.validation.constraints.Min;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthService authService;

    @InjectMocks
    private CommentService commentService;

    private Comment comment;
    private Post post;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Test User");

        post = new Post();
        post.setId(1L);
        post.setTitle("Test Post");

        comment = new Comment();
        comment.setId(1L);
        comment.setContent("Test comment");
        comment.setPost(post);
        comment.setUser(user);
    }

    @Test
    void getAllComments_ValidRequest_ReturnsAllComments() {
        when(commentRepository.findAll()).thenReturn(List.of(comment));

        List<CommentResponseDto> result = commentService.getAllComments();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getId()).isEqualTo(comment.getId());
        assertThat(result.getFirst().getContent()).isEqualTo(comment.getContent());
    }

    @Test
    void getCommentsByPostId_PostExists_ReturnsComments() {
        when(commentRepository.findByPostId(1L)).thenReturn(List.of(comment));

        List<CommentResponseDto> result = commentService.getCommentsByPostId(1L);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getId()).isEqualTo(comment.getId());
        assertThat(result.getFirst().getContent()).isEqualTo(comment.getContent());
    }

    @Test
    void createComment_ValidData_CreatesComment() {
        CommentCreateDto createDto = new CommentCreateDto();
        createDto.setContent("New comment");
        createDto.setPostId(1L);

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(authService.getCurrentUser()).thenReturn(UserDetailsImpl.build(user));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentResponseDto result = commentService.createComment(createDto);

        assertThat(result.getId()).isEqualTo(comment.getId());
        assertThat(result.getUser().getId()).isEqualTo(comment.getUser().getId());
    }

    @Test
    void deleteComment_WhenExists_DeletesComment() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        doNothing().when(commentRepository).softDelete(1L);

        commentService.deleteComment(1L);

        verify(commentRepository, times(1)).softDelete(1L);
    }

    @Test
    void deleteComment_WhenNotExists_ThrowsException() {
        when(commentRepository.findById(999L)).thenReturn(Optional.empty());

//        commentService.deleteComment(999L); call commentService.deleteComment() in the assertThatThrownBy, not here

        assertThatThrownBy(() -> commentService.deleteComment(999L)).isInstanceOf(ResourceNotFoundException.class);
        verify(commentRepository, never()).softDelete(anyLong());
    }
}