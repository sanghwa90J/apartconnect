package com.aptconnect.menu.complaint.repository;

import com.aptconnect.menu.complaint.entity.ComplaintFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ComplaintFileRepository extends JpaRepository<ComplaintFile, Long> {
    List<ComplaintFile> findByComplaintId(Long complaintId);
}
