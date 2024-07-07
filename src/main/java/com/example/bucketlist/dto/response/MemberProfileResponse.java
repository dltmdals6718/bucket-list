package com.example.bucketlist.dto.response;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MemberProfileResponse {

    private String loginId;
    private String email;
    private String nickname;
    private String profileImg;

}
