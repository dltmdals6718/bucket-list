package com.example.bucketlist.service;

import com.example.bucketlist.domain.Member;
import com.example.bucketlist.domain.ProfileImage;
import com.example.bucketlist.dto.request.MemberProfileUpdateRequest;
import com.example.bucketlist.dto.request.MemberSignupRequest;
import com.example.bucketlist.dto.response.MemberProfileResponse;
import com.example.bucketlist.repository.MemberRepository;
import com.example.bucketlist.utils.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final S3Uploader s3Uploader;

    public Long signup(MemberSignupRequest memberSignupRequest) {

        Member member = new Member();
        member.setLoginId(memberSignupRequest.getLoginId());
        member.setLoginPwd(passwordEncoder.encode(memberSignupRequest.getLoginPwd()));
        member.setEmail(memberSignupRequest.getEmail());
        member.setNickname(memberSignupRequest.getNickname());

        Long memberId = memberRepository.save(member).getId();

        return memberId;
    }

    public Boolean existsLoginId(String loginId) {
        return memberRepository.existsByLoginId(loginId);
    }

    public MemberProfileResponse getMemberProfile(Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException());

        MemberProfileResponse memberProfileResponse = new MemberProfileResponse();
        memberProfileResponse.setLoginId(member.getLoginId());
        memberProfileResponse.setEmail(member.getEmail());
        memberProfileResponse.setNickname(member.getNickname());

        ProfileImage profileImage = member.getProfileImage();
        if (profileImage != null)
            memberProfileResponse.setProfileImg(s3Uploader.getProfileImgPath(member.getProfileImage()));

        return memberProfileResponse;
    }

    @Transactional
    public void updateMemberProfile(Long memberId, MemberProfileUpdateRequest profileUpdateRequest, MultipartFile uploadProfileImage) throws IOException {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException());

        // todo: 중복된 닉네임 정책
        member.setNickname(profileUpdateRequest.getNickname());

        if (uploadProfileImage != null) {

            ProfileImage oldProfileImage = member.getProfileImage();
            if (oldProfileImage != null)
                s3Uploader.deleteProfileImg(oldProfileImage);

            ProfileImage profileImage = s3Uploader.uploadProfileImg(uploadProfileImage);
            profileImage.setMember(member);
            member.setProfileImage(profileImage);
        }

    }

}
