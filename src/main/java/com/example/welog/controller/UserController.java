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

// @RestController
// @RequestMapping("/api/users")
// @CrossOrigin(origins = "*", maxAge = 3600)
// public class UserController {
    
//     private final UserService userService;
    
//     @Autowired
//     public UserController(UserService userService) {
//         this.userService = userService;
//     }
    
//     @PostMapping
//     public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody UserCreateDto userCreateDto) {
//         UserResponseDto createdUser = userService.createUser(userCreateDto);
//         return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
//     }
    
//     @GetMapping("/{id}")
//     public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
//         UserResponseDto user = userService.getUserById(id);
//         return ResponseEntity.ok(user);
//     }
    
//     @GetMapping("/username/{username}")
//     public ResponseEntity<UserResponseDto> getUserByUsername(@PathVariable String username) {
//         UserResponseDto user = userService.getUserByUsername(username);
//         return ResponseEntity.ok(user);
//     }
    
//     @GetMapping
//     public ResponseEntity<Page<UserResponseDto>> getAllUsers(
//             @RequestParam(defaultValue = "0") int page,
//             @RequestParam(defaultValue = "10") int size,
//             @RequestParam(defaultValue = "createdAt") String sortBy,
//             @RequestParam(defaultValue = "desc") String sortDir) {
        
//         Sort sort = sortDir.equalsIgnoreCase("desc") ? 
//             Sort.by(sortBy).descending() : 
//             Sort.by(sortBy).ascending();
        
//         Pageable pageable = PageRequest.of(page, size, sort);
//         Page<UserResponseDto> users = userService.getAllUsers(pageable);
//         return ResponseEntity.ok(users);
//     }
    
//     @PutMapping("/{id}")
//     public ResponseEntity<UserResponseDto> updateUser(
//             @PathVariable Long id, 
//             @Valid @RequestBody UserCreateDto userUpdateDto) {
//         UserResponseDto updatedUser = userService.updateUser(id, userUpdateDto);
//         return ResponseEntity.ok(updatedUser);
//     }
    
//     @DeleteMapping("/{id}")
//     public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
//         userService.deleteUser(id);
//         return ResponseEntity.noContent().build();
//     }
// }


import com.example.welog.model.User;
import com.example.welog.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.welog.dto.UserPatchDto;


import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        return service.get(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // @PostMapping
    // public ResponseEntity<User> createUser(@RequestBody UserCreateDto userCreateDto) {
    //     return ResponseEntity.status(201).body(service.create(userCreateDto));
    // }

    @PatchMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody UserPatchDto userPatchDto) {
        return ResponseEntity.ok(service.update(id, userPatchDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
