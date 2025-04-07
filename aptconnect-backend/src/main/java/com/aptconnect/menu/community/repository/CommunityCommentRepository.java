package com.aptconnect.menu.community.repository;

import com.aptconnect.menu.community.entity.CommunityComment;
import com.aptconnect.menu.community.entity.CommunityPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommunityCommentRepository extends JpaRepository<CommunityComment, Long> {
    List<CommunityComment> findByPost(CommunityPost post);
}
