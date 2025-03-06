package com.ourfantasy.auction.config;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseCode {
    OK("0000", "OK"),

    // 클라이언트 에러
    BAD_REQUEST("9400", "Bad Request"),
    WRONG_PARAMETER("9401", "Invalid Parameter"),
    METHOD_NOT_ALLOWED("9402", "Method Not Allowed"),
    UNAUTHORIZED("9403", "Unauthorized"),
    REQUEST_TIMEOUT("9900", "Request Timeout"),
    NOT_YET_IMPLEMENTED("9901", "Not Yet Implemented"),
    INTERNAL_SERVER_ERROR("9999", "Internal Server Error");

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