package com.aptconnect.batch;

import com.aptconnect.entity.vote.VotePoll;
import com.aptconnect.entity.vote.VoteStatus;
import com.aptconnect.repository.VotePollRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class VoteStatusUpdateScheduler {
    private final VotePollRepository votePollRepository;

    // 매일 자정(00:00)에 실행
    @Scheduled(cron = "0 0 0 * * *")
    public void updateExpiredVotes() {
        List<VotePoll> expiredVotes = votePollRepository.findByEndDateBeforeAndStatus(LocalDateTime.now(), VoteStatus.ONGOING);

        if (!expiredVotes.isEmpty()) {
            for (VotePoll vote : expiredVotes) {
                vote.setStatus(VoteStatus.COMPLETED);
            }
            votePollRepository.saveAll(expiredVotes);
        }
    }

    // ⚠️ 테스트용 (1분마다 실행, 배포 전 주석 처리할 것)
    @Scheduled(fixedRate = 60000)
    public void testUpdateExpiredVotes() {
        updateExpiredVotes();
    }
}
