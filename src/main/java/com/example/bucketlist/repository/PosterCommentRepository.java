package com.example.bucketlist.repository;

import com.example.bucketlist.domain.PosterComment;
import com.example.bucketlist.repository.querydsl.PosterCommentRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PosterCommentRepository extends JpaRepository<PosterComment, Long>, PosterCommentRepositoryCustom {

}
