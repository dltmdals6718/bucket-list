package com.example.bucketlist.controller;

import com.example.bucketlist.dto.response.PosterOverviewResponse;
import com.example.bucketlist.service.PosterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {


    private final PosterService posterService;

    @Autowired
    public HomeController(PosterService posterService) {
        this.posterService = posterService;
    }

    @GetMapping("/")
    public String home(Model model) {


        PagedModel<PosterOverviewResponse> posterOverview = posterService.getPosterOverview(1, 5, null, null, null, null);
        model.addAttribute("allPosters", posterOverview.getContent());

        return "home";
    }


}
