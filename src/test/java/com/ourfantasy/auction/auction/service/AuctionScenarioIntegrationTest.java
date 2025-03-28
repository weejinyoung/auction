package com.ourfantasy.auction.auction.service;

import com.ourfantasy.auction.auction.controller.AuctionController;
import com.ourfantasy.auction.auction.service.dto.BidRequest;
import com.ourfantasy.auction.auction.service.dto.GetAuctionResponse;
import com.ourfantasy.auction.user.model.User;
import com.ourfantasy.auction.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

@SpringBootTest
@Transactional
public class AuctionScenarioIntegrationTest {

    @Autowired
    private AuctionController auctionController; // 또는 서비스 레이어까지도 필요 시

    @Autowired
    private UserRepository userRepository;

    private final List<LogEntry> logEntries = Collections.synchronizedList(new ArrayList<>());

    @Qualifier("taskExecutor")
    @Autowired
    private TaskExecutor taskExecutor;

    @Test
    void 시나리오_멀티스레드_성능테스트() throws InterruptedException {
        int threadCount = 10;
        CountDownLatch latch = new CountDownLatch(threadCount);
        long globalStart = System.currentTimeMillis();

        // 랜덤 유저 10명 추출
        List<User> selectedUsers = userRepository.findRandomUsers(threadCount);

        for (int i = 0; i < selectedUsers.size() ; i++) {
            final User user = selectedUsers.get(i); // 각 스레드에 하나의 유저 할당
            taskExecutor.execute(() -> {
                try {
                    runScenario(user.getId().intValue());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        long globalEnd = System.currentTimeMillis();
        System.out.println("전체 수행 시간: " + (globalEnd - globalStart) + "ms");
        generateReport();
    }

    private void runScenario(int threadId) {
        long start = System.currentTimeMillis();
        long t = start;

        // 1. 가까운 경매 조회
        final Long[] auctionIdHolder = new Long[1]; // final로 감싸기 위해 배열로 선언

        t = logApi("getAuctions", threadId, t, () -> {

            // TODO. pageNumber 도 랜덤으로 가능하게 진행
            Page<GetAuctionResponse> auctions =
                    auctionController.getNearestClosingAuctionsByCategory(PageRequest.of(0, 10), "ACCESSORY");

            List<GetAuctionResponse> content = auctions.getContent();
            if (!content.isEmpty()) {
                GetAuctionResponse picked = content.get(new Random().nextInt(content.size()));
                auctionIdHolder[0] = picked.getAuctionId(); // 랜덤으로 하나 선택해서 ID 저장
            } else {
                System.err.println("Thread-" + threadId + ": 경매 목록이 비어 있습니다.");
                auctionIdHolder[0] = null;
            }
        });

        Long selectedAuctionId = auctionIdHolder[0];
        if (selectedAuctionId == null) {
            System.err.println("Thread-" + threadId + ": 유효한 경매 ID가 없어 시나리오 종료");
            return;
        }

        // 2. 상세 조회
        t = logApi("getAuctionDetail", threadId, t, () -> {
            GetAuctionResponse detail = auctionController.getAuctionDetail(selectedAuctionId);
        });


        // 3. 비딩
        t = logApi("bid", threadId, t, () -> {
            // TODO. auction 의 bid price(맞추기 - 최소 비드보다는 높게)
            GetAuctionResponse detail = auctionController.getAuctionDetail(selectedAuctionId);
            auctionController.bid(selectedAuctionId, new BidRequest((long) threadId, detail.auction().minimumBidIncrement() + 1000));
        });

        logEntries.add(new LogEntry(threadId, "Total", start, System.currentTimeMillis()));
    }

    private long logApi(String name, int threadId, long prevTime, Runnable call) {
        long start = System.currentTimeMillis();
        try {
            call.run();
        } catch (Exception e) {
            System.err.println("Error in " + name + ": " + e.getMessage());
        }
        long end = System.currentTimeMillis();
        logEntries.add(new LogEntry(threadId, name, start, end));
        return end;
    }

    private void generateReport() {
        for (LogEntry entry : logEntries) {
            System.out.printf("Thread-%d %s: %dms\n", entry.threadId, entry.apiName, (entry.end - entry.start));
        }
    }

    static class LogEntry {
        int threadId;
        String apiName;
        long start, end;

        LogEntry(int threadId, String apiName, long start, long end) {
            this.threadId = threadId;
            this.apiName = apiName;
            this.start = start;
            this.end = end;
        }
    }
}
