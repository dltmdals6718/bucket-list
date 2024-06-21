package com.example.bucketlist.controller;

import com.example.bucketlist.domain.Member;
import com.example.bucketlist.dto.request.MemberSigninRequest;
import com.example.bucketlist.dto.request.MemberSignupRequest;
import com.example.bucketlist.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
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

    @GetMapping("/signin")
    public String signinForm(Model model) {
        model.addAttribute("memberSigninRequest", new MemberSigninRequest());
        return "signin";
    }

    @PostMapping("/signin")
    public String signin(@Validated MemberSigninRequest memberSigninRequest, BindingResult bindingResult) {

        if(bindingResult.hasErrors())
            return "signin";

        Member member = memberService.signin(memberSigninRequest);

        if(member == null)
            bindingResult.reject("로그인실패", "아이디 또는 비밀번호가 틀렸습니다.");

        if(bindingResult.hasErrors())
            return "signin";

        return "redirect:/";
    }


}
