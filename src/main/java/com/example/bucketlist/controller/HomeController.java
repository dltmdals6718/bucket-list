package com.example.bucketlist.controller;

import com.example.bucketlist.dto.response.PosterOverviewResponse;
import com.example.bucketlist.service.PosterService;
import com.example.bucketlist.utils.EscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;

@Controller
public class HomeController {

    private final PosterService posterService;

    @Autowired
    public HomeController(PosterService posterService) {
        this.posterService = posterService;
    }

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/posters")
    public String getPosterOverview(@RequestParam(required = false, defaultValue = "1") int page,
                                    @RequestParam(required = false) String keyword,
                                    @RequestParam(required = false) String tags,
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
        PagedModel<PosterOverviewResponse> posterOverview = posterService.getPosterOverview(page, size, searchTag);
        PagedModel.PageMetadata metadata = posterOverview.getMetadata();
        model.addAttribute("posters", posterOverview.getContent());

        long currentPage = metadata.number() + 1;
        long totalPage = metadata.totalPages();

        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPage", totalPage);

        int blockPageSize = 10;
        long currentPageBlock = (long) Math.ceil(currentPage / (double) blockPageSize);
        long startPage = (currentPageBlock - 1) * blockPageSize + 1;
        long endPage = Math.min(currentPageBlock * blockPageSize, totalPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        if (tags != null && !tags.isBlank()) {
            model.addAttribute("tags", URLEncoder.encode(tags));
        }

        return "poster/view-poster-list";
    }

}
