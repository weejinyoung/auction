package com.ourfantasy.auction.config;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private final ResponseCode responseCode;
    private final String message;

    public CustomException(ResponseCode responseCode) {
        this(responseCode, responseCode.getMessage());
    }

    public CustomException(ResponseCode responseCode, String message) {
        super(message);
        this.responseCode = responseCode;
        this.message = message;
    }
}
