package com.example.usedItem.dto;

import lombok.Getter;

import java.time.LocalDateTime;

import com.example.usedItem.domain.User;
import com.example.usedItem.domain.UserRole; // UserRole의 실제 패키지 경로
import com.example.usedItem.domain.UserStatus;

@Getter
public class UserResponseDto {
    private Long userId;
    private String email;
    private String nickname;
    private UserRole role;
    private UserStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public UserResponseDto(User user) {
        this.userId = user.getId();
        this.email = user.getEmail();
        this.nickname = user.getNickname();
        this.role = user.getRole();
        this.status = user.getStatus();
        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();
    }

    public static UserResponseDto fromEntity(User user) {
        return new UserResponseDto(user);
    }
}