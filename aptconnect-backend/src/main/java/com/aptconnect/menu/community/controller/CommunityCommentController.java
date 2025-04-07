package com.aptconnect.menu.community.controller;

import com.aptconnect.menu.auth.entity.User;
import com.aptconnect.menu.community.entity.CommunityComment;
import com.aptconnect.menu.community.entity.CommunityPost;
import com.aptconnect.menu.auth.service.UserService;
import com.aptconnect.menu.community.service.CommunityCommentService;
import com.aptconnect.menu.community.service.CommunityPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/community/comments")
@RequiredArgsConstructor
public class CommunityCommentController {
    private final CommunityCommentService commentService;
    private final CommunityPostService postService;
    private final UserService userService;

    @PostMapping("/create")
    public String createComment(@RequestParam("postId") Long postId,
                                @RequestParam("content") String content,
                                Authentication authentication) {
        User currentUser = userService.getUserByEmail((authentication.getName()));

        CommunityPost post = postService.getPostById(postId);

        CommunityComment comment = new CommunityComment();
        comment.setPost(post);
        comment.setUser(currentUser);
        comment.setContent(content);

        commentService.saveComment(comment);

        return "redirect:/community/" + postId; // 게시글 상세 페이지로 리다이렉트
    }

    @GetMapping("/delete/{commentId}")
    public String deleteComment(@PathVariable Long commentId, Authentication authentication) {
        CommunityComment comment = commentService.getCommentsByPost(commentService.getPostById(commentId)).get(0);

        User currentUser = userService.getUserByEmail(authentication.getName());

        // 댓글 작성자만 삭제 가능
        if (!comment.getUser().getId().equals(currentUser.getId())) {
            throw new SecurityException("해당 댓글을 삭제할 권한이 없습니다.");
        }

        commentService.deleteComment(commentId);
        return "redirect:/community/" + comment.getPost().getId();
    }

    @PostMapping("/{commentId}/like")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> likeComment(@PathVariable Long commentId, Authentication authentication) {
        int result = commentService.likeComment(commentId, userService.getUserByEmail(authentication.getName()));

        if (result == -1) {
            return ResponseEntity.ok(Map.of(
                    "status", "duplicate",
                    "message", "이미 해당 댓글에 좋아요를 누르셨습니다."
            ));
        }

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "likeCount", result
        ));
    }

    // ✅ 대댓글 저장 API 추가
    @PostMapping("/reply")
    public String replyComment(@RequestParam("parentId") Long parentId,
                               @RequestParam("content") String content,
                               Authentication authentication) {
        // ✅ 현재 로그인한 사용자 가져오기
        User currentUser = userService.getUserByEmail(authentication.getName());

        // ✅ 대댓글 저장 (부모 ID를 설정하여 대댓글로 저장)
        CommunityComment reply = new CommunityComment();
        reply.setUser(currentUser);
        reply.setContent(content);

        commentService.saveReply(parentId, reply);

        // ✅ 댓글이 추가된 게시글 상세 페이지로 리다이렉트
        return "redirect:/community/" + commentService.getCommentById(parentId).getPost().getId();
    }
}