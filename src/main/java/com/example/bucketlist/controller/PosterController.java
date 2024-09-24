package com.example.bucketlist.controller;

import com.example.bucketlist.config.security.CustomUserDetails;
import com.example.bucketlist.domain.Poster;
import com.example.bucketlist.dto.request.PosterWriteRequest;
import com.example.bucketlist.dto.response.PosterDetailsResponse;
import com.example.bucketlist.dto.response.PosterOverviewResponse;
import com.example.bucketlist.exception.ErrorCode;
import com.example.bucketlist.exception.InValidInputException;
import com.example.bucketlist.exception.UnauthenticationException;
import com.example.bucketlist.service.PosterLikeService;
import com.example.bucketlist.service.PosterService;
import com.example.bucketlist.utils.EscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Controller
public class PosterController {

    private final PosterService posterService;
    private final PosterLikeService posterLikeService;

    @Autowired
    public PosterController(PosterService posterService, PosterLikeService posterLikeService) {
        this.posterService = posterService;
        this.posterLikeService = posterLikeService;
    }

    @GetMapping("/posters")
    public String getPosterOverview(@RequestParam(required = false, defaultValue = "1") int page,
                                    @RequestParam(required = false) String keyword,
                                    @RequestParam(required = false) String tags,
                                    @RequestParam(required = false) String sort,
                                    @RequestParam(required = false) String status,
                                    Model model) {

        ArrayList<String> searchTag = new ArrayList<>();
        if (tags != null && !tags.isBlank()) {

            String[] splitTags = tags.split(",");
            String[] escapedTags = Arrays.stream(splitTags)
                    .map(tag -> EscapeUtils.escapeHtml(tag))
                    .toArray(String[]::new);

            searchTag = new ArrayList<>(Arrays.asList(escapedTags));
        }

        int size = 10;
        PagedModel<PosterOverviewResponse> posterOverview = posterService.getPosterOverview(page, size, searchTag, keyword, sort, status);
        PagedModel.PageMetadata metadata = posterOverview.getMetadata();
        model.addAttribute("posters", posterOverview.getContent());

        long currentPage = metadata.number() + 1;
        long totalPage = Math.max(metadata.totalPages(), 1); // 전체 페이지 개수가 0인 경우를 방지

        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPage", totalPage);

        int blockPageSize = 10;
        long currentPageBlock = (long) Math.ceil(currentPage / (double) blockPageSize);
        long startPage = (currentPageBlock - 1) * blockPageSize + 1;
        long endPage = Math.min(currentPageBlock * blockPageSize, totalPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        if (tags != null && !tags.isBlank())
            model.addAttribute("tags", URLEncoder.encode(tags));

        if (keyword != null && !keyword.isBlank())
            model.addAttribute("keyword", URLEncoder.encode(keyword));

        if (sort != null && !sort.isBlank())
            model.addAttribute("sort", URLEncoder.encode(sort));

        if (status != null && !status.isBlank())
            model.addAttribute("status", URLEncoder.encode(status));

        return "poster/view-poster-list";
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
    public ResponseEntity<PagedModel> getPosterOverview(@RequestParam(defaultValue = "1") Integer page,
                                                        @RequestParam(defaultValue = "10") Integer size,
                                                        @RequestParam(required = false) String keyword,
                                                        @RequestParam(required = false) String tags,
                                                        @RequestParam(required = false) String sort,
                                                        @RequestParam(required = false) String status) {

        ArrayList<String> searchTag = new ArrayList<>();
        if (tags != null && !tags.isBlank()) {

            String[] splitTags = tags.split(",");
            String[] escapedTags = Arrays.stream(splitTags)
                    .map(tag -> EscapeUtils.escapeHtml(tag))
                    .toArray(String[]::new);

            searchTag = new ArrayList<>(Arrays.asList(escapedTags));
        }

        PagedModel<PosterOverviewResponse> posterOverview = posterService.getPosterOverview(page, size, searchTag, keyword, sort, status);

        return ResponseEntity.ok(posterOverview);
    }

    @GetMapping("/api/write-posters")
    @ResponseBody
    public ResponseEntity<PagedModel> getWritePosterOverview(@AuthenticationPrincipal CustomUserDetails member,
                                                             @RequestParam(defaultValue = "1") Integer page,
                                                             @RequestParam(defaultValue = "5") Integer size,
                                                             @RequestParam(required = false) Long memberId) {

        PagedModel<PosterOverviewResponse> posterOverview;
        if (member == null) // 비로그인
            posterOverview = posterService.findPosterOverviewByMemberId(memberId, page, size, false);
        else if (memberId == null) // 마이페이지에서 자신 게시글 조회
            posterOverview = posterService.findPosterOverviewByMemberId(member.getId(), page, size, true);
        else // 타 회원 게시글 보기
            posterOverview = posterService.findPosterOverviewByMemberId(memberId, page, size, false);

        return ResponseEntity.ok(posterOverview);
    }

    @GetMapping("/api/like-posters")
    @ResponseBody
    public ResponseEntity<PagedModel> getLikePosterOverview(@AuthenticationPrincipal CustomUserDetails member,
                                                            @RequestParam(defaultValue = "1") Integer page,
                                                            @RequestParam(defaultValue = "5") Integer size) {

        if (member == null) // 비로그인
            throw new UnauthenticationException(ErrorCode.UNAUTHENTICATION);

        PagedModel<PosterOverviewResponse> posterOverview = posterLikeService.getLikePosters(member.getId(), page, size);

        return ResponseEntity.ok(posterOverview);
    }

    @PostMapping("/api/posters/{posterId}/like")
    @ResponseBody
    public ResponseEntity addPosterLike(@AuthenticationPrincipal CustomUserDetails member,
                                        @PathVariable Long posterId) {

        if (member == null)
            throw new UnauthenticationException(ErrorCode.UNAUTHENTICATION);

        Long memberId = member.getId();
        posterLikeService.addPosterLike(posterId, memberId);

        return ResponseEntity
                .noContent()
                .build();
    }

}
