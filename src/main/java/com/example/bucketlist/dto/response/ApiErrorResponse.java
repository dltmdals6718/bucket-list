package com.example.bucketlist.dto.response;

import com.example.bucketlist.exception.ErrorCode;
import lombok.Getter;

@Getter
public class ApiErrorResponse {
    private final String error;
    private final String message;

    public ApiErrorResponse(ErrorCode errorCode) {
        this.error = errorCode.name();
        this.message = errorCode.getMessage();
    }
}
