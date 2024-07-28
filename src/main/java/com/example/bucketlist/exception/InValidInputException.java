package com.example.bucketlist.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.BindingResult;

@Setter
@Getter
public class InValidInputException extends RuntimeException {

    private BindingResult bindingResult;

    public InValidInputException(BindingResult bindingResult) {
        this.bindingResult = bindingResult;
    }
}
