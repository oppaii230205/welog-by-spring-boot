package com.example.welog.service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.welog.dto.SignInRequestDto;
import com.example.welog.dto.SignInResponseDto;
import com.example.welog.dto.UserCreateDto;
import com.example.welog.dto.UserResponseDto;
import com.example.welog.exception.RoleNotFoundException;
import com.example.welog.jwt.JwtUtils;
import com.example.welog.model.ERole;
import com.example.welog.model.Role;
import com.example.welog.model.User;
import com.example.welog.repository.RoleRepository;
import com.example.welog.repository.UserRepository;
import com.example.welog.service.impl.UserDetailsImpl;
import com.example.welog.utils.ResponseDtoMapper;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    // Constructor injection for UserRepository and PasswordEncoder
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository, AuthenticationManager authenticationManager, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    public UserResponseDto signUp(UserCreateDto userCreateDto) {
        // Validate userCreateDto fields
        if (!userCreateDto.getPassword().equals(userCreateDto.getPasswordConfirm())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(userCreateDto.getEmail())) {
            throw new IllegalArgumentException("Email is already in use");
        }

        // Create a new User entity
        User user = new User();
        user.setName(userCreateDto.getName());
        user.setEmail(userCreateDto.getEmail());
        user.setPhoto(userCreateDto.getPhoto() != null ? userCreateDto.getPhoto() : "default.png"); // Set a default photo
        user.setPassword(passwordEncoder.encode(userCreateDto.getPassword())); // Hash the password
        user.setActive(true); // Set the user as active by default
        
        Role defaultRole = roleRepository.findByName(ERole.ROLE_USER).orElseThrow(() -> 
            new RoleNotFoundException("Default role not found"));

        user.setRoles(new HashSet<>(Set.of(defaultRole)));

        // Save the user and return the response DTO
        return ResponseDtoMapper.mapToUserResponseDto(userRepository.save(user));
    }

    public SignInResponseDto signIn(SignInRequestDto signInRequestDto) {
        // (1)
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(signInRequestDto.getEmail(), signInRequestDto.getPassword()));

        // (2)
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // (3)
        String jwt = jwtUtils.generateJwtToken(authentication);

        // (4)
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        // (5)
        Set<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        // (6)
        SignInResponseDto signInResponseDto = SignInResponseDto.builder()
                .username(userDetails.getUsername())
                .email(userDetails.getEmail())
                .id(userDetails.getId())
                .token(jwt)
                .roles(roles)
                .build();

        return signInResponseDto;
    }
}
