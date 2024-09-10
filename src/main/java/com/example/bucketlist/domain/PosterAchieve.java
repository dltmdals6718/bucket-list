package com.example.bucketlist.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class PosterAchieve {

    @Id
    private Long id;
    private String content;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private Poster poster;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany
    private List<PosterAchieveImage> posterAhieveImageList;
}
