package com.example.usedItem.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.usedItem.dto.ScrapedItemResponseDto;
import com.example.usedItem.service.ScrapedItemService;

import java.util.List;

@RestController
@RequestMapping("/api/scraped-items") // 기본 경로
@RequiredArgsConstructor
public class ScrapedItemController {

    private final ScrapedItemService scrapedItemService;

    // 모든 스크랩된 상품 조회 (페이징 필요!)
    @GetMapping
    public ResponseEntity<List<ScrapedItemResponseDto>> getAllScrapedItems() {
        // TODO: 관리자 권한 확인 필요
        // TODO: 페이징 처리 필수 (예: Pageable 파라미터 사용)
        List<ScrapedItemResponseDto> items = scrapedItemService.getAllItems();
        return ResponseEntity.ok(items);
    }

    // ID로 특정 스크랩된 상품 조회
    @GetMapping("/{itemId}")
    public ResponseEntity<ScrapedItemResponseDto> getScrapedItemById(@PathVariable Long itemId) {
        // TODO: 관리자 권한 확인 필요
        ScrapedItemResponseDto itemDto = scrapedItemService.getItemById(itemId);
        return ResponseEntity.ok(itemDto);
    }

    // 참고: 상품 생성(POST), 수정(PUT/PATCH), 삭제(DELETE) API는
    // 일반적으로 이 컨트롤러에서 제공하지 않습니다. 스크래핑 서비스나
    // 내부 관리 로직에서 ScrapedItemService를 직접 호출하여 처리합니다.
}