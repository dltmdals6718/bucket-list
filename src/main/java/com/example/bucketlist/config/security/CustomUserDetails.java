package com.example.bucketlist.config.security;

import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@ToString
public class CustomUserDetails implements UserDetails {

    private Long id;
    private String loginId;
    private String loginPwd;
    private String nickname;
    private String email;
    private String profileImgPath;


    public CustomUserDetails(Long id, String loginId, String loginPwd, String nickname, String email, String profileImgPath) {
        this.id = id;
        this.loginId = loginId;
        this.loginPwd = loginPwd;
        this.nickname = nickname;
        this.email = email;
        this.profileImgPath = profileImgPath;
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

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getLoginId() {
        return loginId;
    }

    public String getProfileImgPath() {
        return profileImgPath;
    }
}
