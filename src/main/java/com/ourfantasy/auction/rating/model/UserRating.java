package com.ourfantasy.auction.rating.model;

import com.ourfantasy.auction.config.persistence.BaseTimeEntity;
import com.ourfantasy.auction.item.model.Item;
import com.ourfantasy.auction.user.model.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserRating extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 평가를 준 사람
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rater_id", nullable = false)
    private User rater;

    // 평가 대상 유저
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ratee_id", nullable = false)
    private User ratee;

    private Double score;
    private String comment;

    @Builder
    public UserRating(User rater, User ratee, Double score, String comment) {
        this.rater = rater;
        this.ratee = ratee;
        this.score = score;
        this.comment = comment;
    }
}
