package com.example.bucketlist.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/posters")
public class PosterImageController {

    @PostMapping("/image")
    @ResponseBody
    public ResponseEntity uploadPosterImage(@RequestPart(name = "posterImage", required = false) MultipartFile uploadPosterImages) {

        Map<String, String> tempResponse = new HashMap<>();
        tempResponse.put("url", "https://cdn.inflearn.com/public/files/posts/dc25c778-7376-4904-ba78-b2e6d3dda93e/3.png");
        return ResponseEntity
                .ok(tempResponse);
    }

}
