package com.example.bucketlist.config.security;

import com.example.bucketlist.domain.Member;
import com.example.bucketlist.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private MemberRepository memberRepository;


    @Autowired
    public CustomUserDetailsService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findMemberByLoginId(username);
        if(member == null)
            throw new UsernameNotFoundException("no user");
        return new CustomUserDetails(member.getLoginId(), member.getLoginPwd(), member.getNickname());
    }
}
