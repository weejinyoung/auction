package com.ourfantasy.auction.auction.model;

import com.ourfantasy.auction.auction.repository.AuctionRepository;
import com.ourfantasy.auction.auction.repository.BiddingRepository;
import com.ourfantasy.auction.auction.service.AuctionService;
import com.ourfantasy.auction.auction.service.dto.BidRequest;
import com.ourfantasy.auction.auction.service.dto.OpenAuctionRequest;
import com.ourfantasy.auction.auction.service.dto.OpenAuctionResponse;
import com.ourfantasy.auction.item.model.Item;
import com.ourfantasy.auction.item.model.ItemCategory;
import com.ourfantasy.auction.item.repository.ItemRepository;
import com.ourfantasy.auction.user.model.User;
import com.ourfantasy.auction.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.task.TaskExecutor;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

@SpringBootTest
@ActiveProfiles("test")
public class AuctionScenarioTest {

    @Autowired private AuctionService auctionService;
    @Autowired private UserRepository userRepository;
    @Autowired private ItemRepository itemRepository;
    @Autowired private AuctionRepository auctionRepository;
    @Autowired private BiddingRepository biddingRepository;
    @Autowired @Qualifier("taskExecutor") private TaskExecutor taskExecutor;
    private final int userCount = 10;
    private final int bidCount = 5;
    private final Random random = new Random();
    private List<User> savedUsers;
    private User owner;
    private Item savedItem;

    @BeforeEach
    void setUp() {
        List<User> users = new ArrayList<>();
        for (int i = 1; i < userCount; i++) {
            User user = User.builderWithValidate()
                    .nickname("test_user_nickname")
                    .email("test_user@test.com")
                    .build();
            users.add(user);
        }

        savedUsers = userRepository.saveAll(users);
        owner = savedUsers.getFirst();

        Item item = Item.builder()
                .owner(owner)
                .name("test item name")
                .detail("test item detail")
                .category(ItemCategory.ACCESSORY)
                .build();

        savedItem = itemRepository.save(item);
    }

    @Test
    void testConcurrentBidding() throws InterruptedException {
        OpenAuctionResponse response = auctionService.openAuction(
                new OpenAuctionRequest(
                        owner.getId(),
                        savedItem.getId(),
                        10000L,
                        1000L,
                        LocalDateTime.now().plusDays(1))
        );

        long minimumBidPrice = response.startingPrice() + response.minimumBidIncrement();

        CountDownLatch bisStartLatch = new CountDownLatch(1);
        CountDownLatch bidCompleteLatch = new CountDownLatch(bidCount - 1);

        for(int i = 2; i <= bidCount; i++) {
            int finalI = i;
            long bidPrice = i * 1000L;
            long finalBidPrice = minimumBidPrice + bidPrice;
            taskExecutor.execute(() -> {
                try {
                    bisStartLatch.await();
                    auctionService.bid(response.auctionId(), new BidRequest(savedUsers.get(finalI).getId(), finalBidPrice));
                } catch (Exception ignored) {
                    System.out.println("User " + finalI + " bid failed");
                } finally {
                    bidCompleteLatch.countDown();
                }
            });
        }

        bisStartLatch.countDown();
        bidCompleteLatch.await();

        Auction auction = auctionRepository.findById(response.auctionId())
                .orElseThrow(() -> new RuntimeException("Auction not found"));

        System.out.println(auction.getHighestBidPrice());
        biddingRepository.findBiddingsByAuctionOrderByCreatedAtAsc(auction)
                .forEach(bidding -> System.out.println(bidding.getBidPrice()));

    }


}
