package com.example.usedItem.dto;

import lombok.Getter;
import java.time.LocalDateTime;

import com.example.usedItem.domain.Keyword;

@Getter
public class KeywordResponseDto {
    private Long keywordId;
    private String keywordText;
    private String targetSiteCode;
    private boolean active;
    private Integer minPrice;
    private Integer maxPrice;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Entity -> DTO 변환을 위한 생성자 또는 정적 팩토리 메소드
    public KeywordResponseDto(Keyword keyword) {
        this.keywordId = keyword.getId();
        this.keywordText = keyword.getKeywordText();
        this.targetSiteCode = keyword.getTargetSiteCode();
        this.active = keyword.isActive();
        this.minPrice = keyword.getMinPrice();
        this.maxPrice = keyword.getMaxPrice();
        this.createdAt = keyword.getCreatedAt();
        this.updatedAt = keyword.getUpdatedAt();
    }

    public static KeywordResponseDto fromEntity(Keyword keyword) {
        return new KeywordResponseDto(keyword);
    }
}