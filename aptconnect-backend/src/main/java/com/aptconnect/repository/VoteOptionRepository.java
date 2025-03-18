package com.aptconnect.repository;

import com.aptconnect.entity.vote.VoteOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoteOptionRepository extends JpaRepository<VoteOption, Long> {
    void deleteAllByPollId(Long pollId);

}
