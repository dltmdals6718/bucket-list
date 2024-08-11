package com.example.bucketlist.domain.oauth;

public interface OAuth2UserInfo {

    String getProviderId();
    String getProvider();
    String getName();

}
