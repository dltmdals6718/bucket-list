package com.example.bucketlist.repository.querydsl;

import com.example.bucketlist.dto.response.PosterDetailsResponse;

import java.util.Optional;

public interface PosterRepositoryCustom {

    Optional<PosterDetailsResponse> findPosterDetailsById(Long posterId);

}
