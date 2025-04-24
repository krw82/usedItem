package com.example.usedItem.service;

import java.util.List;

import com.example.usedItem.dto.UserResponseDto;
import com.example.usedItem.dto.UserSignUpRequestDto;

public interface UserService {

    /**
     * 회원 가입을 처리합니다.
     * 
     * @param requestDto 회원 가입 정보 DTO
     * @return 생성된 사용자 정보 DTO
     */
    UserResponseDto signUp(UserSignUpRequestDto requestDto);

    /**
     * 사용자 ID로 사용자 정보를 조회합니다.
     * 
     * @param userId 조회할 사용자 ID
     * @return 사용자 정보 DTO
     */
    UserResponseDto getUserById(Long userId);

    /**
     * 이메일로 사용자 정보를 조회합니다.
     * 
     * @param email 조회할 사용자 이메일
     * @return 사용자 정보 DTO
     */
    UserResponseDto getUserByEmail(String email);

    /**
     * 모든 사용자 목록을 조회합니다. (관리자용 등)
     * 
     * @return 사용자 정보 DTO 리스트
     */
    List<UserResponseDto> getAllUsers();

    /**
     * 사용자 닉네임을 수정합니다.
     * 
     * @param userId     수정할 사용자 ID
     * @param requestDto 새 닉네임 정보 DTO
     * @return 수정된 사용자 정보 DTO
     */

    /**
     * 사용자를 삭제합니다.
     * 
     * @param userId 삭제할 사용자 ID
     */
    void deleteUser(Long userId);

    /**
     * 이메일 중복 여부를 확인합니다.
     * 
     * @param email 확인할 이메일
     * @return 중복 시 true, 아니면 false
     */
    boolean checkEmailExists(String email);

    /**
     * 닉네임 중복 여부를 확인합니다.
     * 
     * @param nickname 확인할 닉네임
     * @return 중복 시 true, 아니면 false
     */
    boolean checkNicknameExists(String nickname);
}