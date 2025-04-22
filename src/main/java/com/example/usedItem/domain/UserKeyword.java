package com.example.usedItem.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString; // 로그 출력 시 확인 용이
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor // JPA는 기본 생성자가 필요합니다.
@ToString // 객체 정보 로깅 시 유용
@Entity // JPA 엔티티임을 선언
@Table(name = "USER_KEYWORDS") // 매핑할 테이블 이름 지정
public class UserKeyword {

    @Id // 기본 키(Primary Key) 필드
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Oracle 12c 이상 IDENTITY 컬럼 사용 시
    @Column(name = "ID") // 매핑할 컬럼 이름 (선택사항, 필드명과 같으면 생략 가능)
    private Long id;

    @Column(name = "USER_ID", nullable = false, length = 100)
    private String userId; // 추후 User 엔티티와 @ManyToOne 연관관계 설정 가능

    @Column(name = "KEYWORD", nullable = false)
    private String keyword;

    @Column(name = "TARGET_SITE", length = 100)
    private String targetSite;

    @Column(name = "LAST_CHECKED_AT")
    private LocalDateTime lastCheckedAt;

    @CreationTimestamp // 엔티티가 처음 저장될 때 현재 시간 자동 저장
    @Column(name = "CREATED_AT", updatable = false) // 생성 시간은 업데이트되지 않도록 설정
    private LocalDateTime createdAt;

    // 모든 필드를 받는 생성자 (필요에 따라 추가)
    public UserKeyword(String userId, String keyword, String targetSite) {
        this.userId = userId;
        this.keyword = keyword;
        this.targetSite = targetSite;
    }
}
