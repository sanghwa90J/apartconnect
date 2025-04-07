package com.aptconnect.menu.community.controller;

import com.aptconnect.component.AuthenticationFacade;
import com.aptconnect.menu.auth.entity.User;
import com.aptconnect.menu.community.entity.CommunityPost;
import com.aptconnect.menu.auth.service.UserService;
import com.aptconnect.menu.community.service.CommunityCommentService;
import com.aptconnect.menu.community.service.CommunityPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.security.core.Authentication;

import java.util.Map;

@Controller
@RequestMapping("/community")
@RequiredArgsConstructor
public class CommunityController {
    private final CommunityPostService postService;
    private final CommunityCommentService commentService;
    private final UserService userService;
    private final AuthenticationFacade authenticationFacade; // 🔥 사용자 정보 가져오는 컴포넌트

    @GetMapping
    public String viewCommunity(Model model) {
        model.addAttribute("posts", postService.getAllPosts());
        model.addAttribute("currentPage", "community");
        model.addAttribute("currentRole", authenticationFacade.getCurrentUserRole());
        return "/community/community-list";
    }

    @GetMapping("/search")
    public String searchPosts(@RequestParam("q") String keyword, Model model) {
        model.addAttribute("posts", postService.searchPosts(keyword));
        model.addAttribute("currentPage", "community");
        model.addAttribute("currentRole", authenticationFacade.getCurrentUserRole());

        return "/community/community-list";
    }


    @GetMapping("/{communityId}")
    public String viewPostDetail(@PathVariable Long communityId, Model model, Authentication authentication) {
        model.addAttribute("post", postService.getPostById(communityId));
        model.addAttribute("comments", commentService.getCommentsByPost(postService.getPostById(communityId)));
        model.addAttribute("currentPage", "community");
        model.addAttribute("currentRole", authenticationFacade.getCurrentUserRole());
        model.addAttribute("currentUser", userService.getUserByEmail(authentication.getName()));

        return "/community/community-detail";
    }

    @GetMapping("/create")
    public String showPostForm(Model model) {
        model.addAttribute("post", new CommunityPost());
        model.addAttribute("currentPage", "community");
        model.addAttribute("currentRole", authenticationFacade.getCurrentUserRole());
        return "/community/community-form";
    }

    @PostMapping("/create")
    public String createPost(@ModelAttribute CommunityPost post, Authentication authentication) {
        // 🔥 게시글에 사용자 정보 설정
        post.setUser(userService.getUserByEmail(authentication.getName()));

        postService.savePost(post);
        return "redirect:/community";
    }

    @GetMapping("/edit/{communityId}")
    public String editPost(@PathVariable Long communityId, Model model) {
        model.addAttribute("post", postService.getPostById(communityId));
        model.addAttribute("currentPage", "community");
        model.addAttribute("currentRole", authenticationFacade.getCurrentUserRole());

        return "/community/community-edit";
    }

    @PostMapping("/update/{communityId}")
    public String updatePost(@PathVariable Long communityId, @ModelAttribute CommunityPost post, Authentication authentication) {
        postService.updatePost(communityId, post, userService.getUserByEmail(authentication.getName()));
        return "redirect:/community/" + communityId;
    }

    @GetMapping("/delete/{communityId}")
    public String deletePost(@PathVariable Long communityId) {
        postService.deletePost(communityId);
        return "redirect:/community";
    }

    @PostMapping("/{communityId}/like")
    @ResponseBody
    public ResponseEntity<Map<String, Object >> likePost(@PathVariable Long communityId, Authentication authentication) {
        User currentUser =userService.getUserByEmail(authentication.getName());

        CommunityPost post = postService.getPostById(communityId);

        boolean alreadyLiked = postService.isAlreadyLiked(post, currentUser);
        if (alreadyLiked) {
            return ResponseEntity.ok(Map.of(
                    "likeCount", post.getLikeCount(),
                    "status", "duplicate",
                    "message", "이미 좋아요를 누르셨습니다."
            ));
        }

        int likeCount = postService.likePost(communityId, currentUser);

        return ResponseEntity.ok(Map.of(
                "likeCount", likeCount,
                "status", "success",
                "message", "좋아요가 등록되었습니다."
        ));
    }
}
