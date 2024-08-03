package com.example.bucketlist.controller;

import com.example.bucketlist.domain.Poster;
import com.example.bucketlist.repository.PosterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    private PosterRepository posterRepository;

    @Autowired
    public HomeController(PosterRepository posterRepository) {
        this.posterRepository = posterRepository;
    }

    @GetMapping("/")
    public String home(Model model) {
        List<Poster> all = posterRepository.findAll();
        model.addAttribute("posters", all);
        return "home";
    }

}
