package com.ourfantasy.auction.item.init;

import com.ourfantasy.auction.item.model.Item;
import com.ourfantasy.auction.item.model.ItemCategory;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@SpringBootTest
public class ItemDataInitializerByTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private RandomGenerator randomGenerator;

    private final Random random = new Random();

    @Test
    @EnabledIf("false") // 필요할 때만 true로 변경
    @DisplayName("대량의 아이템 데이터 생성 (유저 각각 3~5개 아이템)")
    public void generateItemData() {
        long startTime = System.currentTimeMillis();
        System.out.println("아이템 데이터 생성 시작...");

        // 페이지 단위로 유저를 가져와서 처리
        int pageSize = 1000;
        int currentPage = 0;
        long totalUsers = 0;
        long totalItems = 0;

        while (true) {
            List<User> userBatch = userRepository.findAll(PageRequest.of(currentPage, pageSize)).getContent();
            if (userBatch.isEmpty()) {
                break;
            }

            totalUsers += userBatch.size();

            List<Item> itemBatch = new ArrayList<>();

            for (User user : userBatch) {
                // 각 유저마다 3~5개의 아이템 생성
                int itemCount = random.nextInt(3) + 3;

                for (int i = 0; i < itemCount; i++) {
                    Item item = createRandomItem(user);
                    itemBatch.add(item);
                }

                totalItems += itemCount;
            }

            // 배치 저장
            itemRepository.saveAll(itemBatch);

            // 진행 상황 출력
            System.out.printf("진행 중: %d명의 유저에 대해 %d개 아이템 생성 완료\n",
                    totalUsers, totalItems);

            currentPage++;
        }

        long endTime = System.currentTimeMillis();
        double elapsedSeconds = (endTime - startTime) / 1000.0;

        System.out.println("아이템 생성 완료!");
        System.out.printf("총 %d명의 유저에 대해 %d개 아이템 생성 (소요 시간: %.2f초)\n",
                totalUsers, totalItems, elapsedSeconds);
    }

    private Item createRandomItem(User owner) {
        // 카테고리 먼저 결정
        ItemCategory category = getRandomCategory();

        // 카테고리에 맞는 이름 생성
        String name = randomGenerator.generateRandomItemNameByCategory(category.name());

        // 디테일 생성 (형용사 + 명사 + 상세 설명)
        StringBuilder detailBuilder = new StringBuilder();
        detailBuilder.append(randomGenerator.generateRandomNickname())
                .append("의 ")
                .append(name)
                .append("입니다. ");

        // 랜덤 텍스트 추가
        int sentenceCount = random.nextInt(5) + 3;
        for (int i = 0; i < sentenceCount; i++) {
            detailBuilder.append(generateRandomSentence()).append(" ");
        }

        return Item.builder()
                .owner(owner)
                .name(name)
                .detail(detailBuilder.toString())
                .category(category)
                .build();
    }

    private String generateRandomSentence() {
        String[] sentenceParts = {
                "상태가 매우 좋습니다.",
                "거의 새 제품입니다.",
                "사용감이 있지만 문제 없이 사용 가능합니다.",
                "희소성이 높은 아이템입니다.",
                "한정판입니다.",
                "인기 상품입니다.",
                "최근에 구매했습니다.",
                "직거래 가능합니다.",
                "택배로 보내드립니다.",
                "사용 설명서가 포함되어 있습니다."
        };

        return sentenceParts[random.nextInt(sentenceParts.length)];
    }

    private ItemCategory getRandomCategory() {
        ItemCategory[] categories = ItemCategory.values();
        return categories[random.nextInt(categories.length)];
    }
}