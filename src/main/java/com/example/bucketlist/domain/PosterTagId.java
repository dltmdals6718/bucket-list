package com.example.bucketlist.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter @Setter
public class PosterTagId implements Serializable {

    @Column(name = "poster_id")
    private Long posterId;

    @Column(name = "tag_id")
    private Long tagId;


}
