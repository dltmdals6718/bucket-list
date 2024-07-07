package com.example.bucketlist.utils;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
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

    public String getProfileImgPath(ProfileImage profileImage) {
        return amazonS3Client.getResourceUrl(bucket, PROFILE_IMG_DIR + profileImage.getStoreFileName());
    }

}
