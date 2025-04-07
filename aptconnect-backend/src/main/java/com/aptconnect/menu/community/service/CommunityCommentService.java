package com.aptconnect.menu.community.service;

import com.aptconnect.menu.community.entity.CommentLike;
import com.aptconnect.menu.community.entity.CommunityComment;
import com.aptconnect.menu.community.entity.CommunityPost;
import com.aptconnect.menu.community.repository.CommentLikeRepository;
import com.aptconnect.menu.community.repository.CommunityCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.aptconnect.menu.auth.entity.User;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommunityCommentService {
    private final CommunityCommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;

    public List<CommunityComment> getCommentsByPost(CommunityPost post) {
        return commentRepository.findByPost(post);
    }

    public void saveComment(CommunityComment comment) {
        // ✅ 일반 댓글만 저장하도록 분리 (대댓글 저장 방지)
        if (comment.getParentComment() == null) {
            comment.setCreatedAt(LocalDateTime.now());
            commentRepository.save(comment);
        }
    }

    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
    }

    public CommunityPost getPostById(Long id) {
        CommunityComment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));
        return comment.getPost();
    }

    @Transactional
    public int likeComment(Long commentId, User user) {
        CommunityComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));

        // 이미 좋아요 했는지 체크
        if (commentLikeRepository.existsByUserAndComment(user, comment)) {
            return -1; // 중복
        }

        // 좋아요 저장
        CommentLike like = new CommentLike();
        like.setComment(comment);
        like.setUser(user);
        commentLikeRepository.save(like);

        // 좋아요 카운트 증가
        comment.setLikeCount(comment.getLikeCount() + 1);
        return comment.getLikeCount();
    }

    public void saveReply(Long parentId, CommunityComment reply) {
        // ✅ 부모 댓글 찾기
        CommunityComment parentComment = commentRepository.findById(parentId)
                .orElseThrow(() -> new RuntimeException("부모 댓글을 찾을 수 없습니다."));

        // ✅ 대댓글이므로 반드시 부모 댓글과 같은 게시글 설정
        reply.setPost(parentComment.getPost());
        reply.setParentComment(parentComment);
        reply.setCreatedAt(LocalDateTime.now());

        commentRepository.save(reply);
    }

    public CommunityComment getCommentById(Long parentId) {
        return commentRepository.findById(parentId)
                .orElseThrow(() -> new RuntimeException("부모 댓글을 찾을 수 없습니다."));
    }
}