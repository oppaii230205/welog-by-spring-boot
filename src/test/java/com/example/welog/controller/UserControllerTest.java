package com.example.welog.controller;

import com.example.welog.dto.UserPatchDto;
import com.example.welog.dto.UserResponseDto;
import com.example.welog.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private UserResponseDto userResponseDto;

    @BeforeEach
    void setUp() {
        userResponseDto = new UserResponseDto(1L, "Test User", "test@example.com", null, OffsetDateTime.now(), Collections.emptySet());
    }

    @Test
    void getAllUsers_ValidRequest_ReturnsUsers() {
        when(userService.getAll(any(Pageable.class)))
                .thenReturn(Collections.singletonList(userResponseDto));

        Pageable pageable = PageRequest.of(0, 10);
        ResponseEntity<List<UserResponseDto>> response = userController.getAllUsers(pageable);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().getFirst().getId()).isEqualTo(userResponseDto.getId());
        assertThat(response.getBody().getFirst().getEmail()).isEqualTo(userResponseDto.getEmail());
    }

    @Test
    void getUser_WhenExists_ReturnsUser() {
        when(userService.get(1L)).thenReturn(userResponseDto);

        ResponseEntity<UserResponseDto> response = userController.getUser(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getEmail()).isEqualTo(userResponseDto.getEmail());
    }

    @Test
    void updateUser_WithValidData_UpdatesUser() {
        UserPatchDto patchDto = new UserPatchDto();
        patchDto.setName("Updated Name");

        when(userService.update(anyLong(), any(UserPatchDto.class)))
                .thenReturn(new UserResponseDto(1L, "Updated Name", "test@example.com", null, OffsetDateTime.now(), Collections.emptySet()));

        ResponseEntity<UserResponseDto> response = userController.updateUser(1L, patchDto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getName()).isEqualTo("Updated Name");
    }

    @Test
    void deleteUser_WhenExists_ReturnsNoContent() {
        doNothing().when(userService).delete(anyLong());

        ResponseEntity<Void> response = userController.deleteUser(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}