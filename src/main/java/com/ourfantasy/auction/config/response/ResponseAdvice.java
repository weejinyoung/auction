package com.ourfantasy.auction.config.response;

import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@ControllerAdvice
public class ResponseAdvice implements ResponseBodyAdvice<Object> {

    public static final String PAGE_SIZE = "x-page-size";
    public static final String PAGE_NUMBER = "x-page-number";
    public static final String PAGE_TOTAL_PAGES = "x-page-totalpages";
    public static final String PAGE_TOTAL_ELEMENTS = "x-page-totalelements";

    @Override
    public boolean supports(
            MethodParameter returnType,
            Class<? extends HttpMessageConverter<?>> converterType
    ) {
        return !ApiResponse.class.isAssignableFrom(returnType.getParameterType()) &&
                (returnType.getMethod() != null &&
                        returnType.getMethod().getDeclaringClass().getPackage().toString().contains("auction"));
    }

    @Override
    public Object beforeBodyWrite(
            Object body,
            MethodParameter returnType,
            MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType,
            ServerHttpRequest request,
            ServerHttpResponse response
    ) {
        switch (body) {
            case null -> {
                return ApiResponse.ok(null);
            }
            case Page<?> page -> {
                response.getHeaders().set(PAGE_TOTAL_PAGES, String.valueOf(page.getTotalPages()));
                response.getHeaders().set(PAGE_TOTAL_ELEMENTS, String.valueOf(page.getTotalElements()));
                return ApiResponse.ok(page.getContent());
            }
            case ApiResponse<?> apiResponse -> {
                return body;
            }
            default -> {
            }
        }

        return ApiResponse.ok(body);
    }
}