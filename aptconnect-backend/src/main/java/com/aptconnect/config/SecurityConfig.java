package com.aptconnect.config;

import com.aptconnect.component.CustomAuthenticationSuccessHandler;
import com.aptconnect.entity.User;
import com.aptconnect.repository.UserRepository;
import com.aptconnect.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final CustomUserDetailsService userDetailsService;  // 🔥 필드 자동 주입
    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler; // ✅ 추가
    private final UserRepository userRepository;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)  // CSRF 비활성화 (테스트 용도)
                .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/",  "/register/**", "/login", "/error").permitAll()  // 🔥 `/register` 허용 추가
                        .requestMatchers("/dashboard").access((authentication, context) -> {
                            User user = userRepository.findByEmail(authentication.get().getName()).orElseThrow();
                            return user.getApartmentAccess().equals("APPROVED") ? new AuthorizationDecision(true) : new AuthorizationDecision(false);
                        })
                    .requestMatchers("/admin/**").hasAnyRole("ADMIN", "MASTER")  // 🔥 관리사무소 전용 경로
                    .requestMatchers("/master/**").hasRole("MASTER") // 🔥 MASTER만 접근 가능
                    .anyRequest().authenticated()
                )
                .formLogin(login -> login
                    .loginPage("/login")
                    .loginProcessingUrl("/login")
                    .usernameParameter("email")  // 🔥 'username' 대신 'email' 사용
                    .passwordParameter("password")
                        .successHandler(customAuthenticationSuccessHandler) // ✅ 로그인 성공 후 핸들러 적용
                    .permitAll()
                )
                .logout(logout -> logout
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/")
                    .permitAll()
                );

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }


}
