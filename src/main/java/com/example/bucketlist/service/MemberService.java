package com.example.bucketlist.service;

import com.example.bucketlist.config.security.CustomUserDetails;
import com.example.bucketlist.domain.Member;
import com.example.bucketlist.domain.ProfileImage;
import com.example.bucketlist.dto.request.MemberProfileUpdateRequest;
import com.example.bucketlist.dto.request.MemberPwdUpdateRequest;
import com.example.bucketlist.dto.request.MemberSignupRequest;
import com.example.bucketlist.dto.response.MemberProfileResponse;
import com.example.bucketlist.repository.MemberRepository;
import com.example.bucketlist.repository.ProfileImageRepository;
import com.example.bucketlist.utils.DefaultProfileImageUtil;
import com.example.bucketlist.utils.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final ProfileImageRepository profileImageRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final S3Uploader s3Uploader;
    private final MessageSource messageSource;

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
        if (profileImage == null) {

            if (member.getProvider() == null)
                memberProfileResponse.setProfileImg(DefaultProfileImageUtil.getDefaultProfileImagePath(member.getEmail()));
            else
                memberProfileResponse.setProfileImg(DefaultProfileImageUtil.getDefaultProfileImagePath(member.getProvider() + "_" + member.getProviderId()));

        } else {
            memberProfileResponse.setProfileImg(s3Uploader.getProfileImgPath(profileImage.getStoreFileName()));
        }


        return memberProfileResponse;
    }

    @Transactional
    public String deleteProfileImg(Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException());

        ProfileImage profileImage = member.getProfileImage();
        if (profileImage != null) {
            member.setProfileImage(null);
            s3Uploader.deleteProfileImg(profileImage);
        }

        String defaultProfileImagePath;
        if (member.getProvider() == null)
            defaultProfileImagePath = DefaultProfileImageUtil.getDefaultProfileImagePath(member.getEmail());
        else
            defaultProfileImagePath = DefaultProfileImageUtil.getDefaultProfileImagePath(member.getProvider() + "_" + member.getProviderId());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
        CustomUserDetails userDetails = new CustomUserDetails(principal.getId(), principal.getLoginId(), "", principal.getNickname(), principal.getEmail(), defaultProfileImagePath);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(userDetails, "", null));

        return defaultProfileImagePath;
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

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
            CustomUserDetails userDetails = new CustomUserDetails(principal.getId(), principal.getLoginId(), "", principal.getNickname(), principal.getEmail(), s3Uploader.getProfileImgPath(profileImage.getStoreFileName()));
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(userDetails, "", null));

        }

    }

    @Transactional
    public Map updatePwd(Long memberId, MemberPwdUpdateRequest pwdUpdateRequest) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException());
        HashMap<String, String> errorMsg = new HashMap<>();

        String pwd = pwdUpdateRequest.getPassword();
        if (pwd == null || pwd.isBlank())
            errorMsg.put("password", messageSource.getMessage("blankPwd", null, Locale.getDefault()));

        String newPwd = pwdUpdateRequest.getNewPassword();
        if (newPwd == null || newPwd.isBlank())
            errorMsg.put("newPassword", messageSource.getMessage("blankPwd", null, Locale.getDefault()));

        String confirmNewPwd = pwdUpdateRequest.getConfirmNewPassword();
        if (confirmNewPwd == null || confirmNewPwd.isBlank())
            errorMsg.put("confirmNewPassword", messageSource.getMessage("blankPwd", null, Locale.getDefault()));

        if (!errorMsg.isEmpty())
            return errorMsg;

        if (!newPwd.equals(confirmNewPwd))
            errorMsg.put("confirmNewPassword", messageSource.getMessage("confirmPwdFail", null, Locale.getDefault()));

        if (!passwordEncoder.matches(pwd, member.getLoginPwd()))
            errorMsg.put("password", messageSource.getMessage("invalidPwd", null, Locale.getDefault()));

        if (errorMsg.isEmpty())
            member.setLoginPwd(passwordEncoder.encode(newPwd));

        return errorMsg;
    }

}
