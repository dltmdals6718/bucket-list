package com.example.bucketlist.exception;

import lombok.Getter;

@Getter
public class DuplicatePosterCommentLikeException extends RuntimeException {

    private final ErrorCode errorCode;

    public DuplicatePosterCommentLikeException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
