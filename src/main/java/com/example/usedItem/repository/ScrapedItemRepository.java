package com.example.usedItem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.usedItem.domain.ScrapedItem;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ScrapedItemRepository extends JpaRepository<ScrapedItem, Long> {

    // 특정 사이트의 특정 상품 ID로 조회 (중복 확인용)
    Optional<ScrapedItem> findBySourceSiteCodeAndItemSourceId(String sourceSiteCode, String itemSourceId);

    // 특정 URL로 조회 (중복 확인용)
    Optional<ScrapedItem> findByItemUrl(String itemUrl);

    // 특정 사이트의 특정 상품 ID 존재 여부 확인 (더 효율적)
    boolean existsBySourceSiteCodeAndItemSourceId(String sourceSiteCode, String itemSourceId);

    // 특정 URL 존재 여부 확인 (더 효율적)
    boolean existsByItemUrl(String itemUrl);

    // 알림이 발송되지 않았고, 특정 시간 이후에 스크랩된 상품 조회 (알림 대상 찾기 예시)
    List<ScrapedItem> findByNotifiedFalseAndScrapedAtAfter(LocalDateTime scrapedAfter);

    // 특정 사이트 코드별 상품 조회 (필요 시)
    List<ScrapedItem> findBySourceSiteCodeOrderByScrapedAtDesc(String sourceSiteCode);

}