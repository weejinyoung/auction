package com.ourfantasy.auction.item;

import com.ourfantasy.auction.item.model.Item;
import com.ourfantasy.auction.item.model.ItemCategory;
import com.ourfantasy.auction.item.repository.ItemRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.junit.jupiter.EnabledIf;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

@SpringBootTest
@Transactional
@Commit
public class ItemCategoryUpdater {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private EntityManager entityManager;

    private final Random random = new Random();

    @Test
    @EnabledIf("false") // 실행이 필요할 때만 true로 변경
    @DisplayName("배치 단위로 아이템 로드 후 카테고리 업데이트")
    public void updateCategoriesInBatches() {
        int batchSize = 1000;
        int page = 0;
        long totalUpdated = 0;
        boolean hasNext = true;

        while (hasNext) {
            // 각 배치마다 새로운 트랜잭션에서 처리
            int processed = processItemBatch(page, batchSize);

            if (processed < batchSize) {
                hasNext = false;
            }

            totalUpdated += processed;
            System.out.printf("처리 중: %d 페이지, 총 %d 아이템 업데이트 완료%n", page, totalUpdated);

            page++;
        }

        System.out.println("모든 아이템 카테고리 업데이트 완료: 총 " + totalUpdated + "개");
    }

    public int processItemBatch(int page, int batchSize) {
        Pageable pageable = PageRequest.of(page, batchSize);
        Page<Item> itemPage = itemRepository.findAll(pageable);
        List<Item> items = itemPage.getContent();

        if (items.isEmpty()) {
            return 0;
        }

        // 각 아이템에 랜덤 카테고리 할당
        for (Item item : items) {
            ItemCategory randomCategory = getRandomCategory();
            item.changeCategory(randomCategory);
        }

        // 변경된 항목 저장
        itemRepository.saveAll(items);

        // 영속성 컨텍스트 초기화 (메모리 확보)
        entityManager.flush();
        entityManager.clear();

        return items.size();
    }

    private ItemCategory getRandomCategory() {
        ItemCategory[] categories = ItemCategory.values();
        return categories[random.nextInt(categories.length)];
    }
}