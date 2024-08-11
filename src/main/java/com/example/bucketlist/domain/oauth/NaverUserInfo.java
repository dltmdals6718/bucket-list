package com.example.bucketlist.domain.oauth;

import java.util.Map;

public class NaverUserInfo implements OAuth2UserInfo {

    private Map<String, Object> oAuth2Attribute;

    public NaverUserInfo(Map<String, Object> oAuth2Attribute) {
        this.oAuth2Attribute = oAuth2Attribute;
    }

    @Override
    public String getProviderId() {
        Map<String, Object> response = (Map<String, Object>) oAuth2Attribute.get("response");
        return String.valueOf(response.get("id"));
    }

    @Override
    public String getProvider() {
        return "naver";
    }

    @Override
    public String getName() {
        Map<String, Object> response = (Map<String, Object>) oAuth2Attribute.get("response");
        return String.valueOf(response.get("name"));
    }
}
