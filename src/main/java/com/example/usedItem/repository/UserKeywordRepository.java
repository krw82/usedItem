package com.example.usedItem.repository;

import com.example.usedItem.domain.UserKeyword;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

@Repository // 리포지토리 계층의 컴포넌트임을 선언
// JpaRepository를 상속받습니다.
// 첫 번째 제네릭 타입: 관리할 엔티티 클래스 (UserKeyword)
// 두 번째 제네릭 타입: 엔티티의 ID 타입 (예: Long). 실제 UserKeyword 엔티티의 ID 타입으로 변경해주세요.
public interface UserKeywordRepository extends JpaRepository<UserKeyword, Long> {
    List<UserKeyword> findByUserId(String userId);
}
