package com.aptconnect.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.server.csrf.CsrfToken;
import org.springframework.ui.Model;
import com.aptconnect.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("title", "회원가입");
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String email,
                           @RequestParam String password,
                           @RequestParam String name,
                           @RequestParam String apartmentName,
                           @RequestParam String buildingNumber,
                           @RequestParam String unitNumber,
                           HttpServletRequest request) {
        String createUser = email; // 생성자는 본인 이메일로 설정
        userService.registerUser(email, password, name, apartmentName, buildingNumber, unitNumber, createUser);

        return "redirect:/login?pending"; // 🔥 승인 대기 안내 페이지로 리디렉트
    }

    @GetMapping("/login")
    public String loginPage(HttpServletRequest request, Model model) {
        model.addAttribute("title", "로그인");
        CsrfToken csrfToken = (CsrfToken) request.getAttribute("_csrf");
        model.addAttribute("_csrf", csrfToken);  // 🔥 CSRF 토큰을 모델에 추가
        return "login";
    }
}
