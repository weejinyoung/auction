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
public class ItemRating extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 평가를 준 사람
    @ManyToOne(fetch = FetchType.LAZY)
    private User rater;

    // 평가 대상 아이템
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    private Double score;
    private String comment;

    @Builder
    public ItemRating(User rater, Item item, Double score, String comment) {
        this.rater = rater;
        this.item = item;
        this.score = score;
        this.comment = comment;
    }
}
