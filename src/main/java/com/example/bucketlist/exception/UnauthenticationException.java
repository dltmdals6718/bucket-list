package com.example.bucketlist.exception;

import lombok.Getter;

@Getter
public class UnauthenticationException extends RuntimeException {

    private final ErrorCode errorCode;

    public UnauthenticationException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
