package com.example.bucketlist.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberPwdUpdateRequest {
    private String password;
    private String newPassword;
    private String confirmNewPassword;
}
