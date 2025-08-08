package com.example.welog.controller;

import com.example.welog.dto.CommentCreateDto;
import com.example.welog.dto.CommentResponseDto;
import com.example.welog.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentControllerTest {

    @Mock
    private CommentService commentService;

    @InjectMocks
    private CommentController commentController;

    private CommentResponseDto commentResponseDto;

    @BeforeEach
    void setUp() {
        commentResponseDto = new CommentResponseDto(1L, "Test comment", null, OffsetDateTime.now());
    }

    @Test
    void getAllComments_ValidRequest_ReturnsAllComments() {
        when(commentService.getAllComments()).thenReturn(List.of(commentResponseDto));
        ResponseEntity<List<CommentResponseDto>> response = commentController.getAllComments();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsExactly(commentResponseDto);
    }

    @Test
    void getCommentsByPostId_ReturnsComments() {
        when(commentService.getCommentsByPostId(1L)).thenReturn(List.of(commentResponseDto));

        ResponseEntity<List<CommentResponseDto>> response = commentController.getCommentsByPostId(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsExactly(commentResponseDto);
    }

    @Test
    void createComment_ValidData_ReturnsComment() {
        CommentCreateDto createDto = new CommentCreateDto();
        createDto.setContent("Test comment");
        createDto.setPostId(1L);

        when(commentService.createComment(any(CommentCreateDto.class))).thenReturn(commentResponseDto);

        ResponseEntity<CommentResponseDto> response = commentController.createComment(createDto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getContent()).isEqualTo("Test comment");
    }

    @Test
    void deleteComment_WhenExists_ReturnsNoContent() {
        doNothing().when(commentService).deleteComment(anyLong());

        ResponseEntity<Void> response = commentController.deleteComment(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}