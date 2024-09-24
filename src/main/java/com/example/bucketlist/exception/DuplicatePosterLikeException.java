package com.example.bucketlist.exception;

import lombok.Getter;

@Getter
public class DuplicatePosterLikeException extends RuntimeException {

    private final ErrorCode errorCode;

    public DuplicatePosterLikeException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
