package com.example.bucketlist.exception;

import com.example.bucketlist.exception.authentication.ForbiddenException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class GlobalMvcExceptionHandler {

    @ExceptionHandler({IllegalArgumentException.class})
    public ModelAndView illegalArgumentException(IllegalArgumentException ex) {
        ModelAndView modelAndView = new ModelAndView("error/400");
        modelAndView.addObject("message", "잘못된 요청입니다.");
        return modelAndView;
    }

    @ExceptionHandler({ForbiddenException.class})
    public ModelAndView forbiddenException(ForbiddenException ex) {
        ModelAndView modelAndView = new ModelAndView("error/403");
        modelAndView.addObject("message", ex.getMessage());
        return modelAndView;
    }

}
