package com.example.bucketlist.controller;

import com.example.bucketlist.config.security.CustomUserDetails;
import com.example.bucketlist.domain.Poster;
import com.example.bucketlist.dto.request.PosterWriteRequest;
import com.example.bucketlist.exception.InValidInputException;
import com.example.bucketlist.service.PosterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/posters")
public class PosterController {

    private PosterService posterService;

    @Autowired
    public PosterController(PosterService posterService) {
        this.posterService = posterService;
    }

    @GetMapping("/write")
    public String createPosterForm() {
        return "poster/create-poster";
    }

    @PostMapping("/write")
    @ResponseBody
    public ResponseEntity<Map> createPoster(@AuthenticationPrincipal CustomUserDetails member,
                                            @RequestPart(name = "poster") PosterWriteRequest posterWriteRequest,
                                            BindingResult bindingResult) {

        if (posterWriteRequest.getTitle().isBlank())
            bindingResult.rejectValue("title", "blankPosterTitle", "제목 미입력");

        if (posterWriteRequest.getContent().isBlank())
            bindingResult.rejectValue("content", "blankPosterContent", "내용 미입력");
        else {
            if (posterWriteRequest.getContent().length() > 10000)
                bindingResult.rejectValue("content", "TooLargePosterContent", new Object[]{10000L}, "본문 내용 초과");
        }


        if (bindingResult.hasErrors())
            throw new InValidInputException(bindingResult);

        Long posterId = posterService.createPoster(member.getId(), posterWriteRequest);
        HashMap<String, Long> response = new HashMap<>();
        response.put("posterId", posterId);
        return ResponseEntity
                .ok(response);
    }

    @GetMapping("/{posterId}")
    public String viewPoster(@PathVariable Long posterId, Model model) {

        Poster poster = posterService.viewPoster(posterId);
        model.addAttribute("poster", poster);

        return "poster/view-poster";
    }

    @GetMapping("/{posterId}/update")
    public String updatePosterForm(@PathVariable Long posterId, Model model) {

        // todo: viewPoster -> getPoster
        Poster poster = posterService.viewPoster(posterId);
        model.addAttribute("poster", poster);

        return "poster/update-poster";
    }

    @PutMapping("/{posterId}/update")
    public ResponseEntity<Map> updatePoster(@PathVariable Long posterId,
                                            @RequestPart(name = "poster") PosterWriteRequest posterWriteRequest,
                                            BindingResult bindingResult) {

        if (posterWriteRequest.getTitle().isBlank())
            bindingResult.rejectValue("title", "blankPosterTitle", "제목 미입력");

        if (posterWriteRequest.getContent().isBlank())
            bindingResult.rejectValue("content", "blankPosterContent", "내용 미입력");
        else {
            if (posterWriteRequest.getContent().length() > 10000)
                bindingResult.rejectValue("content", "TooLargePosterContent", new Object[]{10000L}, "본문 내용 초과");
        }

        if (bindingResult.hasErrors())
            throw new InValidInputException(bindingResult);

        posterService.updatePoster(posterId, posterWriteRequest);
        HashMap<String, Long> response = new HashMap<>();
        response.put("posterId", posterId);
        return ResponseEntity
                .ok(response);
    }

}
