package com.example.bucketlist.domain.oauth;

import java.util.Map;

public class GoogleUserInfo implements OAuth2UserInfo {

    private Map<String, Object> oAuth2Attributes;

    public GoogleUserInfo(Map<String, Object> oAuth2Attributes) {
        this.oAuth2Attributes = oAuth2Attributes;
    }

    @Override
    public String getProviderId() {
        return String.valueOf(oAuth2Attributes.get("sub"));
    }

    @Override
    public String getProvider() {
        return "google";
    }

    @Override
    public String getName() {
        return String.valueOf(oAuth2Attributes.get("name"));
    }
}
