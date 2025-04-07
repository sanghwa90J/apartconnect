package com.aptconnect.menu.master.errorLog.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "error_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String level;  // ERROR, WARN, INFO 등

    @Lob // 🔥 메시지를 긴 텍스트로 저장하도록 변경
    @Column(columnDefinition = "TEXT")
    private String message;
    private String exceptionType;

    @Lob // 🔥 메시지를 긴 텍스트로 저장하도록 변경
    @Column(columnDefinition = "TEXT")
    private String stackTrace;
    private String path; // 에러 발생 경로
    private String userAgent;
    private String ipAddress;
    private LocalDateTime timestamp;
}