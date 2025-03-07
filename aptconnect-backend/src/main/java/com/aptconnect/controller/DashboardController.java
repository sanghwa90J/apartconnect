package com.aptconnect.controller;

import com.aptconnect.entity.User;
import com.aptconnect.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

@Controller
@RequiredArgsConstructor
public class DashboardController {
    private final UserRepository userRepository;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // 현재 로그인한 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("사용자 없음"));

        model.addAttribute("user", user);
        model.addAttribute("title", "대시보드");

        if (user.getRole().name().equals("ROLE_ADMIN")) {
            return "admin-dashboard"; // 관리사무소 대시보드
        }
        return "user-dashboard"; // 입주민 대시보드
    }
}
