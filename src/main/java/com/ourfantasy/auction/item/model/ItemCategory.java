package com.ourfantasy.auction.item.model;

public enum ItemCategory {
    DIGITAL,    // 디지털
    FURNITURE,  // 가구
    CLOTHING,   // 의류
    SHOES,      // 신발
    ACCESSORY,  // 악세사리
    BOOK,       // 책
    ETC;        // 기타

    public String getDisplayName() {
        return switch (this) {
            case DIGITAL -> "디지털";
            case FURNITURE -> "가구";
            case CLOTHING -> "의류";
            case SHOES -> "신발";
            case ACCESSORY -> "악세사리";
            case BOOK -> "책";
            case ETC -> "기타";
        };
    }
}
