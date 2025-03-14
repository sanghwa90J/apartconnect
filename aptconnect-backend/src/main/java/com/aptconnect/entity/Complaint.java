package com.aptconnect.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Complaint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title; // 민원 제목

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content; // 민원 내용

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ComplaintCategory category; // 민원 카테고리

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ComplaintStatus status = ComplaintStatus.PENDING; // 기본값: 대기 중

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User createdBy; // 작성자 (익명이라도 DB에는 저장됨)

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now(); // 작성일

    @Column
    private LocalDateTime updatedAt; // 마지막 수정일

    @Column(nullable = false)
    private String apartmentName; // 아파트 이름 (입주민의 소속 아파트)

    @Column(nullable = false)
    private boolean isAnonymous = false; // 익명 여부
}