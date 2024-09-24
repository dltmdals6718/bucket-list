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
public class Poster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp(source = SourceType.DB)
    private LocalDateTime createdDate;

    private String title;
    private String content;
    private String pureContent;
    private Boolean isPrivate;
    private Boolean isAchieve;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany
    private List<PosterImage> posterImageList = new ArrayList<>();

    @OneToMany(mappedBy = "poster")
    private List<PosterTag> posterTags = new ArrayList<>();

    @OneToOne(mappedBy = "poster")
    private PosterAchieve posterAchieve;

    @OneToMany(mappedBy = "poster")
    private List<PosterLike> posterLikes = new ArrayList<>();

}
