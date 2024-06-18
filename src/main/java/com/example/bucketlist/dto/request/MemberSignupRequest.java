package com.example.bucketlist.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MemberSignupRequest {

    private String loginId;
    private String loginPwd;
    private String confirmPwd;

}
