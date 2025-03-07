package com.ourfantasy.auction.config.response;

import lombok.Getter;

import java.util.Objects;

@Getter
public class ApiResponse<T> {
    private final String responseCode;
    private final String message;
    private final T data;

    private ApiResponse(String responseCode, String message, T data) {
        this.responseCode = responseCode;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResponse<T> ok() {
        return ok(null);
    }

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(
                ResponseCode.OK.getCode(),
                ResponseCode.OK.getMessage(),
                data
        );
    }

    public static <T> ApiResponse<T> ok(ResponseCode responseCode) {
        return ok(responseCode, null);
    }

    public static <T> ApiResponse<T> ok(ResponseCode responseCode, T data) {
        return new ApiResponse<>(
                responseCode.getCode(),
                responseCode.getMessage(),
                data
        );
    }

    public static <T> ApiResponse<T> error(ResponseCode responseCode) {
        return new ApiResponse<>(
                responseCode.getCode(),
                responseCode.getMessage(),
                null
        );
    }

    public static <T> ApiResponse<T> error(
            ResponseCode responseCode,
            String errorMessage
    ) {
        return new ApiResponse<>(
                responseCode.getCode(),
                errorMessage,
                null
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApiResponse<?> that = (ApiResponse<?>) o;
        return Objects.equals(responseCode, that.responseCode) &&
                Objects.equals(message, that.message) &&
                Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(responseCode, message, data);
    }

    @Override
    public String toString() {
        return "Response{" +
                "responseCode='" + responseCode + '\'' +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
