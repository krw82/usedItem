package com.example.usedItem.service.Impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder; // PasswordEncoder 주입
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.usedItem.domain.User;
import com.example.usedItem.domain.UserRole;
import com.example.usedItem.domain.UserStatus;
import com.example.usedItem.dto.UserResponseDto;
import com.example.usedItem.dto.UserSignUpRequestDto;
import com.example.usedItem.repository.UserRepository;
import com.example.usedItem.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // 비밀번호 암호화기 주입

    @Transactional
    @Override
    public UserResponseDto signUp(UserSignUpRequestDto requestDto) {
        // 이메일 중복 확인
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
        // 닉네임 중복 확인
        if (userRepository.existsByNickname(requestDto.getNickname())) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());

        // User 엔티티 생성
        User user = User.builder()
                .email(requestDto.getEmail())
                .password(encodedPassword) // 암호화된 비밀번호 저장
                .nickname(requestDto.getNickname())
                .role(UserRole.ROLE_USER) // 기본 역할 설정
                .status(UserStatus.ACTIVE) // 기본 상태 설정
                .build();

        User savedUser = userRepository.save(user);

        return UserResponseDto.fromEntity(savedUser);
    }

    @Transactional(readOnly = true)
    @Override
    public UserResponseDto getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다. ID: " + userId));
        return UserResponseDto.fromEntity(user);
    }

    @Transactional(readOnly = true)
    @Override
    public UserResponseDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다. Email: " + email));
        return UserResponseDto.fromEntity(user);
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserResponseDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(UserResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void deleteUser(Long userId) {
        // 사용자 존재 확인
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("삭제할 사용자를 찾을 수 없습니다. ID: " + userId);
        }
        // TODO: 사용자와 관련된 데이터 처리 정책 필요 (예: 키워드, 알림 등)
        // Cascade 설정 또는 직접 관련 데이터 삭제 로직 추가
        userRepository.deleteById(userId);
    }

    @Transactional(readOnly = true)
    @Override
    public boolean checkEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    @Transactional(readOnly = true)
    @Override
    public boolean checkNicknameExists(String nickname) {
        return userRepository.existsByNickname(nickname);
    }
}