package com.aptconnect.repository;

import com.aptconnect.entity.Complaint;
import com.aptconnect.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    List<Complaint> findByApartmentName(String apartmentName);
    List<Complaint> findByCreatedBy(User createdBy);

    List<Complaint> findByStatus(String status);
}
