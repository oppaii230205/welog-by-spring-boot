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
                .map(ResponseDtoMapper::mapToUserResponseDto)
                .toList();
    }

    public UserResponseDto get(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }

        return ResponseDtoMapper.mapToUserResponseDto(userRepository.findById(id).get());
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

        if (userPatchDto.getName() != null) existingUser.setName(userPatchDto.getName());
        if (userPatchDto.getEmail() != null) existingUser.setEmail(userPatchDto.getEmail());
        if (userPatchDto.getPhoto() != null) existingUser.setPhoto(userPatchDto.getPhoto());

        return ResponseDtoMapper.mapToUserResponseDto(userRepository.save(existingUser));
    }

    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
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

// import com.example.welog.dto.UserCreateDto;
// import com.example.welog.dto.UserResponseDto;
// import com.example.welog.exception.ResourceNotFoundException;
// import com.example.welog.exception.BadRequestException;
// import com.example.welog.model.User;
// import com.example.welog.repository.UserRepository;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.Pageable;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;

// @Service
// @Transactional
// public class UserService {
    
//     private final UserRepository userRepository;
    
//     @Autowired
//     public UserService(UserRepository userRepository) {
//         this.userRepository = userRepository;
//     }
    
//     public UserResponseDto createUser(UserCreateDto userCreateDto) {
//         // Check if username already exists
//         if (userRepository.existsByUsername(userCreateDto.getUsername())) {
//             throw new BadRequestException("Username already exists");
//         }
        
//         // Check if email already exists
//         if (userRepository.existsByEmail(userCreateDto.getEmail())) {
//             throw new BadRequestException("Email already exists");
//         }
        
//         User user = new User();
//         user.setUsername(userCreateDto.getUsername());
//         user.setEmail(userCreateDto.getEmail());
//         user.setPassword(userCreateDto.getPassword()); // In real app, hash the password
//         user.setFirstName(userCreateDto.getFirstName());
//         user.setLastName(userCreateDto.getLastName());
        
//         User savedUser = userRepository.save(user);
//         return convertToResponseDto(savedUser);
//     }
    
//     @Transactional(readOnly = true)
//     public UserResponseDto getUserById(Long id) {
//         User user = userRepository.findById(id)
//             .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
//         return convertToResponseDto(user);
//     }
    
//     @Transactional(readOnly = true)
//     public UserResponseDto getUserByUsername(String username) {
//         User user = userRepository.findByUsername(username)
//             .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
//         return convertToResponseDto(user);
//     }
    
//     @Transactional(readOnly = true)
//     public Page<UserResponseDto> getAllUsers(Pageable pageable) {
//         return userRepository.findAll(pageable).map(this::convertToResponseDto);
//     }
    
//     public UserResponseDto updateUser(Long id, UserCreateDto userUpdateDto) {
//         User user = userRepository.findById(id)
//             .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        
//         // Check if username is being changed and if it's already taken
//         if (!user.getUsername().equals(userUpdateDto.getUsername()) && 
//             userRepository.existsByUsername(userUpdateDto.getUsername())) {
//             throw new BadRequestException("Username already exists");
//         }
        
//         // Check if email is being changed and if it's already taken
//         if (!user.getEmail().equals(userUpdateDto.getEmail()) && 
//             userRepository.existsByEmail(userUpdateDto.getEmail())) {
//             throw new BadRequestException("Email already exists");
//         }
        
//         user.setUsername(userUpdateDto.getUsername());
//         user.setEmail(userUpdateDto.getEmail());
//         user.setFirstName(userUpdateDto.getFirstName());
//         user.setLastName(userUpdateDto.getLastName());
        
//         User updatedUser = userRepository.save(user);
//         return convertToResponseDto(updatedUser);
//     }
    
//     public void deleteUser(Long id) {
//         User user = userRepository.findById(id)
//             .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
//         userRepository.delete(user);
//     }
    
//     private UserResponseDto convertToResponseDto(User user) {
//         return new UserResponseDto(
//             user.getId(),
//             user.getUsername(),
//             user.getEmail(),
//             user.getFirstName(),
//             user.getLastName(),
//             user.getCreatedAt(),
//             user.getUpdatedAt()
//         );
//     }
// }
