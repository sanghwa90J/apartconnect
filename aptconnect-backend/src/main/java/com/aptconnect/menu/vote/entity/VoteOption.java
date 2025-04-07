package com.aptconnect.menu.vote.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class VoteOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String optionText; // 선택지 내용

    @Column(nullable = false)
    private int voteCount = 0; // 해당 선택지의 투표 수

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poll_id", nullable = false)
    private VotePoll poll; // ✅ VotePoll과 관계를 형성하는 필드 추가
}
