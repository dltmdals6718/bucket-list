package com.example.bucketlist.service;

import com.example.bucketlist.domain.Member;
import com.example.bucketlist.dto.request.MemberSigninRequest;
import com.example.bucketlist.dto.request.MemberSignupRequest;
import com.example.bucketlist.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public MemberService(MemberRepository memberRepository, BCryptPasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
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

    public Member signin(MemberSigninRequest memberSigninRequest) {

        String loginId = memberSigninRequest.getLoginId();
        String loginPwd = memberSigninRequest.getLoginPwd();
        Member member = memberRepository.findMemberByLoginId(loginId);

        if(member == null || !passwordEncoder.matches(loginPwd, member.getLoginPwd()))
            return null;

        return member;
    }

    public Boolean existsLoginId(String loginId) {
        return memberRepository.existsByLoginId(loginId);
    }
}
