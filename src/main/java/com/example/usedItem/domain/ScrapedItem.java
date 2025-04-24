package com.example.usedItem.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "SCRAPED_ITEMS", uniqueConstraints = {
        @UniqueConstraint(name = "uk_scraped_item_source", columnNames = { "source_site_code", "item_source_id" }),
        @UniqueConstraint(name = "uk_scraped_item_url", columnNames = { "item_url" })
})
public class ScrapedItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long id;

    @Column(name = "source_site_code", nullable = false, length = 20)
    private String sourceSiteCode;

    @Column(name = "item_source_id", nullable = false, length = 100)
    private String itemSourceId; // 해당 사이트의 상품 ID

    @Column(nullable = false, length = 500) // 제목 길이를 넉넉하게
    private String title;

    private Integer price; // 가격이 없을 수도 있음

    @Column(name = "item_url", nullable = false, length = 2048) // URL 길이를 넉넉하게
    private String itemUrl;

    @Column(name = "image_url", length = 2048)
    private String imageUrl;

    @Column(length = 100)
    private String location;

    @Column(name = "posted_at_source")
    private LocalDateTime postedAtSource; // 사이트에서 가져온 게시 시간

    @CreationTimestamp
    @Column(name = "scraped_at", updatable = false, nullable = false)
    private LocalDateTime scrapedAt; // 우리 시스템이 스크랩한 시간

    // 알림 발송 여부는 Notification 테이블로 관리하는 것이 더 유연할 수 있음
    // 여기서는 간단하게 플래그로 추가
    @Column(name = "is_notified", nullable = false)
    private boolean notified = false;

    // ScrapedItem(1) : Notification(N) 관계
    @OneToMany(mappedBy = "item", fetch = FetchType.LAZY)
    private List<Notification> notifications = new ArrayList<>();

    @Builder
    public ScrapedItem(String sourceSiteCode, String itemSourceId, String title, Integer price, String itemUrl,
            String imageUrl, String location, LocalDateTime postedAtSource) {
        this.sourceSiteCode = sourceSiteCode;
        this.itemSourceId = itemSourceId;
        this.title = title;
        this.price = price;
        this.itemUrl = itemUrl;
        this.imageUrl = imageUrl;
        this.location = location;
        this.postedAtSource = postedAtSource;
        this.notified = false; // 기본값 false
    }

    // 알림 발송 완료 처리 (필요 시)
    public void markAsNotified() {
        this.notified = true;
    }
}