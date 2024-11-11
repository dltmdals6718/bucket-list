package com.example.bucketlist.controller;

import com.example.bucketlist.config.security.CustomUserDetails;
import com.example.bucketlist.exception.ErrorCode;
import com.example.bucketlist.exception.UnauthenticationException;
import com.example.bucketlist.service.PosterCommentLikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PosterCommentLikeController {

    private final PosterCommentLikeService posterCommentLikeService;

    @Autowired
    public PosterCommentLikeController(PosterCommentLikeService posterCommentLikeService) {
        this.posterCommentLikeService = posterCommentLikeService;
    }

    @PostMapping("/api/comments-like/{commentId}")
    public ResponseEntity addPosterCommentLike(@AuthenticationPrincipal CustomUserDetails member,
                                               @PathVariable Long commentId) {

        if (member == null)
            throw new UnauthenticationException(ErrorCode.UNAUTHENTICATION);

        posterCommentLikeService.addPosterCommentLike(commentId, member.getId());

        return ResponseEntity
                .noContent()
                .build();
    }

}
