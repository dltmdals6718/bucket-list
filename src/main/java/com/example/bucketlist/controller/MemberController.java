package com.example.bucketlist.controller;

import com.example.bucketlist.session.MemberSession;
import com.example.bucketlist.session.SessionConst;
import com.example.bucketlist.domain.Member;
import com.example.bucketlist.dto.request.MemberSigninRequest;
import com.example.bucketlist.dto.request.MemberSignupRequest;
import com.example.bucketlist.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
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

    private final MemberService memberService;

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
    public String signup(@Validated MemberSignupRequest memberSignupRequest, BindingResult bindingResult) {

        //빈 폼 입력시 에러 검출
        if(bindingResult.hasErrors())
            return "signup";

        if(!memberSignupRequest.getLoginPwd().equals(memberSignupRequest.getConfirmPwd()))
            bindingResult.rejectValue("confirmPwd", "confirmPwdFail", "비밀번호 미일치");

        // 비밀번호 재확인 미일치 에러 검출
        if(bindingResult.hasErrors())
            return "signup";

        memberService.signup(memberSignupRequest);
        return "redirect:/";
    }

    @GetMapping("/signin")
    public String signinForm(Model model) {
        model.addAttribute("memberSigninRequest", new MemberSigninRequest());
        return "signin";
    }

    @PostMapping("/signin")
    public String signin(@Validated MemberSigninRequest memberSigninRequest, BindingResult bindingResult, HttpServletRequest request) {

        // 빈 폼 입력시 에러 검출
        if(bindingResult.hasErrors())
            return "signin";

        Member member = memberService.signin(memberSigninRequest);

        if(member == null)
            bindingResult.reject("loginFail", "로그인 실패");

        // 폼 정보의 회원이 존재하지 않을때 에러 검출
        if(bindingResult.hasErrors())
            return "signin";

        HttpSession session = request.getSession();
        session.setAttribute(SessionConst.MEMBER_SESSION, new MemberSession(member.getId(), "임시 프로필 이미지 경로"));
        session.setMaxInactiveInterval(60 * 60 * 3);

        return "redirect:/";
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if(session != null)
            session.invalidate();
        return "redirect:/";
    }


}
