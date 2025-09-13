package com.example.welog.controller;

// import com.example.welog.dto.UserCreateDto;
// import com.example.welog.dto.UserResponseDto;
// import com.example.welog.service.UserService;
// import jakarta.validation.Valid;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.PageRequest;
// import org.springframework.data.domain.Pageable;
// import org.springframework.data.domain.Sort;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

import com.example.welog.service.UserService;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.welog.dto.UserPatchDto;
import com.example.welog.dto.UserResponseDto;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUsers(Pageable pageable) {
        return ResponseEntity.ok(service.getAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(service.get(id));
    }

    // @PostMapping
    // public ResponseEntity<User> createUser(@RequestBody UserCreateDto userCreateDto) {
    //     return ResponseEntity.status(201).body(service.create(userCreateDto));
    // }

    @PatchMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable Long id, @RequestBody UserPatchDto userPatchDto) {
        return ResponseEntity.ok(service.update(id, userPatchDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/updateMe")
    public ResponseEntity<UserResponseDto> updateMe(@RequestParam(value = "photo", required = false) MultipartFile photo,
                                                    @RequestParam(value = "name", required = false) String name,
                                                    @RequestParam(value = "email", required = false) String email) {
        return ResponseEntity.ok(service.updateMe(photo, name, email));
    }
}

