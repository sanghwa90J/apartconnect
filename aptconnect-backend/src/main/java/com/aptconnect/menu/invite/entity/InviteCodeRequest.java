package com.aptconnect.menu.invite.entity;

import com.aptconnect.menu.apartment.entity.Apartment;
import com.aptconnect.menu.auth.entity.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InviteCodeRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 요청한 아파트
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "apartment_id")
    private Apartment apartment;

    // 요청한 사용자 (ADMIN)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id")
    private User requester;

    private LocalDateTime expiryDate;

    private Integer usageLimit;

    private String reason;

    @Enumerated(EnumType.STRING)
    private InviteRequestStatus status; // PENDING / APPROVED / REJECTED

    private LocalDateTime createDate;

    private LocalDateTime approveDate;

    public enum InviteRequestStatus {
        PENDING, APPROVED, REJECTED
    }
}