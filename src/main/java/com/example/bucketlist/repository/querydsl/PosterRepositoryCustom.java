package com.example.bucketlist.repository.querydsl;

import com.example.bucketlist.dto.response.PosterDetailsResponse;
import com.example.bucketlist.dto.response.PosterOverviewResponse;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface PosterRepositoryCustom {

    Optional<PosterDetailsResponse> findPosterDetailsById(Long posterId);
    Page<PosterOverviewResponse> findPosterOverview(int page, int size, List<String> tags, String keyword, String sort, String status);
}
