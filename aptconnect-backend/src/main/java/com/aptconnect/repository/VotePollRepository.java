package com.aptconnect.repository;

import com.aptconnect.entity.vote.VotePoll;
import com.aptconnect.entity.vote.VoteStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VotePollRepository extends JpaRepository<VotePoll, Long> {
    // ✅ 모든 투표 조회 (MASTER 전용, 옵션 포함)
    @Query("SELECT DISTINCT v FROM VotePoll v LEFT JOIN FETCH v.options")
    List<VotePoll> findAllWithOptions();

    // ✅ 특정 아파트의 투표 조회 (ADMIN, USER 전용, 옵션 포함)
    @Query("SELECT DISTINCT v FROM VotePoll v LEFT JOIN FETCH v.options WHERE v.apartmentName = :apartmentName")
    List<VotePoll> findByApartmentWithOptions(@Param("apartmentName") String apartmentName);

    // ✅ 특정 투표 조회 (옵션 포함)
    @Query("SELECT v FROM VotePoll v LEFT JOIN FETCH v.options WHERE v.id = :id")
    Optional<VotePoll> findByIdWithOptions(@Param("id") Long id);

    List<VotePoll> findByApartmentName(String apartmentName);

    List<VotePoll> findByEndDateBeforeAndStatus(LocalDateTime now, VoteStatus voteStatus);

    @Query("SELECT DISTINCT v FROM VotePoll v JOIN v.records r WHERE r.user.id = :userId")
    List<VotePoll> findByUserParticipation(@Param("userId") Long userId);
}
