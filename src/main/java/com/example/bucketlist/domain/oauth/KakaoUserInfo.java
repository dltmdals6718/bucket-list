package com.example.bucketlist.domain.oauth;

import java.util.Map;

public class KakaoUserInfo implements OAuth2UserInfo {

    private Long id;
    private Map<String, Object> oAuth2Attributes;

    public KakaoUserInfo(Long id, Map<String, Object> oAuth2Attributes) {
        this.id = id;
        this.oAuth2Attributes = oAuth2Attributes;
    }

    @Override
    public Long getProviderId() {
        return id;
    }

    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public String getName() {
        return ((Map) oAuth2Attributes.get("profile")).get("nickname").toString();
    }
}
