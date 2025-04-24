package com.example.usedItem.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.example.usedItem.domain.ScrapedItem;
import com.example.usedItem.dto.ScrapedItemResponseDto;

public interface ScrapedItemService {

    /**
     * 스크랩된 상품 정보를 저장합니다. (중복 방지 포함)
     * 이미 존재하는 상품(URL 또는 사이트+ID 기준)이면 저장하지 않고 Optional.empty() 반환.
     *
     * @param item 저장할 ScrapedItem 엔티티 (ID는 null이어야 함)
     * @return 저장된 엔티티 Optional, 중복 시 Optional.empty()
     */
    Optional<ScrapedItem> saveItemIfNotExists(ScrapedItem item);

    /**
     * 스크랩된 상품 목록을 저장합니다. (중복 방지 포함)
     *
     * @param items 저장할 ScrapedItem 엔티티 리스트
     * @return 실제로 새로 저장된 엔티티 리스트
     */
    List<ScrapedItem> saveItemsIfNotExists(List<ScrapedItem> items);

    /**
     * ID로 스크랩된 상품 정보를 조회합니다.
     *
     * @param itemId 조회할 상품 ID
     * @return 상품 정보 DTO
     */
    ScrapedItemResponseDto getItemById(Long itemId);

    /**
     * 모든 스크랩된 상품 목록을 조회합니다. (페이징 고려 필요)
     *
     * @return 상품 정보 DTO 리스트
     */
    List<ScrapedItemResponseDto> getAllItems(); // 실제 서비스에서는 페이징 처리 필수

    /**
     * 특정 상품을 알림 발송 완료로 표시합니다.
     *
     * @param itemId 알림 발송 완료 처리할 상품 ID
     */
    void markAsNotified(Long itemId);

    /**
     * 알림 발송 대상을 조회합니다. (예시: 특정 시간 이후 스크랩되고 아직 알림 안 보낸 것)
     *
     * @param scrapedAfter 조회 기준 시간
     * @return 알림 대상 상품 엔티티 리스트
     */
    List<ScrapedItem> findItemsToNotify(LocalDateTime scrapedAfter);

}