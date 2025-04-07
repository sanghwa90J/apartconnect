package com.aptconnect.menu.complaint.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "complaint_files")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComplaintFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;   // 원본 파일명
    private String filePath;   // 저장된 파일 경로
    private String fileType;   // 파일 유형 (image/jpeg, application/pdf 등)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "complaint_id")
    private Complaint complaint;
}