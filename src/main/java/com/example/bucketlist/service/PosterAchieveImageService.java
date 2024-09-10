package com.example.bucketlist.service;

import com.example.bucketlist.domain.PosterAchieveImage;
import com.example.bucketlist.repository.PosterAchieveImageRepository;
import com.example.bucketlist.utils.S3Uploader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class PosterAchieveImageService {

    private final PosterAchieveImageRepository posterAchieveImageRepository;
    private final S3Uploader s3Uploader;

    @Autowired
    public PosterAchieveImageService(PosterAchieveImageRepository posterAchieveImageRepository, S3Uploader s3Uploader) {
        this.posterAchieveImageRepository = posterAchieveImageRepository;
        this.s3Uploader = s3Uploader;
    }

    public String uploadPosterAchieveImage(MultipartFile uploadPosterAchieveImage) throws IOException {

        PosterAchieveImage posterAchieveImage = s3Uploader.uploadPosterAchieveImg(uploadPosterAchieveImage);
        posterAchieveImageRepository.save(posterAchieveImage);

        String posterAchieveImgPath = s3Uploader.getPosterAchieveImgPath(posterAchieveImage);
        return posterAchieveImgPath;
    }


}
