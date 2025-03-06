package com.ourfantasy.auction.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class CustomExceptionHandler {

    private static final String API_EXCEPTION_LOG = "[Exception] : {}";

    private ResponseEntity<ApiResponse<Void>> toResponseEntity(CustomException ex) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.error(
                        ex.getResponseCode(),
                        ex.getMessage()
                ));
    }

    @org.springframework.web.bind.annotation.ExceptionHandler
    public ResponseEntity<ApiResponse<Void>> handlePeautyCustomerException(CustomException ex) {
        log.error(API_EXCEPTION_LOG, ex.getMessage(), ex);
        return toResponseEntity(ex);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUncheckedException(Exception ex) {
        log.error("[Unknown Error] : {}", ex.getMessage(), ex);
        return toResponseEntity(new CustomException(ResponseCode.INTERNAL_SERVER_ERROR));
    }

    @org.springframework.web.bind.annotation.ExceptionHandler({
            ServletRequestBindingException.class,
            HttpRequestMethodNotSupportedException.class,
            HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class,
            MethodArgumentNotValidException.class,
            MissingServletRequestParameterException.class,
            BindException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiResponse<Void>> exceptionHandler(Exception ex) {
        String message;

        if (ex instanceof MethodArgumentNotValidException) {
            message = getSortedErrors(((MethodArgumentNotValidException) ex).getBindingResult()).toString();
        } else if (ex instanceof BindException) {
            message = getSortedErrors(((BindException) ex).getBindingResult()).toString();
        } else if (ex instanceof HttpMessageNotReadableException) {
            String exMessage = ex.getMessage();
            message = exMessage != null && exMessage.contains(":")
                    ? exMessage.split(":")[0]
                    : exMessage;
        } else {
            message = ResponseCode.BAD_REQUEST.getMessage();
        }

        log.warn("[BAD_REQUEST] : {}", message, ex);
        return toResponseEntity(new CustomException(
                ResponseCode.BAD_REQUEST,
                message != null ? message : ResponseCode.BAD_REQUEST.getMessage()
        ));
    }

    private List<String> getSortedErrors(BindingResult bindingResult) {
        List<String> errors = new ArrayList<>();

        for (FieldError error : bindingResult.getFieldErrors()) {
            errors.add(error.getField() + ": " + error.getDefaultMessage());
        }

        for (ObjectError error : bindingResult.getGlobalErrors()) {
            errors.add(error.getObjectName() + ": " + error.getDefaultMessage());
        }

        return errors;
    }
}
