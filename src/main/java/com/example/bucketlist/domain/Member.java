package com.example.bucketlist.domain;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String loginId;
    private String loginPwd;
    private String nickname;
    private String email;

    @OneToOne(mappedBy = "member", cascade = {CascadeType.PERSIST}, orphanRemoval = true)
    private ProfileImage profileImage;
}
