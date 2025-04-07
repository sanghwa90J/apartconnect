package com.aptconnect.menu.complaint.entity;

public enum ComplaintStatus {
    PENDING,    // 대기 중 (기본값)
    IN_PROGRESS, // 처리 중
    RESOLVED,   // 해결됨
    REJECTED    // 거절됨
}
