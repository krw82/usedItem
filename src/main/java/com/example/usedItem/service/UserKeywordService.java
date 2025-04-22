package com.example.usedItem.service;

import lombok.RequiredArgsConstructor; // final 필드 생성자 자동 주입
import lombok.extern.slf4j.Slf4j; // 로깅을 위한 Lombok 어노테이션
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // 트랜잭션 처리

import com.example.usedItem.domain.UserKeyword;
import com.example.usedItem.repository.UserKeywordRepository;

import java.util.List;

@Slf4j // 로그 객체 자동 생성 (log.info(), log.error() 등 사용 가능)
@Service // 서비스 계층의 컴포넌트임을 선언
@RequiredArgsConstructor // final 또는 @NonNull 필드에 대한 생성자 자동 생성 및 주입
public class UserKeywordService {

    private final UserKeywordRepository userKeywordRepository; // 생성자 주입

    /**
     * 새 키워드를 등록합니다 (PL/SQL 프로시저 호출).
     *
     * @param userId     사용자 ID
     * @param keyword    키워드
     * @param targetSite 대상 사이트
     */
    @Transactional // 데이터 변경(INSERT)이 발생하므로 트랜잭션 적용
    public void registerKeyword(String userId, String keyword, String targetSite) {
        // Repository의 프로시저 호출 메소드 사용
        userKeywordRepository.addUserKeywordProcedure(userId, keyword, targetSite);
        log.info("키워드 등록 완료 (PL/SQL): userId={}, keyword={}, site={}", userId, keyword, targetSite);
    }

    /**
     * 특정 사용자의 키워드 목록을 조회합니다 (PL/SQL 프로시저 - EntityManager 사용).
     *
     * @param userId 사용자 ID
     * @return 키워드 목록
     */
    @Transactional(readOnly = true) // 데이터 조회 작업이므로 readOnly=true 설정 (성능 이점)
    public List<UserKeyword> getKeywordsForUser(String userId) {
        log.info("사용자 키워드 조회 (PL/SQL - EntityManager): userId={}", userId);
        // EntityManager를 사용하는 커스텀 Repository 메소드 호출
        return userKeywordRepository.findKeywordsByUserIdWithProcedure(userId);
    }

    /**
     * 특정 사용자의 키워드 목록을 조회합니다 (Spring Data JPA 쿼리 메소드 사용).
     *
     * @param userId 사용자 ID
     * @return 키워드 목록
     */
    @Transactional(readOnly = true)
    public List<UserKeyword> getKeywordsForUserWithQueryMethod(String userId) {
        log.info("사용자 키워드 조회 (JPA Query Method): userId={}", userId);
        // Spring Data JPA가 자동으로 생성한 쿼리 메소드 호출
        return userKeywordRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
}
