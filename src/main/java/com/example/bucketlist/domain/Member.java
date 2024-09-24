package com.example.bucketlist.domain;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SourceType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @CreationTimestamp(source = SourceType.DB)
    private LocalDateTime signupDate;

    private String provider;
    private String providerId;

    @OneToMany(mappedBy = "member")
    private List<Poster> posters = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<PosterAchieve> posterAchieves = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<PosterLike> posterLikes = new ArrayList<>();

}
