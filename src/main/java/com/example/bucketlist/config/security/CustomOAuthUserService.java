package com.example.bucketlist.config.security;

import com.example.bucketlist.domain.Member;
import com.example.bucketlist.domain.ProfileImage;
import com.example.bucketlist.domain.oauth.KakaoUserInfo;
import com.example.bucketlist.domain.oauth.OAuth2UserInfo;
import com.example.bucketlist.repository.MemberRepository;
import com.example.bucketlist.utils.S3Uploader;
import com.example.bucketlist.utils.SHA256Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class CustomOAuthUserService extends DefaultOAuth2UserService {

    private MemberRepository memberRepository;
    private S3Uploader s3Uploader;

    @Autowired
    public CustomOAuthUserService(MemberRepository memberRepository, S3Uploader s3Uploader) {
        this.memberRepository = memberRepository;
        this.s3Uploader = s3Uploader;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User user = super.loadUser(userRequest);

        OAuth2UserInfo oAuth2UserInfo = null;
        if (userRequest.getClientRegistration().getRegistrationId().equals("kakao")) {
            oAuth2UserInfo = new KakaoUserInfo(user.getAttribute("id"), user.getAttribute("kakao_account"));
        }

        String provider = oAuth2UserInfo.getProvider();
        Long providerId = oAuth2UserInfo.getProviderId();
        String name = oAuth2UserInfo.getName();

        Member member = memberRepository.findByProviderAndProviderId(provider, providerId);
        if (member == null) {
            member = new Member();
            member.setProvider(provider);
            member.setProviderId(providerId);
            member.setNickname(name);
            memberRepository.save(member);
        }

        String profileImgPath;
        ProfileImage profileImage = member.getProfileImage();
        if (profileImage == null) {
            String hash = SHA256Util.encrypt(provider + "_" + providerId);
            profileImgPath = "https://gravatar.com/avatar/" + hash + "?d=identicon&s=200";
        } else {
            profileImgPath = s3Uploader.getProfileImgPath(profileImage);
        }

        CustomUserDetails details = new CustomUserDetails(member.getId(), provider + "_" + providerId, "", name, "", profileImgPath, user.getAttributes());
        return details;
    }

}
