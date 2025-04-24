package com.example.usedItem.service.Impl;

import jakarta.persistence.EntityNotFoundException; // 예외 타입 주의
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.usedItem.domain.Keyword;
import com.example.usedItem.domain.User;
import com.example.usedItem.dto.KeywordRequestDto;
import com.example.usedItem.dto.KeywordResponseDto;
import com.example.usedItem.repository.KeywordRepository;
import com.example.usedItem.repository.UserRepository;
import com.example.usedItem.service.KeywordService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor // final 필드 생성자 자동 주입
public class KeywordServiceImpl implements KeywordService {

    private final KeywordRepository keywordRepository;
    private final UserRepository userRepository; // 사용자 존재 확인 및 연관관계 설정용

    @Transactional
    @Override
    public KeywordResponseDto addKeyword(Long userId, KeywordRequestDto requestDto) {
        // 1. 사용자 조회 (존재하지 않으면 예외 발생)
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다. userId: " + userId));

        // 2. 중복 키워드 확인 (선택 사항)
        if (keywordRepository.existsByUserIdAndKeywordTextAndTargetSiteCode(userId, requestDto.getKeywordText(),
                requestDto.getTargetSiteCode())) {
throw new IllegalArgumentException("이미 등록된 키워드입니다."); // 혹은 다른 처리 방식
        }

        // 3. Keyword 엔티티 생성 및 저장
        Keyword keyword = Keyword.builder()
                .user(user) // 연관관계 설정
                .keywordText(requestDto.getKeywordText())
                .targetSiteCode(requestDto.getTargetSiteCode())
                .active(requestDto.getActive() != null ? requestDto.getActive() : true) // null 체크 후 기본값
                .minPrice(requestDto.getMinPrice())
                .maxPrice(requestDto.getMaxPrice())
                .build();

        Keyword savedKeyword = keywordRepository.save(keyword);

        // User 엔티티의 keywords 리스트에도 추가 (양방향 연관관계 관리) - 선택 사항, User 엔티티에 addKeyword 편의
        // 메소드 구현 시 사용
        // user.addKeyword(savedKeyword);

        // 4. DTO로 변환하여 반환
        return KeywordResponseDto.fromEntity(savedKeyword);
    }

    @Transactional(readOnly = true) // 읽기 전용 트랜잭션
    @Override
    public List<KeywordResponseDto> getKeywordsByUser(Long userId) {
        // 사용자 존재 확인 (선택 사항이나, 없는 사용자에 대해 빈 리스트 반환이 자연스러움)
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("사용자를 찾을 수 없습니다. userId: " + userId);
        }

        List<Keyword> keywords = keywordRepository.findByUserIdOrderByCreatedAtDesc(userId);

        // Entity 리스트 -> DTO 리스트 변환
        return keywords.stream()
                .map(KeywordResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public KeywordResponseDto updateKeyword(Long userId, Long keywordId, KeywordRequestDto requestDto) {
        // 1. 사용자의 특정 키워드 조회 (존재하지 않거나, 사용자의 키워드가 아니면 예외 발생)
        Keyword keyword = keywordRepository.findByIdAndUserId(keywordId, userId)
                .orElseThrow(() -> new EntityNotFoundException("키워드를 찾을 수 없거나 수정 권한이 없습니다. keywordId: " + keywordId));

        // 2. 키워드 정보 업데이트 (Setter 사용 또는 별도 update 메소드 구현)
        keyword.setKeywordText(requestDto.getKeywordText());
        keyword.setTargetSiteCode(requestDto.getTargetSiteCode());
        if (requestDto.getActive() != null) { // null이 아닐 경우에만 업데이트
            keyword.setActive(requestDto.getActive());
        }
        keyword.setMinPrice(requestDto.getMinPrice()); // null 허용
        keyword.setMaxPrice(requestDto.getMaxPrice()); // null 허용
        // updatedAt은 @UpdateTimestamp에 의해 자동 관리됨

        // JPA의 변경 감지(Dirty Checking)에 의해 save 호출 없이도 업데이트 되지만, 명시적으로 save 호출도 가능
        // Keyword updatedKeyword = keywordRepository.save(keyword);

        // 3. DTO로 변환하여 반환
        return KeywordResponseDto.fromEntity(keyword);
    }

    @Transactional
    @Override
    public void deleteKeyword(Long userIdd, Long keywordId) {
        // 1. 사용자의 특정 키워드 조회 (존재하지 않거나, 사용자의 키워드가 아니면 예외 발생)
        Keyword keyword = keywordRepository.findByIdAndUserId(keywordId, userIdd)
                .orElseThrow(() -> new EntityNotFoundException("키워드를 찾을 수 없거나 삭제 권한이 없습니다. keywordId: " + keywordId));

        // 2. 키워드 삭제
        keywordRepository.delete(keyword);

        // User 엔티티에서 제거 (orphanRemoval=true 설정 시 자동으로 DB에서도 삭제됨) - 선택 사항
        // User user = keyword.getUser();
        // if (user != null) {
        // user.getKeywords().remove(keyword);
        // }
    }
}