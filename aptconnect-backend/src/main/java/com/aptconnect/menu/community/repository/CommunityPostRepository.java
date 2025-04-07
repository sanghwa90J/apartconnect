package com.aptconnect.menu.community.repository;

import com.aptconnect.menu.community.entity.CommunityPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommunityPostRepository extends JpaRepository<CommunityPost, Long> {
    List<CommunityPost> findByCategory(String category);
    List<CommunityPost> findByTitleContainingOrContentContaining(String title, String content);
}
