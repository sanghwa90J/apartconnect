package com.aptconnect.menu.community.repository;

import com.aptconnect.menu.community.entity.CommunityPost;
import com.aptconnect.menu.community.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import com.aptconnect.menu.auth.entity.User;
import org.springframework.stereotype.Repository;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    boolean existsByUserAndPost(User user, CommunityPost post);
}
