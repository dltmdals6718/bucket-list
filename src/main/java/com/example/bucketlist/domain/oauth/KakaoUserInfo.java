package com.example.bucketlist.domain.oauth;

import java.util.Map;

public class KakaoUserInfo implements OAuth2UserInfo {

    private String id;
    private Map<String, Object> oAuth2Attributes;

    public KakaoUserInfo(String id, Map<String, Object> oAuth2Attributes) {
        this.id = id;
        this.oAuth2Attributes = oAuth2Attributes;
    }

    @Override
    public String getProviderId() {
        return id;
    }

    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public String getName() {
        Map<String, Object> profile = (Map<String, Object>) oAuth2Attributes.get("profile");
        return String.valueOf(profile.get("nickname"));
    }
}
