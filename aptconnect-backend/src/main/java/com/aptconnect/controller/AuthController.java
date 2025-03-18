package com.aptconnect.controller;

import com.aptconnect.entity.Apartment;
import com.aptconnect.entity.User;
import com.aptconnect.service.ApartmentService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.server.csrf.CsrfToken;
import org.springframework.ui.Model;
import com.aptconnect.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.List;


@Controller
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final ApartmentService apartmentService; // 🔥 아파트 서비스 추가
    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("title", "회원가입");
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String email,
                           @RequestParam String password,
                           @RequestParam String name,
                           @RequestParam Long apartmentId, // ✅ 변경: 아파트 ID로 받음
                           @RequestParam String buildingNumber,
                           @RequestParam String unitNumber,
                           HttpServletRequest request) {
        // ✅ 아파트 ID를 기반으로 아파트 이름 조회
        Apartment apartment = apartmentService.getApartmentById(apartmentId);

        if (apartment == null) {
            return "redirect:/register?error=invalidApartment"; // 🚨 존재하지 않는 아파트 예외 처리
        }

        // ✅ 회원가입 처리
        userService.registerUser(email, password, name, apartment.getName(), buildingNumber, unitNumber);

        return "redirect:/login"; // 🔥 로그인 후 대시보드로 이동
    }

    // 🔎 아파트 검색 API 추가
    @GetMapping("/register/search")
    @ResponseBody
    public List<Apartment> searchApartments(@RequestParam String query) {
        return apartmentService.searchApartments(query);
    }

    @GetMapping("/login")
    public String loginPage(HttpServletRequest request, Model model) {
        model.addAttribute("title", "로그인");
        CsrfToken csrfToken = (CsrfToken) request.getAttribute("_csrf");
        model.addAttribute("_csrf", csrfToken);  // 🔥 CSRF 토큰을 모델에 추가
        return "login";
    }
}
