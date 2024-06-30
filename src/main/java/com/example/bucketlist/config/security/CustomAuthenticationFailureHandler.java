package com.example.bucketlist.config.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;

public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

        System.out.println("exception = " + exception.getClass());

        String errorMsg;
        if (exception instanceof BadCredentialsException) {
            errorMsg = "아이디 또는 비밀번호를 확인해주세요.";
        } else {
            errorMsg = "알 수 없는 이유로 로그인에 실패했습니다.";
        }
        request.setAttribute("errorMsg", errorMsg);
        request.getRequestDispatcher("/members/signin-error").forward(request, response);
    }
}
