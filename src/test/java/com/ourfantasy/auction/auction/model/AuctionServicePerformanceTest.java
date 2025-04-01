package com.ourfantasy.auction.auction.model;

import com.ourfantasy.auction.auction.service.AuctionService;
import com.ourfantasy.auction.auction.service.dto.GetAuctionResponse;
import com.ourfantasy.auction.auction.service.dto.GetAuctionResponseWithRating;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;



@SpringBootTest
public class AuctionServicePerformanceTest {

    @Autowired
    private AuctionService auctionService;

    @Test
    @DisplayName("기존 vs 평점 포함 메서드 성능 비교")
    public void compareAuctionServicePerformance() {
        Pageable pageable = PageRequest.of(0, 10);
        String itemCategory = "악세사리"; // 예: 실제 존재하는 카테고리로

        // 기존 메서드 시간 측정
        long startOld = System.nanoTime();
        Page<GetAuctionResponse> oldResult = auctionService.getNearestClosingAuctionsByCategory(pageable, itemCategory);
        long endOld = System.nanoTime();
        long timeOld = (endOld - startOld) / 1_000_000; // ms 단위

        // 새 메서드 시간 측정
        long startNew = System.nanoTime();
        Page<GetAuctionResponseWithRating> newResult = auctionService.getNearestClosingAuctionsByCategoryWithRating(pageable, itemCategory);
        long endNew = System.nanoTime();
        long timeNew = (endNew - startNew) / 1_000_000; // ms 단위

        // 출력
        System.out.println("⏱ 기존 메서드 시간: " + timeOld + " ms, 결과 개수: " + oldResult.getContent().size());
        System.out.println("⏱ 평점 포함 메서드 시간: " + timeNew + " ms, 결과 개수: " + newResult.getContent().size());
    }
}
