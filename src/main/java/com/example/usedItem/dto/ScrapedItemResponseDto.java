package com.example.usedItem.dto;

import lombok.Getter;

import java.time.LocalDateTime;

import com.example.usedItem.domain.ScrapedItem;

@Getter
public class ScrapedItemResponseDto {
    private Long itemId;
    private String sourceSiteCode;
    private String itemSourceId;
    private String title;
    private Integer price;
    private String itemUrl;
    private String imageUrl;
    private String location;
    private LocalDateTime postedAtSource;
    private LocalDateTime scrapedAt;
    private boolean notified; // 알림 발송 여부 포함

    public ScrapedItemResponseDto(ScrapedItem item) {
        this.itemId = item.getId();
        this.sourceSiteCode = item.getSourceSiteCode();
        this.itemSourceId = item.getItemSourceId();
        this.title = item.getTitle();
        this.price = item.getPrice();
        this.itemUrl = item.getItemUrl();
        this.imageUrl = item.getImageUrl();
        this.location = item.getLocation();
        this.postedAtSource = item.getPostedAtSource();
        this.scrapedAt = item.getScrapedAt();
        this.notified = item.isNotified(); // boolean getter는 isXXX
    }

    public static ScrapedItemResponseDto fromEntity(ScrapedItem item) {
        return new ScrapedItemResponseDto(item);
    }
}