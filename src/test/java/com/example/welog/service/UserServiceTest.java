package com.example.welog.service;

import com.example.welog.dto.UserPatchDto;
import com.example.welog.dto.UserResponseDto;
import com.example.welog.exception.ResourceNotFoundException;
import com.example.welog.model.User;
import com.example.welog.repository.UserRepository;
import com.example.welog.utils.ResponseDtoMapper;
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

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");
    }

    @Test
    void getAll_ValidRequest_ReturnsAllUsers() {
        when(userRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(user)));

        Pageable pageable = PageRequest.of(0, 10);
        List<UserResponseDto> result = userService.getAll(pageable);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void getUser_UserExists_ReturnsUser() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserResponseDto result = userService.get(user.getId());

        assertThat(result.getId()).isEqualTo(user.getId());
        assertThat(result.getName()).isEqualTo(user.getName());
    }

    @Test
    void getUser_UserNotExists_ThrowsException() {
        when(userRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> userService.get(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateUser_ValidData_UpdatesUser() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserPatchDto patchDto = new UserPatchDto();
        patchDto.setName("Updated Name");

        UserResponseDto result = userService.update(1L, patchDto);

        assertThat(result.getName()).isEqualTo("Updated Name");
    }

    @Test
    void delete_UserExists_ReturnsNoContent() {
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).softDelete(1L);

        userService.delete(1L);

        verify(userRepository, times(1)).softDelete(1L);
    }
}