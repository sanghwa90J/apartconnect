package com.aptconnect.menu.auth.service;

import com.aptconnect.menu.auth.entity.User;
import com.aptconnect.menu.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email));

        if ("N".equals(user.getUseYn())) {
            throw new DisabledException("관리자의 승인이 필요합니다.");
        }

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())  // 🔥 email 사용
                .password(user.getPassword())  // 🔥 비밀번호 사용
                .roles(user.getRole().name())  // 🔥 권한 정보
                .build();
    }
}