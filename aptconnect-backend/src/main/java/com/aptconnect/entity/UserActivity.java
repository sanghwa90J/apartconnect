package com.aptconnect.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_activity")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 사용자 정보

    @Column(nullable = false)
    private LocalDateTime loginTime; // 로그인 시간

    @Column(nullable = false, length = 45)
    private String ipAddress; // IP 주소

    @Column(nullable = false)
    private int loginCount; // 접속 횟수 (누적)
}
