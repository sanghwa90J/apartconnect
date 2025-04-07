package com.aptconnect.menu.community.service;

import com.aptconnect.menu.auth.entity.User;
import com.aptconnect.menu.community.entity.Report;
import com.aptconnect.menu.community.entity.ReportStatus;
import com.aptconnect.menu.community.entity.ReportType;
import com.aptconnect.menu.community.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;

    public boolean submitReport(User reporter, Long targetId, ReportType type, String reason) {
        if (reportRepository.existsByReporterAndTargetIdAndType(reporter, targetId, type)) {
            return false; // 중복 신고 방지
        }

        Report report = new Report();
        report.setReporter(reporter);
        report.setTargetId(targetId);
        report.setType(type);
        report.setReason(reason);
        report.setCreatedAt(LocalDateTime.now());

        reportRepository.save(report);
        return true;
    }

    public List<Report> getAllReports() {
        return reportRepository.findAll();
    }

    public List<Report> getReportsByStatus(ReportStatus status) {
        return reportRepository.findByStatus(status);
    }

    public void updateReportStatus(Long reportId, ReportStatus status) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("신고 내역을 찾을 수 없습니다."));
        report.setStatus(status);
        reportRepository.save(report);
    }

    public Report getReportById(Long id) {
        return reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("신고 정보를 찾을 수 없습니다."));
    }
    
    public List<Report> getReportsByApartment(String apartmentName) {
        return reportRepository.findAll().stream()
                .filter(report -> apartmentName.equals(report.getReporter().getApartmentName()))
                .toList();
    }
}