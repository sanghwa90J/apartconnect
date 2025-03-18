package com.aptconnect.service;

import com.aptconnect.entity.Role;
import com.aptconnect.entity.User;
import com.aptconnect.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("사용자 정보를 찾을 수 없습니다."));
    }

    public void registerUser(String email, String password, String name, String apartmentName,
                             String buildingNumber, String unitNumber) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setName(name);
        user.setRole(Role.USER);
        user.setUseYn("Y");
        user.setApartmentAccess("PENDING"); // ✅ 기본적으로 아파트 접근 불가
        user.setCreateUser(email);
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

    public Map<String, Long> getUserStatus() {
        Map<String, Long> userStatus = new LinkedHashMap<>();
        userStatus.put("MASTER", userRepository.countByRole(Role.MASTER));
        userStatus.put("ADMIN", userRepository.countByRole(Role.ADMIN));
        userStatus.put("USER", userRepository.countByRole(Role.USER));

        return userStatus;
    }

    public Map<String, Long> getMonthlyUserRegistration() {
        Map<String, Long> monthlyData = new LinkedHashMap<>();
        LocalDate now = LocalDate.now();

        for (int i = 5; i >= 0; i--) {
            YearMonth yearMonth = YearMonth.from(now.minusMonths(i));
            long count = userRepository.countByRegistrationMonth(yearMonth.getYear(), yearMonth.getMonthValue());
            monthlyData.put(yearMonth.toString(), count);
        }

        return monthlyData;
    }
}
