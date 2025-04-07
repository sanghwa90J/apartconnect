package com.aptconnect.component;

import com.aptconnect.menu.auth.entity.User;
import com.aptconnect.menu.master.userActivity.entity.UserActivity;
import com.aptconnect.menu.master.userActivity.repository.UserActivityRepository;
import com.aptconnect.menu.auth.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collection;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    @Autowired
    private UserActivityRepository userActivityRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        String redirectUrl = "/dashboard"; // 기본값

        for (GrantedAuthority authority : authorities) {
            String role = authority.getAuthority();
            if (role.equals("ROLE_MASTER")){
                redirectUrl = "/master/dashboard";
                break; // MASTER이면 바로 리디렉트 결정

            } else if( role.equals("ROLE_ADMIN")) {
                redirectUrl = "/admin/dashboard";
                break; // ADMIN이면 바로 리디렉트 결정

            } else if( role.equals("ROLE_USER")) {
                redirectUrl = "/announcements";
                break; // ADMIN이면 바로 리디렉트 결정
            }
        }

        // ✅ 사용자 로그인 기록 저장 추가
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElse(null);

        if (user != null) {
            String ipAddress = request.getRemoteAddr();
            String userAgent = request.getHeader("User-Agent");

            // 기존 로그인 기록 조회
            UserActivity lastActivity = userActivityRepository.findByUserIdOrderByLoginTimeDesc(user.getId())
                    .stream().findFirst().orElse(null);
            int loginCount = (lastActivity != null) ? lastActivity.getLoginCount() + 1 : 1;

            // 로그인 기록 저장
            UserActivity activity = new UserActivity();
            activity.setUser(user);
            activity.setLoginTime(LocalDateTime.now());
            activity.setIpAddress(ipAddress);
            activity.setLoginCount(loginCount);

            userActivityRepository.save(activity);
        }

        response.sendRedirect(redirectUrl);
    }
}
