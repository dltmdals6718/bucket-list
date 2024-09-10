package com.example.bucketlist.repository;

import com.example.bucketlist.domain.PosterAchieveImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PosterAchieveImageRepository extends JpaRepository<PosterAchieveImage, Long> {

    Optional<PosterAchieveImage> findByStoreFileName(String storeFileName);

}
