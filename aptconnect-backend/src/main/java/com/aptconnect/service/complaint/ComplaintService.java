package com.aptconnect.service.complaint;

import com.aptconnect.entity.complaint.Complaint;
import com.aptconnect.entity.User;
import com.aptconnect.entity.complaint.ComplaintStatus;
import com.aptconnect.repository.complaint.ComplaintRepository;
import com.aptconnect.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ComplaintService {
    private final ComplaintRepository complaintRepository;
    private final UserRepository userRepository;

    public Complaint saveComplaint(Complaint complaint) {
        return complaintRepository.save(complaint);
    }

    public List<Complaint> getAllComplaints() {
        return complaintRepository.findAll();
    }

    public List<Complaint> getComplaintsByApartment(String apartmentName) {
        return complaintRepository.findByApartmentName(apartmentName);
    }

    public List<Complaint> getComplaintsByStatus(String status) {
        return complaintRepository.findByStatus(status);
    }

    public List<Complaint> getComplaintsByUser(User currentUser) {
        return complaintRepository.findByCreatedBy(currentUser);
    }

    public Complaint getComplaintById(Long id) {
        return complaintRepository.findComplaintWithUserById(id)
                .orElseThrow(() -> new RuntimeException("민원을 찾을 수 없습니다."));
    }

    public void deleteComplaint(Long id) {
        complaintRepository.deleteById(id);
    }

    public void updateComplaint(Long id, Complaint updatedComplaint, User user) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("민원을 찾을 수 없습니다."));

        if (!complaint.getCreatedBy().equals(user)) {
            throw new SecurityException("해당 민원을 수정할 권한이 없습니다.");
        }

        complaint.setTitle(updatedComplaint.getTitle());
        complaint.setContent(updatedComplaint.getContent());
        complaint.setCategory(updatedComplaint.getCategory());
        complaint.setUpdatedAt(LocalDateTime.now());

        complaintRepository.save(complaint);
    }

    // 🔥 민원 상태 변경 메서드
    @Transactional
    public Complaint updateComplaintStatus(Long complaintId, String newStatus) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("🚨 민원을 찾을 수 없습니다."));

        if (!isValidStatus(newStatus)) {
            throw new IllegalArgumentException("🚨 유효하지 않은 상태 값입니다: " + newStatus);
        }

        complaint.setStatus(ComplaintStatus.valueOf(newStatus));
        complaintRepository.save(complaint);

        return complaint;
    }

    private boolean isValidStatus(String status) {
        return Arrays.stream(ComplaintStatus.values()).anyMatch(s -> s.name().equals(status));
    }
}