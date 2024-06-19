package com.example.bucketlist.controller;

import com.example.bucketlist.dto.request.MemberSignupRequest;
import com.example.bucketlist.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/members")
public class MemberController {

    private MemberService memberService;

    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("memberSignupRequest", new MemberSignupRequest());
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(MemberSignupRequest memberSignupRequest) {
        memberService.signup(memberSignupRequest);
        return "redirect:/";
    }


}
