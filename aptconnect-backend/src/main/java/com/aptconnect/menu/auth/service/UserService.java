package com.aptconnect.menu.auth.service;

import com.aptconnect.menu.invite.entity.InviteCode;
import com.aptconnect.menu.auth.entity.Role;
import com.aptconnect.menu.auth.entity.User;
import com.aptconnect.menu.invite.repository.InviteCodeRepository;
import com.aptconnect.menu.auth.repository.UserRepository;
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
    private final InviteCodeRepository inviteCodeRepository;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, InviteCodeRepository inviteCodeRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.inviteCodeRepository = inviteCodeRepository;
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("사용자 정보를 찾을 수 없습니다."));
    }

    public void registerUser(String email, String password, String name, String apartmentName,
                             String buildingNumber, String unitNumber, String inviteCodeStr) {
        InviteCode inviteCode = inviteCodeRepository.findByCode(inviteCodeStr)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 초대 코드입니다."));

        if (inviteCode.getExpireAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("초대 코드가 만료되었습니다.");
        }
        if (inviteCode.getRemainingUses() <= 0) {
            throw new IllegalArgumentException("초대 코드 사용 가능 횟수를 초과했습니다.");
        }

        // ✅ 사용자 등록
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setName(name);
        user.setRole(Role.USER);
        user.setUseYn("Y");
        user.setApartmentAccess("PENDING");
        user.setCreateUser(email);
        user.setCreateDate(LocalDateTime.now());
        user.setApartmentName(apartmentName);
        user.setBuildingNumber(buildingNumber);
        user.setUnitNumber(unitNumber);

        userRepository.save(user);

        // 사용자 등록 후 초대 코드 사용 횟수 차감
        inviteCode.incrementUsage();
        inviteCodeRepository.save(inviteCode);
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
