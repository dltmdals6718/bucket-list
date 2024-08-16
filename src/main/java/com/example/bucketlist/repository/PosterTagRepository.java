package com.example.bucketlist.repository;

import com.example.bucketlist.domain.Poster;
import com.example.bucketlist.domain.PosterTag;
import com.example.bucketlist.domain.PosterTagId;
import com.example.bucketlist.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PosterTagRepository extends JpaRepository<PosterTag, PosterTagId> {
     List<PosterTag> findByPoster(Poster poster);
     Optional<PosterTag> findByPosterAndTag(Poster poster, Tag tag);
     void deleteByPosterAndTag(Poster poster, Tag tag);
}
