package com.example.bucketlist.controller;

import com.example.bucketlist.config.security.CustomUserDetails;
import com.example.bucketlist.dto.request.PosterAchieveRequest;
import com.example.bucketlist.exception.InValidInputException;
import com.example.bucketlist.service.PosterAchieveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
public class PosterAchieveController {

    private final PosterAchieveService posterAchieveService;

    @Autowired
    public PosterAchieveController(PosterAchieveService posterAchieveService) {
        this.posterAchieveService = posterAchieveService;
    }

    @GetMapping("/poster-achieves/write/{posterId}")
    public String posterAchieveForm(@PathVariable Long posterId) {
        return "poster-achieve/create-poster-achieve";
    }

    @PostMapping("/api/poster-achieves/{posterId}")
    @ResponseBody
    public ResponseEntity<Map> createPosterAchieve(@PathVariable Long posterId,
                                                   @AuthenticationPrincipal CustomUserDetails member,
                                                   @RequestPart(name = "posterAchieve") PosterAchieveRequest posterAchieveRequest,
                                                   BindingResult bindingResult) {

        if (posterAchieveRequest.getContent().isBlank())
            bindingResult.rejectValue("content", "blankPosterAchieveContent", "내용 미입력");
        else {
            if (posterAchieveRequest.getContent().length() > 10000)
                bindingResult.rejectValue("content", "TooLargePosterAchieveContent", new Object[]{10000L}, "본문 내용 초과");
        }

        if (bindingResult.hasErrors())
            throw new InValidInputException(bindingResult);

        posterAchieveService.createPosterAchieve(member.getId(), posterId, posterAchieveRequest);

        HashMap<String, Long> response = new HashMap<>();
        response.put("posterId", posterId);
        return ResponseEntity
                .ok(response);
    }

}
