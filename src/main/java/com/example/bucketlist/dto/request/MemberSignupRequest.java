package com.example.bucketlist.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MemberSignupRequest {

    @NotBlank(message = "아이디를 입력해주세요.")
    private String loginId;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String loginPwd;

    private String confirmPwd;

    @NotBlank(message = "닉네임을 입력해주세요.")
    private String nickname;

    @Pattern(regexp = "^[^\s@]+@[^\s@]+\\.[^\s@]+$", message = "올바른 이메일 형식을 입력해주세요.")
    private String email;

    @Pattern(regexp = "^[0-9]+$", message = "올바른 인증 번호를 입력해주세요.")
    private String emailCode;

}
