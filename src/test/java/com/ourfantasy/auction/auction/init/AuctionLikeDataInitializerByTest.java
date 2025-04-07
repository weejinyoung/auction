package com.ourfantasy.auction.auction.init;

import com.ourfantasy.auction.auction.model.Auction;
import com.ourfantasy.auction.auction.model.AuctionLike;
import com.ourfantasy.auction.auction.repository.AuctionLikeRepository;
import com.ourfantasy.auction.auction.repository.AuctionRepository;
import com.ourfantasy.auction.user.model.User;
import com.ourfantasy.auction.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

@SpringBootTest
public class AuctionLikeDataInitializerByTest {

    @Autowired
    private AuctionRepository auctionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuctionLikeRepository auctionLikeRepository;

    private final Random random = new Random();

    @Test
    @DisplayName("경매 좋아요 더미 데이터 생성 (유저별 10~100개)")
    public void generateAuctionLikes() {
        List<User> allUsers = userRepository.findAll();
        List<Auction> allAuctions = auctionRepository.findAll();

        List<AuctionLike> likeList = new ArrayList<>();

        for (User user : allUsers) {
            // 한 유저당 최소 10개 ~ 최대 100개 좋아요
            int likeCount = random.nextInt(91) + 10;

            Set<Long> likedAuctionIds = new HashSet<>();

            for (int i = 0; i < likeCount; i++) {
                Auction auction;
                do {
                    auction = allAuctions.get(random.nextInt(allAuctions.size()));
                } while (!likedAuctionIds.add(auction.getId())); // 중복 방지

                AuctionLike like = AuctionLike.builder()
                        .user(user)
                        .auction(auction)
                        .build();

                likeList.add(like);
            }
        }

        auctionLikeRepository.saveAll(likeList);
        System.out.printf("✅ 총 %d개의 좋아요 생성 완료\n", likeList.size());
    }
}
