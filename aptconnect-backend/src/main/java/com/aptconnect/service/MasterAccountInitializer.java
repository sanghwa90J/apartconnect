package com.aptconnect.service;

import com.aptconnect.entity.Role;
import com.aptconnect.entity.User;
import com.aptconnect.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MasterAccountInitializer {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Value("${master.email}")
    private String masterEmail;

    @Value("${master.password}")
    private String masterPassword;

    @Value("${master.name}")
    private String masterName;

    @PostConstruct
    public void createMasterAccount() {
        Optional<User> existingMaster = userRepository.findByEmail(masterEmail);

        if (existingMaster.isEmpty()) {
            User master = new User();
            master.setEmail(masterEmail);
            master.setPassword(passwordEncoder.encode(masterPassword)); // 🔥 비밀번호 암호화
            master.setName(masterName);
            master.setRole(Role.MASTER);
            master.setUseYn("Y");
            master.setCreateUser("SYSTEM");
//            master.setApartmentName("MASTER");
//            master.setBuildingNumber("MASTER");
//            master.setUnitNumber("MASTER");
            master.setApartmentAccess("APPROVED");

            userRepository.save(master);
            System.out.println("🔥 MASTER 계정 생성 완료: " + masterEmail);
        } else {
            System.out.println("✅ MASTER 계정이 이미 존재함: " + masterEmail);
        }
    }}
