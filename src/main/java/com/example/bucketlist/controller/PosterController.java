package com.example.bucketlist.controller;

import com.example.bucketlist.config.security.CustomUserDetails;
import com.example.bucketlist.domain.Poster;
import com.example.bucketlist.dto.request.PosterWriteRequest;
import com.example.bucketlist.dto.response.PosterDetailsResponse;
import com.example.bucketlist.dto.response.PosterOverviewResponse;
import com.example.bucketlist.exception.InValidInputException;
import com.example.bucketlist.service.PosterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
public class PosterController {

    private PosterService posterService;

    @Autowired
    public PosterController(PosterService posterService) {
        this.posterService = posterService;
    }

    @GetMapping("/posters/write")
    public String createPosterForm() {
        return "poster/create-poster";
    }

    @PostMapping("/api/posters/write")
    @ResponseBody
    public ResponseEntity<Map> createPoster(@AuthenticationPrincipal CustomUserDetails member,
                                            @RequestPart(name = "poster") PosterWriteRequest posterWriteRequest,
                                            BindingResult bindingResult) {

        if (posterWriteRequest.getTitle().isBlank())
            bindingResult.rejectValue("title", "blankPosterTitle", "제목 미입력");

        if (posterWriteRequest.getContent().isBlank()) {
            bindingResult.rejectValue("content", "blankPosterContent", "내용 미입력");
        } else {
            if (posterWriteRequest.getContent().length() > 10000)
                bindingResult.rejectValue("content", "TooLargePosterContent", new Object[]{10000L}, "본문 내용 초과");
        }

        if (posterWriteRequest.getTags().size() > 10) {
            bindingResult.rejectValue("tags", "tooManyPosterTags", new Object[]{10}, "태그 개수 초과");
        }

        if (bindingResult.hasErrors())
            throw new InValidInputException(bindingResult);

        Long posterId = posterService.createPoster(member.getId(), posterWriteRequest);
        HashMap<String, Long> response = new HashMap<>();
        response.put("posterId", posterId);
        return ResponseEntity
                .ok(response);
    }

    @GetMapping("/posters/{posterId}")
    public String viewPoster(@PathVariable Long posterId, Model model) {

        PosterDetailsResponse poster = posterService.getPosterForView(posterId);
        model.addAttribute("poster", poster);

        return "poster/view-poster";
    }

    @GetMapping("/posters/{posterId}/update")
    public String updatePosterForm(@AuthenticationPrincipal CustomUserDetails member,
                                   @PathVariable Long posterId, Model model) {

        Poster poster = posterService.getPosterForUpdate(member.getId(), posterId);
        model.addAttribute("poster", poster);

        return "poster/update-poster";
    }

    @PutMapping("/api/posters/{posterId}/update")
    @ResponseBody
    public ResponseEntity<Map> updatePoster(@AuthenticationPrincipal CustomUserDetails member,
                                            @PathVariable Long posterId,
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

        posterService.updatePoster(member.getId(), posterId, posterWriteRequest);
        HashMap<String, Long> response = new HashMap<>();
        response.put("posterId", posterId);
        return ResponseEntity
                .ok(response);
    }

    @GetMapping("/api/posters")
    @ResponseBody
    public ResponseEntity<PagedModel> getPosterOverview(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "5") Integer size) {
        PagedModel<PosterOverviewResponse> posterOverview = posterService.getPosterOverview(page, size, null);
        return ResponseEntity.ok(posterOverview);
    }

}
