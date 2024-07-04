package com.example.bucketlist.controller;

import com.example.bucketlist.config.security.CustomUserDetails;
import com.example.bucketlist.domain.Member;
import com.example.bucketlist.dto.request.MemberProfileUpdateRequest;
import com.example.bucketlist.service.MailService;
import com.example.bucketlist.dto.request.MemberSigninRequest;
import com.example.bucketlist.dto.request.MemberSignupRequest;
import com.example.bucketlist.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;

@Controller
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;
    private final MailService mailService;
    private final MessageSource messageSource;

    @Autowired
    public MemberController(MemberService memberService, MailService mailService, MessageSource messageSource) {
        this.memberService = memberService;
        this.mailService = mailService;
        this.messageSource = messageSource;
    }

    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("memberSignupRequest", new MemberSignupRequest());
        return "member/signup";
    }

    @PostMapping("/signup")
    public String signup(@Validated MemberSignupRequest memberSignupRequest, BindingResult bindingResult) {

        //빈 폼 입력시 에러 검출
        if (bindingResult.hasErrors())
            return "member/signup";

        if (!memberSignupRequest.getLoginPwd().equals(memberSignupRequest.getConfirmPwd()))
            bindingResult.rejectValue("confirmPwd", "confirmPwdFail", "비밀번호 미일치");

        if (!mailService.checkMailVerificationCode(memberSignupRequest.getEmail(), Integer.parseInt(memberSignupRequest.getEmailCode())))
            bindingResult.rejectValue("emailCode", "emailCodeFail", "이메일 인증 번호 미일치");

        if (memberService.existsLoginId(memberSignupRequest.getLoginId()))
            bindingResult.rejectValue("loginId", "signupDuplicateLoginId", "중복된 아이디");

        if (bindingResult.hasErrors())
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
    public String profile(@AuthenticationPrincipal CustomUserDetails customUserDetails, Model model) {
        Member member = memberService.findMemberById(customUserDetails.getId());
        model.addAttribute("member", member);

        // todo: 회원 Model
        if (member.getProfileImage() != null)
            model.addAttribute("tempProfilePath", memberService.profileImgPath(member.getProfileImage()));
        return "member/profile";
    }

    @PutMapping("/profile")
    @ResponseBody
    public ResponseEntity updateMember(@AuthenticationPrincipal CustomUserDetails member, @RequestPart(name = "profile") @Validated MemberProfileUpdateRequest profileUpdateRequest, BindingResult bindingResult, @RequestPart(name = "profileImg", required = false) MultipartFile uploadProfileImage) throws IOException {

        HashMap<String, String> errorMsg = new HashMap<>();

        if (uploadProfileImage.getSize() > 1024 * 1024)
            errorMsg.put("profileImg", messageSource.getMessage("fileSizeOver", new String[]{"1MB"}, Locale.getDefault()));

        if (bindingResult.hasErrors()) {
            bindingResult.getFieldErrors().forEach(fieldError -> {
                String message;
                try {
                    message = messageSource.getMessage(fieldError.getCode(), null, Locale.getDefault());
                } catch (NoSuchMessageException ex) {
                    message = fieldError.getDefaultMessage();
                }
                errorMsg.put(fieldError.getField(), message);
            });
        }

        if (errorMsg.size() != 0)
            return ResponseEntity
                    .badRequest()
                    .body(errorMsg);

        memberService.updateMemberProfile(member.getId(), profileUpdateRequest, uploadProfileImage);

        return ResponseEntity
                .ok()
                .build();
    }

}
