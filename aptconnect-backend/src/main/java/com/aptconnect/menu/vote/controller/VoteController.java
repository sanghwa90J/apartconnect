package com.aptconnect.menu.vote.controller;

import com.aptconnect.component.AuthenticationFacade;
import com.aptconnect.menu.auth.entity.Role;
import com.aptconnect.menu.vote.entity.VotePoll;
import com.aptconnect.menu.vote.entity.VoteOption;
import com.aptconnect.menu.vote.entity.VotePollRequestDTO;
import com.aptconnect.menu.vote.entity.VoteStatus;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import com.aptconnect.menu.auth.entity.User;
import com.aptconnect.menu.vote.service.VoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/votes")
@RequiredArgsConstructor
public class VoteController {
    private final VoteService voteService;
    private final AuthenticationFacade authenticationFacade; // 로그인한 사용자 정보 가져오기

    // ✅ 투표 목록 조회 (권한에 따라 다르게 조회)
    @GetMapping
    public String listVotes(Model model, @ModelAttribute("currentUser") User currentUser) {
        String currentRole = authenticationFacade.getCurrentUserRole();
        List<VotePoll> votes;

        if (currentUser.getRole() == Role.MASTER) {
            votes = voteService.getAllVotes(); // MASTER는 모든 투표 확인 가능
        } else {
            votes = voteService.getVotesByApartment(currentUser.getApartmentName()); // 일반 사용자는 자기 아파트 투표만 확인
        }

        model.addAttribute("votes", votes);
        model.addAttribute("currentRole", currentRole);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("currentPage", "votes"); // 🎯 현재 페이지 정보 추가

        return "/votes/list";
    }

    // ✅ 특정 투표 상세 조회
    @GetMapping("/{pollId}")
    public String viewVote(@PathVariable("pollId") Long pollId, Model model, @ModelAttribute("currentUser") User currentUser) {
        VotePoll vote = voteService.getVoteById(pollId).orElseThrow(() -> new RuntimeException("투표를 찾을 수 없습니다."));

        // 사용자가 속한 아파트의 투표인지 체크 (MASTER 제외)
        if (currentUser.getRole() != Role.MASTER &&
                !vote.getApartmentName().equals(currentUser.getApartmentName())) {
            throw new SecurityException("해당 투표를 볼 수 없습니다.");
        }

        // ✅ 투표 참여 여부 확인
        boolean hasVoted = voteService.hasUserVoted(currentUser.getId(), vote.getId());


        // ✅ 총 투표 수 계산
        int totalVotes = vote.getOptions().stream().mapToInt(VoteOption::getVoteCount).sum();

        model.addAttribute("hasVoted", hasVoted);
        model.addAttribute("isOngoing", vote.getStatus() == VoteStatus.ONGOING);
        model.addAttribute("totalVotes", totalVotes);
        model.addAttribute("currentRole", authenticationFacade.getCurrentUserRole());
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("vote", vote);
        model.addAttribute("currentPage", "votes"); // 🎯 현재 페이지 정보 추가

        return "/votes/vote-detail";
    }

    // ✅ 투표 참여
    @PostMapping("/{pollId}/vote")
    public String participateVote(@PathVariable("pollId") Long pollId,
                                  @RequestParam("optionId") Long optionId,
                                  @ModelAttribute("currentUser") User currentUser) {
        voteService.participateInVote(pollId, currentUser.getId(), optionId);
        return "redirect:/votes/" + pollId;
    }

    // ✅ 투표 생성 페이지
    @GetMapping("/create")
    public String createVoteForm(Model model, @ModelAttribute("currentUser") User currentUser,
                                 @ModelAttribute("savedVotePollRequest") VotePollRequestDTO savedVotePollRequest) {

        // ✅ FlashAttribute에서 받은 값이 없으면 새로운 DTO 객체 생성
        if (!model.containsAttribute("savedVotePollRequest")) {
            model.addAttribute("votePollRequest", new VotePollRequestDTO());
        } else {
            model.addAttribute("votePollRequest", savedVotePollRequest);
        }

        model.addAttribute("votePollRequest", new VotePollRequestDTO());
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("currentPage", "votes"); // 🎯 현재 페이지 정보 추가
        model.addAttribute("currentRole", authenticationFacade.getCurrentUserRole());
        return "/votes/vote-create";
    }

    // ✅ 투표 생성 처리
    @PostMapping("/create")
    public String createVote(@ModelAttribute VotePollRequestDTO requestDTO,
                             @ModelAttribute("currentUser") User currentUser,
                             RedirectAttributes redirectAttributes) {
        try {
            voteService.createVote(requestDTO, currentUser);
            return "redirect:/votes"; // 성공 시 투표 목록으로 이동

        } catch (IllegalArgumentException ex) {
            // 에러 메시지와 기존 입력값을 FlashAttribute로 전달
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            redirectAttributes.addFlashAttribute("savedVotePollRequest", requestDTO);
            return "redirect:/votes/create"; // 실패 시 다시 폼 화면으로 리다이렉트
        }
    }

    // ✅ 투표 수정 화면
    @GetMapping("/{pollId}/edit")
    public String editVote(@PathVariable("pollId") Long pollId, Model model, @ModelAttribute("currentUser") User currentUser) {
        VotePoll vote = voteService.getVoteById(pollId)
                .orElseThrow(() -> new RuntimeException("투표를 찾을 수 없습니다."));

        // 관리자 권한 확인
        if (currentUser.getRole() != Role.MASTER && !vote.getCreatedBy().equals(currentUser)) {
            throw new SecurityException("해당 투표를 수정할 권한이 없습니다.");
        }

        model.addAttribute("vote", vote);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("currentPage", "votes"); // 🎯 현재 페이지 정보 추가
        model.addAttribute("currentRole", authenticationFacade.getCurrentUserRole());

        return "/votes/vote-edit";
    }

    // ✅ 투표 업데이트 처리
    @PostMapping("/update/{pollId}")
    public String updateVote(@PathVariable("pollId") Long pollId,
                             @Valid @ModelAttribute VotePollRequestDTO requestDTO,
                             @ModelAttribute("currentUser") User currentUser) {
        voteService.updateVote(pollId, requestDTO, currentUser);
        return "redirect:/votes/" + pollId;
    }

    // ✅ 투표 삭제
    @GetMapping("/{pollId}/delete")
    public String deleteVote(@PathVariable("pollId") Long pollId, @ModelAttribute("currentUser") User currentUser) {
        voteService.deleteVote(pollId, currentUser);
        return "redirect:/votes";
    }

    // ✅ 사용자가 참여한 투표 목록 조회
    @GetMapping("/my-votes")
    public String myVotes(Model model, @ModelAttribute("currentUser") User currentUser) {
        List<VotePoll> myVotes = voteService.getVotesByUser(currentUser.getId());
        model.addAttribute("votes", myVotes);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("currentRole", authenticationFacade.getCurrentUserRole());
        model.addAttribute("currentPage", "my-votes");
        return "/votes/my-votes";
    }
}