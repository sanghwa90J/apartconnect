package com.aptconnect.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name="users")
public class User  implements UserDetails {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false, unique = true)
        private String email;

        @Column(nullable = false)
        private String password;

        @Column(nullable = false)
        private String name;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private Role role; // ROLE_USER, ROLE_ADMIN, ROLE_MASTER

        @Column(nullable = false, length = 1)
        private String useYn = "Y";  // Y: 사용중, N: 비활성화 [default N]

        @Column(nullable = false)
        private String apartmentAccess = "PENDING"; // ✅ 아파트 접근 권한 (PENDING, APPROVED)

        @CreationTimestamp
        @Column(updatable = false)
        private LocalDateTime createDate;  // 생성 날짜

        @Column(nullable = false)
        private String createUser;  // 생성한 사용자

        @UpdateTimestamp
        private LocalDateTime updateDate;  // 수정 날짜

        private String updateUser;  // 수정한 사용자

        // 🔥 아파트 정보 추가
        @Column(nullable = false)
        private String apartmentName;  // 아파트 이름 (예: A아파트)

        @Column(nullable = false)
        private String buildingNumber; // 동 번호 (예: 101동)

        @Column(nullable = false)
        private String unitNumber; // 호수 (예: 101호)

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return List.of(() -> role.name());
        }

        @Override
        public String getUsername() {
            return email;
        }

        @Override
        public boolean isAccountNonExpired() { return true; }

        @Override
        public boolean isAccountNonLocked() { return true; }

        @Override
        public boolean isCredentialsNonExpired() { return true; }

        @Override
        public boolean isEnabled() { return "Y".equals(useYn); }  // "N"이면 계정 비활성화

        @Override
        public boolean equals(Object obj) {
                if (this == obj) return true;
                if (obj == null || getClass() != obj.getClass()) return false;
                User user = (User) obj;
                return id != null && id.equals(user.id);
        }

        @Override
        public int hashCode() {
                return getClass().hashCode();
        }

}
