package com.example.bucketlist.exception;

import com.example.bucketlist.exception.authentication.ForbiddenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@RestControllerAdvice
public class GlobalApiExceptionHandler {

    private MessageSource messageSource;

    @Autowired
    public GlobalApiExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler({InValidInputException.class})
    public ResponseEntity<Map> validException(InValidInputException ex) {

        Map<String, Object> map = new HashMap<>();
        BindingResult bindingResult = ex.getBindingResult();

        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        for (FieldError fieldError : fieldErrors) {
            map.put(fieldError.getField(), messageSource.getMessage(fieldError.getCode(), fieldError.getArguments(), fieldError.getDefaultMessage(), Locale.getDefault()));
        }

        List<ObjectError> globalErrors = bindingResult.getGlobalErrors();
        for (ObjectError globalError : globalErrors) {
            map.put(globalError.getObjectName(), messageSource.getMessage(globalError.getCode(), globalError.getArguments(), globalError.getDefaultMessage(), Locale.getDefault()));
        }

        return ResponseEntity
                .badRequest()
                .body(map);
    }

    @ExceptionHandler({ForbiddenException.class})
    public ResponseEntity<String> forbiddenException(ForbiddenException ex) {
        return ResponseEntity
                .status(ex.getErrorCode().getHttpStatus())
                .body(ex.getMessage());
    }

    @ExceptionHandler({UnauthenticationException.class})
    public ResponseEntity<String> unauthenticationException(UnauthenticationException ex) {
        return ResponseEntity
                .status(ex.getErrorCode().getHttpStatus())
                .body(ex.getErrorCode().getMessage());
    }

    @ExceptionHandler({DuplicatePosterLikeException.class})
    public ResponseEntity<String> duplicatePosterLikeException(DuplicatePosterLikeException ex) {
        return ResponseEntity
                .status(ex.getErrorCode().getHttpStatus())
                .body(ex.getErrorCode().getMessage());
    }






}
