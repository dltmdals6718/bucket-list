package com.example.bucketlist.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberProfileUpdateRequest {

    @NotBlank(message = "닉네임을 입력해주세요.")
    private String nickname;

}
