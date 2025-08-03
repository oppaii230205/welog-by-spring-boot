package com.example.welog.service;

import com.example.welog.dto.UserCreateDto;
import com.example.welog.dto.UserPatchDto;
import com.example.welog.dto.UserResponseDto;
import com.example.welog.exception.ResourceNotFoundException;
import com.example.welog.model.User;
import com.example.welog.repository.UserRepository;
import com.example.welog.utils.ResponseDtoMapper;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
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

    // For security
    public Boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public void save(User user) {
        userRepository.save(user);
    }
}