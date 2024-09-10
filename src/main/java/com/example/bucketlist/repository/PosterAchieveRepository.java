package com.example.bucketlist.repository;

import com.example.bucketlist.domain.PosterAchieve;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PosterAchieveRepository extends JpaRepository<PosterAchieve, Long> {

}
