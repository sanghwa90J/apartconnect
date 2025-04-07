package com.aptconnect.menu.announcement.controller;

import com.aptconnect.component.AuthenticationFacade;
import com.aptconnect.menu.announcement.entity.Announcement;
import com.aptconnect.menu.announcement.entity.AnnouncementType;
import com.aptconnect.menu.auth.entity.User;
import com.aptconnect.menu.announcement.service.AnnouncementService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/announcements")
@RequiredArgsConstructor
public class AnnouncementController {
    private final AnnouncementService announcementService;
    private final AuthenticationFacade authenticationFacade; // 로그인한 사용자 정보 가져오기

    // 공지 목록 조회 (Master: 전체 / Admin: 자기 아파트 공지만)
    @GetMapping
    public String viewAnnouncements(@ModelAttribute("currentUser") User currentUser,Model model) {
        String currentRole = authenticationFacade.getCurrentUserRole();
        String apartmentName = authenticationFacade.getCurrentUserApartment();

        if(!currentRole.contains("ROLE_")){
            System.out.println("TEST-POINT : " + currentRole);
            currentRole += "ROLE_" + currentRole;
        }

        List<Announcement> announcements;
        if ("ROLE_MASTER".equals(currentRole)) {
            announcements = announcementService.getAllAnnouncements(); // 전체 공지 조회
        } else {
            announcements = announcementService.getAnnouncementsByApartment(apartmentName); // 해당 아파트 공지만 조회
        }

        model.addAttribute("currentPage", "announcements"); // 🎯 현재 페이지 정보 추가
        model.addAttribute("announcements", announcements);
        model.addAttribute("currentRole", currentRole);
        model.addAttribute("currentUser", currentUser);
        return "/announcements/announcements"; // 공통 템플릿 사용
    }

    // 공지 생성 폼 (Master & Admin)
    @GetMapping("/create")
    public String createAnnouncementForm(Model model, @ModelAttribute("currentUser") User currentUser) {
        String currentRole = authenticationFacade.getCurrentUserRole();

        Announcement announcement = new Announcement();

        if ("ROLE_ADMIN".equals(currentRole)) {
            // ✅ ADMIN은 아파트 공지(LOCAL)로 기본 설정
            announcement.setType(AnnouncementType.LOCAL);
            announcement.setApartmentName(currentUser.getApartmentName()); // 소속 아파트 자동 설정
        }

        model.addAttribute("currentRole", currentRole);
        model.addAttribute("announcement", announcement);

        return "/announcements/announcement-form";
    }

    // 공지 저장 (Master는 전체 공지, Admin은 특정 아파트 공지)
    @PostMapping("/create")
    public String createAnnouncement(@ModelAttribute Announcement announcement) {
        String username = authenticationFacade.getCurrentUserEmail();
        announcement.setAuthor(username);
        announcementService.saveAnnouncement(announcement);
        return "redirect:/announcements"; // 저장 후 목록으로 이동
    }


    // 공지 삭제 (소프트 삭제)
    @PostMapping("/delete/{id}")
    public String deleteAnnouncement(@PathVariable Long id) {
        String currentRole = authenticationFacade.getCurrentUserRole();

        if (!"ROLE_MASTER".equals(currentRole) && !"ROLE_ADMIN".equals(currentRole)) {
            throw new AccessDeniedException("공지사항 삭제 권한이 없습니다.");
        }

        announcementService.deleteAnnouncement(id);
        return "redirect:/announcements";
    }

    @GetMapping("/{id}")
    public String viewAnnouncementDetail(@PathVariable Long id, Model model, @ModelAttribute("currentUser") User currentUser) {

        model.addAttribute("announcement", announcementService.getAnnouncementById(id));
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("currentRole", authenticationFacade.getCurrentUserRole());
        model.addAttribute("currentPage", "announcements");
        return "/announcements/announcement-detail";
    }

    // 공지 수정 폼
    @GetMapping("/edit/{id}")
    public String editAnnouncementForm(@PathVariable Long id, Model model) {
        Announcement announcement = announcementService.getAnnouncementById(id);
        model.addAttribute("announcement", announcement);
        model.addAttribute("currentRole", authenticationFacade.getCurrentUserRole());
        return "/announcements/announcement-form"; // 공통 폼 재사용
    }

    // 공지 수정 처리
    @PostMapping("/edit/{id}")
    public String updateAnnouncement(@PathVariable Long id, @ModelAttribute Announcement updated) {
        announcementService.updateAnnouncement(id, updated.getTitle(), updated.getContent());
        return "redirect:/announcements/" + id;
    }
}
