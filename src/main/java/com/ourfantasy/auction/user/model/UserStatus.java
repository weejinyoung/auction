package com.ourfantasy.auction.user.model;

public enum UserStatus {
    ACTIVE,     // 활성화된 사용자
    INACTIVE,   // 비활성화된 사용자
    SUSPENDED;    // 정지된 사용자

    public String getDisplayName() {
        return switch (this) {
            case ACTIVE -> "활성화";
            case INACTIVE -> "비활성화";
            case SUSPENDED -> "계정 정지";
        };
    }
}
