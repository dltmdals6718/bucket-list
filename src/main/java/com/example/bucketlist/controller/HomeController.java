package com.example.bucketlist.controller;

import com.example.bucketlist.dto.response.PosterOverviewResponse;
import com.example.bucketlist.service.PosterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


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
                                    Model model) {

        int size = 10;
        PagedModel<PosterOverviewResponse> posterOverview = posterService.getPosterOverview(page, size, null);
        PagedModel.PageMetadata metadata = posterOverview.getMetadata();
        System.out.println("metadata = " + metadata);
        model.addAttribute("posters", posterOverview.getContent());

        long currentPage = posterOverview.getMetadata().number() + 1;
        long totalPage = posterOverview.getMetadata().totalPages();

        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPage", totalPage);

        int blockPageSize = 10;
        long currentPageBlock = (long) Math.ceil(currentPage / (double) blockPageSize);
        long startPage = (currentPageBlock - 1) * blockPageSize + 1;
        long endPage = Math.min(currentPageBlock * blockPageSize, totalPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "poster/view-poster-list";
    }

}
