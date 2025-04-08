package com.ourfantasy.auction.auction.repository;

import com.ourfantasy.auction.auction.service.AuctionService;
import com.ourfantasy.auction.auction.service.dto.GetAuctionResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@SpringBootTest
public class AuctionServicePerformanceTest {

    @Autowired
    private AuctionService auctionService;

    @Test
    @DisplayName("카테고리별 경매 조회: 기본 vs 좋아요+팔로우 포함 비교")
    public void compareAuctionQueryPerformance() {
        String itemCategory = "디지털"; // displayName 기준 (예: "전자기기", "의류" 등)
        PageRequest pageable = PageRequest.of(0, 20);

        System.out.println("⏱️ 테스트 시작: 카테고리 = " + itemCategory);

        // 첫 번째 메서드 실행 시간 측정
        long start1 = System.currentTimeMillis();
        Page<GetAuctionResponse> result1 = auctionService.getNearestClosingAuctionsByCategory(pageable, itemCategory);
        long end1 = System.currentTimeMillis();
        System.out.printf("✅ [기본 조회] 실행 시간: %.2f초 (결과 수: %d)\n", (end1 - start1) / 1000.0, result1.getTotalElements());

        // 두 번째 메서드 실행 시간 측정
        long start2 = System.currentTimeMillis();
        Page<GetAuctionResponse> result2 = auctionService.getNearestClosingAuctionsByCategoryWithLikeAndFollow(pageable, itemCategory);
        long end2 = System.currentTimeMillis();
        System.out.printf("✅ [좋아요+팔로우 포함 조회] 실행 시간: %.2f초 (결과 수: %d)\n", (end2 - start2) / 1000.0, result2.getTotalElements());

        // 비교 출력
        double diff = (end2 - start2) - (end1 - start1);
        System.out.printf("📊 실행 시간 차이: %.2f초 (좋아요+팔로우 쪽이 %s)\n",
                Math.abs(diff) / 1000.0,
                diff > 0 ? "느림" : "빠름");
    }
}
