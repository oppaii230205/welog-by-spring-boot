package com.example.welog.dto;

public class UserCreateDto {
  private String name;
  private String email;
  private String password;
  private String passwordConfirm;

  public UserCreateDto() {
  }
  public UserCreateDto(String name, String email, String password, String passwordConfirm) {
    this.name = name;
    this.email = email;
    this.password = password;
    this.passwordConfirm = passwordConfirm;
  }
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public String getEmail() {
    return email;
  }
  public void setEmail(String email) {
    this.email = email;
  }
  public String getPassword() {
    return password;
  }
  public void setPassword(String password) {
    this.password = password;
  }
  public String getPasswordConfirm() {
    return passwordConfirm;
  }
  public void setPasswordConfirm(String passwordConfirm) {
    this.passwordConfirm = passwordConfirm;
  }
}
