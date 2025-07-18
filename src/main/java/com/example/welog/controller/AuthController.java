package com.example.welog.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.welog.dto.UserCreateDto;
import com.example.welog.model.User;
import com.example.welog.service.UserService;

@RestController
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/api/v1/users/signup")
    public ResponseEntity<?> signUp(@RequestBody UserCreateDto userCreateDto) {
        try {
            User createdUser = userService.create(userCreateDto);
            return ResponseEntity.status(201).body(createdUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
