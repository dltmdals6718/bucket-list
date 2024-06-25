package com.example.bucketlist.exception;

import lombok.Getter;

@Getter
public class DuplicateMailVerificationException extends RuntimeException {

    private final ErrorCode errorCode;

    public DuplicateMailVerificationException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
