package com.example.bucketlist.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.example.bucketlist.domain.Member;
import com.example.bucketlist.domain.ProfileImage;
import com.example.bucketlist.dto.request.MemberProfileUpdateRequest;
import com.example.bucketlist.dto.request.MemberSignupRequest;
import com.example.bucketlist.repository.MemberRepository;
import com.example.bucketlist.repository.ProfileImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AmazonS3Client amazonS3Client;

    @Autowired
    public MemberService(MemberRepository memberRepository, BCryptPasswordEncoder passwordEncoder, AmazonS3Client amazonS3Client) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.amazonS3Client = amazonS3Client;
    }

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

    public Member findMemberById(Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException());

        return member;
    }

    @Transactional
    public void updateMemberProfile(Long memberId, MemberProfileUpdateRequest profileUpdateRequest, MultipartFile uploadProfileImage) throws IOException {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException());

        // todo: 중복된 닉네임 정책
        member.setNickname(profileUpdateRequest.getNickname());

        ProfileImage profileImage = new ProfileImage();
        String storeFileName = UUID.randomUUID().toString();
        profileImage.setUploadFileName(uploadProfileImage.getOriginalFilename());
        profileImage.setStoreFileName(storeFileName);
        profileImage.setMember(member);

        // todo: 파일 관리 클래스
        ProfileImage oldProfileImage = member.getProfileImage();
        if (oldProfileImage != null)
            amazonS3Client.deleteObject("bucket-list-aws-s3", "profile/" + oldProfileImage.getStoreFileName());

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(uploadProfileImage.getContentType());
        metadata.setContentLength(uploadProfileImage.getSize());
        amazonS3Client.putObject("bucket-list-aws-s3", "profile/" + storeFileName, uploadProfileImage.getInputStream(), metadata);
        member.setProfileImage(profileImage);

    }

    // todo: 파일 관리 클래스
    public String profileImgPath(ProfileImage profileImage) {
        return amazonS3Client.getResourceUrl("bucket-list-aws-s3", "profile/" + profileImage.getStoreFileName());
    }

}
