package com.example.bucketlist.service;

import com.example.bucketlist.domain.Poster;
import com.example.bucketlist.dto.request.PosterWriteRequest;
import com.example.bucketlist.repository.PosterImageRepository;
import com.example.bucketlist.repository.PosterRepository;
import com.example.bucketlist.utils.UploadFileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PosterService {

    private PosterRepository posterRepository;
    private PosterImageRepository posterImageRepository;

    @Autowired
    public PosterService(PosterRepository posterRepository, PosterImageRepository posterImageRepository) {
        this.posterRepository = posterRepository;
        this.posterImageRepository = posterImageRepository;
    }

    @Transactional
    public Long createPoster(PosterWriteRequest posterWriteRequest) {

        Poster poster = new Poster();
        poster.setTitle(posterWriteRequest.getTitle());
        poster.setContent(posterWriteRequest.getContent());
        posterRepository.save(poster);

        List<String> imageUUIDs = UploadFileUtil.imageUUIDExtractor(posterWriteRequest.getContent());
        for (String imageUUID : imageUUIDs) {
            posterImageRepository.findByStoreFileName(imageUUID)
                    .ifPresent(posterImage -> {
                        posterImage.setPoster(poster);
                    });
        }

        return poster.getId();
    }

    public Poster viewPoster(Long posterId) {

        Poster poster = posterRepository.findById(posterId)
                .orElseThrow(() -> new IllegalArgumentException());

        return poster;
    }
}
