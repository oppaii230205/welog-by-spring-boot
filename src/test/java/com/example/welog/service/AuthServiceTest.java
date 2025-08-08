package com.example.welog.service;

import com.example.welog.dto.SignInRequestDto;
import com.example.welog.dto.SignInResponseDto;
import com.example.welog.dto.UserCreateDto;
import com.example.welog.dto.UserResponseDto;
import com.example.welog.jwt.JwtUtils;
import com.example.welog.model.ERole;
import com.example.welog.model.Role;
import com.example.welog.model.User;
import com.example.welog.repository.RoleRepository;
import com.example.welog.repository.UserRepository;
import com.example.welog.service.impl.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtils  jwtUtils;

    @InjectMocks
    private AuthService authService;

    private UserCreateDto userCreateDto;
    private SignInRequestDto signInRequestDto;
    private User user;
    private Role role;

    @BeforeEach
    public void setUp() {
        userCreateDto = new UserCreateDto();
        userCreateDto.setName("Test User");
        userCreateDto.setEmail("test@example.com");
        userCreateDto.setPassword("test1234");
        userCreateDto.setPasswordConfirm("test1234");

        signInRequestDto = new SignInRequestDto();
        signInRequestDto.setEmail("test@example.com");
        signInRequestDto.setPassword("test1234");

        role = new Role();
        role.setName(ERole.ROLE_USER);

        user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");
        user.setRoles(Set.of(role));
    }

    @Test
    void signUp_ValidInput_ReturnsUserResponseDto() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(roleRepository.findByName(ERole.ROLE_USER)).thenReturn(Optional.of(role));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        UserResponseDto result = authService.signUp(userCreateDto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(user.getId());
        assertThat(result.getName()).isEqualTo(user.getName());
        assertThat(result.getEmail()).isEqualTo(user.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void signUp_EmailAlreadyExists_ThrowsException() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> authService.signUp(userCreateDto)).isInstanceOf(Exception.class);
    }

    @Test
    void signUp_PasswordConfirmNotMatched_ThrowsException() {
        userCreateDto.setPasswordConfirm("notMatched");

        assertThatThrownBy(() -> authService.signUp(userCreateDto)).isInstanceOf(Exception.class);
    }

    @Test
    void signIn_ValidInput_ReturnsUserResponseDto() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        UserDetailsImpl userDetails = new UserDetailsImpl(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtUtils.generateJwtToken(authentication)).thenReturn("jwtToken");

        // Act
        SignInResponseDto result = authService.signIn(signInRequestDto);

        // Assert
        assertThat(result).isNotNull();
        assertThat("jwtToken").isEqualTo(result.getToken());
        assertThat(user.getEmail()).isEqualTo(result.getEmail());
        assertThat(result.getRoles()).contains("ROLE_USER");
    }
    
}
