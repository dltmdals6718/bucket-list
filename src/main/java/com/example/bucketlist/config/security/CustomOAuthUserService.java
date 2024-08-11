package com.example.bucketlist.config.security;

import com.example.bucketlist.domain.Member;
import com.example.bucketlist.domain.ProfileImage;
import com.example.bucketlist.domain.oauth.GoogleUserInfo;
import com.example.bucketlist.domain.oauth.KakaoUserInfo;
import com.example.bucketlist.domain.oauth.NaverUserInfo;
import com.example.bucketlist.domain.oauth.OAuth2UserInfo;
import com.example.bucketlist.repository.MemberRepository;
import com.example.bucketlist.utils.DefaultProfileImageUtil;
import com.example.bucketlist.utils.S3Uploader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

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
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        if (registrationId.equals("kakao")) {
            oAuth2UserInfo = new KakaoUserInfo(user.getAttribute("id").toString(), user.getAttribute("kakao_account"));
        } else if (registrationId.equals("google")) {
            Map<String, Object> attributes = user.getAttributes();
            oAuth2UserInfo = new GoogleUserInfo(attributes);
        } else if (registrationId.equals("naver")) {
            Map<String, Object> attributes = user.getAttributes();
            oAuth2UserInfo = new NaverUserInfo(attributes);
        }

        String provider = oAuth2UserInfo.getProvider();
        String providerId = oAuth2UserInfo.getProviderId();
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

            if (member.getProvider() == null)
                profileImgPath = DefaultProfileImageUtil.getDefaultProfileImagePath(member.getEmail());
            else
                profileImgPath = DefaultProfileImageUtil.getDefaultProfileImagePath(member.getProvider() + "_" + member.getProviderId());

        } else {
            profileImgPath = s3Uploader.getProfileImgPath(profileImage);
        }

        CustomUserDetails details = new CustomUserDetails(member.getId(), provider + "_" + providerId, "", name, "", profileImgPath, user.getAttributes());
        return details;
    }

}
