package com.example.bucketlist.domain.oauth;

public interface OAuth2UserInfo {

    Long getProviderId();
    String getProvider();
    String getName();

}
