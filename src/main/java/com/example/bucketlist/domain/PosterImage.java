package com.example.bucketlist.domain;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SourceType;

import java.time.LocalDateTime;

@Entity
@Data
public class PosterImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "poster_id")
    private Poster poster;

    @CreationTimestamp(source = SourceType.DB)
    private LocalDateTime uploadDate;

    private String uploadFileName;
    private String storeFileName;

}
