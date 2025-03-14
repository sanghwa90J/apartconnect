package com.aptconnect.component;

import com.aptconnect.entity.User;
import com.aptconnect.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthenticationFacade {

    private final UserRepository userRepository;

    /**
     * 현재 로그인한 사용자의 이메일 가져오기
     */
    public String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername(); // Spring Security에서 username은 이메일로 설정됨
        }
        return null;
    }

    /**
     * 현재 로그인한 사용자의 Role 가져오기
     */
    public String getCurrentUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getAuthorities() != null) {
            return authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .findFirst().orElse("ROLE_USER"); // 기본값은 USER
        }
        return "ROLE_USER";
    }

    /**
     * 현재 로그인한 사용자의 아파트 이름 가져오기
     */
    public String getCurrentUserApartment() {
        String email = getCurrentUserEmail();
        if (email != null) {
            User user = userRepository.findByEmail(email).orElse(null);
            if (user != null) {
                return user.getApartmentName(); // User 엔티티에 apartmentName 필드가 있어야 함
            }
        }
        return null;
    }
}