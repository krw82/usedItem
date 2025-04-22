package com.example.usedItem.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.usedItem.domain.UserKeyword;
import com.example.usedItem.service.UserKeywordService;

import java.util.List;

@Slf4j
@RestController // RESTful 웹 서비스의 컨트롤러임을 선언 (@Controller + @ResponseBody)
@RequestMapping("/api/keywords") // 이 컨트롤러의 모든 핸들러 메소드는 /api/keywords 경로로 매핑됨
@RequiredArgsConstructor
public class KeywordController {

    private final UserKeywordService userKeywordService; // 서비스 계층 주입

    /**
     * 새 키워드를 등록하는 API 엔드포인트
     * HTTP POST /api/keywords
     * 요청 파라미터: userId, keyword, targetSite
     */
    @PostMapping
    public ResponseEntity<String> addKeyword(@RequestParam String userId,
            @RequestParam String keyword,
            @RequestParam(required = false) String targetSite) { // targetSite는 선택적 파라미터로 변경
        log.info("API 요청: 키워드 등록 - userId={}, keyword={}, site={}", userId, keyword, targetSite);
        userKeywordService.registerKeyword(userId, keyword, targetSite);
        // 성공 시 200 OK 응답과 메시지 반환
        return ResponseEntity.ok("키워드가 성공적으로 등록되었습니다. (PL/SQL)");
    }

    /**
     * 특정 사용자의 키워드 목록을 조회하는 API 엔드포인트
     * HTTP GET /api/keywords/{userId}
     * 경로 변수: userId
     */
    @GetMapping("/{userId}")
    public ResponseEntity<List<UserKeyword>> getUserKeywords(@PathVariable String userId) {
        log.info("API 요청: 사용자 키워드 조회 - userId={}", userId);
        // PL/SQL 프로시저를 사용하는 서비스 메소드 호출
        List<UserKeyword> keywords = userKeywordService.getKeywordsForUser(userId);

        // (대안) JPA 쿼리 메소드를 사용하는 서비스 메소드 호출
        // List<UserKeyword> keywords =
        // userKeywordService.getKeywordsForUserWithQueryMethod(userId);

        // 성공 시 200 OK 응답과 키워드 목록(JSON) 반환
        return ResponseEntity.ok(keywords);
    }
}
