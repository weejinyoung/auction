package com.ourfantasy.auction.auction.init;

import com.ourfantasy.auction.auction.model.Auction;
import com.ourfantasy.auction.auction.repository.AuctionRepository;
import com.ourfantasy.auction.item.model.Item;
import com.ourfantasy.auction.item.repository.ItemRepository;
import com.ourfantasy.auction.user.model.User;
import com.ourfantasy.auction.user.repository.UserRepository;
import com.ourfantasy.auction.util.RandomGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@SpringBootTest
public class AuctionDataInitializerByTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private AuctionRepository auctionRepository;

    @Autowired
    private RandomGenerator randomGenerator;

    private final Random random = new Random();

    @Test
    @EnabledIf("false") // 필요할 때만 true로 변경
    @DisplayName("사용자의 아이템 중 2~3개를 뽑아서 경매 생성")
    public void generateAuctionData() {
        long startTime = System.currentTimeMillis();
        System.out.println("경매 데이터 생성 시작...");

        // 페이지 단위로 유저를 가져와서 처리
        int pageSize = 1000;
        int currentPage = 0;
        long totalUsers = 0;
        long totalAuctions = 0;

        while (true) {
            List<User> userBatch = userRepository.findAll(PageRequest.of(currentPage, pageSize)).getContent();
            if (userBatch.isEmpty()) {
                break;
            }

            totalUsers += userBatch.size();
            List<Auction> auctionBatch = new ArrayList<>();

            for (User user : userBatch) {
                List<Item> userItems = itemRepository.findByOwner(user);

                // 사용자에게 아이템이 2개 미만이면 건너뜀
                if (userItems.size() < 2) {
                    continue;
                }

                // 아이템을 섞어서 랜덤하게 선택
                Collections.shuffle(userItems);

                // 2~3개의 아이템을 선택해 경매 생성
                int auctionItemCount = Math.min(random.nextInt(2) + 2, userItems.size());

                for (int i = 0; i < auctionItemCount; i++) {
                    Item item = userItems.get(i);

                    // 랜덤 시작가와 최소 입찰 증가액 설정
                    long startingPrice = randomGenerator.generateRandomRoundedPrice(1000, 50000);
                    long minimumBidIncrement = startingPrice / 10;

                    // 마감일 설정 (현재로부터 3~14일 사이)
                    LocalDateTime closingAt = LocalDateTime.now().plusDays(random.nextInt(12) + 3);

                    Auction auction = Auction.builderWithValidate()
                            .cosigner(user)
                            .item(item)
                            .startingPrice(startingPrice)
                            .minimumBidIncrement(minimumBidIncrement)
                            .closingAt(closingAt)
                            .build();

                    auctionBatch.add(auction);
                    totalAuctions++;
                }
            }

            // 배치 저장
            if (!auctionBatch.isEmpty()) {
                auctionRepository.saveAll(auctionBatch);
            }

            // 진행 상황 출력
            System.out.printf("진행 중: %d명의 유저에 대해 %d개 경매 생성 완료\n",
                    totalUsers, totalAuctions);

            currentPage++;
        }

        long endTime = System.currentTimeMillis();
        double elapsedSeconds = (endTime - startTime) / 1000.0;

        System.out.println("경매 생성 완료!");
        System.out.printf("총 %d개 경매 생성 (소요 시간: %.2f초)\n",
                totalAuctions, elapsedSeconds);
    }
}