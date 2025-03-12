package com.ourfantasy.auction.auction;

import com.ourfantasy.auction.item.model.Item;
import com.ourfantasy.auction.item.repository.ItemRepository;
import com.ourfantasy.auction.user.model.User;
import com.ourfantasy.auction.user.repository.UserRepository;
import com.ourfantasy.auction.auction.model.Auction;
import com.ourfantasy.auction.auction.repository.AuctionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.EnabledIf;

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

    private final Random random = new Random();

    @Test
    @EnabledIf("false") // 반드시 필요할 때만 true 로 해주세요
    @DisplayName("사용자의 아이템 중 2~3개를 뽑아서 경매 생성")
    public void generateAuctionData() {

        List<User> users = userRepository.findAll();

        System.out.println("총 유저 수: " + users.size());

        for (User user : users) {
            List<Item> items = itemRepository.findByOwner(user);
            if (items.size() < 2) {
                continue;
            }

            int auctionItemCount = random.nextInt(2) + 2; // 2 또는 3
            for (int i = 0; i < auctionItemCount && i < items.size(); i++) {
                Item item = items.get(i);
                Auction auction = Auction.openAuction(
                        user,
                        item,
                        random.nextInt(41) * 1000L + 10000L, // 무작위 시작 가격: 10,000원에서 50,000원 사이 (천원 단위)
                        random.nextInt(21) * 1000L + 10000L, // 무작위 최소 입찰 증가액: 10,000원에서 30,000원 사이 (천원 단위)
                        item.getCreatedAt().plusDays(7)
                );
                auctionRepository.save(auction);
            }
        }

        System.out.println("경매 생성 완료");
    }
}