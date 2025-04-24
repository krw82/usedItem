package com.example.usedItem.controller;

import jakarta.validation.Valid; // javax -> jakarta
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.usedItem.dto.KeywordRequestDto;
import com.example.usedItem.dto.KeywordResponseDto;
import com.example.usedItem.service.KeywordService;

import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}/keywords") // 사용자별 키워드 관리를 위한 경로
@RequiredArgsConstructor
public class KeywordController {

    private final KeywordService keywordService;

    // 키워드 등록
    @PostMapping
    public ResponseEntity<KeywordResponseDto> addKeyword(
            @PathVariable Long userId,
            @Valid @RequestBody KeywordRequestDto requestDto) { // @Valid로 DTO 유효성 검사 활성화
        KeywordResponseDto responseDto = keywordService.addKeyword(userId, requestDto);
        // 생성 성공 시 201 Created 상태 코드와 함께 응답
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    // 특정 사용자의 키워드 목록 조회
    @GetMapping
    public ResponseEntity<List<KeywordResponseDto>> getUserKeywords(@PathVariable Long userId) {
        List<KeywordResponseDto> keywords = keywordService.getKeywordsByUser(userId);
        return ResponseEntity.ok(keywords); // 조회 성공 시 200 OK
    }

    // 특정 키워드 수정
    @PutMapping("/{keywordId}")
    public ResponseEntity<KeywordResponseDto> updateKeyword(
            @PathVariable Long userId,
            @PathVariable Long keywordId,
            @Valid @RequestBody KeywordRequestDto requestDto) {
        KeywordResponseDto updatedKeyword = keywordService.updateKeyword(userId, keywordId, requestDto);
        return ResponseEntity.ok(updatedKeyword); // 수정 성공 시 200 OK
    }

    // 특정 키워드 삭제
    @DeleteMapping("/{keywordId}")
    public ResponseEntity<Void> deleteKeyword(
            @PathVariable Long userId,
            @PathVariable Long keywordId) {
        keywordService.deleteKeyword(userId, keywordId);
        // 삭제 성공 시 204 No Content 상태 코드 (본문 없음)
        return ResponseEntity.noContent().build();
    }
}