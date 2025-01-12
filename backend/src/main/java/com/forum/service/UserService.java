package com.forum.service;

import com.forum.dto.request.LoginRequest;
import com.forum.dto.request.RegisterRequest;
import com.forum.dto.response.JwtResponse;
import com.forum.dto.response.UserResponse;
import com.forum.entity.User;

import java.util.List;

public interface UserService {
    JwtResponse login(LoginRequest loginRequest);
    UserResponse register(RegisterRequest registerRequest);
    UserResponse getUserProfile(Long userId);
    UserResponse updateProfile(Long userId, User user);
    void deleteUser(Long userId);
    List<UserResponse> getAllUsers();
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
} 