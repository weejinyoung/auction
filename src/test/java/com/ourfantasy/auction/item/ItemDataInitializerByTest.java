package com.ourfantasy.auction.item;

import com.ourfantasy.auction.item.model.Item;
import com.ourfantasy.auction.user.model.User;
import com.ourfantasy.auction.user.repository.UserRepository;
import com.ourfantasy.auction.item.repository.ItemRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.EnabledIf;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@SpringBootTest
public class ItemDataInitializerByTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private final Random random = new Random();

    @Test
    @EnabledIf("false") // 반드시 필요할 때만 true 로 해주세요
    @DisplayName("대량의 아이템 데이터 생성 (20,000명 각각 3~5개 아이템)")
    public void generateItemData() {

        List<User> users = userRepository.findAll();

        System.out.println("총 유저 수: " + users.size());

        List<Item> items = new ArrayList<>();
        int totalItems = 0;

        for (User user : users) {
            int itemCount = random.nextInt(3) + 3;
            for (int i = 0; i < itemCount; i++) {
                Item item = createItem(user, totalItems + i + 1);
                items.add(item);
            }
            totalItems += itemCount;
            if (items.size() >= 5000) {
                itemRepository.saveAll(items);
                items.clear();
                System.out.println("중간 저장 완료: " + totalItems + "개 아이템");
            }
        }

        if (!items.isEmpty()) {
            itemRepository.saveAll(items);
        }

        System.out.println("아이템 생성 완료: 총 " + totalItems + "개");
    }


    private Item createItem(User owner, int itemNumber) {
        StringBuilder detailBuilder = new StringBuilder();
        int repeatCount = random.nextInt(11) + 10;
        detailBuilder.append("detail-".repeat(repeatCount));
        detailBuilder.append(itemNumber);
        String name = "item" + itemNumber;
        return Item.createItem(owner, name, detailBuilder.toString());
    }
}