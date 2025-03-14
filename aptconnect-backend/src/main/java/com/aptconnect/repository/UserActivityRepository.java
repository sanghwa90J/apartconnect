package com.aptconnect.repository;

import com.aptconnect.entity.UserActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

@Repository
public interface UserActivityRepository extends JpaRepository<UserActivity, Long> {
    // 특정 사용자 로그인 기록 조회
    List<UserActivity> findByUserIdOrderByLoginTimeDesc(Long userId);

    // 전체 로그인 기록 (페이징)
    Page<UserActivity> findAll(Pageable pageable);

    // 특정 IP에서 로그인한 사용자 조회
    List<UserActivity> findByIpAddress(String ipAddress);
}
