package com.aptconnect.menu.community.entity;

import com.aptconnect.menu.auth.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ReportType type; // POST or COMMENT

    private Long targetId; // 신고 대상 ID

    private String reason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id")
    private User reporter;

    private LocalDateTime createdAt = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportStatus status = ReportStatus.PENDING;

    public String getTargetLink() {
        if (this.type == ReportType.POST) {
            return "/community/" + targetId;
        } else if (this.type == ReportType.COMMENT) {
            return "/community/comments/" + targetId;
        }
        return "#";
    }
}