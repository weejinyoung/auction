package com.ourfantasy.auction.config.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseCode {
    // 기존 코드
    OK("0000", "OK"),

    // 일반적인 클라이언트 에러
    BAD_REQUEST("9400", "Bad Request"),
    WRONG_PARAMETER("9401", "Invalid Parameter"),
    METHOD_NOT_ALLOWED("9402", "Method Not Allowed"),
    UNAUTHORIZED("9403", "Unauthorized"),
    REQUEST_TIMEOUT("9900", "Request Timeout"),
    NOT_YET_IMPLEMENTED("9901", "Not Yet Implemented"),
    INTERNAL_SERVER_ERROR("9999", "Internal Server Error"),

    // 경매 관련 오류 코드 (1000번대)
    AUCTION_NOT_FOUND("1001", "경매를 찾을 수 없습니다"),
    AUCTION_ALREADY_ENDED("1002", "이미 종료된 경매입니다"),
    AUCTION_NOT_ACTIVE("1003", "활성화되지 않은 경매입니다"),
    AUCTION_NOT_STARTED("1004", "아직 시작되지 않은 경매입니다"),
    AUCTION_INVALID_STATUS("1005", "유효하지 않은 경매 상태입니다"),
    AUCTION_REQUIRED("1006", "경매 정보는 필수입니다"),
    AUCTION_INACTIVE_USER("1007", "비활성화된 사용자는 경매를 개설할 수 없습니다"),
    CLOSING_TIME_INVALID("1008", "입찰 종료시간은 현재 시간 이후여야 합니다"),
    INVALID_STARTING_PRICE("1009", "경매 시작가는 0 이상이어야 합니다"),
    INVALID_MINIMUM_BID_INCREMENT("1010", "최소 입찰 증가액은 0 이상이어야 합니다"),
    ITEM_NOT_OWNED("1011", "아이템의 소유주만 경매를 시작할 수 있습니다"),

    // 입찰 관련 오류 코드 (2000번대)
    BID_AMOUNT_TOO_LOW("2001", "입찰 금액이 너무 낮습니다"),
    BID_INVALID_PRICE("2002", "유효하지 않은 입찰 금액입니다"),
    BID_ALREADY_EXISTS("2003", "이미 동일한 입찰이 존재합니다"),
    BID_NOT_FOUND("2004", "입찰을 찾을 수 없습니다"),
    BID_OWNER_CONFLICT("2005", "자신의 경매에는 입찰할 수 없습니다"),
    BID_CREATE_FAILED("2006", "입찰 생성에 실패했습니다"),
    BID_INCREMENT_VIOLATION("2007", "최소 입찰 증가액을 충족하지 않습니다"),
    BIDDER_REQUIRED("2008", "입찰자 정보는 필수입니다"),
    INACTIVE_BIDDER("2009", "비활성화된 사용자는 응찰을 할 수 없습니다"),

    // 사용자 관련 오류 코드 (3000번대)
    USER_NOT_FOUND("3001", "사용자를 찾을 수 없습니다"),
    USER_UNAUTHORIZED("3002", "권한이 없는, 또는 로그인하지 않은 사용자입니다"),
    USER_INVALID_INPUT("3003", "유효하지 않은 사용자 입력입니다"),
    INVALID_NICKNAME("3004", "유효하지 않은 닉네임입니다"),
    INVALID_EMAIL("3005", "유효하지 않은 이메일입니다"),

    // 아이템 관련 오류 코드 (4000번대)
    ITEM_NOT_FOUND("4001", "아이템을 찾을 수 없습니다"),
    ITEM_ALREADY_IN_AUCTION("4002", "이미 경매 중인 아이템입니다"),
    ITEM_CATEGORY_NOT_FOUND_BY_DISPLAY_NAME("4003", "해당 카테고리를 찾을 수 없습니다"),

    // 낙찰 관련 오류 코드 (5000번대)
    SUCCESSFUL_BID_NOT_FOUND("5001", "낙찰 정보를 찾을 수 없습니다"),
    SUCCESSFUL_BID_ALREADY_EXISTS("5002", "이미 낙찰된 경매입니다"),
    SUCCESSFUL_BID_CREATE_FAILED("5003", "낙찰 처리에 실패했습니다");

    private final String code;
    private final String message;

    public static ResponseCode lookupResponseCode(String code) {
        for (ResponseCode responseCode : values()) {
            if (responseCode.getCode().equals(code)) {
                return responseCode;
            }
        }
        return null;
    }
}