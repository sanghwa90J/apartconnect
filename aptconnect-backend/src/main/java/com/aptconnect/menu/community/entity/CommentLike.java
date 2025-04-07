package com.aptconnect.menu.community.entity;

import com.aptconnect.menu.auth.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "community_comment_like", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "comment_id"})
})
@Getter
@Setter
@NoArgsConstructor
public class CommentLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "comment_id", nullable = false)
    private CommunityComment comment;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
