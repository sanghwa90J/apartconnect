package com.aptconnect.entity.announcement;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "announcement")
@Getter
@Setter
@NoArgsConstructor
public class Announcement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING) // ENUM 저장
    @Column(nullable = false, length = 20)
    private AnnouncementType type; // 공지 유형 (전체, 특정 아파트) GENERAL 또는 LOCAL (소속 구분)

    @Enumerated(EnumType.STRING)
    private AnnouncementPriority priority;  // NORMAL, IMPORTANT, MAINTENANCE (중요도 구분)

    @Column(nullable = false, length = 255)
    private String title; // 공지 제목

    @Lob
    @Column(nullable = false)
    private String content; // 공지 내용

    @Column(nullable = false, length = 100)
    private String author; // 작성자 (관리자 이름 or Master)

    @Column(length = 100)
    private String apartmentName; // 특정 아파트 공지 (전체 공지일 경우 NULL)

    @Column(nullable = false)
    private LocalDateTime createdAt; // 생성일시

    @Column
    private LocalDateTime updatedAt; // 수정일시

    @Column(nullable = false)
    private boolean deleted = false; // 소프트 삭제

    // 공지 생성 시 기본 값 설정
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    // 공지 수정 시 updatedAt 업데이트
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
