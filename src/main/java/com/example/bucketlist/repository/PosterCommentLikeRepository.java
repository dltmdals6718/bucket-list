package com.example.bucketlist.repository;

import com.example.bucketlist.domain.Member;
import com.example.bucketlist.domain.PosterComment;
import com.example.bucketlist.domain.PosterCommentLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PosterCommentLikeRepository extends JpaRepository<PosterCommentLike, Long> {

    Optional<PosterCommentLike> findByPosterCommentAndMember(PosterComment posterComment, Member member);

}
