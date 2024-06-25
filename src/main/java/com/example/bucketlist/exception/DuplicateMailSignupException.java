package com.example.bucketlist.exception;


import lombok.Getter;

@Getter
public class DuplicateMailSignupException extends RuntimeException {

    private final ErrorCode errorCode;

    public DuplicateMailSignupException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
