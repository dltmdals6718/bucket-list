package com.example.bucketlist.service;

import com.example.bucketlist.domain.Poster;
import com.example.bucketlist.dto.request.PosterWriteRequest;
import com.example.bucketlist.repository.PosterImageRepository;
import com.example.bucketlist.repository.PosterRepository;
import com.example.bucketlist.utils.S3Uploader;
import com.example.bucketlist.utils.UploadFileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PosterService {

    private S3Uploader s3Uploader;
    private PosterRepository posterRepository;
    private PosterImageRepository posterImageRepository;

    @Autowired
    public PosterService(S3Uploader s3Uploader, PosterRepository posterRepository, PosterImageRepository posterImageRepository) {
        this.s3Uploader = s3Uploader;
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

    @Transactional
    public Poster updatePoster(Long posterId, PosterWriteRequest posterWriteRequest) {

        Poster poster = posterRepository.findById(posterId)
                .orElseThrow(() -> new IllegalArgumentException());
        poster.setTitle(posterWriteRequest.getTitle());
        poster.setContent(posterWriteRequest.getContent());

        List<String> existingUUIDs = posterImageRepository.findAllByPoster(poster).stream()
                .map(p -> p.getStoreFileName())
                .collect(Collectors.toList());
        List<String> receivedUUIDs = UploadFileUtil.imageUUIDExtractor(posterWriteRequest.getContent());


        // 게시글 수정시 삭제된 업로드 이미지 파일 삭제 방법
        // 기존 게시글의 이미지 UUIDs 현재 게시글의 이미지 UUIDs 제거하면 삭제된 UUIDs가 남는다.
        existingUUIDs.removeAll(receivedUUIDs);
        for (String existingUUID : existingUUIDs) {
            posterImageRepository.findByStoreFileName(existingUUID)
                    .ifPresent(posterImage -> {
                        posterImageRepository.delete(posterImage);
                        s3Uploader.deletePosterImg(posterImage);
                    });
        }


        // 게시글 수정시 추가된 업로드 이미지 파일 등록
        for (String receivedUUID : receivedUUIDs) {
            posterImageRepository.findByStoreFileName(receivedUUID)
                    .filter(posterImage -> posterImage.getPoster() == null)
                    .ifPresent(posterImage -> posterImage.setPoster(poster));
        }

        return poster;
    }
}
