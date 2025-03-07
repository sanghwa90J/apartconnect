package com.aptconnect.controller;

import com.aptconnect.entity.Role;
import com.aptconnect.entity.User;
import com.aptconnect.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/master")
@PreAuthorize("hasRole('MASTER')") // 🔥 MASTER만 접근 가능
public class MasterController {

    private final UserRepository userRepository;

    @GetMapping("/dashboard")
    public String masterDashboard(Model model) {
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "master-dashboard";
    }

    @PostMapping("/assign-admin")
    public String assignAdmin(@RequestParam Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("사용자 없음"));
        user.setRole(Role.ADMIN); // 🔥 ADMIN 권한 부여
        userRepository.save(user);
        return "redirect:/master/dashboard";
    }
}