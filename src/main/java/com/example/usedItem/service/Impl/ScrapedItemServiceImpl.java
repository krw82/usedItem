package com.example.usedItem.service.Impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // 로깅 추가
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.usedItem.domain.ScrapedItem;
import com.example.usedItem.dto.ScrapedItemResponseDto;
import com.example.usedItem.repository.ScrapedItemRepository;
import com.example.usedItem.service.ScrapedItemService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j // 로그 사용
@Service
@RequiredArgsConstructor
public class ScrapedItemServiceImpl implements ScrapedItemService {

    private final ScrapedItemRepository scrapedItemRepository;

    @Transactional
    @Override
    public Optional<ScrapedItem> saveItemIfNotExists(ScrapedItem item) {
        // 중복 확인 (URL 또는 사이트코드+상품ID 기준) - 둘 다 확인하거나 정책에 따라 하나만 확인
        boolean exists = scrapedItemRepository.existsByItemUrl(item.getItemUrl()) ||
                scrapedItemRepository.existsBySourceSiteCodeAndItemSourceId(item.getSourceSiteCode(),
                        item.getItemSourceId());

        if (exists) {
            log.debug("스크랩 아이템 중복: URL={}, Site={}, ID={}", item.getItemUrl(), item.getSourceSiteCode(),
                    item.getItemSourceId());
            return Optional.empty(); // 이미 존재하면 저장하지 않음
        } else {
            log.info("새 스크랩 아이템 저장: Title={}", item.getTitle());
            return Optional.of(scrapedItemRepository.save(item));
        }
    }

    @Transactional
    @Override
    public List<ScrapedItem> saveItemsIfNotExists(List<ScrapedItem> items) {
        List<ScrapedItem> savedItems = new ArrayList<>();
        for (ScrapedItem item : items) {
            // saveItemIfNotExists 메소드 재활용
            saveItemIfNotExists(item).ifPresent(savedItems::add);
            // 직접 구현 시:
            // boolean exists = scrapedItemRepository.existsByItemUrl(item.getItemUrl()) ||
            // scrapedItemRepository.existsBySourceSiteCodeAndItemSourceId(item.getSourceSiteCode(),
            // item.getItemSourceId());
            // if (!exists) {
            // savedItems.add(scrapedItemRepository.save(item));
            // }
        }
        log.info("총 {}개의 아이템 중 {}개 새로 저장됨.", items.size(), savedItems.size());
        return savedItems;
    }

    @Transactional(readOnly = true)
    @Override
    public ScrapedItemResponseDto getItemById(Long itemId) {
        ScrapedItem item = scrapedItemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("스크랩된 상품을 찾을 수 없습니다. ID: " + itemId));
        return ScrapedItemResponseDto.fromEntity(item);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ScrapedItemResponseDto> getAllItems() {
        // 경고: 데이터가 많을 경우 성능 문제가 발생할 수 있습니다. 반드시 페이징 처리를 도입해야 합니다.
        log.warn("페이징 처리 없이 모든 스크랩 아이템을 조회합니다. 데이터 양에 따라 성능 문제가 발생할 수 있습니다.");
        List<ScrapedItem> items = scrapedItemRepository.findAll(); // 예시로 findAll 사용
        return items.stream()
                .map(ScrapedItemResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void markAsNotified(Long itemId) {
        ScrapedItem item = scrapedItemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("스크랩된 상품을 찾을 수 없습니다. ID: " + itemId));

        // ScrapedItem 엔티티에 상태 변경 메소드 추가 권장
        // 예: item.markAsNotified();
        item.markAsNotified(); // 엔티티에 구현된 메소드 호출 (아래 엔티티 코드 참고)

        // 변경 감지에 의해 저장됨. 명시적 save 호출도 가능.
        // scrapedItemRepository.save(item);
        log.info("스크랩 아이템 알림 완료 처리: ID={}", itemId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ScrapedItem> findItemsToNotify(LocalDateTime scrapedAfter) {
        log.info("{} 이후 스크랩되었고 알림 미발송된 아이템 조회", scrapedAfter);
        return scrapedItemRepository.findByNotifiedFalseAndScrapedAtAfter(scrapedAfter);
    }
}