package com.example.bucketlist.domain;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class PosterAchieveImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "poster_achieve_id")
    private PosterAchieve posterAchieve;

    private String uploadFileName;
    private String storeFileName;

}
