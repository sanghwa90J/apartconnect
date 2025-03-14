package com.aptconnect.config;

import com.aptconnect.entity.User;
import com.aptconnect.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice // 🔥 모든 컨트롤러에서 공통적으로 실행됨
@RequiredArgsConstructor
public class GlobalControllerAdvice {
    private final UserRepository userRepository;

    @ModelAttribute("currentUser") // ✅ 모든 컨트롤러에서 자동으로 사용 가능
    public User getCurrentUser(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return userRepository.findByEmail(authentication.getName()).orElse(null);
        }
        return null;
    }
}