package com.example.hanium2023.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    INVALID_PERMISSION(HttpStatus.UNAUTHORIZED, "사용자가 권한이 없습니다."),
    DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "DB에러"),
    PUBLIC_API_ERROR(HttpStatus.BAD_REQUEST, "Public api 호출 에러")
    ;
    private HttpStatus status;
    private String message;
}