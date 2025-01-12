package com.forum.dto.response;

import com.forum.entity.Role;
import com.forum.entity.UserStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String avatar;
    private String bio;
    private UserStatus status;
    private Set<String> roles;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public static UserResponse fromUser(com.forum.entity.User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setAvatar(user.getAvatar());
        response.setBio(user.getBio());
        response.setStatus(user.getStatus());
        response.setRoles(user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet()));
        response.setCreateTime(user.getCreateTime());
        response.setUpdateTime(user.getUpdateTime());
        return response;
    }
} 