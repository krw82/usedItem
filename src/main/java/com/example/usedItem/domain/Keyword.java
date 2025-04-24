package com.example.usedItem.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter // 연관관계 편의 메소드를 위해 Setter 허용 (또는 별도 메소드)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "KEYWORDS", indexes = {
        @Index(name = "idx_keyword_user_id", columnList = "user_id"),
        @Index(name = "idx_keyword_site_text", columnList = "target_site_code, keyword_text")
})
public class Keyword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "keyword_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // 지연 로딩 설정
    @JoinColumn(name = "user_id", nullable = false) // 외래키 컬럼 지정
    private User user;

    @Column(name = "keyword_text", nullable = false)
    private String keywordText;

    @Column(name = "target_site_code", nullable = false, length = 20)
    private String targetSiteCode; // 예: "BUNJANG", "JOONGGO"

    @Column(name = "is_active", nullable = false)
    private boolean active = true; // 기본값 true

    @Column(name = "min_price")
    private Integer minPrice; // Null 가능하도록 Wrapper 타입 사용

    @Column(name = "max_price")
    private Integer maxPrice; // Null 가능하도록 Wrapper 타입 사용

    @Column(name = "last_checked_at")
    private LocalDateTime lastCheckedAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Keyword(1) : Notification(N) 관계
    @OneToMany(mappedBy = "keyword", fetch = FetchType.LAZY)
    private List<Notification> notifications = new ArrayList<>();

    @Builder
    public Keyword(User user, String keywordText, String targetSiteCode, boolean active, Integer minPrice,
            Integer maxPrice) {
        this.user = user;
        this.keywordText = keywordText;
        this.targetSiteCode = targetSiteCode;
        this.active = active;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
    }

    // 연관관계 편의 메소드 (양방향)
    public void setUser(User user) {
        // 기존 User와의 연관관계 제거 (필요 시)
        if (this.user != null) {
            this.user.getKeywords().remove(this);
        }
        this.user = user;
        // 무한 루프 방지
        if (user != null && !user.getKeywords().contains(this)) {
            user.getKeywords().add(this);
        }
    }
}