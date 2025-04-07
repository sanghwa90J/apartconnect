package com.aptconnect.menu.invite.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InviteCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;  // 실제 사용할 초대 코드 문자열

    @Column(nullable = false)
    private String apartmentName; // 어떤 아파트용 초대 코드인지

    @Column(nullable = false)
    private String buildingNumber; // 동

    @Column(nullable = false)
    private String unitNumber; // 호수

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime expireAt; // 유효기간 (예: 생성일 + 7일 등)

    @Column(nullable = false)
    private int maxUsageCount = 1; // 최대 사용 가능 횟수 (기본 1회)

    @Column(nullable = false)
    private int usedCount = 0; // 현재 사용된 횟수

    @Column(nullable = false, length = 1)
    private String useYn = "Y"; // Y: 사용가능, N: 사용중지

    public boolean isValid() {
        return "Y".equals(useYn)
                && LocalDateTime.now().isBefore(expireAt)
                && usedCount < maxUsageCount;
    }

    public void incrementUsage() {
        this.usedCount++;
        if (usedCount >= maxUsageCount) {
            this.useYn = "N";
        }
    }

    public int getRemainingUses() {
        return maxUsageCount - usedCount;
    }

}