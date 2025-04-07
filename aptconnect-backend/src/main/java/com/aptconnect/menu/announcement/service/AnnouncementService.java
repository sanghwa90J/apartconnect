package com.aptconnect.menu.announcement.service;

import com.aptconnect.menu.announcement.entity.Announcement;
import com.aptconnect.menu.announcement.repository.AnnouncementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnnouncementService {
    private final AnnouncementRepository announcementRepository;

    // 공지 저장
    public void saveAnnouncement(Announcement announcement) {
        announcementRepository.save(announcement);
    }

    // 공지 수정
    public Announcement updateAnnouncement(Long id, String title, String content) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("공지사항을 찾을 수 없습니다."));
        announcement.setTitle(title);
        announcement.setContent(content);
        return announcementRepository.save(announcement);
    }

    // 공지 삭제 (소프트 삭제)
    public void deleteAnnouncement(Long id) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("공지사항을 찾을 수 없습니다."));
        announcement.setDeleted(true);
        announcementRepository.save(announcement);
    }

    // 전체 공지 조회
    public List<Announcement> getAllAnnouncements() {
        return announcementRepository.findByDeletedFalseOrderByCreatedAtDesc();
    }

    // 특정 아파트 공지만 조회
    public List<Announcement> getAnnouncementsByApartment(String apartmentName) {
        return announcementRepository.findByApartmentNameAndDeletedFalseOrderByCreatedAtDesc(apartmentName);
    }

    public Announcement getAnnouncementById(Long id) {
        return announcementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("공지사항을 찾을 수 없습니다."));
    }
}
