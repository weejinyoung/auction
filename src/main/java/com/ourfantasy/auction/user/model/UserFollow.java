package com.ourfantasy.auction.user.model;

import com.ourfantasy.auction.config.persistence.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Getter
@Table(
        name = "user_follow",
        indexes = {
                @Index(name = "idx_follower_id", columnList = "follower_id"),
                @Index(name = "idx_followee_id", columnList = "followee_id"),
                @Index(name = "idx_unique_follower_followee", columnList = "follower_id, followee_id", unique = true)
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserFollow extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 팔로우를 하는 사람
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower_id", nullable = false)
    private User follower;

    // 팔로우를 당하는 사람
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "followee_id", nullable = false)
    private User followee;

    @Builder
    public UserFollow(User follower, User followee) {
        this.follower = follower;
        this.followee = followee;
    }
}
