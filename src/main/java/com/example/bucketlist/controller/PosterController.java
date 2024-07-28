package com.example.bucketlist.controller;

import com.example.bucketlist.config.security.CustomUserDetails;
import com.example.bucketlist.dto.request.PosterWriteRequest;
import com.example.bucketlist.exception.InValidInputException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/posters")
public class PosterController {


    @GetMapping("/write")
    public String createPosterForm() {
        return "poster/create-poster";
    }

    @PostMapping("/write")
    @ResponseBody
    public ResponseEntity<Map> createPoster(@AuthenticationPrincipal CustomUserDetails member,
                                            @RequestPart(name = "poster") PosterWriteRequest posterWriteRequest,
                                            BindingResult bindingResult,
                                            @RequestPart(name = "posterImages", required = false) List<MultipartFile> uploadPosterImages) {

        if (posterWriteRequest.getTitle().isBlank())
            bindingResult.rejectValue("title", "blankPosterTitle", "제목 미입력");

        if (posterWriteRequest.getContent().isBlank())
            bindingResult.rejectValue("content", "blankPosterContent", "내용 미입력");

        if (uploadPosterImages != null) {

            if (uploadPosterImages.size() > 5)
                bindingResult.addError(new ObjectError("posterImages", new String[]{"fileCountOver"}, new Object[]{5}, "파일 최대 개수 초과."));

            long totalFileSize = uploadPosterImages.stream()
                    .mapToLong(MultipartFile::getSize)
                    .sum();

            if (totalFileSize > 1024 * 1024 * 30)
                bindingResult.addError(new ObjectError("posterImages", new String[]{"fileSizeOver"}, new Object[]{"30MB"}, "파일 최대 용량 초과."));

        }

        if (bindingResult.hasErrors())
            throw new InValidInputException(bindingResult);

        Long tempPosterId = 13L;
        HashMap<String, Long> response = new HashMap<>();
        response.put("posterId", tempPosterId);
        return ResponseEntity
                .ok(response);
    }

}
