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
    private static final int BATCH_SIZE = 10_000;

    @Test
    @DisplayName("경매 좋아요 더미 데이터 생성 (유저별 10~100개)")
    public void generateAuctionLikes() {
        long startTime = System.currentTimeMillis();
        System.out.println("경매 좋아요 생성 시작...");

        List<User> allUsers = userRepository.findAll();
        List<Auction> allAuctions = auctionRepository.findAll();

        List<AuctionLike> likeList = new ArrayList<>(BATCH_SIZE);

        int processedUsers = 0;
        long totalLikes = 0;

        for (User user : allUsers) {
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
                totalLikes++;
            }

            processedUsers++;

            if (likeList.size() >= BATCH_SIZE) {
                auctionLikeRepository.saveAll(likeList);
                likeList.clear();
                System.out.printf("💾 중간 저장: %d명 처리 완료, 누적 좋아요 %d개\n", processedUsers, totalLikes);
            }

            if (processedUsers % 1000 == 0 || processedUsers == allUsers.size()) {
                System.out.printf("🔄 진행 중: %d명 유저 처리 중\n", processedUsers);
            }
        }

        if (!likeList.isEmpty()) {
            auctionLikeRepository.saveAll(likeList);
        }

        long endTime = System.currentTimeMillis();
        double elapsedSeconds = (endTime - startTime) / 1000.0;

        System.out.printf("✅ 총 %d개의 좋아요 생성 완료 (소요 시간: %.2f초, 초당 %.1f건)\n",
                totalLikes, elapsedSeconds, totalLikes / elapsedSeconds);
    }
}