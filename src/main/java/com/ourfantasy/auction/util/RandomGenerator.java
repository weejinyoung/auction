package com.ourfantasy.auction.util;

import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class RandomGenerator {

    private final List<String> adjectives = new ArrayList<>();
    private final List<String> nouns = new ArrayList<>();
    private final List<String> accessories = new ArrayList<>();
    private final List<String> clothing = new ArrayList<>();
    private final List<String> digital = new ArrayList<>();
    private final List<String> shoes = new ArrayList<>();
    private final Random random = new Random();

    public RandomGenerator(ResourceLoader resourceLoader) {
        try {
            // 형용사 파일 로드
            var adjectiveFile = resourceLoader.getResource("classpath:properties/random/adjectives.txt");
            try (var reader = new BufferedReader(new InputStreamReader(adjectiveFile.getInputStream()))) {
                reader.lines().forEach(adjectives::add);
            }

            // 명사 파일 로드
            var nounFile = resourceLoader.getResource("classpath:properties/random/nouns.txt");
            try (var reader = new BufferedReader(new InputStreamReader(nounFile.getInputStream()))) {
                reader.lines().forEach(nouns::add);
            }

            // 액세서리 파일 로드
            var accessoryFile = resourceLoader.getResource("classpath:properties/random/accessories.txt");
            try (var reader = new BufferedReader(new InputStreamReader(accessoryFile.getInputStream()))) {
                reader.lines().forEach(accessories::add);
            }

            // 의류 파일 로드
            var clothingFile = resourceLoader.getResource("classpath:properties/random/clothing.txt");
            try (var reader = new BufferedReader(new InputStreamReader(clothingFile.getInputStream()))) {
                reader.lines().forEach(clothing::add);
            }

            // 디지털 제품 파일 로드
            var digitalFile = resourceLoader.getResource("classpath:properties/random/digital.txt");
            try (var reader = new BufferedReader(new InputStreamReader(digitalFile.getInputStream()))) {
                reader.lines().forEach(digital::add);
            }

            // 신발 파일 로드
            var shoesFile = resourceLoader.getResource("classpath:properties/random/shoes.txt");
            try (var reader = new BufferedReader(new InputStreamReader(shoesFile.getInputStream()))) {
                reader.lines().forEach(shoes::add);
            }
        } catch (IOException e) {
            throw new RuntimeException("랜덤 데이터 파일을 로드하는데 실패했습니다.", e);
        }
    }

    /**
     * 랜덤 닉네임 생성
     * @return 형용사 + 명사 형태의 랜덤 닉네임
     */
    public String generateRandomNickname() {
        return adjectives.get(random.nextInt(adjectives.size())) + " " +
                nouns.get(random.nextInt(nouns.size()));
    }

    /**
     * 숫자를 포함한 랜덤 닉네임 생성
     * @return 형용사 + 명사 + 숫자 형태의 랜덤 닉네임
     */
    public String generateRandomNicknameWithNumber() {
        return generateRandomNickname() + " " + random.nextInt(1000);
    }

    /**
     * 랜덤 액세서리 이름 생성
     * @return 형용사 + 액세서리 형태의 이름
     */
    public String generateRandomAccessory() {
        if (accessories.isEmpty()) {
            return "랜덤 액세서리";
        }
        return adjectives.get(random.nextInt(adjectives.size())) + " " +
                accessories.get(random.nextInt(accessories.size()));
    }

    /**
     * 랜덤 의류 이름 생성
     * @return 형용사 + 의류 형태의 이름
     */
    public String generateRandomClothing() {
        if (clothing.isEmpty()) {
            return "랜덤 의류";
        }
        return adjectives.get(random.nextInt(adjectives.size())) + " " +
                clothing.get(random.nextInt(clothing.size()));
    }

    /**
     * 랜덤 디지털 제품 이름 생성
     * @return 형용사 + 디지털 제품 형태의 이름
     */
    public String generateRandomDigital() {
        if (digital.isEmpty()) {
            return "랜덤 디지털 제품";
        }
        return adjectives.get(random.nextInt(adjectives.size())) + " " +
                digital.get(random.nextInt(digital.size()));
    }

    /**
     * 랜덤 신발 이름 생성
     * @return 형용사 + 신발 형태의 이름
     */
    public String generateRandomShoes() {
        if (shoes.isEmpty()) {
            return "랜덤 신발";
        }
        return adjectives.get(random.nextInt(adjectives.size())) + " " +
                shoes.get(random.nextInt(shoes.size()));
    }

    /**
     * 주어진 범위 내에서 랜덤 가격 생성
     * @param min 최소 가격
     * @param max 최대 가격
     * @return min에서 max 사이의 랜덤 가격
     */
    public int generateRandomPrice(int min, int max) {
        return min + random.nextInt(max - min + 1);
    }

    /**
     * 주어진 범위 내에서 랜덤 가격 생성 (1000 단위로 반올림)
     * @param min 최소 가격
     * @param max 최대 가격
     * @return 1000 단위로 반올림된 랜덤 가격
     */
    public int generateRandomRoundedPrice(int min, int max) {
        int price = min + random.nextInt(max - min + 1);
        return Math.round(price / 1000.0f) * 1000;
    }

    /**
     * 카테고리에 따른 랜덤 상품명 생성
     * @param category 상품 카테고리
     * @return 해당 카테고리에 맞는 랜덤 상품명
     */
    public String generateRandomItemNameByCategory(String category) {
        return switch (category.toUpperCase()) {
            case "ACCESSORY" -> generateRandomAccessory();
            case "CLOTHING" -> generateRandomClothing();
            case "DIGITAL" -> generateRandomDigital();
            case "SHOES" -> generateRandomShoes();
            default -> generateRandomNickname(); // 기본 형식 사용
        };
    }

    /**
     * 랜덤한 boolean 값 생성
     * @param probabilityTrue true가 나올 확률 (0.0 ~ 1.0)
     * @return 랜덤 boolean 값
     */
    public boolean generateRandomBoolean(double probabilityTrue) {
        return random.nextDouble() < probabilityTrue;
    }

    /**
     * 리스트에서 랜덤 요소 선택
     * @param list 선택할 리스트
     * @return 리스트에서 랜덤으로 선택된 요소
     */
    public <T> T getRandomElement(List<T> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.get(random.nextInt(list.size()));
    }
}