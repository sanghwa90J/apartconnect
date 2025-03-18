package com.aptconnect.service;

import com.aptconnect.entity.Role;
import com.aptconnect.entity.User;
import com.aptconnect.entity.vote.*;
import com.aptconnect.repository.UserRepository;
import com.aptconnect.repository.VoteOptionRepository;
import com.aptconnect.repository.VotePollRepository;
import com.aptconnect.repository.VoteRecordRepository;
import lombok.RequiredArgsConstructor;
import com.aptconnect.entity.vote.VoteOption;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VoteService {
    private final VotePollRepository votePollRepository;
    private final VoteOptionRepository voteOptionRepository;
    private final VoteRecordRepository voteRecordRepository;
    private final UserRepository userRepository; // ✅ UserRepository 추가

    // ✅ 사용자의 권한에 따라 투표 조회 방식 변경
    public List<VotePoll> getAllVotes() {
        return votePollRepository.findAll();
    }

    // ✅ 특정 아파트 투표 목록 조회 (ADMIN & USER용)
    public List<VotePoll> getVotesByApartment(String apartmentName) {
        return votePollRepository.findByApartmentName(apartmentName);
    }

    // ✅ 특정 투표 조회 (옵션 포함)
    public Optional<VotePoll> getVoteById(Long id) {
        return votePollRepository.findByIdWithOptions(id);
    }

    // ✅ 투표 참여
    @Transactional
    public void participateInVote(Long pollId, Long userId, Long optionId) {
        VotePoll votePoll = votePollRepository.findById(pollId)
                .orElseThrow(() -> new RuntimeException("투표를 찾을 수 없습니다."));

        // 🚨 투표 마감 여부 체크
        if (votePoll.getStatus() == VoteStatus.COMPLETED) {
            throw new RuntimeException("이미 마감된 투표입니다.");
        }

        if (voteRecordRepository.existsByUserIdAndPollId(userId, pollId)) {
            throw new RuntimeException("이미 투표에 참여하셨습니다.");
        }

        VoteOption selectedOption = voteOptionRepository.findById(optionId)
                .orElseThrow(() -> new RuntimeException("선택한 옵션을 찾을 수 없습니다."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        VotePoll poll = votePollRepository.findById(pollId)
                .orElseThrow(() -> new RuntimeException("투표를 찾을 수 없습니다."));

        VoteRecord record = new VoteRecord();
        record.setUser(user);
        record.setPoll(poll);
        record.setSelectedOption(selectedOption);

        voteRecordRepository.save(record);

        // ✅ 선택지의 투표 수 증가
        selectedOption.setVoteCount(selectedOption.getVoteCount() + 1);
        voteOptionRepository.save(selectedOption);
    }

    public boolean hasUserVoted(Long userId, Long pollId) {
        return voteRecordRepository.existsByUserIdAndPollId(userId, pollId);
    }

    // ✅ 투표 생성
    @Transactional
    public void createVote(VotePollRequestDTO requestDTO, User admin) {// ✅ 선택지 최소 개수 검증 (2개 이상)
        if (requestDTO.getOptions() == null || requestDTO.getOptions().size() < 2) {
            throw new IllegalArgumentException("투표에는 최소 2개의 선택지가 필요합니다.");
        }

        VotePoll votePoll = new VotePoll();
        votePoll.setTitle(requestDTO.getTitle());
        votePoll.setDescription(requestDTO.getDescription());
        votePoll.setStartDate(requestDTO.getStartDate());
        votePoll.setEndDate(requestDTO.getEndDate());
        votePoll.setStatus(VoteStatus.ONGOING);
        votePoll.setCreatedBy(admin);
        votePoll.setApartmentName(admin.getApartmentName());

        VotePoll savedPoll = votePollRepository.save(votePoll);

        // 선택지 추가
        List<VoteOption> options = requestDTO.getOptions().stream()
                .map(text -> {
                    VoteOption option = new VoteOption();
                    option.setOptionText(text);
                    option.setPoll(savedPoll);
                    return option;
                })
                .collect(Collectors.toList());

        voteOptionRepository.saveAll(options);
    }

    @Transactional
    public void updateVote(Long pollId, VotePoll updatedVotePoll, List<String> newOptionTexts, User admin) {
        VotePoll existingPoll = votePollRepository.findById(pollId)
                .orElseThrow(() -> new RuntimeException("수정할 투표를 찾을 수 없습니다."));

        // ✅ 수정 권한 확인 (생성자 또는 MASTER만 가능)
        if (!existingPoll.getCreatedBy().equals(admin) && admin.getRole() != Role.MASTER) {
            throw new SecurityException("해당 투표를 수정할 권한이 없습니다.");
        }

        // ✅ 기본 정보 업데이트
        existingPoll.setTitle(updatedVotePoll.getTitle());
        existingPoll.setDescription(updatedVotePoll.getDescription());
        existingPoll.setStartDate(updatedVotePoll.getStartDate());
        existingPoll.setEndDate(updatedVotePoll.getEndDate());
        existingPoll.setStatus(updatedVotePoll.getStatus());

        // ✅ 기존 옵션 가져오기
        List<VoteOption> existingOptions = existingPoll.getOptions();

        // ✅ 새로운 옵션과 비교하여 삭제해야 할 기존 옵션 찾기
        Iterator<VoteOption> iterator = existingOptions.iterator();
        while (iterator.hasNext()) {
            VoteOption option = iterator.next();
            if (!newOptionTexts.contains(option.getOptionText())) { // 새로운 리스트에 없는 옵션이면 삭제
                voteOptionRepository.delete(option);
                iterator.remove();
            }
        }

        // ✅ 새로운 옵션 추가
        for (String optionText : newOptionTexts) {
            if (existingOptions.stream().noneMatch(opt -> opt.getOptionText().equals(optionText))) {
                VoteOption newOption = new VoteOption();
                newOption.setOptionText(optionText);
                newOption.setPoll(existingPoll);
                voteOptionRepository.save(newOption);
                existingOptions.add(newOption);
            }
        }

        votePollRepository.save(existingPoll);
    }


    @Transactional
    public void deleteVote(Long id, User user) {
        VotePoll votePoll = votePollRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("투표를 찾을 수 없습니다."));

        // 삭제 권한 확인
        if (!votePoll.getCreatedBy().equals(user) && user.getRole() != Role.MASTER) {
            throw new SecurityException("해당 투표를 삭제할 권한이 없습니다.");
        }

        // ✅ 관련된 VoteRecord 먼저 삭제
        voteRecordRepository.deleteAllByPollId(votePoll.getId());

        // ✅ 관련된 VoteOption 삭제
        voteOptionRepository.deleteAllByPollId(votePoll.getId());

        // ✅ 투표 삭제
        votePollRepository.delete(votePoll);
    }

    public List<VotePoll> getVotesByUser(Long userId) {
        return votePollRepository.findByUserParticipation(userId);
    }
}