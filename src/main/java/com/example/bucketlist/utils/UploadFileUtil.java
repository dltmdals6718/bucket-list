package com.example.bucketlist.utils;

import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UploadFileUtil {

    public static boolean isImageFile(MultipartFile file) {

        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf('.')+1);

        List<String> supportExtension = new ArrayList<>(Arrays.asList("jpeg", "jpg", "png"));
        if(!supportExtension.contains(fileExtension))
            return false;

        return true;
    }

    public static List<String> imageUUIDExtractor(String html) {

        String regex = "<img\\s+[^>]*src=\"([^\"]+)\"[^>]*>";
        ArrayList<String> imageUUIDs = new ArrayList<>();

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(html);

        while (matcher.find()) {
            String imageUrl = matcher.group(1);
            imageUUIDs.add(imageUrl.substring(imageUrl.lastIndexOf("/") + 1));
        }

        return imageUUIDs;
    }
}
