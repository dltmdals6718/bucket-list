package com.example.bucketlist.repository.querydsl;

import com.example.bucketlist.dto.response.comment.PosterCommentResponse;
import org.springframework.data.domain.Page;

public interface PosterCommentRepositoryCustom {

    Page<PosterCommentResponse> findPosterCommentsByPosterId(Long posterId, int page, int size);

}
