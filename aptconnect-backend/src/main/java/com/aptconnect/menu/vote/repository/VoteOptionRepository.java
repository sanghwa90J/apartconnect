package com.aptconnect.menu.vote.repository;

import com.aptconnect.menu.vote.entity.VoteOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoteOptionRepository extends JpaRepository<VoteOption, Long> {
    void deleteAllByPollId(Long pollId);

}
