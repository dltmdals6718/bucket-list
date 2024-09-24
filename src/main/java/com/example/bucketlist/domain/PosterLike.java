package com.example.bucketlist.domain;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class PosterLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "poster_id")
    private Poster poster;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

}
