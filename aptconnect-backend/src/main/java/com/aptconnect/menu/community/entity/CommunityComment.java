package com.aptconnect.menu.community.entity;

import com.aptconnect.menu.auth.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class CommunityComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private CommunityPost post;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "parent_comment_id")
    private CommunityComment parentComment;

    private int likeCount = 0;

    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL)
    private List<CommunityComment> replies;

    private boolean reported = false;
}