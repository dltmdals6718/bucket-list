package com.example.bucketlist.service;

import com.example.bucketlist.domain.Member;
import com.example.bucketlist.dto.request.MemberSignupRequest;
import com.example.bucketlist.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

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

    }
}