package com.example.welog.controller;

import com.example.welog.BaseIntegrationTest;
import com.example.welog.dto.SignInRequestDto;
import com.example.welog.dto.SignInResponseDto;
import com.example.welog.dto.UserCreateDto;
import com.example.welog.dto.UserResponseDto;
import com.example.welog.model.ERole;
import com.example.welog.model.Role;
import com.example.welog.repository.RoleRepository;
import com.example.welog.repository.UserRepository;
import com.example.welog.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class AuthControllerIntegrationTest extends BaseIntegrationTest {
    private static final Logger log = LoggerFactory.getLogger(AuthControllerIntegrationTest.class);

    @LocalServerPort
    private int port;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/v1/auth";
        userRepository.deleteAll();
        roleRepository.deleteAll();

        // Setup roles
        Role userRole = new Role(ERole.ROLE_USER);
        roleRepository.save(userRole);
    }

    @Test
    void signUp_ValidUser_ReturnsCreated() {
        // Arrange
        UserCreateDto userCreateDto = new UserCreateDto();
        userCreateDto.setName("Test User");
        userCreateDto.setEmail("test@example.com");
        userCreateDto.setPassword("password");
        userCreateDto.setPasswordConfirm("password");

        // Act
        ResponseEntity<UserResponseDto> response = restTemplate.postForEntity(
                baseUrl + "/signup",
                userCreateDto,
                UserResponseDto.class
        );

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(userCreateDto.getEmail(), response.getBody().getEmail());
        assertTrue(response.getBody().getRoles().contains("ROLE_USER"));
    }

    @Test
    void signIn_ValidCredentials_ReturnsToken() {
        log.debug("=================== signIn_ValidCredentials_ReturnsToken ==================");

        // Arrange - create a user first
        UserCreateDto userCreateDto = new UserCreateDto();
        userCreateDto.setName("Test User");
        userCreateDto.setEmail("test@example.com");
        userCreateDto.setPassword("password");
        userCreateDto.setPasswordConfirm("password");
        restTemplate.postForEntity(baseUrl + "/signup", userCreateDto, Void.class);


        // Debug
        userRepository.findAll().forEach(u -> log.debug(u.getEmail() + ": " + u.getPassword()));


        SignInRequestDto signInRequestDto = new SignInRequestDto();
        signInRequestDto.setEmail("test@example.com");
        signInRequestDto.setPassword("password");

        // Act
        ResponseEntity<SignInResponseDto> response = restTemplate.postForEntity(
                baseUrl + "/signin",
                signInRequestDto,
                SignInResponseDto.class
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getToken());
        assertEquals("Bearer", response.getBody().getType());
    }
}