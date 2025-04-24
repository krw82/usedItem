package com.example.usedItem.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.usedItem.dto.UserResponseDto;
import com.example.usedItem.dto.UserSignUpRequestDto;
import com.example.usedItem.service.UserService;

import java.util.List;
import java.util.Map; // for check result

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 회원 가입
    @PostMapping("/signup")
    public ResponseEntity<UserResponseDto> signUp(@Valid @RequestBody UserSignUpRequestDto requestDto) {
        UserResponseDto responseDto = userService.signUp(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    // 이메일 중복 확인
    @GetMapping("/check-email")
    public ResponseEntity<Map<String, Boolean>> checkEmailExists(@RequestParam String email) {
        boolean exists = userService.checkEmailExists(email);
        return ResponseEntity.ok(Map.of("exists", exists));
    }

    // 닉네임 중복 확인
    @GetMapping("/check-nickname")
    public ResponseEntity<Map<String, Boolean>> checkNicknameExists(@RequestParam String nickname) {
        boolean exists = userService.checkNicknameExists(nickname);
        return ResponseEntity.ok(Map.of("exists", exists));
    }

    // 사용자 ID로 조회
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long userId) {
        // TODO: 본인 또는 관리자만 조회 가능하도록 권한 체크 필요 (Spring Security)
        UserResponseDto userDto = userService.getUserById(userId);
        return ResponseEntity.ok(userDto);
    }

    // 이메일로 조회 (예시 - 필요에 따라 추가)
    @GetMapping("/by-email")
    public ResponseEntity<UserResponseDto> getUserByEmail(@RequestParam String email) {
        // TODO: 권한 체크 필요
        UserResponseDto userDto = userService.getUserByEmail(email);
        return ResponseEntity.ok(userDto);
    }

    // 모든 사용자 조회 (관리자용)
    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        // TODO: 관리자 권한 체크 필요 (Spring Security)
        List<UserResponseDto> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // 사용자 삭제
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        // TODO: 본인 또는 관리자만 삭제 가능하도록 권한 체크 필요 (Spring Security)
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}