package com.example.usedItem.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "NOTIFICATIONS", indexes = {
        @Index(name = "idx_notification_user_id", columnList = "user_id"),
        @Index(name = "idx_notification_item_id", columnList = "item_id")
})
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private ScrapedItem item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "keyword_id", nullable = false)
    private Keyword keyword; // 어떤 키워드로 인해 발생한 알림인지 추적

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false, length = 10)
    private NotificationType notificationType; // EMAIL, PUSH 등

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private NotificationStatus status = NotificationStatus.PENDING; // 기본 상태

    @Column(name = "attempt_count", nullable = false)
    private int attemptCount = 0;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "error_message", length = 1000)
    private String errorMessage;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public Notification(User user, ScrapedItem item, Keyword keyword, NotificationType notificationType) {
        this.user = user;
        this.item = item;
        this.keyword = keyword;
        this.notificationType = notificationType;
        this.status = NotificationStatus.PENDING;
        this.attemptCount = 0;
    }

    // 상태 변경 메소드들 (예시)
    public void markAsSent(LocalDateTime sentTime) {
        this.status = NotificationStatus.SENT;
        this.sentAt = sentTime;
        this.errorMessage = null; // 성공 시 에러 메시지 초기화
    }

    public void markAsFailed(String errorMsg) {
        this.status = NotificationStatus.FAILED;
        this.attemptCount++;
        this.errorMessage = errorMsg;
    }

    public void markAsRead() {
        this.status = NotificationStatus.READ;
    }
}

// --- Enums ---
enum NotificationType {
    EMAIL, PUSH, SMS // 필요에 따라 추가
}

enum NotificationStatus {
    PENDING, // 발송 대기
    SENT, // 발송 성공
    FAILED, // 발송 실패
    READ // 사용자가 읽음 (구현 시 필요)
}