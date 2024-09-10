package com.example.bucketlist.utils;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.example.bucketlist.domain.PosterAchieveImage;
import com.example.bucketlist.domain.PosterImage;
import com.example.bucketlist.domain.ProfileImage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class S3Uploader {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private String PROFILE_IMG_DIR = "profile/";
    private String POSTER_IMG_DIR = "poster/";
    private String POSTER_ACHIEVE_IMG_DIR = "posterAchieve/";

    public ProfileImage uploadProfileImg(MultipartFile uploadProfileImage) throws IOException {
        ProfileImage profileImage = new ProfileImage();
        profileImage.setUploadFileName(uploadProfileImage.getOriginalFilename());
        profileImage.setStoreFileName(UUID.randomUUID().toString());

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(uploadProfileImage.getContentType());
        metadata.setContentLength(uploadProfileImage.getSize());
        amazonS3Client.putObject(bucket, PROFILE_IMG_DIR + profileImage.getStoreFileName(), uploadProfileImage.getInputStream(), metadata);

        return profileImage;
    }

    public void deleteProfileImg(ProfileImage profileImage) {
        amazonS3Client.deleteObject(bucket, PROFILE_IMG_DIR + profileImage.getStoreFileName());
    }

    public String getProfileImgPath(String storeFileName) {
        return amazonS3Client.getResourceUrl(bucket, PROFILE_IMG_DIR + storeFileName);
    }

    public PosterImage uploadPosterImg(MultipartFile uploadPosterImage) throws IOException {
        PosterImage posterImage = new PosterImage();
        posterImage.setUploadFileName(uploadPosterImage.getOriginalFilename());
        posterImage.setStoreFileName(UUID.randomUUID().toString());

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(uploadPosterImage.getContentType());
        metadata.setContentLength(uploadPosterImage.getSize());
        amazonS3Client.putObject(bucket, POSTER_IMG_DIR + posterImage.getStoreFileName(), uploadPosterImage.getInputStream(), metadata);

        return posterImage;
    }

    public void deletePosterImg(PosterImage posterImage) {
        amazonS3Client.deleteObject(bucket, POSTER_IMG_DIR + posterImage.getStoreFileName());
    }

    public String getPosterImgPath(PosterImage posterImage) {
        return amazonS3Client.getResourceUrl(bucket, POSTER_IMG_DIR + posterImage.getStoreFileName());
    }

    public PosterAchieveImage uploadPosterAchieveImg(MultipartFile uploadPosterAchieveImage) throws IOException {
        PosterAchieveImage posterAchieveImage = new PosterAchieveImage();
        posterAchieveImage.setUploadFileName(uploadPosterAchieveImage.getOriginalFilename());
        posterAchieveImage.setStoreFileName(UUID.randomUUID().toString());

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(uploadPosterAchieveImage.getContentType());
        metadata.setContentLength(uploadPosterAchieveImage.getSize());
        amazonS3Client.putObject(bucket, POSTER_ACHIEVE_IMG_DIR + posterAchieveImage.getStoreFileName(), uploadPosterAchieveImage.getInputStream(), metadata);
        return posterAchieveImage;
    }

    public void deletePosterAchieveImg(PosterAchieveImage posterAchieveImage) {
        amazonS3Client.deleteObject(bucket, POSTER_ACHIEVE_IMG_DIR + posterAchieveImage.getStoreFileName());
    }

    public String getPosterAchieveImgPath(PosterAchieveImage posterAchieveImage) {
        return amazonS3Client.getResourceUrl(bucket, POSTER_ACHIEVE_IMG_DIR + posterAchieveImage.getStoreFileName());
    }



}
