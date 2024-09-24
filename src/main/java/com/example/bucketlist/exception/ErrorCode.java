package com.example.bucketlist.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    DUPLICATE_MAIL_SIGNUP(HttpStatus.CONFLICT, "이미 등록된 이메일입니다."),
    DUPLICATE_MAIL_VERIFICATION(HttpStatus.TOO_MANY_REQUESTS, "이메일 인증 코드 전송 5분마다 가능합니다."),
    UNAUTHENTICATION(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다."),
    DUPLICATE_POSTER_LIKE(HttpStatus.CONFLICT, "이미 좋아요를 누른 게시글입니다.");

    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

}
