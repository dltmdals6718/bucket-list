package com.example.bucketlist.repository;

import com.example.bucketlist.domain.PosterTag;
import com.example.bucketlist.domain.PosterTagId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PosterTagRepository extends JpaRepository<PosterTag, PosterTagId> {

}
