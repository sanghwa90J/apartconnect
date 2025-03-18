package com.aptconnect.repository;

import com.aptconnect.entity.announcement.Complaint;
import com.aptconnect.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    List<Complaint> findByApartmentName(String apartmentName);
    List<Complaint> findByCreatedBy(User createdBy);

    List<Complaint> findByStatus(String status);

    @Query("SELECT c FROM Complaint c JOIN FETCH c.createdBy WHERE c.id = :id")
    Optional<Complaint> findComplaintWithUserById(@Param("id") Long id);
}
