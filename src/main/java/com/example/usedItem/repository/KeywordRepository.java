package com.example.usedItem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.usedItem.domain.Keyword;

import java.util.List;
import java.util.Optional;

@Repository
public interface KeywordRepository extends JpaRepository<Keyword, Long> {

    // 특정 사용자의 모든 키워드 조회 (생성 시간 역순 정렬)
    List<Keyword> findByUserIdOrderByCreatedAtDesc(Long userId);

    // 특정 사용자의 특정 키워드 조회 (수정/삭제 시 사용)
    Optional<Keyword> findByIdAndUserId(Long id, Long userId);

    // 특정 사용자의 특정 키워드 텍스트와 사이트 코드로 키워드 존재 여부 확인 (중복 등록 방지용)
    boolean existsByUserIdAndKeywordTextAndTargetSiteCode(Long userId, String keywordText, String targetSiteCode);
}