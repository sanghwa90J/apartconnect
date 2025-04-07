package com.aptconnect.menu.community.repository;

import com.aptconnect.menu.auth.entity.User;
import com.aptconnect.menu.community.entity.CommentLike;
import com.aptconnect.menu.community.entity.CommunityComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    boolean existsByUserAndComment(User user, CommunityComment comment);

}
