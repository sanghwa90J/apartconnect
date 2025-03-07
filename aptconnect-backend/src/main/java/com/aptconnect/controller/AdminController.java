package com.aptconnect.controller;

import com.aptconnect.entity.Role;
import com.aptconnect.entity.User;
import com.aptconnect.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;

    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model) {
        // 현재 로그인한 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("사용자 없음"));

        model.addAttribute("user", user);
        model.addAttribute("title", "관리자 대시보드");

        return "admin-dashboard"; // admin-dashboard.html 템플릿 반환
    }

    // 가입 요청 목록 보기 (현재 관리자의 아파트 사용자만 조회, 본인 제외)
    @GetMapping("/admin/users")
    public String viewUsers(Model model, Authentication authentication) {
        User adminUser = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("관리자 정보 없음"));

        List<User> pendingUsers;
        List<User> allUsers;

        if (adminUser.getRole() == Role.MASTER) {
            // MASTER는 모든 사용자 조회 가능 (자기 자신 제외)
            pendingUsers = userRepository.findByEmailNotAndApartmentAccess("PENDING", adminUser.getEmail());
            allUsers = userRepository.findByEmailNot(adminUser.getEmail());

        } else {
            // ADMIN은 자기 아파트 사용자만 조회 가능 (자기 자신 제외)
            String apartmentName = adminUser.getApartmentName();
            pendingUsers = userRepository.findByApartmentNameAndEmailNotAndApartmentAccess(apartmentName, "PENDING", adminUser.getEmail());
            allUsers = userRepository.findByApartmentNameAndEmailNot(apartmentName, adminUser.getEmail());
        }

        model.addAttribute("pendingUsers", pendingUsers);
        model.addAttribute("allUsers", allUsers);
        return "admin-users";
    }

    // 회원가입 승인 처리
    @PostMapping("/admin/approve/{userId}")
    public String approveUser(@PathVariable Long userId, Authentication authentication) {
        User adminUser = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("관리자 정보 없음"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        // ADMIN이 자기 아파트의 사용자만 승인할 수 있도록 제한
        if (adminUser.getRole() != Role.MASTER && !adminUser.getApartmentName().equals(user.getApartmentName())) {
            throw new RuntimeException("다른 아파트 사용자는 승인할 수 없습니다.");
        }

        user.setApartmentAccess("APPROVED"); // ✅ 승인 처리
        userRepository.save(user);
        return "redirect:/admin/users";
    }

    // 회원가입 거절 처리 (사용자 삭제)
    @PostMapping("/admin/reject/{userId}")
    public String rejectUser(@PathVariable Long userId, Authentication authentication) {
        User adminUser = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("관리자 정보 없음"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        if (adminUser.getRole() != Role.MASTER && !adminUser.getApartmentName().equals(user.getApartmentName())) {
            throw new RuntimeException("다른 아파트 사용자는 삭제할 수 없습니다.");
        }

        userRepository.deleteById(userId);
        return "redirect:/admin/users";
    }

    // ROLE 변경 처리
    @PostMapping("/admin/update-role/{userId}")
    public String updateUserRole(@PathVariable Long userId, @RequestParam String newRole, Authentication authentication) {
        User adminUser = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("관리자 정보 없음"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        if (adminUser.getRole() != Role.MASTER && !adminUser.getApartmentName().equals(user.getApartmentName())) {
            throw new RuntimeException("다른 아파트 사용자의 권한을 변경할 수 없습니다.");
        }

        if (user.getRole() == Role.MASTER) {
            throw new RuntimeException("MASTER의 권한은 변경할 수 없습니다.");
        }

        user.setRole(Role.valueOf(newRole)); // 새로운 역할 설정
        userRepository.save(user);
        return "redirect:/admin/users";
    }

}
