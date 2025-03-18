package com.aptconnect.controller;

import com.aptconnect.entity.Role;
import com.aptconnect.entity.User;
import com.aptconnect.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
@PreAuthorize("hasRole('ROLE_ADMIN')")  // 🔥 ADMIN만 접근 가능
public class AdminController {
    private final UserRepository userRepository;

    @GetMapping("/dashboard")
    public String adminDashboard(Model model, Authentication authentication) {
        User adminUser = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        model.addAttribute("currentPage", "dashboard"); // 🎯 현재 페이지 정보 추가

        return "/admin/dashboard";
    }

    // 🔥 입주민 관리 페이지
    @GetMapping("/users")
    public String viewUsers(@RequestParam(required = false) String search, Model model, @ModelAttribute("currentUser") User adminUser) {
        String apartmentName = adminUser.getApartmentName();  // ✅ ADMIN이 관리하는 아파트 이름 가져오기

        // 🔥 같은 아파트 소속 유저 중 PENDING 또는 APPROVED 상태인 유저 조회 (MASTER 제외)
        List<User> users = userRepository.findByApartmentNameAndApartmentAccessIn(apartmentName, List.of("PENDING", "APPROVED"))
                .stream()
                .filter(user -> user.getRole() != Role.MASTER)  // MASTER 계정 제외
                .toList();

        // 🔥 ADMIN 권한을 가진 유저만 별도로 필터링
        List<User> adminUsers = users.stream()
                .filter(user -> user.getRole() == Role.ADMIN)
                .collect(Collectors.toList());

        // 🔥 일반 입주민 유저 (ADMIN 제외)
        List<User> residentUsers = users.stream()
                .filter(user -> user.getRole() != Role.ADMIN)
                .collect(Collectors.toList());

        // 🔎 검색 기능 (이름 or 이메일 포함)
        if (search != null && !search.isEmpty()) {
            residentUsers = residentUsers.stream()
                    .filter(user -> user.getName().contains(search) || user.getEmail().contains(search))
                    .toList();
        }

        model.addAttribute("adminUsers", adminUsers);  // ADMIN 리스트
        model.addAttribute("users", residentUsers);   // 일반 입주민 리스트
        model.addAttribute("searchKeyword", search);
        model.addAttribute("currentPage", "users");

        return "/admin/users";
    }

    @PostMapping("/users/update-access")
    public String updateApartmentAccess(@RequestParam Long userId, @RequestParam String newAccess, @ModelAttribute("currentUser") User adminUser) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        // ✅ ADMIN이 관리하는 아파트 입주민만 변경 가능
        if (!user.getApartmentName().equals(adminUser.getApartmentName())) {
            throw new SecurityException("해당 아파트 소속이 아닌 사용자는 관리할 수 없습니다.");
        }

        if (!newAccess.equals("PENDING") && !newAccess.equals("APPROVED")) {
            throw new IllegalArgumentException("잘못된 접근 상태 값");
        }

        user.setApartmentAccess(newAccess); // ✅ 상태 변경
        userRepository.save(user);
        return "redirect:/admin/users";
    }
}
