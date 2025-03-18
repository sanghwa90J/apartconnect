package com.aptconnect.repository;

import com.aptconnect.entity.vote.VoteRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoteRecordRepository extends JpaRepository<VoteRecord, Long> {
    boolean existsByUserIdAndPollId(Long userId, Long pollId);
    void deleteAllByPollId(Long pollId);

}
