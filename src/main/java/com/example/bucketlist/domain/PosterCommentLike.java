package com.example.bucketlist.domain;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SourceType;

import java.time.LocalDateTime;

@Entity
@Data
public class PosterCommentLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "comment_id")
    private PosterComment posterComment;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @CreationTimestamp(source = SourceType.DB)
    private LocalDateTime createdDate;

}
