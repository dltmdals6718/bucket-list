package com.example.bucketlist.repository;

import com.example.bucketlist.domain.Poster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PosterRepository extends JpaRepository<Poster, Long> {

}
