package com.aptconnect.menu.vote.repository;

import com.aptconnect.menu.vote.entity.VoteRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoteRecordRepository extends JpaRepository<VoteRecord, Long> {
    boolean existsByUserIdAndPollId(Long userId, Long pollId);
    void deleteAllByPollId(Long pollId);

}
