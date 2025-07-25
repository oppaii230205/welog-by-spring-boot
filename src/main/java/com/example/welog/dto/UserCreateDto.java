package com.example.welog.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateDto {
  @NotBlank(message = "Name is required")
  private String name;

  @NotBlank(message = "Email is required")
  @Email(message = "Email should be valid")
  @Size(min = 5, max = 50, message = "Email should be between 5 and 50 characters")
  private String email;
  
  private String photo;

  @NotBlank(message = "Password is required")
  @Size(min = 8, max = 100, message = "Password should be between 8 and 100 characters")
  private String password;

  @NotBlank(message = "Password confirmation is required")
  private String passwordConfirm;

}
