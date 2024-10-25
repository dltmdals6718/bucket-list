package com.example.bucketlist.controller;

import com.example.bucketlist.config.security.CustomUserDetails;
import com.example.bucketlist.dto.request.comment.PosterCommentUpdateRequest;
import com.example.bucketlist.dto.request.comment.PosterCommentWriteRequest;
import com.example.bucketlist.dto.response.comment.PosterCommentResponse;
import com.example.bucketlist.exception.ErrorCode;
import com.example.bucketlist.exception.InValidInputException;
import com.example.bucketlist.exception.UnauthenticationException;
import com.example.bucketlist.service.PosterCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class PosterCommentController {

    private final PosterCommentService posterCommentService;

    @Autowired
    public PosterCommentController(PosterCommentService posterCommentService) {
        this.posterCommentService = posterCommentService;
    }

    @GetMapping("/api/posters/{posterId}/comments")
    @ResponseBody
    public ResponseEntity<PagedModel> getComments(@AuthenticationPrincipal CustomUserDetails member,
                                                  @PathVariable Long posterId,
                                                  @RequestParam(defaultValue = "1") Integer page,
                                                  @RequestParam(defaultValue = "10") Integer size) {

        PagedModel<PosterCommentResponse> comments = posterCommentService.getComments(member, posterId, page, size);

        return ResponseEntity
                .ok(comments);
    }

    @PostMapping("/api/comments/write/{posterId}")
    @ResponseBody
    public ResponseEntity createComment(@AuthenticationPrincipal CustomUserDetails member,
                                        @PathVariable Long posterId,
                                        @RequestBody PosterCommentWriteRequest commentWriteRequest,
                                        BindingResult bindingResult) {

        if (member == null)
            throw new UnauthenticationException(ErrorCode.UNAUTHENTICATION);

        if (commentWriteRequest.getContent().isBlank())
            bindingResult.rejectValue("content", "blankPosterCommentContent", "내용 미입력");


        if (bindingResult.hasErrors())
            throw new InValidInputException(bindingResult);


        posterCommentService.createComment(posterId, member.getId(), commentWriteRequest);

        return ResponseEntity
                .ok()
                .build();
    }

    @DeleteMapping("/api/posters/{posterId}/comments/{commentId}")
    @ResponseBody
    public ResponseEntity deleteComment(@AuthenticationPrincipal CustomUserDetails member,
                                        @PathVariable Long posterId,
                                        @PathVariable Long commentId) {

        posterCommentService.deleteComment(posterId, commentId, member.getId());

        return ResponseEntity
                .noContent()
                .build();
    }

    @PutMapping("/api/posters/{posterId}/comments/{commentId}")
    @ResponseBody
    public ResponseEntity updateComment(@AuthenticationPrincipal CustomUserDetails member,
                                        @PathVariable Long posterId,
                                        @PathVariable Long commentId,
                                        @RequestBody PosterCommentUpdateRequest commentUpdateRequest,
                                        BindingResult bindingResult) {
        if (member == null)
            throw new UnauthenticationException(ErrorCode.UNAUTHENTICATION);

        if (commentUpdateRequest.getContent().isBlank())
            bindingResult.rejectValue("content", "blankPosterCommentContent", "내용 미입력");

        if (bindingResult.hasErrors())
            throw new InValidInputException(bindingResult);

        posterCommentService.updateComment(posterId, commentId, member.getId(), commentUpdateRequest);

        return ResponseEntity
                .ok()
                .build();
    }




}
