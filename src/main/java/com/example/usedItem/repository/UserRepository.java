package com.example.usedItem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository; // @Repository 추가 권장

import com.example.usedItem.domain.User;

import java.util.Optional;

@Repository // 명시적으로 추가
public interface UserRepository extends JpaRepository<User, Long> {

    // 이메일로 사용자 찾기 (로그인 또는 중복 체크 시 사용)
    Optional<User> findByEmail(String email);

    // 닉네임으로 사용자 찾기 (중복 체크 시 사용)
    Optional<User> findByNickname(String nickname);

    // 이메일 존재 여부 확인 (더 효율적)
    boolean existsByEmail(String email);

    // 닉네임 존재 여부 확인 (더 효율적)
    boolean existsByNickname(String nickname);
}