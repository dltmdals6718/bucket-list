package com.example.bucketlist.service;

import com.example.bucketlist.domain.PosterImage;
import com.example.bucketlist.repository.PosterImageRepository;
import com.example.bucketlist.utils.S3Uploader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class PosterImageService {

    private PosterImageRepository posterImageRepository;
    private S3Uploader s3Uploader;

    @Autowired
    public PosterImageService(PosterImageRepository posterImageRepository, S3Uploader s3Uploader) {
        this.posterImageRepository = posterImageRepository;
        this.s3Uploader = s3Uploader;
    }

    public String uploadPosterImage(MultipartFile uploadPosterImage) throws IOException {

        PosterImage posterImage = s3Uploader.uploadPosterImg(uploadPosterImage);
        posterImageRepository.save(posterImage);

        String posterImgPath = s3Uploader.getPosterImgPath(posterImage);
        return posterImgPath;
    }


}
