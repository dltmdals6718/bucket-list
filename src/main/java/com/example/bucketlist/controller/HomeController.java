package com.example.bucketlist.controller;

import com.example.bucketlist.repository.PosterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class HomeController {

    private PosterRepository posterRepository;

    @Autowired
    public HomeController(PosterRepository posterRepository) {
        this.posterRepository = posterRepository;
    }

    @GetMapping("/")
    public String home() {
        return "home";
    }

}
