package com.aptconnect.menu.community.entity;

import com.aptconnect.menu.auth.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "community_post_like", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "post_id"})
})
@Getter
@Setter
@NoArgsConstructor
public class PostLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private CommunityPost post;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
