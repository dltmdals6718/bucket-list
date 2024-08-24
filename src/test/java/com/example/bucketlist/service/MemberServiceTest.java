package com.example.bucketlist.service;

import com.example.bucketlist.domain.Member;
import com.example.bucketlist.dto.request.MemberProfileUpdateRequest;
import com.example.bucketlist.dto.request.MemberSignupRequest;
import com.example.bucketlist.repository.MemberRepository;
import com.example.bucketlist.utils.EscapeUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.NoSuchElementException;


@SpringBootTest
@Transactional
class MemberServiceTest {

    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public MemberServiceTest(MemberService memberService, MemberRepository memberRepository, BCryptPasswordEncoder passwordEncoder) {
        this.memberService = memberService;
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Test
    @DisplayName("회원가입")
    void signup() {

        //given
        MemberSignupRequest memberSignupRequest = new MemberSignupRequest();
        memberSignupRequest.setLoginId("loginId");
        memberSignupRequest.setLoginPwd("loginPwd");

        String nickname = "<script>alert('hello')</script>";
        memberSignupRequest.setNickname(nickname);

        //when
        Long memberId = memberService.signup(memberSignupRequest);

        //then
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException());

        Assertions
                .assertThat(member.getLoginId())
                .isEqualTo(memberSignupRequest.getLoginId());
        Assertions
                .assertThat(passwordEncoder.matches(memberSignupRequest.getLoginPwd(), member.getLoginPwd()))
                .isTrue();

        Assertions
                .assertThat(member.getNickname())
                .isEqualTo(EscapeUtils.escapeHtml(nickname));

    }

    @Test
    @DisplayName("닉네임 변경시 이스케이프 적용")
    public void updateNicknameEscape() throws IOException {

        // given
        Member member = new Member();
        member.setNickname("name");
        member.setEmail("test@test.com");
        memberRepository.save(member);

        MemberProfileUpdateRequest profileUpdateRequest = new MemberProfileUpdateRequest();
        String nickname = "<script>alert('hello')</script>";
        profileUpdateRequest.setNickname(nickname);

        // when
        memberService.updateMemberProfile(member.getId(), profileUpdateRequest, null);

        // then
        Member findMember = memberRepository.findById(member.getId())
                .orElseThrow(() -> new IllegalArgumentException());
        Assertions
                .assertThat(findMember.getNickname())
                .isEqualTo(EscapeUtils.escapeHtml(nickname));


    }
}