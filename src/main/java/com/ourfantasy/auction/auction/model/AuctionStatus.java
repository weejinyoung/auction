package com.ourfantasy.auction.auction.model;

public enum AuctionStatus {
    ACTIVE,     // 진행 중인 경매
    COMPLETED,  // 낙찰되어 완료된 경매
    PENDING,    // 대기 중(승인 대기 등)
    CANCELED,    // 취소된 경매
    PASSED_IN;  // 유찰된 경매

    public String getDisplayName() {
        return switch (this) {
            case ACTIVE -> "진행 중";
            case COMPLETED -> "낙찰 완료";
            case PENDING -> "대기 중";
            case CANCELED -> "취소";
            case PASSED_IN -> "유찰";
        };
    }
}