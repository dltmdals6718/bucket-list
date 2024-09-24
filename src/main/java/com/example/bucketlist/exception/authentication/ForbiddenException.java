package com.example.bucketlist.exception.authentication;

import com.example.bucketlist.exception.ErrorCode;
import lombok.Getter;

@Getter
public class ForbiddenException extends RuntimeException {
    private final ErrorCode errorCode;

    public ForbiddenException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
