package com.example.welog.service;

import com.example.welog.dto.UserCreateDto;
import com.example.welog.dto.UserPatchDto;
import com.example.welog.dto.UserResponseDto;
import com.example.welog.exception.ResourceNotFoundException;
import com.example.welog.model.User;
import com.example.welog.repository.UserRepository;
import com.example.welog.service.impl.UserDetailsImpl;
import com.example.welog.utils.ResponseDtoMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final AuthService authService;

    @Value("${app.upload.dir}")
    private String uploadDir;

    public UserService(UserRepository userRepository, AuthService authService) {
        this.userRepository = userRepository;
        this.authService = authService;
    }

    public List<UserResponseDto> getAll(Pageable pageable) {
        Page<User> page = userRepository.findAll(PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSortOr(Sort.by(Sort.Direction.ASC, "id"))));

        return page.getContent().stream()
//                .filter(user -> user.getDeletedAt() == null)
                .map(ResponseDtoMapper::mapToUserResponseDto)
                .toList();
    }

    public UserResponseDto get(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }

        User user = userRepository.findById(id).get();

//        if (user.getDeletedAt() != null) {
//            throw new ResourceNotFoundException("User not found with id: " + id);
//        }

        return ResponseDtoMapper.mapToUserResponseDto(user);
    }

    public UserResponseDto create(UserCreateDto userCreateDto) {
        if (userCreateDto.getName() == null || userCreateDto.getEmail() == null || userCreateDto.getPassword() == null || userCreateDto.getPasswordConfirm() == null) {
            throw new IllegalArgumentException("Name, email, password, and password confirmation must not be null");
        }

        if (!userCreateDto.getPassword().equals(userCreateDto.getPasswordConfirm())) {
            throw new IllegalArgumentException("Password and password confirmation do not match");
        }

        User user = new User();
        user.setName(userCreateDto.getName());
        user.setEmail(userCreateDto.getEmail());
        user.setPassword(userCreateDto.getPassword()); // In a real application, hash the password

        return ResponseDtoMapper.mapToUserResponseDto(userRepository.save(user));
    }

    public UserResponseDto update(Long id, UserPatchDto userPatchDto) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }

        User existingUser = userRepository.findById(id).get();

//        if (existingUser.getDeletedAt() != null) {
//            throw new ResourceNotFoundException("User not found with id: " + id);
//        }

        if (userPatchDto.getName() != null) existingUser.setName(userPatchDto.getName());
        if (userPatchDto.getEmail() != null) existingUser.setEmail(userPatchDto.getEmail());
        if (userPatchDto.getPhoto() != null) existingUser.setPhoto(userPatchDto.getPhoto());

        return ResponseDtoMapper.mapToUserResponseDto(userRepository.save(existingUser));
    }

    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }

//        User user = userRepository.findById(id).get();
//
//        if (user.getDeletedAt() != null) {
//            throw new ResourceNotFoundException("User not found with id: " + id);
//        }

        userRepository.softDelete(id);
    }

    public UserResponseDto updateMe(MultipartFile photo, String name, String email) {
        // Get the authenticated user's ID from the security context
        UserDetailsImpl userDetails = authService.getCurrentUser();

        User user = userRepository.findById(userDetails.getId()).orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userDetails.getId()));

        // ensure upload dir exists
        Path uploadPath = Paths.get(uploadDir).resolve("users");
        if (!Files.exists(uploadPath)) {
            try {
                Files.createDirectories(uploadPath);
            } catch (IOException e) {
                throw new RuntimeException("Failed to create upload directory", e);
            }
        }

        if (photo != null && !photo.isEmpty()) {
            String originalFilename = photo.getOriginalFilename();
            String fileExtension = "";

            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            String newFilename = "user_" + user.getId() + "_" + System.currentTimeMillis() + fileExtension;
            Path filePath = uploadPath.resolve(newFilename);

            try {
                Files.copy(photo.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                user.setPhoto(newFilename);
            } catch (IOException e) {
                throw new RuntimeException("Failed to store file " + newFilename, e);
            }
        }
        
        // TODO: validate name and email
        if (name != null && !name.isEmpty()) {
            user.setName(name);
        }

        if (email != null && !email.isEmpty()) {
            user.setEmail(email);
        }
        
        // logger.debug("+++++++++++++++++++++++++++++++++++++++++++++++++++++");
        // logger.debug(">> Updated user info: " + user.getName() + ", " + user.getEmail() + ", " + user.getPhoto());

        return ResponseDtoMapper.mapToUserResponseDto(userRepository.save(user));
    }

    // For security
    public Boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public void save(User user) {
        userRepository.save(user);
    }
}