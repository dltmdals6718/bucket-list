package com.example.bucketlist.domain;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class PosterTag {

    @EmbeddedId
    private PosterTagId id = new PosterTagId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("posterId")
    @JoinColumn(name = "poster_id")
    private Poster poster;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("tagId")
    @JoinColumn(name = "tag_id")
    private Tag tag;

}
