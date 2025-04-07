package com.aptconnect.menu.community.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import com.aptconnect.menu.auth.entity.User;
import com.aptconnect.menu.community.entity.Report;
import com.aptconnect.menu.community.entity.ReportStatus;
import com.aptconnect.menu.community.entity.ReportType;
import com.aptconnect.menu.auth.service.UserService;
import com.aptconnect.menu.community.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@Controller
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;
    private final UserService userService;

    @GetMapping
    public String viewReports(Model model, Authentication authentication) {
        User currentUser = userService.getUserByEmail(authentication.getName());
        String role = currentUser.getRole().name();

        List<Report> reports;

        if ("MASTER".equals(role)) {
            reports = reportService.getAllReports();
        } else if ("ADMIN".equals(role)) {
            reports = reportService.getReportsByApartment(currentUser.getApartmentName());
        } else {
            throw new SecurityException("접근 권한이 없습니다.");
        }

        model.addAttribute("reports", reports);
        model.addAttribute("currentPage", "reports");
        model.addAttribute("currentRole", "ROLE_" + role);
        return "/report/report-list";
    }

    @GetMapping("/{targetId}")
    public String viewReportDetail(@PathVariable Long targetId, Model model, Authentication authentication) {
        User currentUser = userService.getUserByEmail(authentication.getName());
        Report report = reportService.getReportById(targetId);

        if ("ADMIN".equals(currentUser.getRole().name()) &&
                !currentUser.getApartmentName().equals(report.getReporter().getApartmentName())) {
            throw new SecurityException("접근 권한이 없습니다.");
        }

        model.addAttribute("report", report);
        model.addAttribute("currentPage", "reports");
        model.addAttribute("currentRole", "ROLE_" + currentUser.getRole().name());
        return "/report/report-detail";
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> submitReport(@RequestParam("type") ReportType type,
                                                            @RequestParam("targetId") Long targetId,
                                                            @RequestParam("reason") String reason,
                                                            Authentication authentication) {
        User reporter = userService.getUserByEmail(authentication.getName());
        boolean success = reportService.submitReport(reporter, targetId, type, reason);

        if (!success) {
            return ResponseEntity.ok(Map.of(
                    "status", "duplicate",
                    "message", "이미 신고한 항목입니다."
            ));
        }

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "신고가 정상적으로 접수되었습니다."
        ));
    }

    @PatchMapping("/{targetId}/status")
    public String updateStatus(@PathVariable Long targetId,
                                               @RequestParam("status") ReportStatus status) {
        reportService.updateReportStatus(targetId, status);
        return "redirect:/reports/" + targetId;
    }
}
