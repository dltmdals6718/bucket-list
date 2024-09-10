package com.example.bucketlist.controller;

import com.example.bucketlist.service.PosterAchieveImageService;
import com.example.bucketlist.utils.UploadFileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/api/poster-achieves")
public class PosterAchieveImageController {

    private final PosterAchieveImageService posterAchieveImageService;

    @Autowired
    public PosterAchieveImageController(PosterAchieveImageService posterAchieveImageService) {
        this.posterAchieveImageService = posterAchieveImageService;
    }

    @PostMapping("/image")
    @ResponseBody
    public ResponseEntity uploadPosterAchieveImage(@RequestPart(name = "posterAchieveImage", required = false) MultipartFile uploadPosterAchieveImage) throws IOException {

        Map<String, String> response = new HashMap<>();

        if (uploadPosterAchieveImage != null && !UploadFileUtil.isImageFile(uploadPosterAchieveImage)) {
            response.put("error", "이미지 파일을 업로드해주세요.");
            return ResponseEntity
                    .badRequest()
                    .body(response);
        }

        String posterAchieveImageImgPath = posterAchieveImageService.uploadPosterAchieveImage(uploadPosterAchieveImage);
        response.put("url", posterAchieveImageImgPath);
        return ResponseEntity
                .ok(response);
    }

}
