package com.aptconnect.menu.complaint.entity;

import com.aptconnect.menu.auth.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // 🔥 추가
public class ComplaintComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "complaint_id", nullable = false)
    private Complaint complaint; // 해당 댓글이 속한 민원

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 댓글 작성자

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content; // 댓글 내용

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now(); // 작성 시간
}
