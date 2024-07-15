package com.example.bucketlist.config.security;

import com.example.bucketlist.domain.Member;
import com.example.bucketlist.domain.ProfileImage;
import com.example.bucketlist.repository.MemberRepository;
import com.example.bucketlist.utils.S3Uploader;
import com.example.bucketlist.utils.SHA256Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private MemberRepository memberRepository;
    private S3Uploader s3Uploader;


    @Autowired
    public CustomUserDetailsService(MemberRepository memberRepository, S3Uploader s3Uploader) {
        this.memberRepository = memberRepository;
        this.s3Uploader = s3Uploader;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findMemberByLoginId(username);
        if (member == null)
            throw new UsernameNotFoundException("no user");

        String profileImgPath;
        ProfileImage profileImage = member.getProfileImage();
        if (profileImage == null) {
            String email = member.getEmail();
            String hash = SHA256Util.encrypt(email);
            profileImgPath = "https://gravatar.com/avatar/" + hash + "?d=identicon&s=200";
        } else {
            profileImgPath = s3Uploader.getProfileImgPath(profileImage);
        }

        return new CustomUserDetails(member.getId(), member.getLoginId(), member.getLoginPwd(), member.getNickname(), member.getEmail(), profileImgPath);
    }
}
