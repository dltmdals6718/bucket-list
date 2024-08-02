package com.example.bucketlist.repository;

import com.example.bucketlist.domain.PosterImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PosterImageRepository extends JpaRepository<PosterImage, Long> {

    Optional<PosterImage> findByStoreFileName(String storeFileName);

}
