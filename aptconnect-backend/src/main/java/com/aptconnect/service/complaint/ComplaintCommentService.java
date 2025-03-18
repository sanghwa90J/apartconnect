package com.aptconnect.service.complaint;

import com.aptconnect.entity.User;

import com.aptconnect.entity.complaint.CommentDTO;
import com.aptconnect.entity.complaint.Complaint;
import com.aptconnect.entity.complaint.ComplaintComment;
import com.aptconnect.repository.complaint.ComplaintCommentRepository;
import com.aptconnect.repository.complaint.ComplaintRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ComplaintCommentService {
    private final ComplaintCommentRepository commentRepository;
    private final ComplaintRepository complaintRepository;

    // ✅ 특정 민원의 댓글 목록 조회
    public List<ComplaintComment> getCommentsByComplaint(Long complaintId) {
        return commentRepository.findByComplaintIdOrderByCreatedAtAsc(complaintId);
    }

    // ✅ 댓글 작성
    @Transactional
    public ComplaintComment addComment(Long complaintId, String content, User user) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("해당 민원을 찾을 수 없습니다."));

        ComplaintComment newComment = new ComplaintComment();

        newComment.setContent(content);
        newComment.setUser(user);
        newComment.setComplaint(complaint);
        newComment.setCreatedAt(LocalDateTime.now());

        return commentRepository.save(newComment);
    }

    // ✅ 댓글 삭제 (본인만 가능)
    @Transactional
    public void deleteComment(Long commentId, User user) {
        ComplaintComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("해당 댓글을 찾을 수 없습니다."));

        if (!comment.getUser().getId().equals(user.getId())) {
            throw new SecurityException("본인의 댓글만 삭제할 수 있습니다.");
        }

        commentRepository.delete(comment);
    }

    public ComplaintComment updateComment(Long commentId, String newContent, User currentUser) {
        ComplaintComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));

        // 🔥 댓글 작성자 본인인지 검증
        if (!comment.getUser().equals(currentUser)) {
            throw new SecurityException("본인 댓글만 수정할 수 있습니다.");
        }

        comment.setContent(newContent);
        return commentRepository.save(comment);
    }
}