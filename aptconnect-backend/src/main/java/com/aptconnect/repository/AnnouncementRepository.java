package com.aptconnect.repository;

import com.aptconnect.entity.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    // 전체 공지 조회 (삭제되지 않은 것만)
    List<Announcement> findByDeletedFalseOrderByCreatedAtDesc();

    // 특정 아파트 공지만 조회
    List<Announcement> findByApartmentNameAndDeletedFalseOrderByCreatedAtDesc(String apartmentName);
}
