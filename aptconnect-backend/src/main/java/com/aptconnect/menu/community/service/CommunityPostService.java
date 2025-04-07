package com.aptconnect.menu.community.service;

import com.aptconnect.menu.community.entity.CommunityPost;
import com.aptconnect.menu.community.entity.PostLike;
import com.aptconnect.menu.community.repository.CommunityPostRepository;
import com.aptconnect.menu.community.repository.PostLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.aptconnect.menu.auth.entity.User;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommunityPostService {
    private final CommunityPostRepository postRepository;
    private final PostLikeRepository postLikeRepository;

    public List<CommunityPost> getAllPosts() {
        return postRepository.findAll();
    }

    public CommunityPost getPostById(Long id) {
        return postRepository.findById(id).orElseThrow(() -> new RuntimeException("Post not found"));
    }

    public void savePost(CommunityPost post) {
        post.setCreatedAt(LocalDateTime.now());
        postRepository.save(post);
    }

    public void updatePost(Long id, CommunityPost updatedPost, User user) {
        CommunityPost post = getPostById(id);
        post.setTitle(updatedPost.getTitle());
        post.setContent(updatedPost.getContent());
        post.setCategory(updatedPost.getCategory());
        post.setUpdatedAt(LocalDateTime.now());
        post.setUser(user); // ✅ 업데이트 시에도 사용자 정보 유지
        postRepository.save(post);
    }

    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }

    @Transactional
    public int likePost(Long postId, User user) {
        CommunityPost post = getPostById(postId);

        // 🔥 이미 좋아요 누른 경우 무시
        if (postLikeRepository.existsByUserAndPost(user, post)) {
            return post.getLikeCount();
        }

        // 좋아요 기록 저장
        PostLike postLike = new PostLike();
        postLike.setPost(post);
        postLike.setUser(user);
        postLikeRepository.save(postLike);

        // 좋아요 수 증가
        post.setLikeCount(post.getLikeCount() + 1);
        return post.getLikeCount();
    }

    public boolean isAlreadyLiked(CommunityPost post, User user) {
        return postLikeRepository.existsByUserAndPost(user, post);
    }

    public List<CommunityPost> searchPosts(String keyword) {
        return postRepository.findByTitleContainingOrContentContaining(keyword, keyword);
    }
}
