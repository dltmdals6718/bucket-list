package com.example.bucketlist.repository;

import com.example.bucketlist.domain.Member;
import com.example.bucketlist.domain.Poster;
import com.example.bucketlist.domain.PosterLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PosterLikeRepository extends JpaRepository<PosterLike, Long> {

    Optional<PosterLike> findByPosterAndMember(Poster poster, Member member);
}
