package com.example.bucketlist.controller;

import com.amazonaws.services.s3.AmazonS3Client;
import com.example.bucketlist.config.security.CustomUserDetails;
import com.example.bucketlist.service.MailService;
import com.example.bucketlist.dto.request.MemberSigninRequest;
import com.example.bucketlist.dto.request.MemberSignupRequest;
import com.example.bucketlist.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    private final MailService mailService;
    private final AmazonS3Client amazonS3Client;

    @Autowired
    public MemberController(MemberService memberService, MailService mailService, AmazonS3Client amazonS3Client) {
        this.memberService = memberService;
        this.mailService = mailService;
        this.amazonS3Client = amazonS3Client;
    }

    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("memberSignupRequest", new MemberSignupRequest());
        return "member/signup";
    }

    @PostMapping("/signup")
    public String signup(@Validated MemberSignupRequest memberSignupRequest, BindingResult bindingResult) {

        //빈 폼 입력시 에러 검출
        if(bindingResult.hasErrors())
            return "member/signup";

        if(!memberSignupRequest.getLoginPwd().equals(memberSignupRequest.getConfirmPwd()))
            bindingResult.rejectValue("confirmPwd", "confirmPwdFail", "비밀번호 미일치");

        if(!mailService.checkMailVerificationCode(memberSignupRequest.getEmail(), Integer.parseInt(memberSignupRequest.getEmailCode())))
            bindingResult.rejectValue("emailCode","emailCodeFail", "이메일 인증 번호 미일치");

        if (memberService.existsLoginId(memberSignupRequest.getLoginId()))
            bindingResult.rejectValue("loginId", "signupDuplicateLoginId", "중복된 아이디");

        if(bindingResult.hasErrors())
            return "member/signup";

        memberService.signup(memberSignupRequest);
        return "redirect:/";
    }

    @GetMapping("/signin")
    public String signinForm(Model model) {
        model.addAttribute("memberSigninRequest", new MemberSigninRequest());
        return "member/signin";
    }

    @PostMapping("/signin-error")
    public String signinError(Model model, HttpServletRequest request) {
        String loginId = request.getParameter("loginId");
        String errorMsg = (String) request.getAttribute("errorMsg");
        MemberSigninRequest memberSigninRequest = new MemberSigninRequest();
        memberSigninRequest.setLoginId(loginId);
        model.addAttribute("memberSigninRequest", memberSigninRequest);
        model.addAttribute("errorMsg", errorMsg);
        return "member/signin";
    }

    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        System.out.println("customUserDetails = " + customUserDetails);
        return "member/profile";
    }

}
