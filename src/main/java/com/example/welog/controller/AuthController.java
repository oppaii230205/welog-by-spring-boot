package com.example.welog.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.welog.dto.SignInRequestDto;
import com.example.welog.dto.SignInResponseDto;
import com.example.welog.dto.UserCreateDto;
import com.example.welog.dto.UserResponseDto;
import com.example.welog.service.AuthService;

import jakarta.validation.Valid;

@RestController
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/api/v1/auth/signup")
    public ResponseEntity<?> signUp(@RequestBody UserCreateDto userCreateDto) {
        UserResponseDto createdUser = authService.signUp(userCreateDto);
        return ResponseEntity.status(201).body(createdUser);
    }

    @PostMapping("/api/v1/auth/signin")
    public ResponseEntity<?> signIn(@RequestBody @Valid SignInRequestDto signInRequestDto) {
        SignInResponseDto signInResponseDto = authService.signIn(signInRequestDto);
        return ResponseEntity.ok(signInResponseDto);
    }

}
