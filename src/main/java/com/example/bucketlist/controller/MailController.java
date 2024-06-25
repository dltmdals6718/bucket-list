package com.example.bucketlist.controller;

import com.example.bucketlist.dto.response.ApiErrorResponse;
import com.example.bucketlist.exception.DuplicateMailSignupException;
import com.example.bucketlist.exception.DuplicateMailVerificationException;
import com.example.bucketlist.service.MailService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class MailController {

    private final MailService mailService;

    @Autowired
    public MailController(MailService mailService) {
        this.mailService = mailService;
    }

    @PostMapping("/mails")
    public ResponseEntity sendMailVerificationCode(@RequestBody Map<String, String> map) throws MessagingException {
        String email = map.get("email");
        mailService.sendMailVerificationCode(email);
        return ResponseEntity
                .noContent()
                .build();
    }

    @ExceptionHandler({DuplicateMailVerificationException.class})
    public ResponseEntity<ApiErrorResponse> duplicateMailVerificationHandle(DuplicateMailVerificationException ex) {
        ApiErrorResponse errorResponse = new ApiErrorResponse(ex.getErrorCode());
        return ResponseEntity
                .status(ex.getErrorCode().getHttpStatus())
                .body(errorResponse);
    }
    @ExceptionHandler({DuplicateMailSignupException.class})
    public ResponseEntity<ApiErrorResponse> duplicateMailSignupHandle(DuplicateMailSignupException ex) {
        ApiErrorResponse errorResponse = new ApiErrorResponse(ex.getErrorCode());
        return ResponseEntity
                .status(ex.getErrorCode().getHttpStatus())
                .body(errorResponse);
    }

}
