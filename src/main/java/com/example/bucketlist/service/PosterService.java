package com.example.bucketlist.service;

import com.example.bucketlist.domain.Poster;
import com.example.bucketlist.dto.request.PosterWriteRequest;
import com.example.bucketlist.repository.PosterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PosterService {

    private PosterRepository posterRepository;

    @Autowired
    public PosterService(PosterRepository posterRepository) {
        this.posterRepository = posterRepository;
    }

    public Long createPoster(PosterWriteRequest posterWriteRequest) {

        Poster poster = new Poster();
        poster.setTitle(posterWriteRequest.getTitle());
        poster.setContent(posterWriteRequest.getContent());
        posterRepository.save(poster);

        return poster.getId();
    }

    public Poster viewPoster(Long posterId) {

        Poster poster = posterRepository.findById(posterId)
                .orElseThrow(() -> new IllegalArgumentException());

        return poster;
    }
}
