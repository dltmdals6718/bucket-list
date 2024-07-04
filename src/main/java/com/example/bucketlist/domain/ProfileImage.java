package com.example.bucketlist.domain;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class ProfileImage {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "member_id")
    private Member member;

    private String uploadFileName;
    private String storeFileName;
}
