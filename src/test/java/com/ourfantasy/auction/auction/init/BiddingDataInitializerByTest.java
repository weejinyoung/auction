package com.ourfantasy.auction.auction.init;

import com.ourfantasy.auction.auction.model.Auction;
import com.ourfantasy.auction.auction.model.Bidding;
import com.ourfantasy.auction.auction.repository.AuctionRepository;
import com.ourfantasy.auction.auction.repository.BiddingRepository;
import com.ourfantasy.auction.user.model.User;
import com.ourfantasy.auction.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@SpringBootTest
public class BiddingDataInitializerByTest {

    @Autowired
    private AuctionRepository auctionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BiddingRepository biddingRepository;

    private final Random random = new Random();

    @Test
    @DisplayName("경매 데이터에 대해 랜덤 입찰 생성")
    public void generateBiddingData() {
        int pageSize = 1000;
        int currentPage = 0;
        long totalBids = 0;

        List<User> allUsers = userRepository.findAll(); // 모든 사용자 (입찰자 후보)
        if (allUsers.isEmpty()) {
            System.out.println("❌ 사용자 없음. 테스트 종료");
            return;
        }

        System.out.println("💥 Bidding 데이터 생성 시작...");

        while (true) {
            List<Auction> auctionBatch = auctionRepository.findAll(PageRequest.of(currentPage, pageSize)).getContent();
            if (auctionBatch.isEmpty()) break;

            List<Bidding> biddingBatch = new ArrayList<>();

            for (Auction auction : auctionBatch) {
                // 비활성 경매는 제외
                if (auction.isInactive()) continue;

                // 랜덤 입찰자 수 (1~5명)
                int bidderCount = random.nextInt(10) + 1;
                Collections.shuffle(allUsers); // 랜덤 순서

                long currentBidPrice = auction.getHighestBidPrice();

                int added = 0;
                for (User bidderCandidate : allUsers) {
                    if (added >= bidderCount) break;

                    // 출품자 본인은 입찰 불가
                    if (bidderCandidate.getId().equals(auction.getCosigner().getId())) continue;

                    long bidPrice = currentBidPrice + auction.getMinimumBidIncrement();
                    try {
                        Bidding bidding = auction.bid(bidderCandidate, bidPrice);
                        biddingBatch.add(bidding);
                        currentBidPrice = bidPrice;
                        added++;
                        totalBids++;
                    } catch (Exception e) {
                        // 유효성 검사 실패 시 무시하고 다음 유저로 진행
                        continue;
                    }
                }
            }

            if (!biddingBatch.isEmpty()) {
                biddingRepository.saveAll(biddingBatch);
            }

            System.out.printf("📦 페이지 %d 처리 완료, 누적 입찰: %d\n", currentPage + 1, totalBids);
            currentPage++;
        }

        System.out.printf("✅ 전체 완료! 총 %d건 입찰 생성됨.\n", totalBids);
    }

    @Test
    @DisplayName("임의의 10개 경매에 대해 랜덤 입찰 생성")
    public void generateSampleBiddingForTenAuctions() {
        List<Auction> auctions = auctionRepository.findAll(PageRequest.of(0, 10)).getContent();
        List<User> allUsers = userRepository.findAll();

        if (auctions.isEmpty() || allUsers.isEmpty()) {
            System.out.println("❌ 경매 또는 사용자 데이터 부족으로 테스트 종료");
            return;
        }

        List<Bidding> biddingBatch = new ArrayList<>();
        long totalBids = 0;

        for (Auction auction : auctions) {
            if (auction.isInactive()) continue;

            int bidderCount = random.nextInt(10) + 1; // 1~10명
            Collections.shuffle(allUsers);

            long currentBidPrice = auction.getHighestBidPrice();
            int added = 0;

            for (User bidderCandidate : allUsers) {
                if (added >= bidderCount) break;
                if (bidderCandidate.getId().equals(auction.getCosigner().getId())) continue;

                long bidPrice = currentBidPrice + auction.getMinimumBidIncrement();

                try {
                    Bidding bidding = auction.bid(bidderCandidate, bidPrice);
                    biddingBatch.add(bidding);
                    currentBidPrice = bidPrice;
                    added++;
                    totalBids++;
                } catch (Exception e) {
                    continue;
                }
            }
        }

        if (!biddingBatch.isEmpty()) {
            biddingRepository.saveAll(biddingBatch);
        }

        System.out.printf("✅ 샘플 테스트 완료! 10개 경매에 대해 총 %d건 입찰 생성됨.\n", totalBids);
    }

}
