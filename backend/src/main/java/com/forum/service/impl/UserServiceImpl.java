package com.forum.service.impl;

import com.forum.dto.request.LoginRequest;
import com.forum.dto.request.RegisterRequest;
import com.forum.dto.response.JwtResponse;
import com.forum.dto.response.UserResponse;
import com.forum.entity.Role;
import com.forum.entity.User;
import com.forum.entity.UserStatus;
import com.forum.repository.RoleRepository;
import com.forum.repository.UserRepository;
import com.forum.security.jwt.JwtUtils;
import com.forum.security.service.UserDetailsImpl;
import com.forum.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @Override
    public JwtResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Set<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toSet());

        return new JwtResponse(
            jwt,
            userDetails.getId(),
            userDetails.getUsername(),
            userDetails.getEmail(),
            roles
        );
    }

    @Override
    @Transactional
    public UserResponse register(RegisterRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }

        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("邮箱已被注册");
        }

        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setStatus(UserStatus.ACTIVE);

        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName(Role.RoleType.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("默认角色未找到"));
        roles.add(userRole);
        user.setRoles(roles);

        return UserResponse.fromUser(userRepository.save(user));
    }

    @Override
    public UserResponse getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("用户不存在"));
        return UserResponse.fromUser(user);
    }

    @Override
    @Transactional
    public UserResponse updateProfile(Long userId, User updateUser) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("用户不存在"));

        if (updateUser.getAvatar() != null) {
            user.setAvatar(updateUser.getAvatar());
        }
        if (updateUser.getBio() != null) {
            user.setBio(updateUser.getBio());
        }
        if (updateUser.getEmail() != null && !updateUser.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(updateUser.getEmail())) {
                throw new RuntimeException("邮箱已被注册");
            }
            user.setEmail(updateUser.getEmail());
        }

        return UserResponse.fromUser(userRepository.save(user));
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("用户不存在");
        }
        userRepository.deleteById(userId);
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserResponse::fromUser)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
} 