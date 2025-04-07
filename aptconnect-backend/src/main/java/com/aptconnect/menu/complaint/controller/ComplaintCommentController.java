package com.aptconnect.menu.complaint.controller;

import com.aptconnect.component.AuthenticationFacade;
import com.aptconnect.menu.auth.entity.User;

import com.aptconnect.menu.complaint.entity.CommentDTO;
import com.aptconnect.menu.auth.service.UserService;
import com.aptconnect.menu.complaint.service.ComplaintCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/complaints/{complaintId}/comments")
@RequiredArgsConstructor
public class ComplaintCommentController {
    private final ComplaintCommentService commentService;
    private final UserService userService;
    private final AuthenticationFacade authenticationFacade;
    private final ComplaintCommentService complaintCommentService;

    // ✅ 댓글 목록 조회 (AJAX 요청 지원)
    @GetMapping
    @ResponseBody
    public ResponseEntity<List<CommentDTO>> getComments(@PathVariable Long complaintId) {

        return ResponseEntity.ok(commentService.getCommentsByComplaint(complaintId).stream()
                .map(CommentDTO::new)
                .toList()
        );
    }

    // ✅ 댓글 작성
    @PostMapping
    @ResponseBody
    public ResponseEntity<CommentDTO> addComment(@PathVariable Long complaintId, @RequestBody Map<String, String> request, Authentication authentication) {
        User currentUser = userService.getUserByEmail(authentication.getName());

        return ResponseEntity.ok(new CommentDTO(commentService.addComment(complaintId, request.get("content"), currentUser)));
    }

    // ✅ 댓글 삭제
    @DeleteMapping("/{commentId}")
    @ResponseBody
    public ResponseEntity<String> deleteComment(@PathVariable Long commentId, Authentication authentication) {
        commentService.deleteComment(commentId, userService.getUserByEmail(authenticationFacade.getCurrentUserEmail()));
        return ResponseEntity.ok("댓글이 삭제되었습니다.");
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentDTO> updateComment(
            @PathVariable Long commentId,
            @RequestBody Map<String, String> requestBody,
            Authentication authentication) {

        User currentUser = userService.getUserByEmail(authentication.getName());

        String newContent = requestBody.get("content");

        if (newContent == null || newContent.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(new CommentDTO(complaintCommentService.updateComment(commentId, newContent, currentUser)));
    }

}