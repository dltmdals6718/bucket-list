package com.example.bucketlist.session;

import lombok.Getter;

@Getter
public class MemberSession {
    private final Long memberId;
    private final String profileImagePath;

    public MemberSession(Long memberId, String profileImagePath) {
        this.memberId = memberId;
        this.profileImagePath = profileImagePath;
    }
}
