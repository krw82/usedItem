package com.example.usedItem.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 기본 생성자 접근 제한
@Entity
@Table(name = "USERS") // 실제 테이블 이름 지정
public class User {

    @Id
    // Oracle 12c 이상 IDENTITY 컬럼 사용 시
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // Oracle 11g 이하 또는 시퀀스 사용 시
    // @GeneratedValue(strategy = GenerationType.SEQUENCE, generator =
    // "user_seq_generator")
    // @SequenceGenerator(name = "user_seq_generator", sequenceName = "USER_ID_SEQ",
    // allocationSize = 1)
    @Column(name = "user_id")
    private Long id;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(nullable = false) // 실제로는 해시된 비밀번호 저장
    private String password;

    @Column(unique = true, nullable = false, length = 50)
    private String nickname;

    @Enumerated(EnumType.STRING) // Enum 이름을 DB에 문자열로 저장
    @Column(name = "user_role", nullable = false, length = 20)
    private UserRole role = UserRole.ROLE_USER; // 기본값 설정

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserStatus status = UserStatus.ACTIVE; // 기본값 설정

    @CreationTimestamp // JPA가 INSERT 시 자동 생성 시간 관리
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp // JPA가 UPDATE 시 자동 수정 시간 관리
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // User(1) : Keyword(N) 관계
    // mappedBy: Keyword 엔티티의 user 필드에 의해 매핑됨을 명시
    // cascade: User 저장/삭제 시 Keyword도 함께 처리
    // orphanRemoval: User에서 Keyword를 제거하면 DB에서도 삭제
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Keyword> keywords = new ArrayList<>();

    // User(1) : Notification(N) 관계 (보통 User 삭제 시 Notification은 남겨둠)
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Notification> notifications = new ArrayList<>();

    // 빌더 패턴 사용 (Lombok @Builder 활용)
    @Builder
    public User(String email, String password, String nickname, UserRole role, UserStatus status) {
        this.email = email;
        this.password = password; // 실제로는 서비스 레이어에서 해싱 필요
        this.nickname = nickname;
        this.role = (role != null) ? role : UserRole.ROLE_USER;
        this.status = (status != null) ? status : UserStatus.ACTIVE;
    }

    // 연관관계 편의 메소드 (양방향 관계 시 필요)
    public void addKeyword(Keyword keyword) {
        this.keywords.add(keyword);
        if (keyword.getUser() != this) { // 무한 루프 방지
            keyword.setUser(this);
        }
    }
}
