package com.aptconnect.service;

import com.aptconnect.entity.Complaint;
import com.aptconnect.entity.User;
import com.aptconnect.repository.ComplaintRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ComplaintService {
    private final ComplaintRepository complaintRepository;

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

    public Optional<Complaint> getComplaintById(Long id) {
        return complaintRepository.findById(id);
    }

    public void deleteComplaint(Long id) {
        complaintRepository.deleteById(id);
    }

    public void updateComplaint(Long id, Complaint updatedComplaint, User currentUser) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("민원을 찾을 수 없습니다."));

        if (!complaint.getCreatedBy().equals(currentUser)) {
            throw new SecurityException("해당 민원을 수정할 권한이 없습니다.");
        }

        complaint.setTitle(updatedComplaint.getTitle());
        complaint.setContent(updatedComplaint.getContent());
        complaint.setCategory(updatedComplaint.getCategory());
        complaint.setUpdatedAt(LocalDateTime.now());

        complaintRepository.save(complaint);
    }

}