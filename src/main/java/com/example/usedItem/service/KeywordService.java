package com.example.usedItem.service;

import java.util.List;

import com.example.usedItem.dto.KeywordRequestDto;
import com.example.usedItem.dto.KeywordResponseDto;

public interface KeywordService {

    /**
     * 사용자의 새 키워드를 등록합니다.
     *
     * @param userId     사용자 ID
     * @param requestDto 키워드 정보 DTO
     * @return 등록된 키워드 정보 DTO
     */
    KeywordResponseDto addKeyword(Long userId, KeywordRequestDto requestDto);

    /**
     * 특정 사용자의 모든 키워드 목록을 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 키워드 정보 DTO 리스트
     */
    List<KeywordResponseDto> getKeywordsByUser(Long userId);

    /**
     * 특정 키워드의 정보를 수정합니다.
     *
     * @param userId     사용자 ID (본인 확인용)
     * @param keywordId  수정할 키워드 ID
     * @param requestDto 수정할 키워드 정보 DTO
     * @return 수정된 키워드 정보 DTO
     */
    KeywordResponseDto updateKeyword(Long userId, Long keywordId, KeywordRequestDto requestDto);

    /**
     * 특정 키워드를 삭제합니다.
     *
     * @param userId    사용자 ID (본인 확인용)
     * @param keywordId 삭제할 키워드 ID
     */
    void deleteKeyword(Long userId, Long keywordId);
}