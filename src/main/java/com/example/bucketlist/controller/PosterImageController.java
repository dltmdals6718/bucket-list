package com.example.bucketlist.controller;

import com.example.bucketlist.service.PosterImageService;
import com.example.bucketlist.utils.UploadFileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/api/posters")
public class PosterImageController {

    private PosterImageService posterImageService;

    @Autowired
    public PosterImageController(PosterImageService posterImageService) {
        this.posterImageService = posterImageService;
    }

    @PostMapping("/image")
    @ResponseBody
    public ResponseEntity uploadPosterImage(@RequestPart(name = "posterImage", required = false) MultipartFile uploadPosterImage) throws IOException {

        Map<String, String> response = new HashMap<>();

        if (uploadPosterImage != null && !UploadFileUtil.isImageFile(uploadPosterImage)) {
            response.put("error", "이미지 파일을 업로드해주세요.");
            return ResponseEntity
                    .badRequest()
                    .body(response);
        }

        String posterImgPath = posterImageService.uploadPosterImage(uploadPosterImage);
        response.put("url", posterImgPath);
        return ResponseEntity
                .ok(response);
    }

}
