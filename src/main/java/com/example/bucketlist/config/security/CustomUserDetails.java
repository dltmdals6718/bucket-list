package com.example.bucketlist.config.security;

import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@ToString
public class CustomUserDetails implements UserDetails {

    private String loginId;
    private String loginPwd;
    private String nickname;


    public CustomUserDetails(String loginId, String loginPwd, String nickname) {
        this.loginId = loginId;
        this.loginPwd = loginPwd;
        this.nickname = nickname;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return this.loginPwd;
    }

    @Override
    public String getUsername() {
        return this.loginId;
    }

    public String getNickname() {
        return nickname;
    }
}
