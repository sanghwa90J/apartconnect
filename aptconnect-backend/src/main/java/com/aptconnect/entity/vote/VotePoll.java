package com.aptconnect.entity.vote;

import com.aptconnect.entity.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class VotePoll {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title; // 투표 제목

    @Column(columnDefinition = "TEXT")
    private String description; // 투표 설명

    @Column(nullable = false)
    private LocalDateTime startDate; // 투표 시작일

    @Column(nullable = false)
    private LocalDateTime endDate; // 투표 마감일

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VoteStatus status = VoteStatus.ONGOING; // ✅ 상태 추가

    @OneToMany(mappedBy = "poll", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VoteOption> options = new ArrayList<>(); // ✅ 초기화 추가 (NPE 방지)

    @OneToMany(mappedBy = "poll", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VoteRecord> records= new ArrayList<>(); // ✅ 초기화 추가 (NPE 방지)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Column(nullable = false)
    private String apartmentName; // ✅ 추가: 투표가 속한 아파트
}