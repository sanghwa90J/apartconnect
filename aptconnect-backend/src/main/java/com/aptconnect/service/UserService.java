package com.aptconnect.service;

import com.aptconnect.entity.Role;
import com.aptconnect.entity.User;
import com.aptconnect.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void registerUser(String email, String password, String name, String apartmentName,
                             String buildingNumber, String unitNumber, String createUser) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setName(name);
        user.setRole(Role.USER);
        user.setUseYn("Y");
        user.setApartmentAccess("PENDING"); // ✅ 기본적으로 아파트 접근 불가
        user.setCreateUser(createUser);
        user.setCreateDate(LocalDateTime.now());

        // 🔥 아파트 정보 저장
        user.setApartmentName(apartmentName);
        user.setBuildingNumber(buildingNumber);
        user.setUnitNumber(unitNumber);


        userRepository.save(user);
    }

    public void updateUser(Long id, String name, String updateUser) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("사용자 없음"));
        user.setName(name);
        user.setUpdateUser(updateUser);
        user.setUpdateDate(LocalDateTime.now());

        userRepository.save(user);
    }
}
