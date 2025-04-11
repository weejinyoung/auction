package com.ourfantasy.auction.auction.repository;

import com.ourfantasy.auction.auction.model.Auction;
import com.ourfantasy.auction.auction.model.AuctionLike;
import com.ourfantasy.auction.auction.model.Bidding;
import com.ourfantasy.auction.auction.service.dto.AuctionWithCountsProjection;
import com.ourfantasy.auction.auction.service.dto.GetAuctionResponseWithLikeAndFollow;
import com.ourfantasy.auction.item.model.Item;
import com.ourfantasy.auction.item.model.ItemCategory;
import com.ourfantasy.auction.user.model.User;
import com.ourfantasy.auction.user.model.UserFollow;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("경매 인기순 정렬 + 좋아요/팔로우 조인 테스트")
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AuctionRepositoryWithLikeAndFollowTest {

    @Autowired
    private AuctionCustomRepository auctionQueryRepository;

    @Autowired
    private EntityManager em;

    @Test
    void 입찰수_기준_정렬되고_좋아요_팔로우_조인_정상작동한다() {
        // 유저 생성
        User user1 = User.builderWithValidate()
                .email("user1@example.com")
                .nickname("닉네임1")
                .build();
        User user2 = User.builderWithValidate()
                .email("user2@example.com")
                .nickname("닉네임2")
                .build();
        User user3 = User.builderWithValidate()
                .email("user3@example.com")
                .nickname("닉네임3")
                .build();
        em.persist(user1);
        em.persist(user2);
        em.persist(user3);

        // 아이템 생성 (owner 설정 필수)
        Item item1 = Item.builder()
                .name("상의")
                .detail("상세 설명1")
                .category(ItemCategory.CLOTHING)
                .owner(user1)
                .build();
        Item item2 = Item.builder()
                .name("하의")
                .detail("상세 설명2")
                .category(ItemCategory.CLOTHING)
                .owner(user2)
                .build();
        em.persist(item1);
        em.persist(item2);

        // 경매 생성 및 활성화 (startingPrice, minimumBidIncrement, closingAt 필수)
        LocalDateTime closingAt = LocalDateTime.now().plusDays(1);

        Auction auction1 = Auction.builderWithValidate()
                .item(item1)
                .cosigner(user1)
                .startingPrice(1000L)
                .minimumBidIncrement(100L)
                .closingAt(closingAt)
                .build();
        Auction auction2 = Auction.builderWithValidate()
                .item(item2)
                .cosigner(user2)
                .startingPrice(2000L)
                .minimumBidIncrement(200L)
                .closingAt(closingAt)
                .build();
        em.persist(auction1);
        em.persist(auction2);

        // 입찰 생성 (auction2가 더 인기 있게)
        em.persist(Bidding.builder().auction(auction1).bidder(user1).bidPrice(1100L).build());
        em.persist(Bidding.builder().auction(auction2).bidder(user2).bidPrice(2200L).build());
        em.persist(Bidding.builder().auction(auction2).bidder(user1).bidPrice(2400L).build());

        // 좋아요 생성 (auction2에 2개)
        em.persist(AuctionLike.builder().auction(auction1).user(user1).build());
        em.persist(AuctionLike.builder().auction(auction2).user(user2).build());
        em.persist(AuctionLike.builder().auction(auction2).user(user3).build());

        // 팔로우 생성 (user1이 user2 팔로우)
        em.persist(UserFollow.builder().follower(user1).followee(user2).build());

        em.flush();
        em.clear();

        // when
        Pageable pageable = PageRequest.of(0, 10);
        Page<AuctionWithCountsProjection> result = auctionQueryRepository
                .getNearestClosingAuctionsByCategoryWithLikeAndFollow(pageable, ItemCategory.CLOTHING);

        // then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(2);

        // 입찰 수 기준 정렬 -> auction2가 먼저 나와야 함
        assertThat(result.getContent().get(0).auction().getId()).isEqualTo(auction2.getId());

        // 로그
        result.getContent().forEach(a ->
                System.out.println("경매 ID: " + a.auction().getId() + ", 아이템명: " + a.auction().getItem().getName()));
    }
}
